import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { JobDTO, ScheduleDTO } from '../../generated-api';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { SchedulerService } from '../../services/scheduler.service';
import { JobService } from "../../services/job.service";

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.css']
})
export class ScheduleComponent implements OnInit {
  jobs: JobDTO[] = [];
  schedules: ScheduleDTO[] = [];
  displayedColumns: string[] = ['title', 'description', 'actions'];
  scheduleForm: FormGroup;
  dialogRef!: MatDialogRef<any>;
  selectedJob!: JobDTO;
  calculatedEndTime!: Date | null;
  availableStartTimes: Date[] = [];

  @ViewChild('scheduleDialog') scheduleDialog!: TemplateRef<any>;

  constructor(
    private jobService: JobService,
    private scheduleService: SchedulerService,
    private dialog: MatDialog,
    private fb: FormBuilder
  ) {
    this.scheduleForm = this.fb.group({
      startTime: [null, Validators.required]
    });
    this.generateAvailableStartTimes();
  }

  ngOnInit() {
    this.getJobs();
    this.getAllSchedules();
  }

  getJobs() {
    this.jobService.showJob().subscribe({
      next: (response: any) => {
        this.jobs = Array.isArray(response) ? response : [response];
        console.log('Jobs retrieved:', this.jobs);
      },
      error: (error: any) => {
        console.error('Error while retrieving jobs:', error);
      }
    });
  }

  getAllSchedules() {
    this.scheduleService.getAllSchedules().subscribe({
      next: (response: ScheduleDTO[]) => {
        this.schedules = response;
        console.log('Schedules retrieved:', this.schedules);
      },
      error: (error: any) => {
        console.error('Error while retrieving schedules:', error);
      }
    });
  }

  generateAvailableStartTimes() {
    const startHour = 8;
    const endHour = 18;
    const interval = 30;
    this.availableStartTimes = [];

    const today = new Date();
    today.setSeconds(0, 0);
    for (let hour = startHour; hour < endHour; hour++) {
      for (let minutes = 0; minutes < 60; minutes += interval) {
        const time = new Date(today.getTime());
        time.setHours(hour, minutes, 0, 0);
        this.availableStartTimes.push(time);
      }
    }
  }

  openScheduleDialog(job: JobDTO) {
    this.selectedJob = job;
    this.scheduleForm.reset();
    this.calculatedEndTime = null;
    this.dialogRef = this.dialog.open(this.scheduleDialog, {
      width: '600px',
      data: { job }
    });
  }

  onStartTimeChange() {
    if (this.scheduleForm.value.startTime && this.selectedJob.duration) {
      const startTime = new Date(this.scheduleForm.value.startTime);
      const durationInMinutes = this.selectedJob.duration / 60;
      this.calculatedEndTime = new Date(startTime.getTime() + durationInMinutes * 60000);
    } else {
      this.calculatedEndTime = null;
    }
  }

  scheduleJob() {
    if (!this.selectedJob.id) {
      console.error('Job ID is undefined');
      return;
    }

    const startTime = new Date(this.scheduleForm.value.startTime);
    if (!startTime) {
      console.error('Start time is invalid');
      return;
    }

    if (!this.selectedJob.duration) {
      console.error('Job duration is undefined');
      return;
    }

    console.log('this.selectedJob.requiredMachineType:', this.selectedJob.requiredMachineType);

    // @ts-ignore
    const machineTypeString = this.selectedJob.requiredMachineType.name; // Adjust if needed

    const scheduleData: ScheduleDTO = {
      jobId: this.selectedJob.id,
      // @ts-ignore
      machineType: machineTypeString,
      dueDate: startTime.toISOString(),
      startTime: startTime.toISOString(),
      duration: this.selectedJob.duration,
      status: 'SCHEDULED'
    };

    console.log('scheduleData:', scheduleData);

    this.scheduleService.createSchedule(scheduleData).subscribe({
      next: (response) => {
        console.log('Schedule created successfully:', response);
        this.dialogRef.close();
        this.getAllSchedules();
      },
      error: (error) => {
        console.error('Error creating schedule:', error);
      }
    });
  }

  secondsToDuration(seconds: number) {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    if (hours === 0) {
      return `${minutes}m`;
    } else if (minutes === 0) {
      return `${hours}h`;
    }
    return `${hours}h ${minutes}m`;
  }

  protected readonly Number = Number;
}
