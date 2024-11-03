import { Component, OnInit } from '@angular/core';
import { JsonService } from '../../services/json.service';
import { MachineDTO } from '../../generated-api';
import Swal from 'sweetalert2';

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
          "createdAt": "2024-11-03T18:54:56.216Z",
          "updatedAt": "2024-11-03T18:54:56.216Z"
        }
      ]`;
    this.jsonInputContent = this.jsonExample;
  }

  toggleJsonInput() {
    this.showJsonInput = !this.showJsonInput;
  }

  submitMachine() {
    console.log('submitMachine called');
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

    console.log('machinesToSubmit:', machinesToSubmit);

    this.jsonService.importMachine(machinesToSubmit).subscribe({
      next: (response: string) => {
        console.log('Response:', response);
        Swal.fire({
          icon: 'success',
          title: 'Elemento importato con successo',
          showConfirmButton: false,
          timer: 1000
        });
        this.resetForm();
      },
      error: (error) => {
        console.error("Errore durante l'importazione delle Machines:", error);
        alert("Errore durante l'importazione delle Machines: " + JSON.stringify(error));
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
  };
}
