import { Component, OnInit } from '@angular/core';
import { MachineTypeControllerService } from '../../generated-api';
import { MachineTypeDTO } from '../../generated-api';
import Swal from 'sweetalert2';
import { NgForm } from '@angular/forms';
import { forkJoin } from 'rxjs';

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
    this.jsonExample = `
  [
    {
      "id": 1, // Aggiungi questo campo se desideri specificare l'ID
      "name": "MachineTypeName",
      "description": "Esempio Descrizione"
    },
    {
      "name": "AnotherMachineType",
      "description": "Un altro esempio senza ID"
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

    let machineTypesToSubmit: Array<MachineTypeDTO>;

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

        for (const mt of machineTypesToSubmit) {
          if (!mt.name || !mt.description) {
            Swal.fire({
              icon: 'error',
              title: 'Errore',
              text: 'Tutti i campi (name e description) sono obbligatori nei Machine Types.',
            });
            return;
          }
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

    console.log('machineTypesToSubmit:', machineTypesToSubmit);

    const requests = machineTypesToSubmit.map(machineType =>
      this.machineService.createOrUpdateMachineType(machineType)
    );

    forkJoin(requests).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Machine Type creati con successo.',
          showConfirmButton: false,
          timer: 1500
        });
        this.resetForm();
        if (form) {
          form.resetForm();
        }
      },
      error: (error: any) => {
        console.error('Error creating machine type:', error);
        let errorMessage = 'Non è stato possibile creare il Machine Type. Per favore, riprova più tardi.';
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        }

        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: errorMessage,
        });
      }
    });
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
