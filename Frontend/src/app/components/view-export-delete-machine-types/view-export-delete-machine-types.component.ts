import { Component } from '@angular/core';
import { JsonService } from '../../services/json.service';

interface MachineType {
  id: number;
  name: string;
  description: string;
}

@Component({
  selector: 'app-view-export-delete-machine-types',
  templateUrl: './view-export-delete-machine-types.component.html',
  styleUrl: './view-export-delete-machine-types.component.css'
})
export class ViewExportDeleteMachineTypesComponent {
  machineTypes: MachineType[] = [];

  constructor(
    private jsonService: JsonService
  ) {}

  ngOnInit(): void {
    this.jsonService.exportMachineType().subscribe({
      next: (response: any) => {
        this.machineTypes = Array.isArray(response) ? response : [response];
        console.log("Machine types retrieved:", this.machineTypes);
      },
      error: (error) => {
        console.error("Error while retrieving machine types:", error);
      }
    });
  }
}
