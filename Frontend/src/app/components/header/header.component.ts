import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
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
