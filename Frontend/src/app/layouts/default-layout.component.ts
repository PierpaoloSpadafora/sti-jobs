import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../components/header/header.component';
import { SidebarComponent } from '../components/sidebar/sidebar.component';

@Component({
  selector: 'app-default-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, SidebarComponent],
  template: `
    <app-header></app-header>
    <div class="layout-container">
      <app-sidebar></app-sidebar>
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .layout-container {
      display: flex;
      height: calc(100vh - 60px); /* Sottrai l'altezza dell'header */
    }
    
    .main-content {
      flex: 1;
      overflow-y: auto;
      padding: 20px;
      margin-left: 280px; /* Larghezza della sidebar */
    }
  `]
})
export class DefaultLayoutComponent {}
