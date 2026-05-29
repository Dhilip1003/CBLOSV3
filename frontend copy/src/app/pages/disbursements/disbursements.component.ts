import { Component } from '@angular/core';
import { ApiService } from '../../core/api.service';
import { readError } from '../../core/utils';

@Component({
  selector: 'app-disbursements',
  standalone: true,
  template: `
    <h1 class="page-title">Funding control</h1>
    <p class="page-sub">Disbursement ledger and nostro trace.</p>
    <div class="cblos-card card p-4">
      <h2 class="h6 mb-3">Ledger extract</h2>
      <button type="button" class="btn btn-accent btn-sm" (click)="load()">Refresh</button>
      <pre class="pre-report p-3 mt-3 rounded">{{ output }}</pre>
    </div>
  `
})
export class DisbursementsComponent {
  output = '—';
  constructor(private readonly api: ApiService) {}
  load(): void {
    this.api.disbursementReport().subscribe({
      next: (t) => (this.output = t),
      error: (e) => (this.output = readError(e, 'Failed'))
    });
  }
}
