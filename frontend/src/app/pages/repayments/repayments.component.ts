import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { RepaymentRow } from '../../core/models';
import { readError } from '../../core/utils';

@Component({
  selector: 'app-repayments',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './repayments.component.html'
})
export class RepaymentsComponent {
  accountId: number | null = null;
  rows: RepaymentRow[] = [];
  log = '—';

  constructor(private readonly api: ApiService) {}

  load(): void {
    if (!this.accountId) return;
    this.api.repaymentSchedule(this.accountId).subscribe({
      next: (d) => (this.rows = d),
      error: (e) => (this.log = readError(e, 'Failed'))
    });
  }

  pay(row: RepaymentRow): void {
    if (!this.accountId || !row.id) return;
    this.api.payInstallment(this.accountId, row.id).subscribe({
      next: (t) => {
        this.log = t;
        this.load();
      },
      error: (e) => (this.log = readError(e, 'Failed'))
    });
  }
}
