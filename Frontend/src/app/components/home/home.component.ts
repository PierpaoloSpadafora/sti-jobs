import { Component, OnInit } from '@angular/core';
import { ScheduleControllerService, JobDTO,
  JsonControllerService } from '../../generated-api';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { ScheduleWithMachineDTO } from '../../generated-api';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  scheduleData: ScheduleWithMachineDTO[] = [];
  jobsMap: Map<number, JobDTO> = new Map<number, JobDTO>();
  machinesMap: Map<number, string> = new Map<number, string>();
  machineNamesMap: Map<number, string> = new Map<number, string>();

  scheduleTypes = [
    { label: 'All Jobs', value: 'ALL' },
    { label: 'Scheduled by Due Date', value: 'DUE_DATE' },
    { label: 'Scheduled by Priority', value: 'PRIORITY' },
    { label: 'Scheduled by Duration', value: 'DURATION' },
    { label: 'Scheduled by FCFS', value: 'FCFS' },
    { label: 'Scheduled by RR', value: 'RR' },
    { label: 'External Scheduler ', value: 'EXTERNAL' }
  ];
  selectedScheduleType = 'ALL';
  errorMessage: string = '';
  newSelectedScheduleType: string = this.selectedScheduleType;

  daysPerPageOptions = [1, 3, 5, 7];
  selectedDaysPerPage = 3;
  currentPage = 0;
  paginatedDates: string[] = [];

  hasScheduledJobs = false;
  loading = true;

  schedulesByDateAndMachine: Map<string, Map<number, ScheduleWithMachineDTO[]>> = new Map<string, Map<number, ScheduleWithMachineDTO[]>>();

  constructor(
    private scheduleService: ScheduleControllerService,
    private jsonService: JsonControllerService,
    private router: Router
  ) { }

  ngOnInit() {
    this.fetchData(this.selectedScheduleType);
  }

  onScheduleTypeChange() {
    this.fetchData(this.newSelectedScheduleType);
  }

  fetchData(scheduleType: string) {
    this.loading = true;

    let scheduleObservable: Observable<ScheduleWithMachineDTO[]>;
    switch (scheduleType) {
      case 'PRIORITY':
        scheduleObservable = this.jsonService.exportJobScheduledPriority();
        break;
      case 'DUE_DATE':
        scheduleObservable = this.jsonService.exportJobScheduledDueDate();
        break;
      case 'DURATION':
        scheduleObservable = this.jsonService.exportJobScheduledDuration();
        break;
      case 'EXTERNAL':
        scheduleObservable = this.jsonService.exportJobScheduledExternal();
        break;
      case 'FCFS':
        scheduleObservable = this.jsonService.exportJobScheduledFCFS();
        break;
      case 'RR':
        scheduleObservable = this.jsonService.exportJobScheduledRR();
        break;
      default:
        scheduleObservable = this.scheduleService.getAllSchedules();
    }

    scheduleObservable.subscribe({
      next: (scheduleData: ScheduleWithMachineDTO[]) => {
        if (scheduleData.length === 0) {
          this.errorMessage = 'Nessun job pianificato trovato per il tipo selezionato.';
          this.loading = false;
        } else {
          this.errorMessage = '';
          this.selectedScheduleType = scheduleType;
          this.newSelectedScheduleType = scheduleType;
          this.scheduleData = scheduleData;

          this.jsonService.exportJob().subscribe({
            next: (jobs: JobDTO[]) => {
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

                  // Fetch machine names
                  this.jsonService.exportMachine().subscribe({
                    next: (machines) => {
                      this.machineNamesMap.clear();
                      machines.forEach(machine => {
                        if (machine.id !== undefined && machine.name !== undefined) {
                          this.machineNamesMap.set(machine.id, machine.name);
                        }
                      });

                      this.processData();
                      this.loading = false;
                    },
                    error: (error: unknown) => {
                      console.error('Error fetching machines data', error);
                      this.loading = false;
                    }
                  });
                },
                error: (error: unknown) => {
                  console.error('Error fetching machine types data', error);
                  this.loading = false;
                }
              });
            },
            error: (error: unknown) => {
              console.error('Error fetching jobs data', error);
              this.loading = false;
            }
          });
        }
      },
      error: (error: unknown) => {
        console.error('Error fetching schedule data', error);
        this.loading = false;
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'Impossibile caricare i dati. Il file potrebbe non essere presente.',
        });
        // Ripristina il valore della select
        this.newSelectedScheduleType = this.selectedScheduleType;
      }
    });
  }

  processData() {
    if (this.scheduleData.length === 0) {
      this.hasScheduledJobs = false;
      return;
    }
    this.hasScheduledJobs = true;

    const schedulesByDateAndMachine: Map<string, Map<number, ScheduleWithMachineDTO[]>> = new Map();

    this.scheduleData.forEach(schedule => {
      const date = schedule.startTime ? new Date(schedule.startTime).toDateString() : 'Unknown Date';
      const machineTypeId = schedule.machineTypeId || 0;

      if (!schedulesByDateAndMachine.has(date)) {
        schedulesByDateAndMachine.set(date, new Map<number, ScheduleWithMachineDTO[]>());
      }

      const machineMap = schedulesByDateAndMachine.get(date)!;

      if (!machineMap.has(machineTypeId)) {
        machineMap.set(machineTypeId, []);
      }

      machineMap.get(machineTypeId)!.push(schedule);
    });

    const sortedDates = Array.from(schedulesByDateAndMachine.keys()).sort((a, b) => new Date(a).getTime() - new Date(b).getTime());

    this.schedulesByDateAndMachine = new Map([...schedulesByDateAndMachine.entries()]
      .sort((a, b) => new Date(a[0]).getTime() - new Date(b[0]).getTime()));

    this.paginatedDates = sortedDates;

    this.currentPage = 0;

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

  getUniqueMachineTypes(date: string): string[] {
    const machineTypeIds = Array.from(this.schedulesByDateAndMachine.get(date)?.keys() || []);
    const uniqueTypes = new Set<string>();

    machineTypeIds.forEach(id => {
      const machineName = this.machinesMap.get(id) || 'Unknown Machine Type';
      uniqueTypes.add(machineName);
    });

    return Array.from(uniqueTypes);
  }

  getSchedulesForMachineType(date: string, machineTypeName: string): ScheduleWithMachineDTO[][] {
    const machineTypeId = Array.from(this.machinesMap.entries())
      .find(([_, name]) => name === machineTypeName)?.[0];

    if (!machineTypeId) return [];

    const schedules = this.schedulesByDateAndMachine.get(date)?.get(machineTypeId) || [];

    // Group schedules by machineId
    const groupedSchedules = new Map<number, ScheduleWithMachineDTO[]>();

    schedules.forEach(schedule => {
      const machineId = schedule.machineId || 0;
      if (!groupedSchedules.has(machineId)) {
        groupedSchedules.set(machineId, []);
      }
      groupedSchedules.get(machineId)?.push(schedule);
    });

    return Array.from(groupedSchedules.values());
  }

  get visibleDates(): string[] {
    const start = this.currentPage * this.selectedDaysPerPage;
    return this.paginatedDates.slice(start, start + this.selectedDaysPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.paginatedDates.length / this.selectedDaysPerPage);
  }

  nextPage() {
    if ((this.currentPage + 1) * this.selectedDaysPerPage < this.paginatedDates.length) {
      this.currentPage++;
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
    }
  }

  onDaysPerPageChange() {
    this.currentPage = 0;
  }

  navigateToSchedule() {
    this.router.navigate(['/schedule']);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const options: Intl.DateTimeFormatOptions = {
      weekday: 'long',
      day: '2-digit',
      month: 'long',
      year: 'numeric'
    };
    return new Intl.DateTimeFormat('it-IT', options).format(date);
  }

  getMachineName(machineId: number | undefined): string {
    if (!machineId) return 'Unknown Machine';
    return `${this.machineNamesMap.get(machineId) || 'Unknown'} - ${machineId}`;
  }

  protected readonly Array = Array;
}
