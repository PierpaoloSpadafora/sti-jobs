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
  selector: 'app-show-job',
  templateUrl: './show-job.component.html',
  styleUrl: './show-job.component.css'
})
export class ShowJobComponent implements OnInit  {

  jobs: Job[] = [];

  constructor(
    private jobService: JobService
  ) {}

  ngOnInit(): void {
    this.jobService.showJob().subscribe({
      next: (response: any) => { 
        this.jobs = Array.isArray(response) ? response : [response]; 
        console.log("Jobs ottenuti:", this.jobs);
      },
      error: (error) => {
        console.error("Errore durante il recupero dei job:", error);
      }
    });
  }
}
