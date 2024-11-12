import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { JobDTO, ScheduleDTO } from '../../generated-api';
import { ScheduleControllerService } from '../../generated-api';
import { JobService } from '../../services/job.service';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.css']
})
export class ScheduleComponent implements OnInit {
  jobs: JobDTO[] = [];
  displayedColumns: string[] = ['title', 'description', 'actions'];
  scheduleForm: FormGroup;
  dialogRef!: MatDialogRef<any>;
  selectedJob!: JobDTO;

  @ViewChild('scheduleDialog') scheduleDialog!: TemplateRef<any>;

  constructor(
    private jobService: JobService,
    private scheduleService: ScheduleControllerService,
    private dialog: MatDialog,
    private fb: FormBuilder
  ) {
    this.scheduleForm = this.fb.group({
      startTime: [null, Validators.required],
      endTime: [null, Validators.required]
    });
  }

  ngOnInit() {
    this.jobService.showJob().subscribe({
      next: (response: any) => {
        this.jobs = Array.isArray(response) ? response : [response];
        console.log('Jobs retrieved:', this.jobs);
      },
      error: (error) => {
        console.error('Error while retrieving jobs:', error);
      }
    });
  }

  openScheduleDialog(job: JobDTO) {
    this.selectedJob = job;
    this.scheduleForm.reset();
    this.dialogRef = this.dialog.open(this.scheduleDialog, {
      width: '400px',
      data: { job }
    });
  }

  scheduleJob() {
    if (!this.selectedJob.id) {
      console.error('Job ID is undefined');
      return;
    }

    const scheduleData: ScheduleDTO = {
      jobId: this.selectedJob.id,
      startTime: this.scheduleForm.value.startTime.toISOString(),
      endTime: this.scheduleForm.value.endTime.toISOString()
    };

    this.scheduleService.createSchedule(scheduleData).subscribe({
      next: (response) => {
        console.log('Schedule created successfully:', response);
        this.dialogRef.close();
      },
      error: (error) => {
        console.error('Error creating schedule:', error);
      }
    });
  }
}
