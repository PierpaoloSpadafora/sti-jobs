import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor() { }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('user-email');
  }

  login(email: string): void {
    localStorage.setItem('user-email', email);
  }

  logout(): void {
    localStorage.removeItem('user-email');
  }
}
