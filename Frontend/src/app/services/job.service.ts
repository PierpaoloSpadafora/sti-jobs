import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {JobDTO} from "../generated-api";

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
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  updateJob(job: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${job.id}`, job);
  }

  getJobById(id: number): Observable<JobDTO> {
    return this.http.get<JobDTO>(`${this.baseUrl}/jobs-by-id/${id}`);
  }



}
