import {Component, OnInit} from '@angular/core';
import { JsonService } from '../../services/json.service';
import { MachineTypeService } from '../../services/machineType.service';
import { MachineType } from '../../interfaces/interfaces';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EditMachineTypesDialogComponent } from '../edit-machine-types-dialog/edit-machine-types-dialog.component';
import Swal from "sweetalert2";

@Component({
  selector: 'app-view-export-delete-machine-types',
  templateUrl: './view-export-delete-machine-types.component.html',
  styleUrls: ['./view-export-delete-machine-types.component.css']
})
export class ViewExportDeleteMachineTypesComponent implements OnInit {
  machineTypes: MachineType[] = [];
  isLoading = false;

  constructor(
    private jsonService: JsonService,
    private machineTypeService: MachineTypeService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadMachineTypes();
  }

  loadMachineTypes(): void {
    this.isLoading = true;
    this.jsonService.exportMachineType().subscribe({
      next: (response: any) => {
        this.machineTypes = Array.isArray(response) ? response as MachineType[] : [];
        this.isLoading = false;
      },
      error: (error) => {
        console.error("Error while retrieving machine types:", error);
        this.isLoading = false;
        this.showMessage('Error loading machine types. Please try again later.');
      }
    });
  }

  delete(id: number): void {
    const machineType = this.machineTypes.find(t => t.id === id);
    if (!machineType) {
      this.showMessage('Machine type not found.');
      return;
    }

    Swal.fire({
      title: 'Sei sicuro?',
      text: `Vuoi eliminare il tipo di macchina "${machineType.name}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sì, elimina!',
      cancelButtonText: 'Annulla'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.machineTypeService.delete(id).subscribe({
          next: () => {
            this.machineTypes = this.machineTypes.filter(type => type.id !== id);
            Swal.fire(
              'Eliminato!',
              `Il tipo di macchina "${machineType.name}" è stato eliminato.`,
              'success'
            );
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error deleting machine type:', error);
            this.isLoading = false;
            if (error.status === 404) {
              Swal.fire(
                'Errore!',
                'Tipo di macchina non trovato. Potrebbe essere già stato eliminato.',
                'error'
              );
            } else {
              Swal.fire(
                'Errore!',
                'Non è stato possibile eliminare il tipo di macchina. Per favore, riprova più tardi.',
                'error'
              );
            }
          }
        });
      }
    });
  }

  openEditDialog(type: MachineType): void {
    const dialogRef = this.dialog.open(EditMachineTypesDialogComponent, {
      width: '500px',
      data: { ...type }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.isLoading = true;
        this.machineTypeService.update(result).subscribe({
          next: (updated) => {
            const index = this.machineTypes.findIndex(t => t.id === updated.id);
            if (index !== -1) {
              this.machineTypes[index] = updated;
              this.machineTypes = [...this.machineTypes];
            }
            this.showMessage('Machine type updated successfully');
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error updating machine type:', error);
            this.isLoading = false;
            this.showMessage('Error updating machine type. Please try again later.');
          }
        });
      }
    });
  }

  private showMessage(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
  }

}
