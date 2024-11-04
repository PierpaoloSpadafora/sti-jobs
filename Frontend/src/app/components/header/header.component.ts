import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  userEmail = '';

  constructor(private router: Router, private loginService: LoginService) {
    this.userEmail = localStorage.getItem('user-email') || '';
  }

  logout() {
    this.loginService.logout();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return this.loginService.isLoggedIn();
  }
}
