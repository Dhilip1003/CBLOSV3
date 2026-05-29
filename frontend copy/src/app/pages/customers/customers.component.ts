import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { CorporateCustomer } from '../../core/models';
import { readError } from '../../core/utils';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './customers.component.html'
})
export class CustomersComponent implements OnInit {
  customers: CorporateCustomer[] = [];
  taxId = '';
  companyName = '';
  industryType = '';
  message = '';
  error = false;

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.api.getCustomers().subscribe({
      next: (d) => (this.customers = d),
      error: (e) => {
        this.message = readError(e, 'Load failed');
        this.error = true;
      }
    });
  }

  submit(): void {
    this.message = '';
    this.api.onboardCustomer({
      taxId: this.taxId.trim(),
      companyName: this.companyName.trim(),
      industryType: this.industryType.trim() || undefined
    }).subscribe({
      next: (c) => {
        this.message = 'Client reference: ' + c.id;
        this.error = false;
        this.taxId = this.companyName = this.industryType = '';
        this.reload();
      },
      error: (e) => {
        this.message = readError(e, 'Register failed');
        this.error = true;
      }
    });
  }

  copyId(id: number): void {
    navigator.clipboard.writeText(String(id));
  }
}
