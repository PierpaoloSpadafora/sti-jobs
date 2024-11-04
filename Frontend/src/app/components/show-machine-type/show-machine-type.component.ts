import { Component } from '@angular/core';
import { JsonService } from '../../services/json.service';

interface MachineType {
  id: number;
  name: string;
  description: string;
}

@Component({
  selector: 'app-show-machine-type',
  templateUrl: './show-machine-type.component.html',
  styleUrl: './show-machine-type.component.css'
})
export class ShowMachineTypeComponent {
  machineTypes: MachineType[] = [];

  constructor(
    private jsonService: JsonService
  ) {}

  ngOnInit(): void {
    this.jsonService.exportMachineType().subscribe({
      next: (response: any) => { 
        this.machineTypes = Array.isArray(response) ? response : [response]; 
        console.log("Machine types ottenuti:", this.machineTypes);
      },
      error: (error) => {
        console.error("Errore durante il recupero dei machine types:", error);
      }
    });
  }
}
