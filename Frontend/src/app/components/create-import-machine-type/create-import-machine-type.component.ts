import { Component, OnInit } from '@angular/core';
import {MachineTypeControllerService} from '../../generated-api';
import { MachineTypeDTO } from '../../generated-api';
import Swal from 'sweetalert2';
import { NgForm } from '@angular/forms';

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

  jsonError: string = '';

  constructor(private machineService: MachineTypeControllerService) {}

  ngOnInit(): void {
    this.jsonExample =
      `[
        {
          "name": "Esempio Nome",
          "description": "Esempio Descrizione"
        }
      ]`;
    this.jsonInputContent = this.jsonExample;
  }

  toggleJsonInput() {
    this.showJsonInput = !this.showJsonInput;
    this.jsonError = '';
    if (!this.showJsonInput) {
      this.resetForm();
    }
  }

  submitMachineType(form?: NgForm) {
    console.log('submitMachineType called');

    if (!this.showJsonInput && form) {
      if (!form.valid) {
        Object.keys(form.controls).forEach(field => {
          const control = form.controls[field];
          control.markAsTouched({ onlySelf: true });
        });

        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'Per favore, compila tutti i campi obbligatori.'
        });
        return;
      }
    }

    let machineTypesToSubmit: Array<Omit<MachineTypeDTO, 'id'>>;

    if (this.showJsonInput) {
      try {
        machineTypesToSubmit = JSON.parse(this.jsonInputContent);

        if (!Array.isArray(machineTypesToSubmit)) {
          Swal.fire({
            icon: 'error',
            title: 'Errore',
            text: 'Il JSON inserito deve essere un array di Machine Types.',
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
      machineTypesToSubmit = [this.machineType];
    }

    const machineTypesToSubmitWithId: MachineTypeDTO[] = machineTypesToSubmit.map(machineType => ({
      ...machineType,
      id: 0
    }));

    console.log('machineTypesToSubmitWithId:', machineTypesToSubmitWithId);

    for (const machineType of machineTypesToSubmitWithId) {
      this.machineService.createMachineType(machineType).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Machine Type creato con successo.',
            showConfirmButton: false,
            timer: 1500
          });
          this.resetForm();
        },
        error: (error) => {
          console.error('Error creating machine type:', error);
          Swal.fire({
            icon: 'error',
            title: 'Errore',
            text: 'Non è stato possibile creare il Machine Type. Per favore, riprova più tardi.',
          });
        }
      });
    }

  }

  resetForm() {
    this.machineType = {
      name: '',
      description: ''
    };
    this.jsonInputContent = this.jsonExample;
    this.jsonError = '';
  }
}
