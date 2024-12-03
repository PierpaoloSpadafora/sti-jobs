import {
  Component,
  OnInit,
  ViewChild,
  TemplateRef,
} from '@angular/core';
import {
  JobDTO,
  ScheduleDTO,
  ScheduleControllerService,
  JobControllerService,
  SchedulerEngineService,
} from '../../generated-api';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import {Observable} from "rxjs";

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.css'],
})
export class ScheduleComponent implements OnInit {
  jobs: JobDTO[] = [];
  schedules: ScheduleDTO[] = [];
  scheduleForm: FormGroup;
  dialogRef!: MatDialogRef<any>;
  selectedJob!: JobDTO;
  calculatedEndTime!: Date | null;
  selectedSchedule?: ScheduleDTO;

  currentJobPage: number = 1;
  jobPageSize: number = 5;
  totalJobPages: number = 1;

  currentSchedulePage: number = 1;
  schedulePageSize: number = 5;
  totalSchedulePages: number = 1;

  hourOptions: number[] = [];
  minuteOptions: number[] = [0, 15, 30, 45];

  @ViewChild('scheduleDialog') scheduleDialog!: TemplateRef<any>;
  @ViewChild('schedulingDialog') schedulingDialog!: TemplateRef<any>;  // Aggiungi questa riga

  selectedSchedulingType: string = '';
  schedulingDialogRef!: MatDialogRef<any>;
  loading: boolean = false;

  constructor(
    private jobService: JobControllerService,
    private scheduleService: ScheduleControllerService,
    private dialog: MatDialog,
    private fb: FormBuilder,
    private schedulerEngineService: SchedulerEngineService
  ) {
    this.scheduleForm = this.fb.group({
      startDate: [new Date(), Validators.required],
      startHour: [8, Validators.required],
      startMinute: [0, Validators.required],
      dueDate: [null, Validators.required],
    });
  }

  ngOnInit() {
    this.getJobs();
    this.initializeTimeOptions();

    this.scheduleForm
      .get('startHour')
      ?.valueChanges.subscribe(() => this.onStartTimeChange());
    this.scheduleForm
      .get('startMinute')
      ?.valueChanges.subscribe(() => this.onStartTimeChange());
  }

  initializeTimeOptions() {
    this.hourOptions = Array.from({ length: 24 }, (_, i) => i);
  }

  getJobs() {
    this.jobService.getAllJobs().subscribe({
      next: (response: any) => {
        this.jobs = Array.isArray(response) ? response : [response];
        console.log('Jobs retrieved:', this.jobs);
        this.totalJobPages = Math.ceil(
          this.jobs.length / this.jobPageSize
        );
        this.currentJobPage = 1;
        this.getAllSchedules();
      },
      error: (error: any) => {
        console.error('Error while retrieving jobs:', error);
      },
    });
  }

  get paginatedJobs(): JobDTO[] {
    const start = (this.currentJobPage - 1) * this.jobPageSize;
    return this.jobs.slice(start, start + this.jobPageSize);
  }

  nextJobPage() {
    if (this.currentJobPage < this.totalJobPages) {
      this.currentJobPage++;
    }
  }

  previousJobPage() {
    if (this.currentJobPage > 1) {
      this.currentJobPage--;
    }
  }

  getAllSchedules() {
    this.scheduleService.getAllSchedules().subscribe({
      next: (response: ScheduleDTO[]) => {
        this.schedules = response;
        console.log('Schedules retrieved:', this.schedules);

        this.totalSchedulePages = Math.ceil(
          this.schedules.length / this.schedulePageSize
        );
        this.currentSchedulePage = 1;

        this.schedules.forEach((schedule) => {
          if (schedule.jobId) {
            const job = this.jobs.find(
              (j) => j.id === schedule.jobId
            );
            if (job) {
              // schedule.jobTitle = job.title; // Se necessario
            } else {
              console.warn(
                `Job with ID ${schedule.jobId} not found.`
              );
            }
          }
        });
      },
      error: (error: any) => {
        console.error('Error while retrieving schedules:', error);
      },
    });
  }

  get paginatedSchedules(): ScheduleDTO[] {
    const start =
      (this.currentSchedulePage - 1) * this.schedulePageSize;
    return this.schedules.slice(start, start + this.schedulePageSize);
  }

  nextSchedulePage() {
    if (this.currentSchedulePage < this.totalSchedulePages) {
      this.currentSchedulePage++;
    }
  }

  previousSchedulePage() {
    if (this.currentSchedulePage > 1) {
      this.currentSchedulePage--;
    }
  }

  openScheduleDialog(job: JobDTO) {
    this.selectedJob = job;
    this.scheduleForm.reset({
      startDate: new Date(),
      startHour: 8,
      startMinute: 0,
      dueDate: null,
    });
    this.calculatedEndTime = null;
    this.dialogRef = this.dialog.open(this.scheduleDialog, {
      width: '600px',
      data: { job },
    });
    this.onStartTimeChange();
  }

  onStartTimeChange() {
    let startDate = this.scheduleForm.value.startDate;
    const startHour = this.scheduleForm.value.startHour;
    const startMinute = this.scheduleForm.value.startMinute;

    if (startDate == null) {
      startDate = new Date();
      this.scheduleForm.patchValue({ startDate });
    }

    if (startHour !== null && startMinute !== null) {
      const startDateTime = new Date(startDate);
      startDateTime.setHours(startHour, startMinute, 0, 0);

      const jobDuration = this.selectedJob.duration ?? 0;
      const durationInMilliseconds = jobDuration * 1000;
      this.calculatedEndTime = new Date(
        startDateTime.getTime() + durationInMilliseconds
      );
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
      cancelButtonText: 'Annulla',
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
      },
    });
  }

  openEditScheduleDialog(schedule: ScheduleDTO) {
    this.selectedSchedule = schedule;
    this.selectedJob = this.jobs.find(
      (job) => job.id === schedule.jobId
    )!;

    const startDateTime = schedule.startTime
      ? new Date(schedule.startTime)
      : null;

    this.scheduleForm.patchValue({
      startDate: startDateTime || new Date(),
      startHour: startDateTime ? startDateTime.getHours() : 8,
      startMinute: startDateTime ? startDateTime.getMinutes() : 0,
      dueDate: schedule.dueDate ? new Date(schedule.dueDate) : null,
    });

    if (schedule.duration && schedule.startTime) {
      this.calculatedEndTime = new Date(
        startDateTime!.getTime() + schedule.duration * 1000
      );
    } else {
      this.calculatedEndTime = null;
    }

    this.dialogRef = this.dialog.open(this.scheduleDialog, {
      width: '600px',
      data: { job: this.selectedJob },
    });
    this.onStartTimeChange();
  }

  scheduleJob() {
    const startDate = this.scheduleForm.value.startDate;
    const startHour = this.scheduleForm.value.startHour;
    const startMinute = this.scheduleForm.value.startMinute;
    const dueDate = this.scheduleForm.value.dueDate;

    if (
      startDate !== null &&
      startHour !== null &&
      startMinute !== null &&
      dueDate !== null
    ) {
      const startDateTime = new Date(startDate);
      startDateTime.setHours(startHour+1, startMinute, 0, 0);

      const dueDateTime = new Date(dueDate);
      dueDateTime.setHours(1, 0, 0, 0);
      /*
      I +1 SONO CONCETTUALMENTE SBAGLIATI MA NECESSARI.
      NON HA UN CAZZO DI SENSO CHE toISOString() TOLGA UN'ORA ALL'ORARIO PERCHÈ IL CALENDARIO SI BASA SU UTC+1 E ISOSTRING NO
      FACCIA TORNARE LA DATA INDIETRO DI UN GIORNO, SPERO CHE GLI SVILUPPATORI DI MOZILLA ESPLODANO COME IN UN FILM DI MICHAEL BAY
      */
      console.log(startDateTime.toISOString())
      console.log(dueDateTime.toISOString())

      const jobDuration = this.selectedJob.duration ?? 0;

      const scheduleData: ScheduleDTO = {
        id: this.selectedSchedule?.id,
        jobId: this.selectedJob.id,
        machineTypeId: this.selectedJob.idMachineType,
        //@ts-ignore
        dueDate: dueDateTime.toISOString(),
        //@ts-ignore
        startTime: startDateTime.toISOString(),
        duration: jobDuration,
        status: 'SCHEDULED',
      };

      if (this.selectedSchedule) {
        this.scheduleService
          .updateSchedule(scheduleData, this.selectedSchedule.id!)
          .subscribe({
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
            },
          });
      } else {
        console.log(scheduleData)
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
          },
        });
      }
    }
  }

  openSchedulingDialog() {
    this.selectedSchedulingType = '';
    this.schedulingDialogRef = this.dialog.open(this.schedulingDialog, {
      width: '400px',
    });
  }

  startScheduling() {
    this.loading = true;
    this.schedulingDialogRef.close();
    let schedulingObservable: Observable<any>;  // Modifica il tipo qui

    switch (this.selectedSchedulingType) {
      case 'scheduleAll':
        schedulingObservable = this.schedulerEngineService.scheduleAll();
        break;
      case 'scheduleDueDate':
        schedulingObservable = this.schedulerEngineService.scheduleDueDate();
        break;
      case 'scheduleDuration':
        schedulingObservable = this.schedulerEngineService.scheduleDuration();
        break;
      case 'schedulePriority':
        schedulingObservable = this.schedulerEngineService.schedulePriority();
        break;
      default:
        this.loading = false;
        return;
    }

    schedulingObservable.subscribe({
      next: (response) => {
        this.loading = false;
        Swal.fire('Successo', 'Scheduling completato con successo.', 'success');
        // Aggiorna i dati se necessario
        this.getAllSchedules();
      },
      error: (error) => {
        this.loading = false;
        console.error('Errore durante lo scheduling:', error);
        Swal.fire('Errore', 'Si è verificato un errore durante lo scheduling.', 'error');
      },
    });
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
