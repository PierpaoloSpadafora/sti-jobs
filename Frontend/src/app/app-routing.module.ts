import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './auth.guard';

import { HomeComponent } from "./components/home/home.component";
import { LoginComponent } from './components/login/login.component';
import { CreateComponent } from './components/create/create.component';
import { ScheduleComponent } from './components/schedule/schedule.component';
import { GraphsComponent } from './components/graphs/graphs.component';
import { CreateImportJobComponent } from './components/create-import-job/create-import-job.component';
import { CreateImportMachineComponent } from "./components/create-import-machine/create-import-machine.component";
import { CreateImportMachineTypeComponent } from "./components/create-import-machine-type/create-import-machine-type.component";
import { ShowJobComponent } from './components/show-job/show-job.component';
import { ShowMachineComponent } from './components/show-machine/show-machine.component';
import { ShowMachineTypeComponent } from './components/show-machine-type/show-machine-type.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'create', component: CreateComponent, canActivate: [AuthGuard] },
  { path: 'schedule', component: ScheduleComponent, canActivate: [AuthGuard] },
  { path: 'graphs', component: GraphsComponent, canActivate: [AuthGuard] },

  { path: 'create-import-jobs', component: CreateImportJobComponent, canActivate: [AuthGuard] },
  { path: 'create-import-machines', component: CreateImportMachineComponent,canActivate:[AuthGuard]},
  { path: 'create-import-machine-types', component: CreateImportMachineTypeComponent,canActivate:[AuthGuard]},

  { path: 'show-job', component: ShowJobComponent,canActivate:[AuthGuard]},
  { path: 'show-machine', component: ShowMachineComponent,canActivate:[AuthGuard]},
  { path: 'show-machine-type', component: ShowMachineTypeComponent,canActivate:[AuthGuard]},

  {path : 'view-export-delete-jobs', component : CreateImportJobComponent, canActivate: [AuthGuard]},
  {path : 'view-export-delete-machines', component : CreateImportMachineComponent, canActivate: [AuthGuard]},
  {path : 'view-export-delete-machine-types', component : CreateImportMachineTypeComponent, canActivate: [AuthGuard]},

  { path : '**', component : LoginComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
