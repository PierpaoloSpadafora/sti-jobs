import { Component, OnInit } from '@angular/core';
import { ChartType } from 'angular-google-charts';
import { forkJoin } from 'rxjs';
import { JobControllerService, JsonControllerService} from '../../generated-api';
import { JobDTO,ScheduleDTO  } from '../../generated-api';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-graphs',
  templateUrl: './graphs.component.html',
  styleUrls: ['./graphs.component.css']
})
export class GraphsComponent implements OnInit {
  pieChart = {
    type: ChartType.PieChart,
    data: [] as [string, number][],
    columns: ['Type', 'Usage'],
    options: {
      title: 'Most Utilized Machine Types',
      width: 550,
      height: 500,
      chartArea: {
        width: '85%',
        height: '85%'
      },
      pieHole: 0.4,
      fontSize: 14,
      fontName: 'Roboto',
      colors: ['#6A5ACD', '#4169E1', '#1E90FF', '#00BFFF', '#87CEFA'],
      titleTextStyle: {
        fontSize: 18,
        bold: true,
        color: '#2C3E50'
      },
      backgroundColor: {
        fill: '#FFFFFF'
      },
      legend: {
        position: 'right',
        textStyle: { color: '#2C3E50', fontSize: 12 }
      }
    }
  };

  barChart = {
    type: ChartType.ColumnChart,
    data: [] as [string, number, number][],
    columns: ['Job', 'Completed', 'Scheduled'],
    options: {
      title: 'Job Overview',
      width: 650,
      height: 450,
      seriesType: 'bars',
      series: {
        0: { color: '#2ECC71' },
        1: { color: '#3498DB' }
      },
      fontSize: 12,
      fontName: 'Roboto',
      titleTextStyle: {
        fontSize: 18,
        bold: true,
        color: '#2C3E50'
      },
      backgroundColor: {
        fill: '#FFFFFF'
      },
      hAxis: {
        title: 'Jobs',
        titleTextStyle: { color: '#2C3E50', italic: false },
        textStyle: { color: '#2C3E50' }
      },
      vAxis: {
        title: 'Number of Jobs',
        titleTextStyle: { color: '#2C3E50', italic: false },
        textStyle: { color: '#2C3E50' }
      },
      animation: {
        startup: true,
        duration: 1000,
        easing: 'out'
      }
    }
  };

  statusChart = {
    type: ChartType.BarChart,
    data: [] as [string, number, number, number, number, number][],
    columns: ['Status', 'PENDING', 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'],
    options: {
      title: 'Jobs by Status',
      width: 650,
      height: 450,
      bars: 'horizontal',
      colors: ['#F39C12', '#3498DB', '#2980B9', '#2ECC71', '#E74C3C'],
      isStacked: true,
      fontSize: 12,
      fontName: 'Roboto',
      titleTextStyle: {
        fontSize: 18,
        bold: true,
        color: '#2C3E50'
      },
      backgroundColor: {
        fill: '#FFFFFF'
      },
      hAxis: {
        title: 'Number of Jobs',
        titleTextStyle: { color: '#2C3E50', italic: false },
        textStyle: { color: '#2C3E50' }
      },
      vAxis: {
        title: 'Status',
        titleTextStyle: { color: '#2C3E50', italic: false },
        textStyle: { color: '#2C3E50' }
      },
      animation: {
        startup: true,
        duration: 1000,
        easing: 'out'
      }
    }
  };

  machineJobsChart = {
    type: ChartType.LineChart,
    data: [] as [string, number, number, number][],
    columns: ['Macchina', 'Schedulati', 'In Progresso', 'Completati'],
    options: {
      title: 'Job per Specifica Macchina',
      width: 650,
      height: 450,
      colors: ['#F39C12', '#3498DB', '#2ECC71'],
      fontSize: 12,
      fontName: 'Roboto',
      titleTextStyle: {
        fontSize: 18,
        bold: true,
        color: '#2C3E50'
      },
      backgroundColor: {
        fill: '#FFFFFF'
      },
      hAxis: {
        title: 'Macchine',
        titleTextStyle: { color: '#2C3E50', italic: false },
        textStyle: { color: '#2C3E50' }
      },
      vAxis: {
        title: 'Numero di Job',
        titleTextStyle: { color: '#2C3E50', italic: false },
        textStyle: { color: '#2C3E50' }
      },
      animation: {
        startup: true,
        duration: 1000,
        easing: 'out'
      }
    }
  };

  isLoading = false;
  jobs: any[] = [];
  machineTypes: any[] = [];

  constructor(
    private jobService: JobControllerService,
    private jsonService: JsonControllerService,
    private loginService: LoginService
  ) {}

  ngOnInit(): void {
    this.loadJobs();
  }

  loadJobs(): void { 
    this.isLoading = true;

    forkJoin({
        jobs: this.jobService.getAllJobs(),
        types: this.jsonService.exportMachineType(),
        machine: this.jsonService.exportMachine(),
        scheduledDueDateJobs: this.jsonService.exportJobScheduledDueDate(),
        scheduledDurationJobs: this.jsonService.exportJobScheduledDuration(),
        scheduledPriorityJobs: this.jsonService.exportJobScheduledPriority()
    }).subscribe({
        next: async (response) => {
            const jobDTOs = Array.isArray(response.jobs) ? (response.jobs as JobDTO[]) : [];
            const scheduledDueDateJobs = Array.isArray(response.scheduledDueDateJobs) ? (response.scheduledDueDateJobs as ScheduleDTO[]) : [];
            const scheduledDurationJobs = Array.isArray(response.scheduledDurationJobs) ? (response.scheduledDurationJobs as ScheduleDTO[]) : [];
            const scheduledPriorityJobs = Array.isArray(response.scheduledPriorityJobs) ? (response.scheduledPriorityJobs as ScheduleDTO[]) : [];
            const allScheduledJobs = [
                ...scheduledDueDateJobs,
                ...scheduledDurationJobs,
                ...scheduledPriorityJobs
            ];
            const scheduledJobDTOs = Array.from(
                new Map(allScheduledJobs.map(job => [job.jobId, job])).values()
            );
            const scheduledJobIds = new Set(scheduledJobDTOs.map(scheduleDto => scheduleDto.jobId));
            const combinedJobs = [
                ...jobDTOs
                    .filter(dto => !scheduledJobIds.has(dto.id))
                    .map(dto => ({
                        id: dto.id!,
                        title: dto.title,
                        description: dto.description,
                        status: dto.status,
                        priority: dto.priority,
                        duration: dto.duration,
                        idMachineType: dto.idMachineType,
                        assigneeEmail: this.loginService.getUserEmail()!
                    })),
                ...scheduledJobDTOs.map(scheduleDto => {
                    const matchingJob = jobDTOs.find(job => job.id === scheduleDto.jobId);
                    return {
                        id: scheduleDto.id!,
                        title: matchingJob?.title || 'Scheduled Job',
                        status: scheduleDto.status || matchingJob?.status || 'SCHEDULED',
                        priority: matchingJob?.priority,
                        duration: scheduleDto.duration,
                        machineId: scheduleDto.assignedMachineId, 
                        machineName: scheduleDto.assignedMachineName,
                        idMachineType: scheduleDto.machineTypeId,
                        dueDate: scheduleDto.dueDate,
                        startTime: scheduleDto.startTime
                    };
                })
            ];

            const types = this.transformMachineTypes(response.types);
            const machines = this.transformMachine(response.machine);
            this.machineTypes = types;
            const scheduledJobs = combinedJobs.filter(job => job.status === 'SCHEDULED');
            this.pieChart.data = this.aggregateMachineTypes(scheduledJobs, types);
            this.barChart.data = this.prepareBarChartData(combinedJobs);
            this.statusChart.data = this.prepareStatusChartData(combinedJobs);
            this.machineJobsChart.data = this.prepareMachineJobsChartData(scheduledJobDTOs, machines);

            this.isLoading = false;
        },
        error: (error) => {
            console.error('Error while retrieving data:', error);
            this.isLoading = false;
            this.showMessage('Error loading data. Please try again later.');
        }
    });
  }


  private prepareBarChartData(jobs: any[]): [string, number, number][] {
    const jobStatusCounts: { [key: string]: { completed: number, scheduled: number } } = {};

    jobs.forEach(job => {
      if (!jobStatusCounts[job.title]) {
        jobStatusCounts[job.title] = { completed: 0, scheduled: 0 };
      }

      if (job.status === 'COMPLETED') {
        jobStatusCounts[job.title].completed++;
      } else if (job.status === 'SCHEDULED') {
        jobStatusCounts[job.title].scheduled++;
      }
    });
    const barData: [string, number, number][] = Object.entries(jobStatusCounts)
      .map(([title, counts]) => [title, counts.completed, counts.scheduled]);
    if (barData.length === 0) {
      barData.push(['No Jobs', 0, 0]);
    }

    return barData;
  }

  private transformMachineTypes(types: any): any[] {
    return Array.isArray(types)
      ? types.map(type => ({
          id: type.id,
          name: type.name,
          usageCount: typeof type.usageCount === 'number' ? type.usageCount : 0
        }))
      : [];
  }

  private transformMachine(machines: any): any[] {
    return Array.isArray(machines)
      ? machines.map(machine => ({
          id: machine.id,
          name: machine.name
        }))
      : [];
  }

  private prepareStatusChartData(jobs: any[]): [string, number, number, number, number, number][] {
    const statusCounts: { [key: string]: number } = {
      'PENDING': 0,
      'SCHEDULED': 0,
      'IN_PROGRESS': 0,
      'COMPLETED': 0,
      'CANCELLED': 0
    };

    jobs.forEach(job => {
      if (statusCounts[job.status] !== undefined) {
        statusCounts[job.status]++;
      }
    });

    return [
      ['PENDING', statusCounts['PENDING'], 0, 0, 0, 0],
      ['SCHEDULED', 0, statusCounts['SCHEDULED'], 0, 0, 0],
      ['IN_PROGRESS', 0, 0, statusCounts['IN_PROGRESS'], 0, 0],
      ['COMPLETED', 0, 0, 0, statusCounts['COMPLETED'], 0],
      ['CANCELLED', 0, 0, 0, 0, statusCounts['CANCELLED']],
    ];
  }

  private aggregateMachineTypes(jobs: any[], types: any[]): [string, number][] {
    const machineTypeUsage: { [key: string]: number } = {};
    jobs.forEach(job => {
      if (job.idMachineType) {
        const machineType = types.find(type => type.id === job.idMachineType);
        if (machineType) {
          const typeName = machineType.name;
          machineTypeUsage[typeName] = (machineTypeUsage[typeName] || 0) + 1;
        } else {
          console.warn(`No machine type found for id: ${job.idMachineType}`);
        }
      }
    });

    const aggregatedData: [string, number][] = Object.entries(machineTypeUsage)
      .map(([name, count]) => [name, count]);
    if (aggregatedData.length === 0) {
      aggregatedData.push(['No data available', 1]);
    }
    return aggregatedData;
  }

  private prepareMachineJobsChartData(scheduledJobs: any[], machines: any[]): [string, number, number, number][] {
    console.log(scheduledJobs);
    const machineIdToNameMap: { [key: string]: string } = {};
    machines.forEach(machine => {
        machineIdToNameMap[machine.id] = machine.name;
    });

    const machineJobCounts: { [key: string]: { scheduled: number, inProgress: number, completed: number } } = {};

    scheduledJobs.forEach(job => {
        const machineName = job.machineName || machineIdToNameMap[job.machineId] || `Machine ${job.machineId}`;
        
        if (!machineJobCounts[machineName]) {
            machineJobCounts[machineName] = { scheduled: 0, inProgress: 0, completed: 0 };
        }
        switch (job.status) {
            case 'SCHEDULED':
                machineJobCounts[machineName].scheduled++;
                break;
            case 'IN_PROGRESS':
                machineJobCounts[machineName].inProgress++;
                break;
            case 'COMPLETED':
                machineJobCounts[machineName].completed++;
                break;
        }
    });
    const chartData: [string, number, number, number][] = Object.entries(machineJobCounts)
        .map(([machineName, counts]) => [
            machineName,
            counts.scheduled,
            counts.inProgress,
            counts.completed
        ]);
    if (chartData.length === 0) {
        chartData.push(['No Machines', 0, 0, 0]);
    }
    return chartData;
  }
  
  private showMessage(message: string): void {
    alert(message);
  }
}
