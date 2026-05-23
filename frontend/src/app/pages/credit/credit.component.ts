import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { readError } from '../../core/utils';

@Component({
  selector: 'app-credit',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './credit.component.html'
})
export class CreditComponent {
  applicationId: number | null = null;
  output = '—';

  constructor(private readonly api: ApiService) {}

  evaluate(): void {
    if (!this.applicationId) return;
    this.api.evaluateCredit(this.applicationId).subscribe({
      next: (d) => (this.output = JSON.stringify(d, null, 2)),
      error: (e) => (this.output = readError(e, 'Failed'))
    });
  }

  risk(): void {
    if (!this.applicationId) return;
    this.api.getRiskScore(this.applicationId).subscribe({
      next: (t) => (this.output = t),
      error: (e) => (this.output = readError(e, 'Failed'))
    });
  }
}
