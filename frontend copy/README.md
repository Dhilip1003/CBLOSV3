# CBLOS Angular UI

Angular 19 + Bootstrap 5 front end for the Corporate Banking Loan Origination System.

## Prerequisites

- Node.js 18+
- Spring Boot API running on `http://localhost:2424`

## Install

```bash
cd frontend
npm install
```

## Development (Angular dev server + API proxy)

```bash
npm start
```

Open `http://localhost:4200` — API calls proxy to port 2424.

## Production build (copy into Spring Boot `static` folder)

```bash
npm run build:spring
```

Then start the Spring Boot app and open `http://localhost:2424`.

## Demo login

| Email | Password | Role |
|-------|----------|------|
| customer@demo.com | password | CUSTOMER |
| officer@bank.com | password | OFFICER |
| manager@bank.com | password | MANAGER |
