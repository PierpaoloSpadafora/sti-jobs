import { Component, OnInit, Inject } from '@angular/core';
import { JsonControllerService } from '../../generated-api';
import { ScheduleControllerService } from '../../generated-api';
import { JobDTO } from '../../generated-api';
import { ScheduleDTO } from '../../generated-api';
import { ChartType } from 'angular-google-charts';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { InjectionToken } from '@angular/core';

export const SCHEDULE_SERVICE = new InjectionToken<ScheduleControllerService>('ScheduleControllerService');
export const JSON_SERVICE = new InjectionToken<JsonControllerService>('JsonControllerService');

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
    { label: 'Scheduled by Priority', value: 'PRIORITY' },
    { label: 'Scheduled by Duration', value: 'DURATION' }
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
    let scheduleObservable;
    switch (this.selectedScheduleType) {
      case 'PRIORITY':
        scheduleObservable = this.jsonService.exportJobScheduledPriority();
        break;
      case 'DUE_DATE':
        scheduleObservable = this.jsonService.exportJobScheduledDueDate();
        break;
      case 'DURATION':
        scheduleObservable = this.jsonService.exportJobScheduledDuration();
        break;
      default:
        scheduleObservable = this.scheduleService.getAllSchedules();
    }

    // Otteniamo prima i job per il mapping
    this.jsonService.exportJob().subscribe({
      next: (jobs) => {
        this.jobsMap.clear();
        jobs.forEach(job => {
          if (job.id !== undefined) {
            this.jobsMap.set(job.id, job);
          }
        });

        // Correzione: aggiungiamo type casting per l'observable
        (scheduleObservable as Observable<ScheduleDTO[]>).subscribe({
          next: (scheduleData: ScheduleDTO[]) => {
            this.scheduleData = scheduleData;
            this.processData();
            this.loading = false;
          },
          error: (error: any) => {
            console.error('Error fetching schedule data', error);
            this.loading = false;
          }
        });
      },
      error: (error: any) => {
        console.error('Error fetching jobs data', error);
        this.loading = false;
      }
    });
  }

  processData() {
    if (this.scheduleData.length === 0) {
      this.hasScheduledJobs = false;
      return;
    }
    this.hasScheduledJobs = true;

    let minStartDate: Date | null = null;
    let maxEndDate: Date | null = null;

    this.chartData = this.scheduleData.map(schedule => {
      const job = this.jobsMap.get(schedule.jobId || 0);
      const taskId = schedule.id?.toString() || '';
      const taskName = job ? job.title : 'Unknown Job';

      let startDate: Date;
      if (schedule.startTime && !isNaN(new Date(schedule.startTime).getTime())) {
        startDate = new Date(schedule.startTime);
      } else {
        console.warn(`Invalid startTime for task ${taskId}, using current date`);
        startDate = new Date();
      }

      let endDate: Date;
      try {
        const duration = schedule.duration || 3600;
        endDate = new Date(startDate.getTime() + duration * 1000);
      } catch (error) {
        console.error(`Error calculating end date for task ${taskId}:`, error);
        endDate = new Date(startDate.getTime() + 3600000);
      }

      if (!minStartDate || startDate < minStartDate) {
        minStartDate = startDate;
      }
      if (!maxEndDate || endDate > maxEndDate) {
        maxEndDate = endDate;
      }

      return [
        taskId,
        taskName,
        startDate,
        endDate,
        null,
        100,
        null
      ];
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