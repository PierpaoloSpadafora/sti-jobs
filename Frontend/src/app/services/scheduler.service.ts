import { Injectable } from '@angular/core';
import { ScheduleControllerService } from '../generated-api';
import { ScheduleDTO } from '../generated-api';
import {Observable, throwError} from 'rxjs';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {catchError} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class SchedulerService {

  private baseUrl = 'http://localhost:7001/sti-jobs/api/v1/schedules';

  constructor(private http: HttpClient, private scheduleApi: ScheduleControllerService) { }

  createSchedule(scheduleData: ScheduleDTO): Observable<ScheduleDTO> {
    return this.http.post<ScheduleDTO>(`${this.baseUrl}/create-schedule`, scheduleData);
  }

  getAllSchedules(): Observable<ScheduleDTO[]> {
    return this.http.get<ScheduleDTO[]>(`${this.baseUrl}/get-all-schedules`);
  }

  deleteSchedule(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  updateSchedule(id: number, scheduleData: ScheduleDTO): Observable<ScheduleDTO> {
    return this.http.put<ScheduleDTO>(`${this.baseUrl}/${id}`, scheduleData)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Qualcosa è andato storto; per favore riprova più tardi.';
    if (error.error instanceof ErrorEvent) {
      console.error('Si è verificato un errore:', error.error.message);
      errorMessage = error.error.message;
    } else {
      console.error(
        `Il backend ha restituito il codice ${error.status}, il corpo della risposta è: `, error.error);
      if (error.error) {
        errorMessage = error.error;
      }
    }
    return throwError(() => new Error(errorMessage));
  }



}
