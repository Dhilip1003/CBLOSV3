import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthUser, CorporateCustomer, LoanOfficer } from './models';

const STORAGE_KEY = 'cblosAuth';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly user = signal<AuthUser | null>(null);

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  get authHeader(): string | null {
    return localStorage.getItem(STORAGE_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.authHeader;
  }

  async login(email: string, password: string): Promise<void> {
    const token = 'Basic ' + btoa(`${email}:${password}`);
    localStorage.setItem(STORAGE_KEY, token);
    await this.refreshUser();
  }

  async refreshUser(): Promise<void> {
    const me = await firstValueFrom(this.http.get<AuthUser>('/api/auth/me'));
    this.user.set(me);
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.user.set(null);
    this.router.navigate(['/login']);
  }

  isBankStaff(): boolean {
    const role = this.user()?.role;
    return role === 'OFFICER' || role === 'MANAGER';
  }

  isCustomer(): boolean {
    return this.user()?.role === 'CUSTOMER';
  }

  isAdmin(): boolean {
    return this.user()?.role === 'ADMIN';
  }

  isManager(): boolean {
    return this.user()?.role === 'MANAGER';
  }

  // Register new customer
  async registerCustomer(customer: CorporateCustomer): Promise<CorporateCustomer> {
    return await firstValueFrom(
      this.http.post<CorporateCustomer>('/api/customers/onboard', customer)
    );
  }

  // Register new loan officer (Admin only)
  async registerOfficer(officer: LoanOfficer): Promise<LoanOfficer> {
    return await firstValueFrom(
      this.http.post<LoanOfficer>('/api/officers/register', officer)
    );
  }

  // Get all officers (Admin/Manager only)
  async getAllOfficers(): Promise<LoanOfficer[]> {
    return await firstValueFrom(
      this.http.get<LoanOfficer[]>('/api/officers/all')
    );
  }

  // Create app user account for new officer
  async createAdminUser(email: string, role: string): Promise<any> {
    return await firstValueFrom(
      this.http.post('/api/admin/create-user', { email, role })
    );
  }
}
