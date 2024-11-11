import { Component, OnInit } from '@angular/core';
import { JsonService } from '../../services/json.service';
import { MachineTypeDTO } from '../../generated-api';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-create-import-machine-type',
  templateUrl: './create-import-machine-type.component.html',
  styleUrls: ['./create-import-machine-type.component.css']
})
export class CreateImportMachineTypeComponent implements OnInit {
  showJsonInput: boolean = false;

  machineType: Omit<MachineTypeDTO, 'id'> = {
    name: '',
    description: ''
  };

  jsonInputContent: string = '';
  jsonExample: string = '';

  constructor(private jsonService: JsonService) {}

  ngOnInit(): void {
    this.jsonExample = 
      `[
        {
          "name": "string",
          "description": "string"
        }
      ]`;
    this.jsonInputContent = this.jsonExample;
  }

  toggleJsonInput() {
    this.showJsonInput = !this.showJsonInput;
  }

  submitMachineType() {
    console.log('submitMachineType called');
    let machineTypesToSubmit: Array<Omit<MachineTypeDTO, 'id'>>;

    if (this.showJsonInput) {
      try {
        machineTypesToSubmit = JSON.parse(this.jsonInputContent);

        if (!Array.isArray(machineTypesToSubmit)) {
          alert('Il JSON inserito deve essere un array di Machine Types.');
          return;
        }

      } catch (error) {
        alert('Il JSON inserito non è valido.');
        return;
      }
    } else {
      machineTypesToSubmit = [this.machineType];
    }

    const machineTypesToSubmitWithId: MachineTypeDTO[] = machineTypesToSubmit.map(machineType => ({
      ...machineType,
      id: 0
    }));

    console.log('machineTypesToSubmitWithId:', machineTypesToSubmitWithId);

    this.jsonService.importMachineType(machineTypesToSubmitWithId).subscribe({
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
        console.error("Errore durante l'importazione dei Machine Types:", error);
        alert("Errore durante l'importazione dei Machine Types: " + JSON.stringify(error));
      },
    });
  }

  resetForm() {
    this.machineType = {
      name: '',
      description: ''
    };
  }
}
