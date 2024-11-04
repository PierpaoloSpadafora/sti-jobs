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
import { MachineTypeControllerService, UserControllerService } from "./generated-api";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {MatIcon, MatIconModule} from "@angular/material/icon";
import {MatMenu, MatMenuModule, MatMenuTrigger} from "@angular/material/menu";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatButtonModule} from "@angular/material/button";
import { ShowJobComponent } from './components/show-job/show-job.component';
import { ShowMachineComponent } from './components/show-machine/show-machine.component';
import { ShowMachineTypeComponent } from './components/show-machine-type/show-machine-type.component';

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
    ShowJobComponent,
    ShowMachineComponent,
    ShowMachineTypeComponent,
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
    MatIconModule,
    MatMenuModule,
    MatMenuTrigger
  ],
  providers: [MachineTypeControllerService, UserControllerService, provideAnimationsAsync()],
  bootstrap: [AppComponent]
})
export class AppModule { }
