import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  userEmail = localStorage.getItem('user-email') || '';

  constructor(private router: Router) {}

  logout() {
    localStorage.removeItem('user-email');
    this.router.navigate(['/login']);
  }
}