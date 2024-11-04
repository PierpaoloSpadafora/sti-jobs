import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { JobDTO } from '../generated-api';
import { MachineDTO } from '../generated-api';
import { MachineTypeDTO } from '../generated-api';

@Injectable({
  providedIn: 'root'
})
export class JsonService {

  private baseUrl = 'http://localhost:7001/sti-jobs/api/v1/json';

  constructor(private http: HttpClient) { }

  importJob(jobs: JobDTO[]): Observable<string> {
    return this.http.post(`${this.baseUrl}/importJob`, jobs, { responseType: 'text' });
  }

  importMachineType(machineTypes: MachineTypeDTO[]): Observable<string> {
    return this.http.post(`${this.baseUrl}/importMachineType`, machineTypes, { responseType: 'text' });
  }

  importMachine(machines: MachineDTO[]): Observable<string> {
    return this.http.post(`${this.baseUrl}/importMachine`, machines, { responseType: 'text' });
  }

  exportJob(): Observable<JobDTO[]> {
    return this.http.get<JobDTO[]>(`${this.baseUrl}/exportJob`);
  }

  exportMachineType(): Observable<MachineTypeDTO[]> {
    return this.http.get<MachineTypeDTO[]>(`${this.baseUrl}/exportMachineType`);
  }

  exportMachine(): Observable<MachineDTO[]> {
    return this.http.get<MachineDTO[]>(`${this.baseUrl}/exportMachine`);
  }

}
