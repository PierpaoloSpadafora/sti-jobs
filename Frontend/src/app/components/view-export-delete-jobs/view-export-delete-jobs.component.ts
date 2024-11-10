import { Component, OnInit } from '@angular/core';
import { JobService } from '../../services/job.service';
import { MatDialog } from '@angular/material/dialog';
import {Job} from '../../interfaces/job';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EditJobDialogComponent } from '../edit-job-dialog/edit-job-dialog.component';

@Component({
  selector: 'app-view-export-delete-jobs',
  templateUrl: './view-export-delete-jobs.component.html',
  styleUrl: './view-export-delete-jobs.component.css'
})
export class ViewExportDeleteJobsComponent implements OnInit {
  jobs: Job[] = [];
  isLoading = false;

  constructor(
    private jobService: JobService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadJobs();
  }

  loadJobs(): void {
    this.jobService.showJob().subscribe({
      next: (response: any) => {
        this.jobs = Array.isArray(response) ? response : [response];
        console.log("Jobs retrieved:", this.jobs);
      },
      error: (error) => {
        console.error("Error while retrieving jobs:", error);
      }
    });
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
