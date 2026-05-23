import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { readError } from '../../core/utils';

@Component({
  selector: 'app-approvals',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './approvals.component.html'
})
export class ApprovalsComponent {
  applicationId: number | null = null;
  comments = '';
  output = '—';

  constructor(private readonly api: ApiService) {}

  approve(): void {
    if (!this.applicationId) return;
    this.api.approve(this.applicationId, this.comments.trim() || undefined).subscribe({
      next: (t) => (this.output = t),
      error: (e) => (this.output = readError(e, 'Failed'))
    });
  }

  reject(): void {
    if (!this.applicationId) return;
    this.api.reject(this.applicationId, this.comments.trim() || undefined).subscribe({
      next: (t) => (this.output = t),
      error: (e) => (this.output = readError(e, 'Failed'))
    });
  }
}
