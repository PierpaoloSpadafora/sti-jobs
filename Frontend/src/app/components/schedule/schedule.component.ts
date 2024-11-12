import { Component, OnInit } from '@angular/core';
import { JobDTO } from '../../generated-api';
import { ScheduleControllerService } from '../../generated-api';
import { JobService } from '../../services/job.service';

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrl: './schedule.component.css'
})
export class ScheduleComponent implements OnInit {
  jobs: JobDTO[] | undefined;
  startTime: any;
  endTime: any;

  constructor(
    private jobService: JobService,
    private scheduleService: ScheduleControllerService
  ) { }

  ngOnInit() {
    this.jobService.showJob().subscribe({
      next: (response: any) => {
        this.jobs = Array.isArray(response) ? response : [response];
        console.log("Jobs retrieved:", this.jobs);
      },
      error: (error) => {
        console.error("Error while retrieving jobs:", error);
      }
    });
  }

  scheduleJob(job: JobDTO) {
    const scheduleData = {
      jobId: job.id,
    };
    this.scheduleService.createSchedule(scheduleData).subscribe(
      response => {
      },
      error => {
      }
    );
  }
}
