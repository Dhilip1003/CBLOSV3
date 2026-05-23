import { NgClass } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { LoanApplication } from '../../core/models';
import { companyName, readError, statusBadgeClass } from '../../core/utils';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [FormsModule, RouterLink, NgClass],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  loans: LoanApplication[] = [];
  customerId: number | null = null;
  loanAmount = '';
  loanType = 'Business';
  selectedId: number | null = null;
  riskScore = '—';
  creditScore = '—';
  docStatus = '—';
  log = 'Select an application from the pipeline.';
  report = '';
  showProcess = false;
  docFile: File | null = null;

  constructor(
    private readonly api: ApiService,
    readonly auth: AuthService
  ) {}

  ngOnInit(): void {
    const u = this.auth.user();
    if (u?.corporateCustomerId) {
      this.customerId = u.corporateCustomerId;
    }
    this.auth.refreshUser().then(() => {
      const refreshed = this.auth.user();
      if (refreshed?.corporateCustomerId) {
        this.customerId = refreshed.corporateCustomerId;
      }
      this.loadLoans();
    }).catch(() => this.loadLoans());
  }

  statusClass = statusBadgeClass;
  company = companyName;

  loadLoans(): void {
    this.api.getLoans().subscribe({
      next: (data) => (this.loans = data),
      error: (e) => (this.log = readError(e, 'Unable to load pipeline.'))
    });
  }

  submitLoan(): void {
    if (!this.customerId) {
      this.log = 'Enter client reference.';
      return;
    }
    const body = {
      loanAmount: this.loanAmount,
      loanType: this.loanType,
      submissionDate: new Date().toISOString().slice(0, 10)
    };
    this.api.submitLoan(this.customerId, body).subscribe({
      next: () => {
        this.log = 'Application recorded.';
        this.loanAmount = '';
        this.loadLoans();
      },
      error: (e) => (this.log = readError(e, 'Submit failed.'))
    });
  }

  open(id: number): void {
    this.selectedId = id;
    this.showProcess = true;
    this.report = '';
    this.riskScore = '—';
    this.creditScore = '—';
    this.docStatus = '—';
    this.log = 'Synchronizing…';
    this.api.getRiskScore(id).subscribe({
      next: (t) => (this.riskScore = t),
      error: () => (this.riskScore = '—')
    });
    this.api.getDocuments(id).subscribe({
      next: (docs) => {
        if (!docs.length) {
          this.docStatus = 'None';
        } else {
          this.docStatus = docs.every((d) => d.isValid) ? 'Clear' : 'Review';
        }
        this.log = 'Ready for action.';
      },
      error: () => {
        this.docStatus = 'Error';
        this.log = 'Ready for action.';
      }
    });
  }

  runCredit(): void {
    if (!this.selectedId) return;
    this.api.evaluateCredit(this.selectedId).subscribe({
      next: (data) => {
        const row = data as { riskScore?: number; creditScore?: number };
        this.log = 'Assessment stored.';
        this.riskScore = String(row.riskScore ?? '—');
        this.creditScore = String(row.creditScore ?? '—');
      },
      error: (e) => (this.log = readError(e, 'Credit failed.'))
    });
  }

  onFile(e: Event): void {
    const input = e.target as HTMLInputElement;
    this.docFile = input.files?.[0] ?? null;
  }

  uploadDoc(): void {
    if (!this.selectedId || !this.docFile) {
      this.log = 'Select a file.';
      return;
    }
    this.api.uploadDocument(this.selectedId, this.docFile).subscribe({
      next: (msg) => {
        this.log = msg;
        this.open(this.selectedId!);
      },
      error: (e) => (this.log = readError(e, 'Upload failed.'))
    });
  }

  validateAll(): void {
    if (!this.selectedId) return;
    this.api.validateAllDocuments(this.selectedId).subscribe({
      next: (t) => {
        this.log = t;
        this.open(this.selectedId!);
      },
      error: (e) => (this.log = readError(e, 'Validate failed.'))
    });
  }

  approve(): void {
    if (!this.selectedId) return;
    this.api.approve(this.selectedId).subscribe({
      next: (t) => {
        this.log = t;
        this.loadLoans();
      },
      error: (e) => (this.log = readError(e, 'Approve failed.'))
    });
  }

  reject(): void {
    if (!this.selectedId) return;
    this.api.reject(this.selectedId).subscribe({
      next: (t) => {
        this.log = t;
        this.loadLoans();
      },
      error: (e) => (this.log = readError(e, 'Reject failed.'))
    });
  }

  loadReport(): void {
    this.api.disbursementReport().subscribe({
      next: (t) => {
        this.report = t;
        this.log = 'Ledger excerpt loaded below.';
      },
      error: (e) => (this.log = readError(e, 'Report failed.'))
    });
  }
}
