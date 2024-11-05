import { Component, OnInit } from '@angular/core';
import { JobService } from '../../services/job.service';

interface Job {
  id: number;
  title: string;
  description: string;
  duration: number;
  priority: string | null;
  status: string | null;
  assignee: string | null;
  requiredMachineType: string | null;
}

@Component({
  selector: 'app-view-export-delete-jobs',
  templateUrl: './view-export-delete-jobs.component.html',
  styleUrl: './view-export-delete-jobs.component.css'
})
export class ViewExportDeleteJobsComponent implements OnInit {
  jobs: Job[] = [];

  constructor(
    private jobService: JobService
  ) {}

  ngOnInit(): void {
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
