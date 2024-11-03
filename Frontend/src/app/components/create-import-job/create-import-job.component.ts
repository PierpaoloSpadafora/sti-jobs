import { Component, OnInit } from '@angular/core';
import { JsonService } from '../../services/json.service';
import { JobDTO } from '../../generated-api';
import { MachineTypeDTO } from '../../generated-api';
import { MachineTypeControllerService } from '../../generated-api';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-create-import-job',
  templateUrl: './create-import-job.component.html',
  styleUrls: ['./create-import-job.component.css']
})
export class CreateImportJobComponent implements OnInit {
  showJsonInput: boolean = false;

  job: JobDTO = {
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
      `[
        {
          "id": 0,
          "title": "string",
          "description": "string",
          "status": "PENDING",
          "assignee": {
            "id": "string",
            "email": "string"
          },
          "priority": "LOW",
          "duration": 0,
          "requiredMachineType": {
            "id": 0,
            "name": "string",
            "description": "string"
          }
        }
      ]`;
    this.jsonInputContent = '';

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
  }

  validateJsonInput() {
    try {
      JSON.parse(this.jsonInputContent);
      this.jsonError = '';
    } catch (e) {
      this.jsonError = 'Il JSON inserito non è valido.';
    }
  }

  submitJob() {
    let jobsToSubmit: JobDTO[];

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
      } catch (error) {
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'Il JSON inserito non è valido.',
        });
        return;
      }
    } else {
      // Calcola la durata totale in secondi
      this.job.duration =
        (this.durationHours * 3600) +
        (this.durationMinutes * 60) +
        this.durationSeconds;

      // Validazione dei campi
      if (!this.job.title) {
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'Il campo Titolo è obbligatorio.',
        });
        return;
      }

      if (isNaN(this.job.duration!) || this.job.duration! < 0) {
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'La durata deve essere un valore positivo.',
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

      jobsToSubmit = [this.job];
    }

    this.jsonService.importJob(jobsToSubmit).subscribe({
      next: (response: string) => {
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
    this.jsonInputContent = '';
    this.jsonError = '';
  }
}
