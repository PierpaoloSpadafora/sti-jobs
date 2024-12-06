import {Component, OnInit} from '@angular/core';
import { JsonControllerService } from "../../generated-api";
import { MachineControllerService } from "../../generated-api";
import { Machine, MachineDTO, MachineTypeDTO, MachineType } from '../../interfaces/interfaces';
import { EditMachineDialogComponent } from '../edit-machine-dialog/edit-machine-dialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { forkJoin } from 'rxjs';
import Swal from "sweetalert2";

@Component({
  selector: 'app-view-export-delete-machines',
  templateUrl: './view-export-delete-machines.component.html',
  styleUrls: ['./view-export-delete-machines.component.css']
})
export class ViewExportDeleteMachinesComponent implements OnInit {
  machines: Machine[] = [];
  isLoading = false;
  machineTypes: MachineType[] = [];

  constructor(
    private jsonService: JsonControllerService,
    private machineService: MachineControllerService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadMachines();
  }

  loadMachines(): void {
    this.isLoading = true;
    forkJoin({
      machines: this.jsonService.exportMachine(),
      types: this.jsonService.exportMachineType()
    }).subscribe({
      next: (response) => {
        this.machineTypes = this.transformMachineTypes(response.types);
        this.machines = this.transformMachines(
          Array.isArray(response.machines) ? response.machines as Machine[] : []
        );
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error("Error while retrieving data:", error);
        this.isLoading = false;
        this.showMessage('Error loading machines. Please try again later.');
      }
    });
  }

  private transformMachineTypes(dtos: MachineTypeDTO[]): MachineType[] {
    return dtos.map(dto => ({
      id: dto.id as number,
      name: dto.name as string,
      description: dto.description as string
    }));
  }

  private transformMachines(dtos: MachineDTO[]): Machine[] {
    return dtos.map(dto => ({
      id: dto.id as number,
      name: dto.name as string,
      status: dto.status as string,
      typeId: dto.typeId as number,
      description: dto.description || '',
      createdAt: dto.createdAt ? new Date(dto.createdAt) : new Date(),
      updatedAt: dto.updatedAt ? new Date(dto.updatedAt) : new Date()
    }));
  }

  getMachineTypeName(typeId: number): string {
    console.log('typeId:', typeId);
    console.log(this.machineTypes);
    const machineType = this.machineTypes.find(type => type.id === typeId);
    return machineType ? machineType.name : 'Unknown Type';
  }

  getStatusClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'available':
        return 'status-active';
      case 'busy':
        return 'status-inactive';
      case 'maintenance':
        return 'status-maintenance';
      default:
        return 'status-unknown';
    }
  }

  delete(id: number): void {
    const machine = this.machines.find(m => m.id === id);
    if (!machine) {
      this.showMessage('Machine not found.');
      return;
    }

    Swal.fire({
      title: 'Sei sicuro?',
      text: `Vuoi eliminare la macchina "${machine.name}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sì, elimina!',
      cancelButtonText: 'Annulla'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.machineService.deleteMachine(id).subscribe({
          next: () => {
            this.machines = this.machines.filter(m => m.id !== id);
            Swal.fire(
              'Eliminato!',
              `La macchina "${machine.name}" è stata eliminata.`,
              'success'
            );
            this.isLoading = false;
          },
          error: (error: any) => {
            console.error('Error deleting machine:', error);
            this.isLoading = false;
            if (error.status === 404) {
              Swal.fire(
                'Errore!',
                'Macchina non trovata. Potrebbe essere già stata eliminata.',
                'error'
              );
            } else {
              Swal.fire(
                'Errore!',
                'Non è stato possibile eliminare la macchina. Per favore, riprova più tardi.',
                'error'
              );
            }
          }
        });
      }
    });
  }

  openEditDialog(machine: Machine): void {
    const dialogRef = this.dialog.open(EditMachineDialogComponent, {
      width: '500px',
      data: { ...machine }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.isLoading = true;
        this.machineService.updateMachine(result, result.id).subscribe({
          next: (updated) => {
            const index = this.machines.findIndex(m => m.id === updated.id);
            if (index !== -1) {
              this.machines[index] = updated as Machine;
              this.machines = [...this.machines];
            }
            this.showMessage('Machine updated successfully');
            this.isLoading = false;
          },
          error: (error: any) => {
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

  exportMachine(machine: Machine): void {
    const enrichedMachine = {
      ...machine,
      machineTypeName: this.getMachineTypeName(machine.typeId)
    };
    const jsonContent = JSON.stringify(enrichedMachine, null, 2);
    const blob = new Blob([jsonContent], { type: 'application/json' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `machine_${machine.id}_export.json`;
    link.click();
    window.URL.revokeObjectURL(url);
    this.showMessage(`Machine ${machine.id} exported successfully`);
  }
}
