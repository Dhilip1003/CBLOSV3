import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { LoanOfficer } from '../../core/models';
import { readError } from '../../core/utils';

@Component({
  selector: 'app-officers',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './officers.component.html'
})
export class OfficersComponent implements OnInit {
  officers: LoanOfficer[] = [];
  employeeId = '';
  name = '';
  designation = '';
  message = '';
  error = false;

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.api.getOfficers().subscribe({
      next: (d) => (this.officers = d),
      error: (e) => {
        this.message = readError(e, 'Load failed');
        this.error = true;
      }
    });
  }

  submit(): void {
    this.api.registerOfficer({
      employeeId: this.employeeId.trim(),
      name: this.name.trim(),
      designation: this.designation.trim() || undefined
    }).subscribe({
      next: () => {
        this.message = 'Recorded';
        this.error = false;
        this.employeeId = this.name = this.designation = '';
        this.reload();
      },
      error: (e) => {
        this.message = readError(e, 'Enroll failed');
        this.error = true;
      }
    });
  }
}
