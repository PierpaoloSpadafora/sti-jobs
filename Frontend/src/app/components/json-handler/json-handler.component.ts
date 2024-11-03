import { Component, Input,  ViewChild, ElementRef} from '@angular/core';
import {JsonService} from "../../services/json.service";

@Component({
  selector: 'app-json-handler',
  templateUrl: './json-handler.component.html',
  styleUrls: ['./json-handler.component.css']
})
export class JsonHandlerComponent {
  @ViewChild('fileInput') fileInput!: ElementRef;
  textareaContent: string = '';
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

    this.jsonService.callJsonEndpoint(jsonObject).subscribe({
      next: (response) => {
        console.log('Risposta dal backend:', response);
      },
      error: (error) => {
        console.error('Errore chiamando il backend:', error);
      }
    });
  }


}
