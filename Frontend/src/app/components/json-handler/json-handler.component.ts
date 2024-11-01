import { Component, Input } from '@angular/core';
import {JsonService} from "../../services/json.service";

@Component({
  selector: 'app-json-handler',
  templateUrl: './json-handler.component.html',
  styleUrls: ['./json-handler.component.css']
})
export class JsonHandlerComponent {
  type = '';
  action = '';

  constructor(private jsonService: JsonService) {
  }

  ngOnInit(): void {
    this.action = this.getAction();
    this.type = this.getType();
  }

  getAction(): string {
    if(this.jsonService.isImport()){
      return 'Import';
    }
    return 'Export';
  }

  getType(): string {
    if(this.jsonService.isJobs()){
      return 'Jobs';
    }
    return 'Projects';
  }

  getIcon(): string {
    if(this.jsonService.isImport()){
      return 'bi bi-cloud-arrow-up';
    }
    if(!this.jsonService.isImport()){
      return 'bi bi-cloud-arrow-down';
    }
    return 'bi bi-bug';
  }



}
