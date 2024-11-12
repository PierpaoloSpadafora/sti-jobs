import { Component } from '@angular/core';
import { JsonService } from '../../services/json.service';
import { MachineTypeService } from '../../services/machineType.service';
import { MachineType } from '../../interfaces/machineType';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EditMachineTypesDialogComponent } from '../edit-machine-types-dialog/edit-machine-types-dialog.component';

@Component({
  selector: 'app-view-export-delete-machine-types',
  templateUrl: './view-export-delete-machine-types.component.html',
  styleUrl: './view-export-delete-machine-types.component.css'
})
export class ViewExportDeleteMachineTypesComponent {
  machineTypes: MachineType[] = [];
  isLoading = false;

  constructor(
    private jsonService: JsonService,
    private machineTypeService: MachineTypeService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.jsonService.exportMachineType().subscribe({
      next: (response: any) => {
        this.machineTypes = Array.isArray(response) ? response : [response];
        console.log("Machine types retrieved:", this.machineTypes);
      },
      error: (error) => {
        console.error("Error while retrieving machine types:", error);
      }
    });
  }

  delete(id: number): void {
    if (confirm('Are you sure you want to delete this machine type?')) {
      this.isLoading = true;
      this.machineTypeService.delete(id).subscribe({
        next: () => {
          this.machineTypes = this.machineTypes.filter(types => types.id !== id);
          this.showMessage('Machine type deleted successfully');
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error deleting machine type:', error);
          this.isLoading = false;
          this.showMessage('Error deleting machine type. Please try again later.');
        }
      });
    }
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
