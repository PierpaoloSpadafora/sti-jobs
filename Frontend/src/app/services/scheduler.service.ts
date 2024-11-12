import { Injectable } from '@angular/core';
import { ScheduleControllerService } from '../generated-api';
import { ScheduleDTO } from '../generated-api';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SchedulerService {

  constructor(private scheduleApi: ScheduleControllerService) { }

  createSchedule(scheduleData: ScheduleDTO): Observable<ScheduleDTO> {
    return this.scheduleApi.createSchedule(scheduleData);
  }

}
