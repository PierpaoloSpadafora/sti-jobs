import { Component, OnInit } from '@angular/core';
import { JsonService } from '../../services/json.service';
import { MachineTypeDTO } from '../../generated-api';

@Component({
  selector: 'app-create-import-machine-type',
  templateUrl: './create-import-machine-type.component.html',
  styleUrls: ['./create-import-machine-type.component.css']
})
export class CreateImportMachineTypeComponent implements OnInit {
  showJsonInput: boolean = false;

  machineType: MachineTypeDTO = {
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
          "id": 0,
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
    let machineTypesToSubmit: MachineTypeDTO[];

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

    this.jsonService.importMachineType(machineTypesToSubmit).subscribe({
      next: (response: string) => {
        alert('Machine Types importati con successo.');
        this.resetForm();
      },
      error: (error) => {
        console.error("Errore durante l'importazione dei Machine Types:", error);
        alert("Errore durante l'importazione dei Machine Types: " + error.message);
      },
    });
  }

  resetForm() {
    this.machineType = {
      name: '',
      description: ''
    };
    this.jsonInputContent = '';
  }
}
