import { Component, OnInit } from '@angular/core';
import { ChartType } from 'angular-google-charts';
import { forkJoin } from 'rxjs';
import { JobControllerService, JsonControllerService} from '../../generated-api';
import { JobDTO  } from '../../generated-api';
import { LoginService } from '../../services/login.service'; // Servizio per `getUserEmail`.

@Component({
  selector: 'app-graphs',
  templateUrl: './graphs.component.html',
  styleUrls: ['./graphs.component.css']
})
export class GraphsComponent implements OnInit {
  

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
        fill: '#FFFFFF' // Sfondo bianco
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
        fill: '#FFFFFF' // Sfondo bianco
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
      types: this.jsonService.exportMachineType()
    }).subscribe({
      next: (response) => {
        const jobDTOs = Array.isArray(response.jobs) ? (response.jobs as JobDTO[]) : [];
        this.jobs = jobDTOs.map(dto => ({
          id: dto.id!,
          title: dto.title,
          description: dto.description,
          status: dto.status,
          priority: dto.priority,
          duration: dto.duration,
          idMachineType: dto.idMachineType,
          assigneeEmail: this.loginService.getUserEmail()!
        }));
        
        const types = this.transformMachineTypes(response.types);
        this.machineTypes = types;
        
        this.barChart.data = this.prepareBarChartData(this.jobs);
        this.statusChart.data = this.prepareStatusChartData(this.jobs);
  
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

  private showMessage(message: string): void {
    alert(message);
  }
}