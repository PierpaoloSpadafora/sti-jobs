import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { HeaderComponent } from './components/header/header.component';
import { NgOptimizedImage } from '@angular/common';
import { CreateComponent } from './components/create/create.component';
import { ScheduleComponent } from './components/schedule/schedule.component';
import { GraphsComponent } from './components/graphs/graphs.component';
import { PageCardComponent } from './components/page-card/page-card.component';
import { CreateImportJobComponent } from "./components/create-import-job/create-import-job.component";
import { CreateImportMachineComponent } from './components/create-import-machine/create-import-machine.component';
import { CreateImportMachineTypeComponent } from './components/create-import-machine-type/create-import-machine-type.component';
import { ViewExportDeleteJobsComponent } from './components/view-export-delete-jobs/view-export-delete-jobs.component';
import { ViewExportDeleteMachinesComponent } from './components/view-export-delete-machines/view-export-delete-machines.component';
import { ViewExportDeleteMachineTypesComponent } from './components/view-export-delete-machine-types/view-export-delete-machine-types.component';
import {
  JobControllerService,
  MachineTypeControllerService,
  ScheduleControllerService,
  UserControllerService
} from "./generated-api";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {MatIconModule} from "@angular/material/icon";
import { MatDialogModule } from '@angular/material/dialog';
import {MatMenuModule, MatMenuTrigger} from "@angular/material/menu";
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatButtonModule} from "@angular/material/button";
import { EditJobDialogComponent } from './components/edit-job-dialog/edit-job-dialog.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    NavbarComponent,
    HeaderComponent,
    CreateComponent,
    ScheduleComponent,
    GraphsComponent,
    PageCardComponent,
    CreateImportJobComponent,
    CreateImportMachineComponent,
    CreateImportMachineTypeComponent,
    ViewExportDeleteJobsComponent,
    ViewExportDeleteMachinesComponent,
    ViewExportDeleteMachineTypesComponent,
    EditJobDialogComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    NgOptimizedImage,
    CommonModule,
    FormsModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatIconModule,
    MatMenuModule,
    MatMenuTrigger,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule
  ],
  providers: [
    MachineTypeControllerService,
    UserControllerService,
    JobControllerService,
    ScheduleControllerService,
    provideAnimationsAsync()],
  bootstrap: [AppComponent]
})
export class AppModule { }
