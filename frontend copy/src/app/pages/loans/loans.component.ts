import { NgClass } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { LoanApplication } from '../../core/models';
import { companyName, readError, statusBadgeClass } from '../../core/utils';

@Component({
  selector: 'app-loans',
  standalone: true,
  imports: [FormsModule, NgClass],
  templateUrl: './loans.component.html'
})
export class LoansComponent implements OnInit {
  loans: LoanApplication[] = [];
  customerId: number | null = null;
  loanAmount = '';
  loanType = 'Business';
  lookupId: number | null = null;
  lookupOut = '—';

  statusClass = statusBadgeClass;
  company = companyName;

  constructor(
    private readonly api: ApiService,
    auth: AuthService
  ) {
    if (auth.user()?.corporateCustomerId) {
      this.customerId = auth.user()!.corporateCustomerId!;
    }
  }

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.api.getLoans().subscribe({ next: (d) => (this.loans = d) });
  }

  submit(): void {
    if (!this.customerId) return;
    this.api.submitLoan(this.customerId, {
      loanAmount: this.loanAmount,
      loanType: this.loanType,
      submissionDate: new Date().toISOString().slice(0, 10)
    }).subscribe({
      next: () => {
        this.loanAmount = '';
        this.reload();
      },
      error: (e) => alert(readError(e, 'Submit failed'))
    });
  }

  detail(): void {
    if (!this.lookupId) return;
    this.api.getLoan(this.lookupId).subscribe({
      next: (d) => (this.lookupOut = JSON.stringify(d, null, 2)),
      error: (e) => (this.lookupOut = readError(e, 'Not found'))
    });
  }

  status(): void {
    if (!this.lookupId) return;
    this.api.getLoanStatus(this.lookupId).subscribe({
      next: (t) => (this.lookupOut = t),
      error: (e) => (this.lookupOut = readError(e, 'Not found'))
    });
  }
}
