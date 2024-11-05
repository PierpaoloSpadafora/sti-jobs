import { Component } from '@angular/core';
import { JsonService } from '../../services/json.service';

interface Machine {
  id: number;
  createdAt: Date;
  description: string;
  name: string;
  status: string;
  updatedAt: Date;
  typeId: number;
}

@Component({
  selector: 'app-view-export-delete-machines',
  templateUrl: './view-export-delete-machines.component.html',
  styleUrl: './view-export-delete-machines.component.css'
})
export class ViewExportDeleteMachinesComponent {
  machines: Machine[] = [];

  constructor(
    private jsonService: JsonService
  ) {}

  ngOnInit(): void {
    this.jsonService.exportMachine().subscribe({
      next: (response: any) => {
        this.machines = Array.isArray(response) ? response : [response];
        console.log("Machines retrieved:", this.machines);
      },
      error: (error) => {
        console.error("Error while retrieving machines:", error);
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'active':
        return 'status-active';
      case 'inactive':
        return 'status-inactive';
      case 'maintenance':
        return 'status-maintenance';
      default:
        return 'status-unknown';
    }
  }
}
