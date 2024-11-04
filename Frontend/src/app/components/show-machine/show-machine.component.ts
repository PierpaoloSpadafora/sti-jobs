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
  selector: 'app-show-machine',
  templateUrl: './show-machine.component.html',
  styleUrl: './show-machine.component.css'
})
export class ShowMachineComponent {
  machines: Machine[] = [];

  constructor(
    private jsonService: JsonService
  ) {}

  ngOnInit(): void {
    this.jsonService.exportMachine().subscribe({
      next: (response: any) => { 
        this.machines = Array.isArray(response) ? response : [response]; 
        console.log("Machine ottenuti:", this.machines);
      },
      error: (error) => {
        console.error("Errore durante il recupero delle machine:", error);
      }
    });
  }
}
