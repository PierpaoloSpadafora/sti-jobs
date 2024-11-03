import { Component, Input,  ViewChild, ElementRef} from '@angular/core';
import {JsonService} from "../../services/json.service";
import {VariablesService} from "../../services/variables.service";

@Component({
  selector: 'app-json-handler',
  templateUrl: './job-project-handler.component.html',
  styleUrls: ['./job-project-handler.component.css']
})
export class JobProjectHandlerComponent {
  @ViewChild('fileInput') fileInput!: ElementRef;
  textareaContent: string = '';
  type = '';
  action = '';

  constructor(private variablesService: VariablesService, private jsonService: JsonService) {
  }

  ngOnInit(): void {
    this.action = this.getAction();
    this.type = this.getType();
  }

  getAction(): string {
    if(this.variablesService.isImport()){
      return 'Import';
    }
    return 'Export';
  }

  getType(): string {
    if(this.variablesService.isJobs()){
      return 'Jobs';
    }
    return 'Projects';
  }

  getIcon(): string {
    if(this.variablesService.isImport()){
      return 'bi bi-cloud-arrow-up';
    }
    if(!this.variablesService.isImport()){
      return 'bi bi-cloud-arrow-down';
    }
    return 'bi bi-bug';
  }

  isImport(): boolean {
    return this.action === 'Import';
  }

  isExport(): boolean {
    return this.action === 'Export';
  }

  isJobs(): boolean {
    return this.type === 'Jobs';
  }

  isProjects(): boolean {
    return this.type === 'Projects';
  }

  onFileButtonClick() {
    this.fileInput.nativeElement.click();
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      console.log('File selezionato:', file);
      console.log(file.text)
      const reader = new FileReader();
      reader.onload = () => {
        try {
          const jsonContent = JSON.parse(reader.result as string);
          this.textareaContent = JSON.stringify(jsonContent, null, 2);
        } catch (error) {
          console.error('Errore nel parsing del JSON:', error);
        }
      };
      reader.readAsText(file);
    }
  }

  submitText() {
    console.log('Testo inviato:', this.textareaContent);

    let jsonObject;
    try {
      jsonObject = JSON.parse(this.textareaContent);
    } catch (error) {
      console.error('Errore nel parsing del JSON:', error);
      alert('Il contenuto inserito non è un JSON valido. Per favore, controlla e riprova.');
      return;
    }

    let jsonDTO = {};
    if (this.isJobs()) {
      jsonDTO = { jobs: [jsonObject] };
    } else if (this.isProjects()) {
      jsonDTO = { projects: [jsonObject] };
    }

    this.jsonService.callJsonEndpoint(jsonDTO).subscribe({
      next: (response: string) => {
        console.log('Risposta dal backend:', response);
        alert(response);
      },
      error: (error) => {
        console.error('Errore chiamando il backend:', error);
        alert('Errore durante l\'importazione: ' + error.message);
      }
    });

  }
}
