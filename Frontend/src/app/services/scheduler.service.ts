import { Injectable } from '@angular/core';
import { ScheduleControllerService } from '../generated-api';
import { ScheduleDTO } from '../generated-api';
import { Observable } from 'rxjs';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class SchedulerService {

  private baseUrl = 'http://localhost:7001/sti-jobs/api/v1/schedules';

  constructor(private http: HttpClient, private scheduleApi: ScheduleControllerService) { }

  createSchedule(scheduleData: ScheduleDTO): Observable<ScheduleDTO> {
    return this.http.post<ScheduleDTO>(`${this.baseUrl}`, scheduleData);
  }

}
