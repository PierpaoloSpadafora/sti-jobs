import { Component, OnInit } from '@angular/core';
import { JobControllerService } from '../../generated-api';
import { JobDTO } from '../../generated-api';
import { ScheduleControllerService } from '../../generated-api';

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
    private jobService: JobControllerService,
    private scheduleService: ScheduleControllerService
  ) { }

  ngOnInit() {
    this.jobService.getAllJobs().subscribe((data) => {
      this.jobs = Array.isArray(data) ? data : [data];
      console.log(this.jobs);
      /*
      for(let job in this.jobs){
        console.log(job.title);
      }
      */
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
