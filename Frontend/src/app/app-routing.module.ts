import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './auth.guard';

import { HomeComponent } from "./components/home/home.component";
import { LoginComponent } from './components/login/login.component';
import { CreateComponent } from './components/create/create.component';
import { ScheduleComponent } from './components/schedule/schedule.component';
import { GraphsComponent } from './components/graphs/graphs.component';
import {JsonHandlerComponent} from "./components/json-handler/json-handler.component";

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'create', component: CreateComponent, canActivate: [AuthGuard] },
  { path: 'schedule', component: ScheduleComponent, canActivate: [AuthGuard] },
  { path: 'graphs', component: GraphsComponent, canActivate: [AuthGuard] },
  { path: 'json', component: JsonHandlerComponent, canActivate: [AuthGuard] },
  { path : '**', component : LoginComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
