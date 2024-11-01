import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  login(): void {
    localStorage.setItem('user-email', 'lalla sbarella');
    this.router.navigate(['/home']);
  }
}
