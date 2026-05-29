import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { Collateral } from '../../core/models';
import { readError } from '../../core/utils';

@Component({
  selector: 'app-collateral',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './collateral.component.html'
})
export class CollateralComponent {
  applicationId: number | null = null;
  listAppId: number | null = null;
  collateralType = '';
  estimatedValue = '';
  rows: Collateral[] = [];
  message = '';
  error = false;

  constructor(private readonly api: ApiService) {}

  submit(): void {
    if (!this.applicationId) return;
    this.api.addCollateral(this.applicationId, {
      collateralType: this.collateralType.trim(),
      estimatedValue: this.estimatedValue
    }).subscribe({
      next: () => {
        this.message = 'Recorded';
        this.error = false;
        this.listAppId = this.applicationId;
        this.load();
      },
      error: (e) => {
        this.message = readError(e, 'Failed');
        this.error = true;
      }
    });
  }

  load(): void {
    if (!this.listAppId) return;
    this.api.getCollateral(this.listAppId).subscribe({
      next: (d) => (this.rows = d),
      error: (e) => {
        this.message = readError(e, 'Load failed');
        this.error = true;
      }
    });
  }
}
