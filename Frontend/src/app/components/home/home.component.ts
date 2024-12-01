import { Component, OnInit } from '@angular/core';
import { ScheduleControllerService } from '../../generated-api';
import { JobDTO } from '../../generated-api';
import { ScheduleDTO } from '../../generated-api';
import { JsonControllerService } from '../../generated-api';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  scheduleData: ScheduleDTO[] = [];
  jobsMap: Map<number, JobDTO> = new Map<number, JobDTO>();
  machinesMap: Map<number, string> = new Map<number, string>();

  scheduleTypes = [
    { label: 'All Jobs', value: 'ALL' },
    { label: 'Scheduled by Due Date', value: 'DUE_DATE' },
    { label: 'Scheduled by Priority', value: 'PRIORITY' },
    { label: 'Scheduled by Duration', value: 'DURATION' }
  ];
  selectedScheduleType = 'ALL';

  hasScheduledJobs = false;
  loading = true;

  schedulesByDateAndMachine: Map<string, Map<number, ScheduleDTO[]>> = new Map<string, Map<number, ScheduleDTO[]>>();

  constructor(
    private scheduleService: ScheduleControllerService,
    private jsonService: JsonControllerService,
    private router: Router
  ) { }

  ngOnInit() {
    this.fetchData();
  }

  onScheduleTypeChange() {
    this.fetchData();
  }

  fetchData() {
    this.loading = true;

    let scheduleObservable;
    switch (this.selectedScheduleType) {
      case 'PRIORITY':
        scheduleObservable = this.jsonService.exportJobScheduledPriority();
        break;
      case 'DUE_DATE':
        scheduleObservable = this.jsonService.exportJobScheduledDueDate();
        break;
      case 'DURATION':
        scheduleObservable = this.jsonService.exportJobScheduledDuration();
        break;
      default:
        scheduleObservable = this.scheduleService.getAllSchedules();
    }

    scheduleObservable.subscribe({
      next: (scheduleData: ScheduleDTO[]) => {
        this.scheduleData = scheduleData;

        this.jsonService.exportJob().subscribe({
          next: (jobs) => {
            this.jobsMap.clear();
            jobs.forEach(job => {
              if (job.id !== undefined) {
                this.jobsMap.set(job.id, job);
              }
            });

            this.jsonService.exportMachineType().subscribe({
              next: (machineTypes) => {
                this.machinesMap.clear();
                machineTypes.forEach(machineType => {
                  if (machineType.id !== undefined && machineType.name !== undefined) {
                    this.machinesMap.set(machineType.id, machineType.name);
                  }
                });

                this.processData();
                this.loading = false;
              },
              error: (error: any) => {
                console.error('Error fetching machine types data', error);
                this.loading = false;
              }
            });
          },
          error: (error: any) => {
            console.error('Error fetching jobs data', error);
            this.loading = false;
          }
        });
      },
      error: (error: any) => {
        console.error('Error fetching schedule data', error);
        this.loading = false;
      }
    });
  }

  processData() {
    if (this.scheduleData.length === 0) {
      this.hasScheduledJobs = false;
      return;
    }
    this.hasScheduledJobs = true;

    const schedulesByDateAndMachine: Map<string, Map<number, ScheduleDTO[]>> = new Map();

    this.scheduleData.forEach(schedule => {
      const date = schedule.startTime ? new Date(schedule.startTime).toDateString() : 'Unknown Date';
      const machineTypeId = schedule.machineTypeId || 0;

      if (!schedulesByDateAndMachine.has(date)) {
        schedulesByDateAndMachine.set(date, new Map<number, ScheduleDTO[]>());
      }

      const machineMap = schedulesByDateAndMachine.get(date)!;

      if (!machineMap.has(machineTypeId)) {
        machineMap.set(machineTypeId, []);
      }

      machineMap.get(machineTypeId)!.push(schedule);
    });

    this.schedulesByDateAndMachine = new Map([...schedulesByDateAndMachine.entries()].sort((a, b) => new Date(a[0]).getTime() - new Date(b[0]).getTime()));

    this.schedulesByDateAndMachine.forEach((machineMap, date) => {
      machineMap.forEach((schedules, machineTypeId) => {
        schedules.sort((a, b) => {
          const dateA = new Date(a.startTime || 0).getTime();
          const dateB = new Date(b.startTime || 0).getTime();
          return dateA - dateB;
        });
      });
    });
  }

  navigateToSchedule() {
    this.router.navigate(['/schedule']);
  }

  protected readonly Array = Array;
}
