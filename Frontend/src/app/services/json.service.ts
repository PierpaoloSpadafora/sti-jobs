import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class JsonService {

  private baseUrl = 'http://localhost:7001/sti-jobs/api/v1/json';

  constructor(private http: HttpClient) { }



  callJsonEndpoint(data: any): Observable<any> {
    const headers = { 'Content-Type': 'application/json' };
    return this.http.post(`${this.baseUrl}/import`, data, { headers, responseType: 'text' });
  }

}
