import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {
  Collateral,
  CorporateCustomer,
  DocumentSummary,
  LoanApplication,
  LoanOfficer,
  RepaymentRow
} from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private readonly http: HttpClient) {}

  me() {
    return this.http.get('/api/auth/me');
  }

  getCustomers() {
    return this.http.get<CorporateCustomer[]>('/api/customers/all');
  }

  onboardCustomer(body: Partial<CorporateCustomer>) {
    return this.http.post<CorporateCustomer>('/api/customers/onboard', body);
  }

  getCustomer(id: number) {
    return this.http.get<CorporateCustomer>(`/api/customers/${id}`);
  }

  getOfficers() {
    return this.http.get<LoanOfficer[]>('/api/officers/all');
  }

  registerOfficer(body: Partial<LoanOfficer>) {
    return this.http.post<LoanOfficer>('/api/officers/register', body);
  }

  getLoans() {
    return this.http.get<LoanApplication[]>('/api/loans/all');
  }

  submitLoan(customerId: number, body: object) {
    return this.http.post<LoanApplication>(`/api/loans/submit/${customerId}`, body);
  }

  getLoan(id: number) {
    return this.http.get<LoanApplication>(`/api/loans/${id}`);
  }

  getLoanStatus(id: number) {
    return this.http.get(`/api/loans/status/${id}`, { responseType: 'text' });
  }

  getDocuments(applicationId: number) {
    return this.http.get<DocumentSummary[]>(`/api/documents/application/${applicationId}`);
  }

  uploadDocument(applicationId: number, file: File) {
    const fd = new FormData();
    fd.append('file', file);
    return this.http.post(`/api/documents/upload/${applicationId}`, fd, { responseType: 'text' });
  }

  validateDocument(documentId: number) {
    return this.http.put(`/api/documents/validate/${documentId}`, null, { responseType: 'text' });
  }

  validateAllDocuments(applicationId: number) {
    return this.http.put(`/api/documents/validate-all/${applicationId}`, null, { responseType: 'text' });
  }

  validationReport(applicationId: number) {
    return this.http.get(`/api/documents/validation-report/${applicationId}`, { responseType: 'text' });
  }

  documentStatus(documentId: number) {
    return this.http.get(`/api/documents/validate/${documentId}`, { responseType: 'text' });
  }

  downloadUrl(documentId: number): string {
    return `/api/documents/download/${documentId}`;
  }

  addCollateral(applicationId: number, body: object) {
    return this.http.post<Collateral>(`/api/collateral/add/${applicationId}`, body);
  }

  getCollateral(applicationId: number) {
    return this.http.get<Collateral[]>(`/api/collateral/loan/${applicationId}`);
  }

  evaluateCredit(applicationId: number) {
    return this.http.post(`/api/credit/evaluate/${applicationId}`, null);
  }

  getRiskScore(applicationId: number) {
    return this.http.get(`/api/credit/risk-score/${applicationId}`, { responseType: 'text' });
  }

  approve(applicationId: number, comments?: string) {
    const q = comments ? `?comments=${encodeURIComponent(comments)}` : '';
    return this.http.post(`/api/approvals/approve/${applicationId}${q}`, null, { responseType: 'text' });
  }

  reject(applicationId: number, comments?: string) {
    const q = comments ? `?comments=${encodeURIComponent(comments)}` : '';
    return this.http.post(`/api/approvals/reject/${applicationId}${q}`, null, { responseType: 'text' });
  }

  disbursementReport() {
    return this.http.get('/api/disbursement/report', { responseType: 'text' });
  }

  repaymentSchedule(accountId: number) {
    return this.http.get<RepaymentRow[]>(`/api/repayments/schedule/${accountId}`);
  }

  payInstallment(accountId: number, installmentId: number) {
    return this.http.put(`/api/repayments/account/${accountId}/pay/${installmentId}`, null, { responseType: 'text' });
  }
}
