import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  constructor(private router: Router, private loginService: LoginService) { } // Inietta il servizio

  ngOnInit(): void {
  }

  login(): void {
    this.loginService.login('lalla sbarella'); // Usa il servizio per gestire il login
    this.router.navigate(['/home']);
  }
}
