import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/auth.service';
import { readError } from '../../core/utils';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  // Tab state
  activeTab = signal<'login' | 'register'>('login');
  
  // Login form
  loginEmail = '';
  loginPassword = '';
  loginError = '';
  loginLoading = false;

  // Register form - Customer
  registerEmail = '';
  registerPassword = '';
  registerPasswordConfirm = '';
  companyName = '';
  taxId = '';
  industryType = 'Technology';
  registerError = '';
  registerSuccess = '';
  registerLoading = false;

  constructor(
    private readonly auth: AuthService,
    private readonly router: Router
  ) {
    if (auth.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  switchTab(tab: 'login' | 'register'): void {
    this.activeTab.set(tab);
    this.loginError = '';
    this.registerError = '';
    this.registerSuccess = '';
  }

  async submitLogin(): Promise<void> {
    this.loginError = '';
    if (!this.loginEmail.trim() || !this.loginPassword) {
      this.loginError = 'Please enter email and password.';
      return;
    }

    this.loginLoading = true;
    try {
      await this.auth.login(this.loginEmail.trim(), this.loginPassword);
      
      // Route based on user role
      const userRole = this.auth.user()?.role;
      if (userRole === 'ADMIN') {
        await this.router.navigate(['/admin/onboard']);
      } else if (userRole === 'CUSTOMER') {
        await this.router.navigate(['/dashboard']);
      } else {
        await this.router.navigate(['/dashboard']);
      }
    } catch (e) {
      this.loginError = readError(e, 'Invalid email or password.');
    } finally {
      this.loginLoading = false;
    }
  }

  async submitRegister(): Promise<void> {
    this.registerError = '';
    this.registerSuccess = '';

    // Validation
    if (!this.registerEmail.trim()) {
      this.registerError = 'Email is required.';
      return;
    }
    if (!this.registerPassword) {
      this.registerError = 'Password is required.';
      return;
    }
    if (this.registerPassword !== this.registerPasswordConfirm) {
      this.registerError = 'Passwords do not match.';
      return;
    }
    if (this.registerPassword.length < 6) {
      this.registerError = 'Password must be at least 6 characters.';
      return;
    }
    if (!this.companyName.trim()) {
      this.registerError = 'Company name is required.';
      return;
    }
    if (!this.taxId.trim()) {
      this.registerError = 'Tax ID is required.';
      return;
    }

    this.registerLoading = true;
    try {
      // Register customer in backend
      await this.auth.registerCustomer({
        id: 0,
        companyName: this.companyName.trim(),
        taxId: this.taxId.trim(),
        industryType: this.industryType
      });

      this.registerSuccess = '✓ Registration successful! You can now login.';
      
      // Reset form
      this.registerEmail = '';
      this.registerPassword = '';
      this.registerPasswordConfirm = '';
      this.companyName = '';
      this.taxId = '';
      this.industryType = 'Technology';

      // Switch to login after 2 seconds
      setTimeout(() => {
        this.switchTab('login');
      }, 2000);
    } catch (e) {
      this.registerError = readError(e, 'Registration failed. Please try again.');
      console.error('Registration error:', e);
    } finally {
      this.registerLoading = false;
    }
  }

  getIndustries(): string[] {
    return [
      'Technology',
      'Manufacturing',
      'Finance',
      'Healthcare',
      'Retail',
      'Consulting',
      'Education',
      'Energy',
      'Real Estate',
      'Transportation',
      'Other'
    ];
  }
}
