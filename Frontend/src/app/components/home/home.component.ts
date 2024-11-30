import { Component, OnInit } from '@angular/core';
import { JsonControllerService } from '../../generated-api';
import { ScheduleControllerService } from '../../generated-api';
import { JobDTO } from '../../generated-api';
import { ScheduleDTO } from '../../generated-api';
import { ChartType } from 'angular-google-charts';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  scheduleData: ScheduleDTO[] = [];
  jobsMap: Map<number, JobDTO> = new Map<number, JobDTO>();

  public chartData: any[] = [];
  public chartOptions: ChartOptions = {
    height: 600,
    gantt: {
      trackHeight: 30,
      barCornerRadius: 5,
      labelStyle: {
        fontName: 'Arial',
        fontSize: 12,
        color: '#000'
      },
      criticalPathEnabled: true,
      timeline: {
        showRowLabels: false
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

  scheduleTypes = [
    { label: 'All Jobs', value: 'ALL' },
    { label: 'Scheduled by Due Date', value: 'DUE_DATE' },
    { label: 'Scheduled by Priority', value: 'PRIORITY' }
  ];

  selectedScheduleType = 'ALL';

  hasScheduledJobs = false;
  loading = true;

  constructor(
    private scheduleService: ScheduleControllerService,
    private jsonService: JsonControllerService,
    private router: Router
  ) { }

  ngOnInit() {
    this.fetchData();
  }

  onScheduleTypeChange() {
    this.fetchData();
  }

  fetchData() {
    this.loading = true;
    let scheduleObservable: Observable<ScheduleDTO[]>;

    if (this.selectedScheduleType === 'ALL') {
      scheduleObservable = this.scheduleService.getAllSchedules();
    } else if (this.selectedScheduleType === 'DUE_DATE') {
      scheduleObservable = this.jsonService.exportJobScheduledDueDate();
    } else if (this.selectedScheduleType === 'PRIORITY') {
      scheduleObservable = this.jsonService.exportJobScheduledPriority();
    } else {
      scheduleObservable = this.scheduleService.getAllSchedules();
    }

    scheduleObservable.subscribe(scheduleData => {
      this.scheduleData = scheduleData;
      this.jsonService.exportJob().subscribe(jobs => {
        this.jobsMap.clear();
        jobs.forEach(job => {
          if (job.id !== undefined) {
            this.jobsMap.set(job.id, job);
          }
        });
        this.processData();
        this.loading = false;
      });
    }, error => {
      console.error('Error fetching schedule data', error);
      this.loading = false;
    });
  }

  processData() {
    if (this.scheduleData.length === 0) {
      this.hasScheduledJobs = false;
      return;
    } else {
      this.hasScheduledJobs = true;
    }

    let minStartDate: Date | null = null;
    let maxEndDate: Date | null = null;

    this.chartData = this.scheduleData.map(schedule => {
      const job = this.jobsMap.get(schedule.jobId || 0);
      const taskId = schedule.id?.toString() || '';
      const taskName = job ? job.title : 'Unknown Job';

      let startDate = new Date(schedule.startTime || '');
      let endDate = new Date(startDate.getTime() + (schedule.duration || 0) * 1000);

      if (isNaN(startDate.getTime())) {
        console.error('Invalid start date for task:', taskId);
        startDate = new Date();
      }
      if (isNaN(endDate.getTime())) {
        console.error('Invalid end date for task:', taskId);
        endDate = new Date(startDate.getTime() + 3600000);
      }

      // Update min and max dates
      if (!minStartDate || startDate < minStartDate) {
        minStartDate = startDate;
      }
      if (!maxEndDate || endDate > maxEndDate) {
        maxEndDate = endDate;
      }

      const row = [
        taskId,
        taskName,
        startDate,
        endDate,
        null,
        100,
        null
      ];

      return row;
    });

    if (minStartDate && maxEndDate) {
      this.chartOptions = {
        ...this.chartOptions,
        hAxis: {
          minValue: minStartDate,
          maxValue: maxEndDate
        }
      };
    }
  }

  navigateToSchedule() {
    this.router.navigate(['/schedule']);
  }

}

interface ChartOptions {
  height: number;
  gantt: {
    trackHeight: number;
    barCornerRadius: number;
    labelStyle: {
      fontName: string;
      fontSize: number;
      color: string;
    };
    criticalPathEnabled: boolean;
    timeline: {
      showRowLabels: boolean;
    };
  };
  hAxis?: {
    minValue: Date;
    maxValue: Date;
  };
}
