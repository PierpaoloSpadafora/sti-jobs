import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Machine } from '../../interfaces/interfaces'; 
import { JsonService } from '../../services/json.service';

@Component({
  selector: 'app-edit-machine-dialog',
  templateUrl: './edit-machine-dialog.component.html',
  styleUrl: './edit-machine-dialog.component.css'
})
export class EditMachineDialogComponent {
  editForm: FormGroup;
  machines: Machine[] = [];

  constructor(
    private fb: FormBuilder,
    private jsonService: JsonService,
    private dialogRef: MatDialogRef<EditMachineDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Machine
  ) {
    this.jsonService.exportMachineType().subscribe({
      next: (response: any) => {
        this.machines = Array.isArray(response) ? response : [response];
        console.log("Machines retrieved:", this.machines);
      },
      error: (error) => {
        console.error("Error while retrieving machines:", error);
      }
    });

    this.editForm = this.fb.group({
      id: [data.id],
      createdAt: [data.createdAt],
      description: [data.description, Validators.required],
      name: [data.name, Validators.required],
      status: [data.status],
      updatedAt: [data.updatedAt],
      typeId: [data.typeId, Validators.required]
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.editForm.valid) {
      this.dialogRef.close(this.editForm.value);
    }
  }
}