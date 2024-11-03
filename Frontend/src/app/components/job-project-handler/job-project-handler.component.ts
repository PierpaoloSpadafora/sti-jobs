import { Component, OnInit } from '@angular/core';
import { JsonService } from '../../services/json.service';
import { JobDTO } from '../../generated-api';
import { MachineTypeDTO } from '../../generated-api';

@Component({
  selector: 'app-job-project-handler',
  templateUrl: './job-project-handler.component.html',
  styleUrls: ['./job-project-handler.component.css']
})
export class JobProjectHandlerComponent implements OnInit {
  showJsonInput: boolean = false;

  job: JobDTO = {
    title: '',
    description: '',
    status: undefined,
    assignee: undefined,
    priority: undefined,
    duration: undefined,
    requiredMachineType: undefined
  };

  get requiredMachineTypeName(): string {
    return this.job.requiredMachineType?.name ?? '';
  }

  set requiredMachineTypeName(name: string) {
    if (!this.job.requiredMachineType) {
      this.job.requiredMachineType = {} as MachineTypeDTO;
    }
    this.job.requiredMachineType.name = name;
  }

  statuses = ['PENDING', 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'];
  priorities = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];

  jsonInputContent: string = '';

  constructor(private jsonService: JsonService) { }

  ngOnInit(): void {
    this.jsonInputContent =
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
  }

  toggleJsonInput() {
    this.showJsonInput = !this.showJsonInput;
  }

  submitJob() {
    let jobsToSubmit: JobDTO[];

    if (this.showJsonInput) {
      try {
        jobsToSubmit = JSON.parse(this.jsonInputContent);

        if (!Array.isArray(jobsToSubmit)) {
          alert('Il JSON inserito deve essere un array di Job.');
          return;
        }
      } catch (error) {
        alert('Il JSON inserito non è valido.');
        return;
      }
    } else {
      jobsToSubmit = [this.job];
    }

    this.jsonService.importJob(jobsToSubmit).subscribe({
      next: (response: string) => {
        alert('Job importati con successo.');
        this.resetForm();
      },
      error: (error) => {
        console.error('Errore durante l\'importazione dei Job:', error);
        alert('Errore durante l\'importazione dei Job: ' + error.message);
      }
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
      requiredMachineType: undefined
    };
  }
}
