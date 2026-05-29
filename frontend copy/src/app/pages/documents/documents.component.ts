import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { DocumentSummary } from '../../core/models';
import { readError } from '../../core/utils';

@Component({
  selector: 'app-documents',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './documents.component.html'
})
export class DocumentsComponent {
  applicationId: number | null = null;
  listAppId: number | null = null;
  file: File | null = null;
  docs: DocumentSummary[] = [];
  msg = '';
  report = '—';

  constructor(
    private readonly api: ApiService,
    readonly auth: AuthService
  ) {}

  onFile(e: Event): void {
    this.file = (e.target as HTMLInputElement).files?.[0] ?? null;
  }

  upload(): void {
    if (!this.applicationId || !this.file) {
      this.msg = 'Application and file required.';
      return;
    }
    this.api.uploadDocument(this.applicationId, this.file).subscribe({
      next: (t) => {
        this.msg = t;
        this.listAppId = this.applicationId;
        this.refresh();
      },
      error: (e) => (this.msg = readError(e, 'Upload failed'))
    });
  }

  refresh(): void {
    if (!this.listAppId) return;
    this.api.getDocuments(this.listAppId).subscribe({
      next: (d) => (this.docs = d),
      error: (e) => (this.msg = readError(e, 'List failed'))
    });
  }

  download(id: number): void {
    window.open(this.api.downloadUrl(id), '_blank');
  }

  certify(id: number): void {
    this.api.validateDocument(id).subscribe({
      next: (t) => {
        alert(t);
        this.refresh();
      },
      error: (e) => alert(readError(e, 'Failed'))
    });
  }

  status(id: number): void {
    this.api.documentStatus(id).subscribe({
      next: (t) => alert(t),
      error: (e) => alert(readError(e, 'Failed'))
    });
  }

  certifyAll(): void {
    if (!this.listAppId) return;
    this.api.validateAllDocuments(this.listAppId).subscribe({
      next: (t) => {
        this.report = t;
        this.refresh();
      },
      error: (e) => (this.report = readError(e, 'Failed'))
    });
  }

  loadReport(): void {
    if (!this.listAppId) return;
    this.api.validationReport(this.listAppId).subscribe({
      next: (t) => (this.report = t),
      error: (e) => (this.report = readError(e, 'Failed'))
    });
  }
}
