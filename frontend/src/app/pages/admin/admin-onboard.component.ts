import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { LoanOfficer } from '../../core/models';
import { readError } from '../../core/utils';

@Component({
  selector: 'app-admin-onboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-onboard.component.html',
  styleUrls: ['./admin-onboard.component.scss']
})
export class AdminOnboardComponent implements OnInit {
  // Form state
  activeTab = signal<'onboard' | 'list'>('onboard');
  
  // Onboard form
  employeeId = '';
  officerName = '';
  designation = 'Senior Loan Officer';
  email = '';
  password = '';
  passwordConfirm = '';
  
  formError = '';
  formSuccess = '';
  isLoading = false;

  // List state
  officers: LoanOfficer[] = [];
  listLoading = false;
  listError = '';

  constructor(private readonly auth: AuthService) {}

  ngOnInit(): void {
    this.checkAdminAccess();
    this.loadOfficers();
  }

  checkAdminAccess(): void {
    if (!this.auth.isAdmin()) {
      window.location.href = '/dashboard';
    }
  }

  switchTab(tab: 'onboard' | 'list'): void {
    this.activeTab.set(tab);
    this.formError = '';
    this.formSuccess = '';
    if (tab === 'list') {
      this.loadOfficers();
    }
  }

  async loadOfficers(): Promise<void> {
    this.listLoading = true;
    this.listError = '';
    try {
      this.officers = await this.auth.getAllOfficers();
    } catch (e) {
      this.listError = readError(e, 'Failed to load officers.');
    } finally {
      this.listLoading = false;
    }
  }

  async submitOnboard(): Promise<void> {
    this.formError = '';
    this.formSuccess = '';

    // Validation
    if (!this.employeeId.trim()) {
      this.formError = 'Employee ID is required.';
      return;
    }
    if (!this.officerName.trim()) {
      this.formError = 'Officer name is required.';
      return;
    }
    if (!this.designation.trim()) {
      this.formError = 'Designation is required.';
      return;
    }
    if (!this.email.trim()) {
      this.formError = 'Email is required.';
      return;
    }
    if (!this.password) {
      this.formError = 'Password is required.';
      return;
    }
    if (this.password !== this.passwordConfirm) {
      this.formError = 'Passwords do not match.';
      return;
    }
    if (this.password.length < 8) {
      this.formError = 'Password must be at least 8 characters.';
      return;
    }

    this.isLoading = true;
    try {
      // Register officer in backend
      const newOfficer = await this.auth.registerOfficer({
        id: 0,
        employeeId: this.employeeId.trim(),
        name: this.officerName.trim(),
        designation: this.designation.trim()
      });

      this.formSuccess = `✓ Officer "${this.officerName}" (ID: ${newOfficer.id}) registered successfully!`;

      // Reset form
      this.employeeId = '';
      this.officerName = '';
      this.designation = 'Senior Loan Officer';
      this.email = '';
      this.password = '';
      this.passwordConfirm = '';

      // Reload officers list
      await this.loadOfficers();
    } catch (e) {
      this.formError = readError(e, 'Failed to register officer. Please check if Employee ID already exists.');
      console.error('Officer registration error:', e);
    } finally {
      this.isLoading = false;
    }
  }

  getDesignations(): string[] {
    return [
      'Senior Loan Officer',
      'Loan Officer',
      'Junior Loan Officer',
      'Credit Analyst',
      'Loan Manager',
      'Regional Manager'
    ];
  }

  formatDate(date: string | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
