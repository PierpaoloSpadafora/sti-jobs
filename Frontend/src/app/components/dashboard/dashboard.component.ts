import { Component, ViewChild, ElementRef  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  @ViewChild('fileInput') fileInput!: ElementRef;
  textareaContent: string = '';

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
    // mandare la chiamata al backend
  }
}