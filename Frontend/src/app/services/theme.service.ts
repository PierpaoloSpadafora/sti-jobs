import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private darkMode = false;

  setDarkMode(isDarkMode: boolean) {
    this.darkMode = isDarkMode;
    if (isDarkMode) {
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.remove('dark-mode');
    }
    localStorage.setItem('dark-mode', isDarkMode ? 'true' : 'false');
  }
  loadTheme() {
    const savedTheme = localStorage.getItem('dark-mode');
    this.setDarkMode(savedTheme === 'true');
  }
  isDarkMode(): boolean {
    return this.darkMode;
  }

  constructor() { }
}
