import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class JobService {

  private baseUrl = 'http://localhost:7001/sti-jobs/api/v1/job';

  constructor(private http: HttpClient) { }

  showJob(): Observable<string> {
    const email = localStorage.getItem('user-email');
    if (!email) {
      throw new Error('User email not found in local storage');
    }
    return this.http.get<any>(`${this.baseUrl}/jobs-by-assignee-email/${email}`);
  }

  deleteJob(id: number): Observable<any> {
    console.log("Cerco di cancellare:", id);
    return this.http.delete(`${this.baseUrl}/delete-job${id}`);
  }

  updateJob(job: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/update-job${job.id}`, job);
  }

}
