
import { Routes } from '@angular/router';
import { DefaultLayoutComponent } from './layouts/default-layout.component';
import { authGuard } from './guard.component';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./components/login/login.component')
      .then(m => m.LoginComponent)
  },
  {
    path: '',
    component: DefaultLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./components/dashboard/dashboard.component')
          .then(m => m.DashboardComponent)
      },
     /* {
        path: 'configuration',
        loadComponent: () => import('./components/configuration/configuration.component')
          .then(m => m.ConfigurationComponent)
      },*/
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
