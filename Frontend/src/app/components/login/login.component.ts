import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserControllerService, UserDTO } from '../../generated-api'; 
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [UserControllerService], 
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  isLoading = false;
  error = '';


  constructor(
    private fb: FormBuilder,
    private router: Router,
    private userService: UserControllerService
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.error = '';
      
      const email = this.loginForm.get('email')?.value;
      
      this.userService.getUserByEmail(email).subscribe({
        next: (user) => {
          localStorage.setItem('user-email', email);
          this.router.navigate(['/scheduler']);
          this.isLoading = false;
        },
        error: (error) => {
          if (error.status === 404) {
            this.createUser(email);
          } else {
            this.error = 'Si è verificato un errore durante il login';
            this.isLoading = false;
            console.error('Errore durante il login:', error); 
          }
        }
      });
    }
  }

  private createUser(email: string): void {
    const userDTO: UserDTO = {
      email: email
    };

    this.userService.createUser(userDTO).subscribe({
      next: (createdUser) => {
        localStorage.setItem('user-email', email);
        this.router.navigate(['/scheduler']);
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Si è verificato un errore durante la creazione dell\'utente';
        this.isLoading = false;
        console.error('Error creating user:', error);
      }
    });
  }
}
