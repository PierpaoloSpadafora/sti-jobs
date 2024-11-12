import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MachineService {

  private baseUrl = 'http://localhost:7001/sti-jobs/api/v1/machine';

  constructor(private http: HttpClient) { }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  update(machine: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${machine.id}`, machine);
  }

}