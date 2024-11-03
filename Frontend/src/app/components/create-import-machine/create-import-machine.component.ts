import { Component, OnInit } from '@angular/core';
import { JsonService } from '../../services/json.service';
import { MachineDTO } from '../../generated-api';

@Component({
  selector: 'app-create-import-machine',
  templateUrl: './create-import-machine.component.html',
  styleUrls: ['./create-import-machine.component.css']
})
export class CreateImportMachineComponent implements OnInit {
  showJsonInput: boolean = false;

  machine: MachineDTO = {
    name: '',
    description: '',
    status: undefined,
    typeId: undefined,
    typeName: '',
  };

  statuses = ['AVAILABLE', 'BUSY', 'MAINTENANCE', 'OUT_OF_SERVICE'];

  jsonInputContent: string = '';
  jsonExample: string = '';

  constructor(private jsonService: JsonService) {}

  ngOnInit(): void {
    this.jsonExample =
      `[
        {
          "id": 0,
          "name": "string",
          "description": "string",
          "status": "AVAILABLE",
          "typeId": 0,
          "typeName": "string",
          "createdAt": "2023-10-04T12:00:00Z",
          "updatedAt": "2023-10-04T12:00:00Z"
        }
      ]`;
    this.jsonInputContent = this.jsonExample;
  }

  toggleJsonInput() {
    this.showJsonInput = !this.showJsonInput;
  }

  submitMachine() {
    let machinesToSubmit: MachineDTO[];

    if (this.showJsonInput) {
      try {
        machinesToSubmit = JSON.parse(this.jsonInputContent);

        if (!Array.isArray(machinesToSubmit)) {
          alert('Il JSON inserito deve essere un array di Machines.');
          return;
        }
      } catch (error) {
        alert('Il JSON inserito non è valido.');
        return;
      }
    } else {
      machinesToSubmit = [this.machine];
    }

    this.jsonService.importMachine(machinesToSubmit).subscribe({
      next: (response: string) => {
        alert('Machines importate con successo.');
        this.resetForm();
      },
      error: (error) => {
        console.error("Errore durante l'importazione delle Machines:", error);
        alert("Errore durante l'importazione delle Machines: " + error.message);
      },
    });
  }

  resetForm() {
    this.machine = {
      name: '',
      description: '',
      status: undefined,
      typeId: undefined,
      typeName: '',
    };
    this.jsonInputContent = '';
  }
}
