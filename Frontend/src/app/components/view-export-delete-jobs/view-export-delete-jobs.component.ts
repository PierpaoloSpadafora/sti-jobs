import { Component, OnInit } from '@angular/core';
import { JobService } from '../../services/job.service';
import { MatDialog } from '@angular/material/dialog';
import {Job, MachineType, MachineTypeDTO} from '../../interfaces/interfaces';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EditJobDialogComponent } from '../edit-job-dialog/edit-job-dialog.component';
import { forkJoin } from 'rxjs';
import { JsonService } from '../../services/json.service';

@Component({
  selector: 'app-view-export-delete-jobs',
  templateUrl: './view-export-delete-jobs.component.html',
  styleUrl: './view-export-delete-jobs.component.css'
})
export class ViewExportDeleteJobsComponent implements OnInit {
  jobs: Job[] = [];
  isLoading = false;
  machineTypes: MachineType[] = [];

  constructor(
    private jobService: JobService,
    private jsonService: JsonService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadJobs();
  }

  loadJobs(): void {
    this.isLoading = true;
    forkJoin({
      jobs: this.jobService.showJob(),
      types: this.jsonService.exportMachineType()
    }).subscribe({
      next: (response) => {
        this.jobs = Array.isArray(response.jobs) && response.jobs.every(job => typeof job === 'object')
          ? response.jobs as Job[]
          : [];
        this.machineTypes = this.transformMachineTypes(response.types);

        console.log("Jobs structure:", this.jobs.map(job => ({
          jobId: job.id,
          requiredMachineType: job.requiredMachineType
        })));

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
    if (confirm('Are you sure you want to delete this job?')) {
      this.isLoading = true;
      this.jobService.deleteJob(id).subscribe({
        next: () => {
          this.jobs = this.jobs.filter(job => job.id !== id);
          this.showMessage('Job deleted successfully');
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error deleting job:', error);
          this.isLoading = false;
          this.showMessage('Error deleting job. Please try again later.');
        }
      });
    }
  }

  openEditDialog(job: Job): void {
    const dialogRef = this.dialog.open(EditJobDialogComponent, {
      width: '500px',
      data: { ...job }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.isLoading = true;
        this.jobService.updateJob(result).subscribe({
          next: (updatedJob) => {
            const index = this.jobs.findIndex(j => j.id === updatedJob.id);
            if (index !== -1) {
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
      case 'in progress':
        return 'status-progress';
      case 'completed':
        return 'status-completed';
      default:
        return 'status-unknown';
    }
  }
}
