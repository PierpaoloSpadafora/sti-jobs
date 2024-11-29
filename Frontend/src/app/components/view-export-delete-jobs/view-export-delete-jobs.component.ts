import { Component, OnInit } from '@angular/core';
import { JobControllerService, JobDTO } from '../../generated-api';
import { JsonControllerService } from '../../generated-api';
import { MatDialog } from '@angular/material/dialog';
import { MachineType, MachineTypeDTO } from '../../interfaces/interfaces';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EditJobDialogComponent } from '../edit-job-dialog/edit-job-dialog.component';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-view-export-delete-jobs',
  templateUrl: './view-export-delete-jobs.component.html',
  styleUrls: ['./view-export-delete-jobs.component.css']
})
export class ViewExportDeleteJobsComponent implements OnInit {

  isLoading = false;
  machineTypes: MachineType[] = [];

  jobs: JobDTO[] = [];

  constructor(
    private jobService: JobControllerService,
    private jsonService: JsonControllerService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private loginService: LoginService
  ) {}

  ngOnInit(): void {
    this.loadJobs();
  }

  loadJobs(): void {
    this.isLoading = true;
    forkJoin({
      jobs: this.jobService.getAllJobs(),
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
          assigneeEmail: this.loginService.getUserEmail()!
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

  getMachineTypeName(machineTypeId: number): string {
    const foundType = this.machineTypes.find(type => type.id === machineTypeId);
    return foundType ? foundType.name : 'Unknown Type';
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
      title: 'Are you sure?',
      text: `Do you want to delete the job "${job.title}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.jobService.deleteJob(id).subscribe({
          next: () => {
            this.jobs = this.jobs.filter(job => job.id !== id);
            Swal.fire(
              'Deleted!',
              `The job "${job.title}" has been deleted.`,
              'success'
            );
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error deleting job:', error);
            this.isLoading = false;
            if (error.status === 404) {
              Swal.fire(
                'Error!',
                'Job not found. It might have already been deleted.',
                'error'
              );
            } else {
              Swal.fire(
                'Error!',
                'Unable to delete the job. Please try again later.',
                'error'
              );
            }
          }
        });
      }
    });
  }

  openEditDialog(job: JobDTO): void {
    const dialogRef = this.dialog.open(EditJobDialogComponent, {
      width: '500px',
      data: { ...job }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.isLoading = true;

        const updatedJob: JobDTO = {
          id: result.id,
          title: result.title || job.title,
          description: result.description || job.description,
          status: result.status || job.status,
          priority: result.priority || job.priority,
          duration: result.duration || job.duration,
          idMachineType: result.idMachineType || job.idMachineType,
          assigneeEmail: result.assigneeEmail || job.assigneeEmail
        };

        console.log('Updated Job:', updatedJob);

        this.jobService.updateJob(updatedJob, updatedJob.id!).subscribe({
          next: (updatedJobResponse) => {
            const index = this.jobs.findIndex(j => j.id === updatedJobResponse.id);
            if (index !== -1) {
              this.jobs[index] = updatedJobResponse;
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
