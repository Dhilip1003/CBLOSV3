import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthUser } from './models';

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
}
