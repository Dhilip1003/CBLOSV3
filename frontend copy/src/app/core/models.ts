export interface AuthUser {
  email: string;
  role: 'CUSTOMER' | 'OFFICER' | 'MANAGER';
  corporateCustomerId: number | null;
  loanOfficerId: number | null;
}

export interface CorporateCustomer {
  id: number;
  taxId: string;
  companyName: string;
  industryType?: string;
}

export interface LoanOfficer {
  id: number;
  employeeId: string;
  name: string;
  designation?: string;
}

export interface LoanApplication {
  applicationId: number;
  loanType?: string;
  loanAmount?: number;
  status?: string;
  submissionDate?: string;
  customer?: CorporateCustomer;
}

export interface DocumentSummary {
  documentId: number;
  applicationId: number;
  fileName: string;
  fileType?: string;
  documentType?: string;
  uploadDate?: string;
  isValid?: boolean;
}

export interface Collateral {
  id: number;
  collateralType: string;
  estimatedValue: number;
}

export interface RepaymentRow {
  id: number;
  installmentNumber: number;
  dueDate: string;
  installmentAmount: number;
  status: string;
}

export interface ApiErrorBody {
  message: string;
}
