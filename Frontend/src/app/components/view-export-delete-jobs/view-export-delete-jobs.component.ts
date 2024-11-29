import { Component, OnInit } from '@angular/core';
import {JobControllerService, JobDTO} from "../../generated-api";
import { JsonControllerService} from "../../generated-api";
import { MatDialog } from '@angular/material/dialog';
import {Job, MachineType, MachineTypeDTO} from '../../interfaces/interfaces';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EditJobDialogComponent } from '../edit-job-dialog/edit-job-dialog.component';
import { forkJoin } from 'rxjs';
import Swal from "sweetalert2";

@Component({
  selector: 'app-view-export-delete-jobs',
  templateUrl: './view-export-delete-jobs.component.html',
  styleUrls: ['./view-export-delete-jobs.component.css']
})
export class ViewExportDeleteJobsComponent implements OnInit {

  isLoading = false;
  machineTypes: MachineType[] = [];

  jobs: {
    duration: number | undefined;
    description: string | undefined;
    id: number;
    assigneeEmail: string | undefined;
    title: string;
    priority: "LOW" | "MEDIUM" | "HIGH" | "URGENT";
    idMachineType: number;
    status: "PENDING" | "SCHEDULED" | "IN_PROGRESS" | "COMPLETED" | "CANCELLED"
  }[] = [];

  constructor(
    private jobService: JobControllerService,
    private jsonService: JsonControllerService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadJobs();
  }

  loadJobs(): void {
    this.isLoading = true;
    forkJoin({
      jobs: this.jobService.getAllJobs(), // Cambiato 'showJob()' in 'getAllJobs()'
      types: this.jsonService.exportMachineType()
    }).subscribe({
      next: (response) => {
        const jobDTOs = Array.isArray(response.jobs) ? response.jobs as JobDTO[] : [];
        this.jobs = jobDTOs.map(dto => ({
          id: dto.id!,
          title: dto.title,
          description: dto.description,
          status: dto.status,
          priority: dto.priority,
          duration: dto.duration,
          idMachineType: dto.idMachineType,
          assigneeEmail: dto.assigneeEmail
        }));
        this.machineTypes = this.transformMachineTypes(response.types);
        this.isLoading = false;
      },
      error: (error) => {
        console.error("Error while retrieving data:", error);
        this.isLoading = false;
        this.showMessage('Error loading data. Please try again later.');
      }
    });
  }

  getMachineTypeName(machineType: any): string {
    if (!machineType) return 'Any';
    if (typeof machineType === 'object' && machineType.id) {
      const machineTypeId = Number(machineType.id);
      const foundType = this.machineTypes.find(type => type.id === machineTypeId);
      return foundType ? foundType.name : 'Unknown Type';
    }
    if (typeof machineType === 'number' || !isNaN(Number(machineType))) {
      const machineTypeId = Number(machineType);
      const foundType = this.machineTypes.find(type => type.id === machineTypeId);
      return foundType ? foundType.name : 'Unknown Type';
    }
    console.warn('Unexpected machine type format:', machineType);
    return 'Unknown Type';
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

  deleteJob(id: number): void {
    const job = this.jobs.find(j => j.id === id);
    if (!job) {
      this.showMessage('Job not found.');
      return;
    }

    Swal.fire({
      title: 'Sei sicuro?',
      text: `Vuoi eliminare il job "${job.title}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sì, elimina!',
      cancelButtonText: 'Annulla'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.jobService.deleteJob(id).subscribe({
          next: () => {
            this.jobs = this.jobs.filter(job => job.id !== id);
            Swal.fire(
              'Eliminato!',
              `Il job "${job.title}" è stato eliminato.`,
              'success'
            );
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error deleting job:', error);
            this.isLoading = false;
            if (error.status === 404) {
              Swal.fire(
                'Errore!',
                'Job non trovato. Potrebbe essere già stato eliminato.',
                'error'
              );
            } else {
              Swal.fire(
                'Errore!',
                'Non è stato possibile eliminare il job. Per favore, riprova più tardi.',
                'error'
              );
            }
          }
        });
      }
    });
  }

  openEditDialog(job: {
    duration: number | undefined;
    description: string | undefined;
    id: number;
    assigneeEmail: string | undefined;
    title: string;
    priority: "LOW" | "MEDIUM" | "HIGH" | "URGENT";
    idMachineType: number;
    status: "PENDING" | "SCHEDULED" | "IN_PROGRESS" | "COMPLETED" | "CANCELLED"
  }): void {
    const dialogRef = this.dialog.open(EditJobDialogComponent, {
      width: '500px',
      data: { ...job }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.isLoading = true;
        this.jobService.updateJob(result, result.id).subscribe({ // Aggiunto 'result.id' come secondo argomento
          next: (updatedJob) => {
            const index = this.jobs.findIndex(j => j.id === updatedJob.id);
            if (index !== -1) {
              // @ts-ignore
              this.jobs[index] = updatedJob;
              this.jobs = [...this.jobs];
            }
            this.showMessage('Job updated successfully');
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error updating job:', error);
            this.isLoading = false;
            this.showMessage('Error updating job. Please try again later.');
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

  getPriorityClass(priority: string | null): string {
    if (!priority) return 'priority-none';

    switch (priority.toLowerCase()) {
      case 'high':
        return 'priority-high';
      case 'medium':
        return 'priority-medium';
      case 'low':
        return 'priority-low';
      default:
        return 'priority-none';
    }
  }

  getStatusClass(status: string | null): string {
    if (!status) return 'status-unknown';

    switch (status.toLowerCase()) {
      case 'pending':
        return 'status-pending';
      case 'in_progress':
      case 'in progress':
        return 'status-progress';
      case 'completed':
        return 'status-completed';
      default:
        return 'status-unknown';
    }
  }
}
