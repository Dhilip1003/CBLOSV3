# LOAN SYSTEM - COMPLETE API TESTING GUIDE

**Base URL:** `http://localhost:2424`  
**Database:** MySQL (loan_db)  
**Port:** 2424

---

## TABLE OF CONTENTS

1. [Loan Officer APIs](#loan-officer-apis)
2. [Customer APIs](#customer-apis)
3. [Loan Application APIs](#loan-application-apis)
4. [Document APIs](#document-apis)
5. [Collateral APIs](#collateral-apis)
6. [Credit Assessment APIs](#credit-assessment-apis)
7. [Approval APIs](#approval-apis)
8. [Disbursement APIs](#disbursement-apis)
9. [Repayment APIs](#repayment-apis)

---

# LOAN OFFICER APIs

## 1. Register Loan Officer

**Endpoint:** `POST /api/officers/register`

**Request Body:**
```json
{
  "employeeId": "EMP001",
  "name": "John Davis",
  "designation": "Senior Loan Officer"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "employeeId": "EMP001",
  "name": "John Davis",
  "designation": "Senior Loan Officer"
}
```

**cURL:**
```bash
curl -X POST http://localhost:2424/api/officers/register \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "EMP001",
    "name": "John Davis",
    "designation": "Senior Loan Officer"
  }'
```

---

## 2. Get Specific Loan Officer

**Endpoint:** `GET /api/officers/{id}`

**URL Parameter:**
- `id`: Officer ID (integer)

**Response (200 OK):**
```json
{
  "id": 1,
  "employeeId": "EMP001",
  "name": "John Davis",
  "designation": "Senior Loan Officer"
}
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/officers/1
```

---

## 3. Get All Loan Officers

**Endpoint:** `GET /api/officers/all`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "employeeId": "EMP001",
    "name": "John Davis",
    "designation": "Senior Loan Officer"
  },
  {
    "id": 2,
    "employeeId": "EMP002",
    "name": "Sarah Johnson",
    "designation": "Junior Loan Officer"
  }
]
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/officers/all
```

---

# CUSTOMER APIs

## 1. Onboard Corporate Customer

**Endpoint:** `POST /api/customers/onboard`

**Request Body:**
```json
{
  "taxId": "TAX123456789",
  "companyName": "TechCorp Industries",
  "industryType": "Information Technology"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "taxId": "TAX123456789",
  "companyName": "TechCorp Industries",
  "industryType": "Information Technology"
}
```

**cURL:**
```bash
curl -X POST http://localhost:2424/api/customers/onboard \
  -H "Content-Type: application/json" \
  -d '{
    "taxId": "TAX123456789",
    "companyName": "TechCorp Industries",
    "industryType": "Information Technology"
  }'
```

---

## 2. Get Specific Customer

**Endpoint:** `GET /api/customers/{id}`

**URL Parameter:**
- `id`: Customer ID (integer)

**Response (200 OK):**
```json
{
  "id": 1,
  "taxId": "TAX123456789",
  "companyName": "TechCorp Industries",
  "industryType": "Information Technology"
}
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/customers/1
```

---

## 3. Get All Customers

**Endpoint:** `GET /api/customers/all`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "taxId": "TAX123456789",
    "companyName": "TechCorp Industries",
    "industryType": "Information Technology"
  },
  {
    "id": 2,
    "taxId": "TAX987654321",
    "companyName": "ManufactureCo Ltd.",
    "industryType": "Manufacturing"
  }
]
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/customers/all
```

---

# LOAN APPLICATION APIs

## 1. Submit Loan Application

**Endpoint:** `POST /api/loans/submit/{customerId}`

**URL Parameter:**
- `customerId`: Customer ID (integer)

**Request Body:**
```json
{
  "loanType": "Business Expansion",
  "loanAmount": 500000,
  "status": "Submitted",
  "submissionDate": "2026-05-15"
}
```

**Response (200 OK):**
```json
{
  "applicationId": 1,
  "customer": {
    "id": 1,
    "taxId": "TAX123456789",
    "companyName": "TechCorp Industries",
    "industryType": "Information Technology"
  },
  "loanOfficer": null,
  "loanType": "Business Expansion",
  "loanAmount": 500000,
  "status": "Submitted",
  "submissionDate": "2026-05-15"
}
```

**cURL:**
```bash
curl -X POST http://localhost:2424/api/loans/submit/1 \
  -H "Content-Type: application/json" \
  -d '{
    "loanType": "Business Expansion",
    "loanAmount": 500000,
    "status": "Submitted",
    "submissionDate": "2026-05-15"
  }'
```

---

## 2. Get Specific Loan Application Details

**Endpoint:** `GET /api/loans/{applicationId}`

**URL Parameter:**
- `applicationId`: Application ID (integer)

**Response (200 OK):**
```json
{
  "applicationId": 1,
  "customer": {
    "id": 1,
    "taxId": "TAX123456789",
    "companyName": "TechCorp Industries",
    "industryType": "Information Technology"
  },
  "loanOfficer": null,
  "loanType": "Business Expansion",
  "loanAmount": 500000,
  "status": "Submitted",
  "submissionDate": "2026-05-15"
}
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/loans/1
```

---

## 3. Check Application Status

**Endpoint:** `GET /api/loans/status/{applicationId}`

**URL Parameter:**
- `applicationId`: Application ID (integer)

**Response (200 OK):**
```
"Submitted"
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/loans/status/1
```

---

## 4. Get All Loan Applications

**Endpoint:** `GET /api/loans/all`

**Response (200 OK):**
```json
[
  {
    "applicationId": 1,
    "customer": {...},
    "loanOfficer": null,
    "loanType": "Business Expansion",
    "loanAmount": 500000,
    "status": "Submitted",
    "submissionDate": "2026-05-15"
  },
  {
    "applicationId": 2,
    "customer": {...},
    "loanOfficer": null,
    "loanType": "Working Capital",
    "loanAmount": 250000,
    "status": "Submitted",
    "submissionDate": "2026-05-15"
  }
]
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/loans/all
```

---

# DOCUMENT APIs

## 1. Upload Document

**Endpoint:** `POST /api/documents/upload/{applicationId}`

**URL Parameter:**
- `applicationId`: Application ID (integer)

**Request Body:** Multipart Form Data
- `file`: Binary file (PDF, PNG, JPG, etc.)

**Response (200 OK):**
```
"Document uploaded successfully: FinancialStatement.pdf"
```

**cURL:**
```bash
curl -X POST http://localhost:2424/api/documents/upload/1 \
  -F "file=@/path/to/FinancialStatement.pdf"
```

**Postman:**
- Method: POST
- URL: http://localhost:2424/api/documents/upload/1
- Body: form-data
- Key: file | Value: [Select file]

---

## 2. Download Document

**Endpoint:** `GET /api/documents/download/{documentId}`

**URL Parameter:**
- `documentId`: Document ID (integer)

**Response (200 OK):**
- Binary file content with appropriate MIME type

**Response Header:**
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="FinancialStatement.pdf"
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/documents/download/1 \
  -o FinancialStatement.pdf
```

---

## 3. List All Documents for Application

**Endpoint:** `GET /api/documents/application/{applicationId}`

**URL Parameter:**
- `applicationId`: Application ID (integer)

**Response (200 OK):**
```json
[
  {
    "documentId": 1,
    "fileName": "FinancialStatement.pdf",
    "fileType": "application/pdf",
    "loanApplication": {...},
    "uploadDate": "2026-05-15T10:30:00",
    "isValid": false
  },
  {
    "documentId": 2,
    "fileName": "BalanceSheet.png",
    "fileType": "image/png",
    "loanApplication": {...},
    "uploadDate": "2026-05-15T10:35:00",
    "isValid": false
  }
]
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/documents/application/1
```

---

## 4. Validate Specific Document

**Endpoint:** `PUT /api/documents/validate/{documentId}`

**URL Parameter:**
- `documentId`: Document ID (integer)

**Response (200 OK):**
```
"Document validated successfully. Document ID: 1"
```

**Response (400 Bad Request):**
```
"Document validation failed. Invalid or empty file."
```

**cURL:**
```bash
curl -X PUT http://localhost:2424/api/documents/validate/1
```

---

## 5. Get Document Validation Status

**Endpoint:** `GET /api/documents/validate/{documentId}`

**URL Parameter:**
- `documentId`: Document ID (integer)

**Response (200 OK):**
```
"Document ID: 1 | Status: Valid | File: FinancialStatement.pdf"
```

**Or if not validated:**
```
"Document ID: 1 | Status: Not Validated | File: FinancialStatement.pdf"
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/documents/validate/1
```

---

## 6. Validate All Documents for Application

**Endpoint:** `PUT /api/documents/validate-all/{applicationId}`

**URL Parameter:**
- `applicationId`: Application ID (integer)

**Response (200 OK):**
```
"Validated 3 out of 3 documents for Application ID: 1"
```

**Or if no documents:**
```
"No documents found for Application ID: 1"
```

**cURL:**
```bash
curl -X PUT http://localhost:2424/api/documents/validate-all/1
```

---

## 7. Get Validation Report for Application

**Endpoint:** `GET /api/documents/validation-report/{applicationId}`

**URL Parameter:**
- `applicationId`: Application ID (integer)

**Response (200 OK):**
```
=== DOCUMENT VALIDATION REPORT ===
Application ID: 1
Total Documents: 2

Document ID: 1
  File Name: FinancialStatement.pdf
  File Type: application/pdf
  Upload Date: 2026-05-15T10:30:00
  Status: VALID
  File Size: 245678 bytes

Document ID: 2
  File Name: BalanceSheet.png
  File Type: image/png
  Upload Date: 2026-05-15T10:35:00
  Status: VALID
  File Size: 125432 bytes

===================================
Valid Documents: 2
Invalid/Unvalidated Documents: 0
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/documents/validation-report/1
```

---

# COLLATERAL APIs

## 1. Add Collateral to Application

**Endpoint:** `POST /api/collateral/add/{applicationId}`

**URL Parameter:**
- `applicationId`: Application ID (integer)

**Request Body:**
```json
{
  "collateralType": "Real Estate",
  "estimatedValue": 750000
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "loanApplication": {...},
  "collateralType": "Real Estate",
  "estimatedValue": 750000
}
```

**cURL:**
```bash
curl -X POST http://localhost:2424/api/collateral/add/1 \
  -H "Content-Type: application/json" \
  -d '{
    "collateralType": "Real Estate",
    "estimatedValue": 750000
  }'
```

---

## 2. View All Collateral for Application

**Endpoint:** `GET /api/collateral/loan/{applicationId}`

**URL Parameter:**
- `applicationId`: Application ID (integer)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "loanApplication": {...},
    "collateralType": "Real Estate",
    "estimatedValue": 750000
  },
  {
    "id": 2,
    "loanApplication": {...},
    "collateralType": "Equipment",
    "estimatedValue": 150000
  }
]
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/collateral/loan/1
```

---

# CREDIT ASSESSMENT APIs

## 1. Evaluate Credit for Application

**Endpoint:** `POST /api/credit/evaluate/{applicationId}`

**URL Parameter:**
- `applicationId`: Application ID (integer)

**Response (200 OK):**
```json
{
  "id": 1,
  "loanApplication": {...},
  "riskScore": 35.5,
  "creditScore": 750,
  "assessmentDate": "2026-05-15"
}
```

**cURL:**
```bash
curl -X POST http://localhost:2424/api/credit/evaluate/1
```

---

## 2. Get Risk Score for Application

**Endpoint:** `GET /api/credit/risk-score/{applicationId}`

**URL Parameter:**
- `applicationId`: Application ID (integer)

**Response (200 OK):**
```
35.5
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/credit/risk-score/1
```

---

# APPROVAL APIs

## 1. Approve Loan Application

**Endpoint:** `POST /api/approvals/approve/{applicationId}`

**URL Parameters:**
- `applicationId`: Application ID (integer)
- `comments` (optional): Approval comments

**Query Parameter (Optional):**
```
comments=Verified financial documents and collateral
```

**Response (200 OK):**
```
"Loan approved successfully. Loan Account and Disbursement Schedule have been generated."
```

**cURL:**
```bash
curl -X POST "http://localhost:2424/api/approvals/approve/1?comments=Verified%20financial%20documents"
```

**cURL (without comments):**
```bash
curl -X POST http://localhost:2424/api/approvals/approve/1
```

---

## 2. Reject Loan Application

**Endpoint:** `POST /api/approvals/reject/{applicationId}`

**URL Parameters:**
- `applicationId`: Application ID (integer)
- `comments` (optional): Rejection reason

**Query Parameter (Optional):**
```
comments=Insufficient collateral value
```

**Response (200 OK):**
```
"Loan application has been rejected."
```

**cURL:**
```bash
curl -X POST "http://localhost:2424/api/approvals/reject/1?comments=Insufficient%20collateral"
```

**cURL (without comments):**
```bash
curl -X POST http://localhost:2424/api/approvals/reject/1
```

---

# DISBURSEMENT APIs

## 1. Get Disbursement Report

**Endpoint:** `GET /api/disbursement/report`

**Response (200 OK):**
```
=== DISBURSEMENT REPORT ===
Total Disbursements: 2

Disbursement ID: 1
Account ID: 1
Amount: 500000.00
Date: 2026-05-15
Reference: REF-2026-0001

Disbursement ID: 2
Account ID: 2
Amount: 250000.00
Date: 2026-05-15
Reference: REF-2026-0002

Total Amount Disbursed: 750000.00
===========================
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/disbursement/report
```

---

# REPAYMENT APIs

## 1. View Repayment Schedule for Account

**Endpoint:** `GET /api/repayments/schedule/{accountId}`

**URL Parameter:**
- `accountId`: Loan Account ID (integer)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "loanAccount": {...},
    "installmentNumber": 1,
    "dueDate": "2026-06-15",
    "installmentAmount": 12500.00,
    "principalComponent": 10000.00,
    "interestComponent": 2500.00,
    "status": "Pending"
  },
  {
    "id": 2,
    "loanAccount": {...},
    "installmentNumber": 2,
    "dueDate": "2026-07-15",
    "installmentAmount": 12500.00,
    "principalComponent": 10100.00,
    "interestComponent": 2400.00,
    "status": "Pending"
  }
]
```

**cURL:**
```bash
curl -X GET http://localhost:2424/api/repayments/schedule/1
```

---

## 2. Record Payment for Installment

**Endpoint:** `PUT /api/repayments/account/{accountId}/pay/{installmentId}`

**URL Parameters:**
- `accountId`: Loan Account ID (integer)
- `installmentId`: Installment ID (integer)

**Response (200 OK):**
```
"Payment successful for Account ID 1!"
```

**cURL:**
```bash
curl -X PUT http://localhost:2424/api/repayments/account/1/pay/1
```

---

# QUICK REFERENCE - ALL ENDPOINTS

## Officer Management
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/officers/register` | Register officer |
| GET | `/api/officers/{id}` | Get officer |
| GET | `/api/officers/all` | Get all officers |

## Customer Management
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/customers/onboard` | Onboard customer |
| GET | `/api/customers/{id}` | Get customer |
| GET | `/api/customers/all` | Get all customers |

## Loan Applications
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/loans/submit/{customerId}` | Submit application |
| GET | `/api/loans/{applicationId}` | Get application |
| GET | `/api/loans/status/{applicationId}` | Get status |
| GET | `/api/loans/all` | Get all applications |

## Document Management
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/documents/upload/{applicationId}` | Upload document |
| GET | `/api/documents/download/{documentId}` | Download document |
| GET | `/api/documents/application/{applicationId}` | List documents |
| PUT | `/api/documents/validate/{documentId}` | Validate document |
| GET | `/api/documents/validate/{documentId}` | Get validation status |
| PUT | `/api/documents/validate-all/{applicationId}` | Validate all documents |
| GET | `/api/documents/validation-report/{applicationId}` | Get validation report |

## Collateral Management
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/collateral/add/{applicationId}` | Add collateral |
| GET | `/api/collateral/loan/{applicationId}` | Get collateral |

## Credit Assessment
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/credit/evaluate/{applicationId}` | Evaluate credit |
| GET | `/api/credit/risk-score/{applicationId}` | Get risk score |

## Approval
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/approvals/approve/{applicationId}` | Approve loan |
| POST | `/api/approvals/reject/{applicationId}` | Reject loan |

## Disbursement
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/disbursement/report` | Get report |

## Repayment
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/repayments/schedule/{accountId}` | Get schedule |
| PUT | `/api/repayments/account/{accountId}/pay/{installmentId}` | Record payment |

---

# TESTING WORKFLOW

## Phase 1: Setup
```
1. POST /api/officers/register
2. POST /api/customers/onboard
```

## Phase 2: Application & Documents
```
3. POST /api/loans/submit/{customerId}
4. POST /api/documents/upload/{applicationId}
5. PUT /api/documents/validate/{documentId}
```

## Phase 3: Collateral & Assessment
```
6. POST /api/collateral/add/{applicationId}
7. POST /api/credit/evaluate/{applicationId}
```

## Phase 4: Approval & Disbursement
```
8. POST /api/approvals/approve/{applicationId}
9. GET /api/disbursement/report
```

## Phase 5: Repayment
```
10. GET /api/repayments/schedule/{accountId}
11. PUT /api/repayments/account/{accountId}/pay/{installmentId}
```

---

**Last Updated:** May 15, 2026  
**Total Endpoints:** 24 APIs
