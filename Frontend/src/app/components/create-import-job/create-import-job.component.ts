import { Component, OnInit } from '@angular/core';
import { JobDTO, MachineTypeDTO } from '../../generated-api';
import { JobControllerService, JsonControllerService, MachineTypeControllerService } from '../../generated-api';
import Swal from 'sweetalert2';
import { NgForm } from '@angular/forms';
import {LoginService} from "../../services/login.service";

@Component({
  selector: 'app-create-import-job',
  templateUrl: './create-import-job.component.html',
  styleUrls: ['./create-import-job.component.css']
})
export class CreateImportJobComponent implements OnInit {
  showJsonInput: boolean = false;

  job: Omit<JobDTO, 'id'> = {
    title: '',
    description: '',
    status: JobDTO.StatusEnum.PENDING,
    priority: JobDTO.PriorityEnum.LOW,
    duration: 0,
    idMachineType: 0,
    assigneeEmail: this.loginService.getUserEmail()!
  };

  durationHours: number = 0;
  durationMinutes: number = 0;
  durationSeconds: number = 0;

  statuses = Object.values(JobDTO.StatusEnum);
  priorities = Object.values(JobDTO.PriorityEnum);

  jsonInputContent: string = '';
  jsonExample: string = '';
  machineTypes: MachineTypeDTO[] = [];
  jsonError: string = '';

  constructor(
    private jobService: JobControllerService,
    private jsonService: JsonControllerService,
    private machineTypeService: MachineTypeControllerService,
    private loginService: LoginService
  ) {}

  ngOnInit(): void {
    this.jsonExample = `[{
      "title": "Job 1",
      "description": "First job",
      "status": "PENDING",
      "priority": "LOW",
      "duration": 3600,
      "idMachineType": 1
    }]`;
    this.jsonInputContent = this.jsonExample;

    this.machineTypeService.getAllMachineTypes('body').subscribe({
      next: (machineTypes) => {
        this.machineTypes = machineTypes as unknown as MachineTypeDTO[];
      },
      error: (error) => {
        console.error('Errore durante il recupero dei Machine Types:', error);
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'Non è stato possibile recuperare i Machine Types.',
        });
      }
    });
  }

  toggleJsonInput() {
    this.showJsonInput = !this.showJsonInput;
    this.jsonError = '';
    if (!this.showJsonInput) {
      this.resetForm();
    }
  }

  validateJsonInput() {
    try {
      const parsed = JSON.parse(this.jsonInputContent);
      if (!Array.isArray(parsed)) {
        this.jsonError = 'Il JSON inserito deve essere un array di Job.';
      } else {
        this.jsonError = '';
      }
    } catch (e) {
      this.jsonError = 'Il JSON inserito non è valido.';
    }
  }

  submitJob(form?: NgForm) {
    const email = localStorage.getItem('user-email');
    if (!email) {
      Swal.fire({
        icon: 'error',
        title: 'Errore',
        text: 'Non è stato trovato un assignee valido.',
      });
      return;
    }

    if (this.showJsonInput) {
      try {
        const jobs: JobDTO[] = JSON.parse(this.jsonInputContent);
        if (!Array.isArray(jobs)) {
          Swal.fire({
            icon: 'error',
            title: 'Errore',
            text: 'Il JSON inserito deve essere un array di Job.',
          });
          return;
        }

        this.jsonService.importJob(jobs, email).subscribe({
          next: () => {
            Swal.fire({
              icon: 'success',
              title: 'Job importati con successo.',
              showConfirmButton: false,
              timer: 1500
            });
            this.resetForm();
          },
          error: (error) => {
            console.error("Errore durante l'importazione dei Job:", error);
            Swal.fire({
              icon: 'error',
              title: 'Errore',
              text: "Errore durante l'importazione dei Job: " + error.message,
            });
          }
        });
      } catch (error) {
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'Il JSON inserito non è valido.',
        });
      }
    } else {
      if (form && !form.valid) {
        Object.keys(form.controls).forEach(field => {
          const control = form.controls[field];
          control.markAsTouched({ onlySelf: true });
        });
        return;
      }

      this.job.duration =
        (this.durationHours * 3600) +
        (this.durationMinutes * 60) +
        this.durationSeconds;

      if (!this.job.duration || this.job.duration <= 0) {
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'La durata deve essere un valore positivo e maggiore di zero.',
        });
        return;
      }

      if (typeof this.job.idMachineType === 'object') {
        this.job.idMachineType = (this.job.idMachineType as MachineTypeDTO).id!;
      }

      this.jobService.createJob(this.job, email).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Job creato con successo.',
            showConfirmButton: false,
            timer: 1500
          });
          this.resetForm();
          if (form) form.resetForm();
        },
        error: (error) => {
          console.error("Errore durante la creazione del Job:", error);
          Swal.fire({
            icon: 'error',
            title: 'Errore',
            text: "Errore durante la creazione del Job: " + error.message,
          });
        }
      });
    }
  }

  resetForm() {
    this.job = {
      title: '',
      description: '',
      status: JobDTO.StatusEnum.PENDING,
      priority: JobDTO.PriorityEnum.LOW,
      duration: 0,
      idMachineType: 0,
      assigneeEmail: this.loginService.getUserEmail()!
    };
    this.durationHours = 0;
    this.durationMinutes = 0;
    this.durationSeconds = 0;
    this.jsonInputContent = this.jsonExample;
    this.jsonError = '';
  }
}
