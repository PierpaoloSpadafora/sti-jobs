import { Routes } from '@angular/router';
import { authGuard } from './auth-guard/guard.component';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./components/login/login.component')
      .then(m => m.LoginComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./components/dashboard/dashboard.component')
      .then(m => m.DashboardComponent),
    canActivate: [authGuard] 
  },
  {
    path: '',
    redirectTo: 'dashboard',  
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'dashboard'  
  }
];