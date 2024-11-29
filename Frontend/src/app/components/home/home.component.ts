import { Component, OnInit } from '@angular/core';
import { JsonControllerService } from '../../generated-api';
import { ScheduleControllerService } from '../../generated-api';
import { JobDTO } from '../../generated-api';
import { ScheduleDTO } from '../../generated-api';
import { ChartType } from 'angular-google-charts';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  scheduleData: ScheduleDTO[] = [];
  jobsMap: Map<number, JobDTO> = new Map<number, JobDTO>();

  public chartData: any[] = [];
  public chartOptions = {
    height: 600,
    gantt: {
      trackHeight: 30,
      barCornerRadius: 5,
      labelStyle: {
        fontName: 'Arial',
        fontSize: 12,
        color: '#000'
      }
    }
  };
  public chartColumns = [
    { type: 'string', label: 'Task ID' },
    { type: 'string', label: 'Task Name' },
    { type: 'date', label: 'Start Date' },
    { type: 'date', label: 'End Date' },
    { type: 'number', label: 'Duration' },
    { type: 'number', label: 'Percent Complete' },
    { type: 'string', label: 'Dependencies' }
  ];
  public chartType = ChartType.Gantt;

  constructor(
    private scheduleService: ScheduleControllerService,
    private jsonService: JsonControllerService
  ) { }

  ngOnInit() {
    this.fetchData();
  }

  fetchData() {
    this.scheduleService.getAllSchedules().subscribe(scheduleData => {
      this.scheduleData = scheduleData;
      this.jsonService.exportJob().subscribe(jobs => {
        jobs.forEach(job => {
          if (job.id !== undefined) {
            this.jobsMap.set(job.id, job);
          }
        });
        this.processData();
      });
    }, error => {
      console.error('Errore nel recupero dei dati di scheduling', error);
    });
  }

  processData() {
    console.log('Raw Schedule Data:', this.scheduleData);
    
    try {
      this.chartData = this.scheduleData.map(schedule => {
        const job = this.jobsMap.get(schedule.jobId || 0);
        const taskId = schedule.id?.toString() || '';
        const taskName = job ? job.title : 'Job Sconosciuto';

        // Assicuriamoci che le date siano oggetti Date validi
        let startDate = new Date(schedule.startTime || '');
        let endDate = new Date(startDate.getTime() + (schedule.duration || 0) * 1000);

        // Verifica se le date sono valide
        if (isNaN(startDate.getTime())) {
          console.error('Invalid start date for task:', taskId);
          startDate = new Date(); // fallback to current date
        }
        if (isNaN(endDate.getTime())) {
          console.error('Invalid end date for task:', taskId);
          endDate = new Date(startDate.getTime() + 3600000); // fallback to start + 1 hour
        }

        const row = [
          taskId,      // string
          taskName,    // string
          startDate,   // Date object
          endDate,     // Date object
          null,        // number or null
          100,         // number
          null         // string or null
        ];

        console.log('Generated row:', row);
        return row;
      });

      console.log('Final Chart Data:', this.chartData);
    } catch (error) {
      console.error('Error processing data:', error);
      this.chartData = []; // Reset in caso di errore
    }
  }

}
