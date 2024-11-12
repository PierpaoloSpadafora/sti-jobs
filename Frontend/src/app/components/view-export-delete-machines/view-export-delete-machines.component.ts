import { Component } from '@angular/core';
import { JsonService } from '../../services/json.service';
import { MachineService } from '../../services/machine.service';
import { Machine } from '../../interfaces/machine';
import { EditMachineDialogComponent } from '../edit-machine-dialog/edit-machine-dialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { MachineType } from '../../interfaces/machineType';
import { forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { MachineDTO, MachineTypeDTO } from '../../interfaces/interfaces';

@Component({
  selector: 'app-view-export-delete-machines',
  templateUrl: './view-export-delete-machines.component.html',
  styleUrl: './view-export-delete-machines.component.css'
})
export class ViewExportDeleteMachinesComponent {
  machines: Machine[] = [];
  isLoading = false;
  machineTypes: MachineType[] = [];

  constructor(
    private jsonService: JsonService,
    private machineService: MachineService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

 ngOnInit(): void {
    this.isLoading = true;
    forkJoin({
      machines: this.jsonService.exportMachine(),
      types: this.jsonService.exportMachineType()
    }).subscribe({
      next: (response) => {
        this.machineTypes = this.transformMachineTypes(response.types);
        this.machines = this.transformMachines(
          Array.isArray(response.machines) ? response.machines : [response.machines]
        );
        console.log("Data retrieved:", { machines: this.machines, types: this.machineTypes });
        this.isLoading = false;
      },
      error: (error) => {
        console.error("Error while retrieving data:", error);
        this.isLoading = false;
        this.showMessage('Error loading data. Please try again later.');
      }
    });
  }

  private transformMachineTypes(dtos: MachineTypeDTO[]): MachineType[] {
    return dtos.filter(dto => 
      dto.id != null && 
      dto.name != null && 
      dto.description != null
    ).map(dto => ({
      id: dto.id!,
      name: dto.name!,
      description: dto.description!
    }));
  }

  private transformMachines(dtos: MachineDTO[]): Machine[] {
    const transformedMachines = dtos.filter(dto => 
      dto.id != null && 
      dto.name != null && 
      dto.status != null && 
      dto.typeId != null
    ).map(dto => ({
      id: dto.id!,
      name: dto.name!,
      status: dto.status!,
      typeId: dto.typeId!,
      description: dto.description || '',
      createdAt: dto.createdAt ? new Date(dto.createdAt) : new Date(),
      updatedAt: dto.updatedAt ? new Date(dto.updatedAt) : new Date()
    }));
    return transformedMachines;
  }

  getMachineTypeName(typeId: number): string {
    const machineType = this.machineTypes.find(type => type.id === typeId);
    return machineType ? machineType.name : 'Unknown Type';
  }

  getStatusClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'active':
        return 'status-active';
      case 'inactive':
        return 'status-inactive';
      case 'maintenance':
        return 'status-maintenance';
      default:
        return 'status-unknown';
    }
  }

  delete(id: number): void {
    if (confirm('Are you sure you want to delete this machine?')) {
      this.isLoading = true;
      this.machineService.delete(id).subscribe({
        next: () => {
          this.machines = this.machines.filter(machine => machine.id !== id);
          this.showMessage('Machine deleted successfully');
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error deleting machine:', error);
          this.isLoading = false;
          this.showMessage('Error deleting machine. Please try again later.');
        }
      });
    }
  }

  openEditDialog(machine: Machine): void {
    const dialogRef = this.dialog.open(EditMachineDialogComponent, {
      width: '500px',
      data: { ...machine }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.isLoading = true;
        this.machineService.update(result).subscribe({
          next: (updated) => {
            const index = this.machines.findIndex(machine => machine.id === updated.id);
            if (index !== -1) {
              this.machines[index] = updated;
              this.machines = [...this.machines];
            }
            this.showMessage('Machine updated successfully');
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error updating machine:', error);
            this.isLoading = false;
            this.showMessage('Error updating machine. Please try again later.');
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
