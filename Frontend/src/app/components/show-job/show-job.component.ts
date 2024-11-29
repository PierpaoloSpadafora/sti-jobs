import { Component, OnInit } from '@angular/core';
import { JobControllerService } from "../../generated-api";
import Swal from 'sweetalert2';

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
  styleUrls: ['./show-job.component.css']
})
export class ShowJobComponent implements OnInit  {

  jobs: Job[] = [];

  constructor(
    private jobService: JobControllerService
  ) {}

  ngOnInit(): void {
    this.getJobs();
  }

  getJobs() {
    this.jobService.getAllJobs().subscribe({
      next: (response: any) => {
        this.jobs = Array.isArray(response) ? response : [response];
        console.log("Jobs ottenuti:", this.jobs);
      },
      error: (error) => {
        console.error("Errore durante il recupero dei job:", error);
      }
    });
  }

  deleteJob(job: Job) {
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
        this.jobService.deleteJob(job.id!).subscribe({
          next: () => {
            Swal.fire(
              'Eliminato!',
              `Il job "${job.title}" è stato eliminato.`,
              'success'
            );
            this.getJobs();
          },
          error: (error) => {
            Swal.fire(
              'Errore!',
              `Non è stato possibile eliminare il job: ${error.message}`,
              'error'
            );
            console.error('Errore durante l\'eliminazione del job:', error);
          }
        });
      }
    });
  }

}
