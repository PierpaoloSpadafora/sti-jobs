import { Component, OnInit } from '@angular/core';
import { JsonService } from '../../services/json.service';
import { JobDTO, MachineTypeDTO } from '../../generated-api';
import { MachineTypeControllerService } from '../../generated-api';
import Swal from 'sweetalert2';
import { NgForm } from '@angular/forms';

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
    status: undefined,
    assignee: undefined,
    priority: undefined,
    duration: undefined,
    requiredMachineType: undefined,
  };

  durationHours: number = 0;
  durationMinutes: number = 0;
  durationSeconds: number = 0;

  statuses = ['PENDING', 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'];
  priorities = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];

  jsonInputContent: string = '';
  jsonExample: string = '';

  machineTypes: MachineTypeDTO[] = [];

  jsonError: string = '';

  constructor(
    private jsonService: JsonService,
    private machineTypeService: MachineTypeControllerService
  ) {}

  ngOnInit(): void {
    this.jsonExample =
      `[{
          "title": "Job 1",
          "description": "First job",
          "status": "PENDING",
          "priority": "LOW",
          "duration": 3600,
          "requiredMachineType": {
            "id": 1,
            "name": "Type 1",
            "description": "Description of Type 1"
          }
        }]`;
    this.jsonInputContent = this.jsonExample;

    this.machineTypeService.getAllMachineTypes().subscribe({
      next: (data: MachineTypeDTO[]) => {
        this.machineTypes = data;
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
        // Optional: further validation of each job object
        this.jsonError = '';
      }
    } catch (e) {
      this.jsonError = 'Il JSON inserito non è valido.';
    }
  }

  submitJob(form?: NgForm) {
    let jobsToSubmit: JobDTO[] = [];
    const email = localStorage.getItem('user-email');

    if (this.showJsonInput) {
      try {
        jobsToSubmit = JSON.parse(this.jsonInputContent);

        if (!Array.isArray(jobsToSubmit)) {
          Swal.fire({
            icon: 'error',
            title: 'Errore',
            text: 'Il JSON inserito deve essere un array di Job.',
          });
          return;
        }

        for (const job of jobsToSubmit) {
          if (
            !job.title ||
            !job.status ||
            !job.priority ||
            job.duration === undefined ||
            job.duration < 0 ||
            !job.requiredMachineType ||
            !job.requiredMachineType.id
          ) {
            Swal.fire({
              icon: 'error',
              title: 'Errore',
              text: 'Uno o più Job nel JSON non sono validi. Assicurati che tutti i campi obbligatori siano presenti e validi.',
            });
            return;
          }

          job.assignee = { email: email || 'default@example.com' };
        }

      } catch (error) {
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'Il JSON inserito non è valido.',
        });
        return;
      }
    } else {
      if (form) {
        if (!form.valid) {
          Object.keys(form.controls).forEach(field => {
            const control = form.controls[field];
            control.markAsTouched({ onlySelf: true });
          });

          Swal.fire({
            icon: 'error',
            title: 'Errore',
            text: 'Per favore, compila tutti i campi obbligatori.',
          });
          return;
        }
      }

      // Calculate duration in seconds
      this.job.duration =
        (this.durationHours * 3600) +
        (this.durationMinutes * 60) +
        this.durationSeconds;

      if (isNaN(this.job.duration!) || this.job.duration! <= 0) {
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'La durata deve essere un valore positivo e maggiore di zero.',
        });
        return;
      }

      if (!this.job.requiredMachineType || !this.job.requiredMachineType.id) {
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'Seleziona un Tipo Macchina Richiesto.',
        });
        return;
      }

      if (email) {
        this.job.assignee = { email };
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'Non è stato trovato un assignee valido.',
        });
        return;
      }

      jobsToSubmit = [this.job];
    }

    // Assign a default id or handle as per backend requirements
    const jobsToSubmitWithId: JobDTO[] = jobsToSubmit.map(job => ({
      ...job,
      id: 0, // Assuming 0 or backend will assign the correct ID
    }));

    this.jsonService.importJob(jobsToSubmitWithId).subscribe({
      next: (response: string) => {
        Swal.fire({
          icon: 'success',
          title: 'Job importati con successo.',
          showConfirmButton: false,
          timer: 1500
        });
        this.resetForm();
        if (form) {
          form.resetForm();
        }
      },
      error: (error) => {
        console.error("Errore durante l'importazione dei Job:", error);
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: "Errore durante l'importazione dei Job: " + error.message,
        });
      },
    });
  }

  resetForm() {
    this.job = {
      title: '',
      description: '',
      status: undefined,
      assignee: undefined,
      priority: undefined,
      duration: undefined,
      requiredMachineType: undefined,
    };
    this.durationHours = 0;
    this.durationMinutes = 0;
    this.durationSeconds = 0;
    this.jsonInputContent = this.jsonExample;
    this.jsonError = '';
  }
}
