import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class VariablesService {
  private state = {
    importJobs: false,
    importProjects: false,
    exportJobs: false,
    exportProjects: false
  };

  constructor() { }

  public isImport(): boolean {
    return this.state.importJobs || this.state.importProjects;
  }

  public isJobs(): boolean {
    return this.state.importJobs || this.state.exportJobs;
  }

  public setAction(actionType: 'import' | 'export', entityType: 'Jobs' | 'Projects') {
    this.resetState();

    const key = `${actionType}${entityType}`;
    if (key in this.state) {
      this.state[key as keyof typeof this.state] = true;
    } else {
      console.warn(`Azione non riconosciuta: ${key}`);
    }
  }

  private resetState() {
    Object.keys(this.state).forEach(key => {
      this.state[key as keyof typeof this.state] = false;
    });
  }

  public getActionStatus() {
    return { ...this.state };
  }
}
