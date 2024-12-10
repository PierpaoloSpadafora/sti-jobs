import {Component, OnInit} from '@angular/core';
import { LoginService } from './services/login.service';
import {ThemeService} from './services/theme.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  title = 'Frontend';

  constructor(
    private loginService: LoginService,
    private themeService: ThemeService) {
  }

  ngOnInit(): void {
    this.themeService.loadTheme();
}

  isLoggedIn(): boolean {
    return this.loginService.isLoggedIn();
  }
}
