import { Component, Input } from '@angular/core';
import { JsonService } from "../../services/json.service";
import { Router } from "@angular/router";
import {VariablesService} from "../../services/variables.service";

@Component({
  selector: 'app-page-card',
  templateUrl: './page-card.component.html',
  styleUrls: ['./page-card.component.css']
})
export class PageCardComponent {
  @Input() title: string = '';
  @Input() buttonText: string = '';
  @Input() icon: string = '';
  @Input() iconSecond: string = '';
  @Input() link: string = '';
  @Input() jsonAction: string = '';

  private actionMap: { [key: string]: () => void };

  constructor(private variablesService: VariablesService, private router: Router) {
    this.actionMap = {
      importJobs: () => this.variablesService.setAction('import', 'Jobs'),
      importProjects: () => this.variablesService.setAction('import', 'Jobs'),
      exportJobs: () => this.variablesService.setAction('export', 'Projects'),
      exportProjects: () => this.variablesService.setAction('export', 'Projects'),
    };
  }

  jsonActionHandler(): void {
    console.log(this.jsonAction);
    const action = this.actionMap[this.jsonAction];
    if (action) {
      action();
    } else {
      console.warn(`Azione non supportata: ${this.jsonAction}`);
    }
    console.log(this.variablesService.isImport());
    console.log(this.variablesService.isJobs());
    this.router.navigate([this.link]);
  }
}
