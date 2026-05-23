import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';
import { LayoutComponent } from './layout/layout.component';
import { LoginComponent } from './pages/login/login.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { CustomersComponent } from './pages/customers/customers.component';
import { OfficersComponent } from './pages/officers/officers.component';
import { LoansComponent } from './pages/loans/loans.component';
import { CollateralComponent } from './pages/collateral/collateral.component';
import { DocumentsComponent } from './pages/documents/documents.component';
import { CreditComponent } from './pages/credit/credit.component';
import { ApprovalsComponent } from './pages/approvals/approvals.component';
import { DisbursementsComponent } from './pages/disbursements/disbursements.component';
import { RepaymentsComponent } from './pages/repayments/repayments.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'customers', component: CustomersComponent },
      { path: 'officers', component: OfficersComponent },
      { path: 'loans', component: LoansComponent },
      { path: 'collateral', component: CollateralComponent },
      { path: 'documents', component: DocumentsComponent },
      { path: 'credit', component: CreditComponent },
      { path: 'approvals', component: ApprovalsComponent },
      { path: 'disbursements', component: DisbursementsComponent },
      { path: 'repayments', component: RepaymentsComponent }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
