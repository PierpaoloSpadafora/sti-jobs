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
      scheduledJobs: this.jsonService.exportJobScheduledDueDate()
    }).subscribe({
      next: async (response) => {
        const jobDTOs = Array.isArray(response.jobs) ? (response.jobs as JobDTO[]) : [];
        const scheduledJobDTOs = Array.isArray(response.scheduledJobs) ? (response.scheduledJobs as ScheduleDTO[]) : [];

        const combinedJobs = [
          ...jobDTOs.map(dto => ({
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
              status: 'SCHEDULED',
              priority: matchingJob?.priority,
              duration: scheduleDto.duration,
              idMachineType: scheduleDto.machineTypeId,
              dueDate: scheduleDto.dueDate,
              startTime: scheduleDto.startTime
            };
          })
        ];

        const types = this.transformMachineTypes(response.types);
        this.machineTypes = types;
        this.pieChart.data = this.aggregateMachineTypes(jobDTOs, types);
        this.barChart.data = this.prepareBarChartData(combinedJobs);
        this.statusChart.data = this.prepareStatusChartData(combinedJobs);

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

  private showMessage(message: string): void {
    alert(message);
  }
}
