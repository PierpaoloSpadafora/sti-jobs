import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { JsonControllerService } from "../../generated-api";
import { MachineTypeDTO } from '../../interfaces/interfaces';

@Component({
  selector: 'app-edit-job-dialog',
  templateUrl: './edit-job-dialog.component.html',
  styleUrl: './edit-job-dialog.component.css'
})
export class EditJobDialogComponent {

  editForm: FormGroup;
  machineTypes: MachineTypeDTO[] = [];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditJobDialogComponent>,
    private jsonService: JsonControllerService,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.editForm = this.fb.group({
      id: [data.id],
      title: [data.title, Validators.required],
      description: [data.description, Validators.required],
      duration: [data.duration, [Validators.required, Validators.min(1)]],
      priority: [data.priority],
      status: [data.status],
      assignee: [data.assignee],
      requiredMachineType: [data.requiredMachineType]
    });
  }

  ngOnInit() {
    this.loadMachineTypes();
  }

  loadMachineTypes() {
    this.jsonService.exportMachineType().subscribe({
      next: (types) => {
        this.machineTypes = types;
      },
      error: (error) => {
        console.error('Error loading machine types:', error);
      }
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
