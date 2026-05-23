import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { readError } from '../../core/utils';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';

  constructor(
    private readonly auth: AuthService,
    private readonly router: Router
  ) {
    if (auth.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  async submit(): Promise<void> {
    this.error = '';
    try {
      await this.auth.login(this.email.trim(), this.password);
      await this.router.navigate(['/dashboard']);
    } catch (e) {
      this.error = readError(e, 'Invalid email or password.');
    }
  }
}
