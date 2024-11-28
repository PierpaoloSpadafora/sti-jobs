import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { JobDTO, ScheduleDTO } from '../../generated-api';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { SchedulerService } from '../../services/scheduler.service';
import { JobService } from "../../services/job.service";
import Swal from "sweetalert2";

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
  selectedSchedule?: ScheduleDTO;

  @ViewChild('scheduleDialog') scheduleDialog!: TemplateRef<any>;

  constructor(
    private jobService: JobService,
    private scheduleService: SchedulerService,
    private dialog: MatDialog,
    private fb: FormBuilder
  ) {
    this.scheduleForm = this.fb.group({
      startTime: [null, Validators.required],
      dueDate: [null, Validators.required]
    });
    this.generateAvailableStartTimes();
  }

  ngOnInit() {
    this.getJobs();
  }

  getJobs() {
    this.jobService.showJob().subscribe({
      next: (response: any) => {
        this.jobs = Array.isArray(response) ? response : [response];
        console.log('Jobs retrieved:', this.jobs);
        this.getAllSchedules();
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

        this.schedules.forEach(schedule => {
          if (schedule.jobId) {
            const job = this.jobs.find(j => j.id === schedule.jobId);
            if (job) {
              schedule.job = job;
            } else {
              console.warn(`Job with ID ${schedule.jobId} not found.`);
            }
          }
        });
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
    const startTime = this.scheduleForm.value.startTime;
    const jobDuration = this.selectedJob.duration ?? 0;

    if (startTime && jobDuration) {
      const start = new Date(startTime);
      const durationInMilliseconds = jobDuration * 1000;
      this.calculatedEndTime = new Date(start.getTime() + durationInMilliseconds);
    } else {
      this.calculatedEndTime = null;
    }
  }

  confirmDeleteSchedule(schedule: ScheduleDTO): void {
    Swal.fire({
      title: 'Sei sicuro?',
      text: `Vuoi eliminare il job schedulato con ID "${schedule.jobId}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sì, elimina!',
      cancelButtonText: 'Annulla'
    }).then((result) => {
      if (result.isConfirmed) {
        this.deleteSchedule(schedule);
      }
    });
  }

  deleteSchedule(schedule: ScheduleDTO) {
    if (!schedule.id) {
      console.error('Schedule ID is undefined');
      return;
    }

    this.scheduleService.deleteSchedule(schedule.id).subscribe({
      next: () => {
        console.log('Schedule deleted successfully');
        this.getAllSchedules();
        Swal.fire(
          'Eliminato!',
          `Il job schedulato con ID "${schedule.jobId}" è stato eliminato.`,
          'success'
        );
      },
      error: (error) => {
        console.error('Error deleting schedule:', error);
        Swal.fire(
          'Errore!',
          'Non è stato possibile eliminare il job schedulato. Per favore, riprova più tardi.',
          'error'
        );
      }
    });
  }

  openEditScheduleDialog(schedule: ScheduleDTO) {
    this.selectedSchedule = schedule;
    this.scheduleForm.patchValue({
      startTime: new Date(schedule.startTime),
      dueDate: new Date(schedule.dueDate)
    });
    if (schedule.duration) {
      this.calculatedEndTime = new Date(new Date(schedule.startTime).getTime() + schedule.duration * 1000);
    } else {
      this.calculatedEndTime = null;
    }
    this.dialogRef = this.dialog.open(this.scheduleDialog, {
      width: '600px',
      data: { job: this.jobs.find(job => job.id === schedule.jobId) }
    });
  }

  scheduleJob() {
    const startTime = this.scheduleForm.value.startTime;
    const dueDate = this.scheduleForm.value.dueDate;

    const jobDuration = this.selectedJob.duration ?? 0;

    const scheduleData: ScheduleDTO = {
      id: this.selectedSchedule?.id,
      jobId: this.selectedJob.id,
      machineType: this.selectedJob.requiredMachineType?.name || '',
      dueDate: dueDate.toISOString(),
      startTime: startTime.toISOString(),
      duration: jobDuration,
      status: 'SCHEDULED'
    };

    if (this.selectedSchedule) {
      this.scheduleService.updateSchedule(this.selectedSchedule.id!, scheduleData).subscribe({
        next: (response) => {
          console.log('Schedule updated successfully:', response);
          this.dialogRef.close();
          this.getAllSchedules();
          Swal.fire(
            'Aggiornato!',
            `Il job schedulato con ID "${scheduleData.jobId}" è stato aggiornato.`,
            'success'
          );
        },
        error: (error) => {
          console.error('Error updating schedule:', error);
          Swal.fire(
            'Errore!',
            'Non è stato possibile aggiornare il job schedulato. Per favore, riprova più tardi.',
            'error'
          );
        }
      });
    } else {
      this.scheduleService.createSchedule(scheduleData).subscribe({
        next: (response) => {
          console.log('Schedule created successfully:', response);
          this.dialogRef.close();
          this.getAllSchedules();
          Swal.fire(
            'Creato!',
            `Il job schedulato con ID "${scheduleData.jobId}" è stato creato.`,
            'success'
          );
        },
        error: (error) => {
          console.error('Error creating schedule:', error);
          Swal.fire(
            'Errore!',
            'Non è stato possibile creare il job schedulato. Per favore, riprova più tardi.',
            'error'
          );
        }
      });
    }
  }

  secondsToDuration(seconds?: number): string {
    if (!seconds) {
      return '0m';
    }
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
