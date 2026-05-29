package com.cblos.service;

import com.cblos.model.Disbursement;
import com.cblos.model.LoanAccount;
import com.cblos.model.RepaymentSchedule;
import com.cblos.repository.DisbursementRepository;
import com.cblos.repository.RepaymentScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DisbursementService {

    @Autowired
    private DisbursementRepository disbursementRepository;
    
    @Autowired
    private RepaymentScheduleRepository scheduleRepository;

    public Disbursement scheduleDisbursement(LoanAccount account) {
        Disbursement disbursement = new Disbursement();
        disbursement.setLoanAccount(account); // Now links to Account
        disbursement.setDisbursedAmount(account.getPrincipalAmount());
        disbursement.setDisbursementDate(LocalDate.now());
        disbursement.setReferenceNumber("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        Disbursement savedDisbursement = disbursementRepository.save(disbursement);
        
        // Logical Generation of 12-month Repayment Schedule in the Database
        generateRepaymentSchedule(account, 12);
        
        return savedDisbursement;
    }
    
    private void generateRepaymentSchedule(LoanAccount account, int months) {
        // Calculate base monthly principal distribution
        BigDecimal totalPrincipal = account.getPrincipalAmount();
        BigDecimal principalPerMonth = totalPrincipal.divide(new BigDecimal(months), 2, RoundingMode.HALF_UP);
        
        // Calculate monthly interest fraction based on our dynamic account rate
        // Monthly Interest = (Principal * Annual Rate Percentage) / 12 Months
        double annualRate = account.getInterestRate(); // e.g., 12.0
        BigDecimal annualRatePercentage = BigDecimal.valueOf(annualRate).divide(BigDecimal.valueOf(100));
        BigDecimal totalAnnualInterest = totalPrincipal.multiply(annualRatePercentage);
        BigDecimal interestPerMonth = totalAnnualInterest.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        // Calculate aggregate monthly payment due
        BigDecimal totalInstallmentAmount = principalPerMonth.add(interestPerMonth);

        for (int i = 1; i <= months; i++) {
            RepaymentSchedule schedule = new RepaymentSchedule();
            schedule.setLoanAccount(account);
            schedule.setInstallmentNumber(i);
            schedule.setDueDate(LocalDate.now().plusMonths(i));
            
            // Map our mathematically sound components to your exact model setters
            schedule.setPrincipalComponent(principalPerMonth);
            schedule.setInterestComponent(interestPerMonth);
            schedule.setInstallmentAmount(totalInstallmentAmount); // ◄── Combined bill
            schedule.setStatus("Pending");
            
            scheduleRepository.save(schedule);
        }
        System.out.println("🗓️ Schedule Generated: " + months + " months initialized with interest component: ₹" + interestPerMonth);
    }

    /**
     * 📊 GET /api/disbursement/report
     * Generates a clean, text-based compliance report for bank auditors.
     */
    public String generateDisbursementReport() {
        // 1. Fetch all financial release rows from the database table
        List<Disbursement> allDisbursements = disbursementRepository.findAll();
        
        if (allDisbursements.isEmpty()) {
            return "--- Disbursement Compliance Report ---\nNo corporate disbursement records found in the core ledger.";
        }

        StringBuilder report = new StringBuilder();
        report.append("================================================================================\n");
        report.append("🏛️ TRUSTEDGE COMMERCIAL BANK - DISBURSEMENT AUDIT REPORT\n");
        report.append("Generated On: ").append(LocalDate.now()).append("\n");
        report.append("================================================================================\n\n");

        // 2. Loop through every payment record and format it into a scannable structure
        for (Disbursement d : allDisbursements) {
            report.append("📍 TRANSACTION RECORD [REF ID: ").append(d.getReferenceNumber()).append("]\n")
                  .append("  • Disbursement Database Row ID : ").append(d.getId()).append("\n")
                  .append("  • Associated Account Asset ID  : ").append(d.getLoanAccount().getId()).append("\n")
                  .append("  • Core Checking Account Number : ").append(d.getLoanAccount().getAccountNumber()).append("\n")
                  .append("  • Funded Corporate Client Name : ").append(d.getLoanAccount().getCustomer().getCompanyName()).append("\n")
                  .append("  • Capital Released to Client   : ₹").append(d.getDisbursedAmount()).append("\n")
                  .append("  • Settlement Timestamp Value   : ").append(d.getDisbursementDate()).append("\n")
                  .append(" ───────────────────────────────────────────────────────────────────────────────\n");
        }
        
        report.append("\n================================================================================\n");
        report.append("🛡️ END OF COMPLIANCE LEDGER AUDIT STRINGS\n");
        report.append("================================================================================\n");

        return report.toString();
    }
    
 // Add this to your DisbursementService
    public List<RepaymentSchedule> getRepaymentSchedule(Integer accountId) {
        return scheduleRepository.findByLoanAccount_Id(accountId);
    }

    public void updateInstallmentStatus(Integer installmentId, String status) {
        RepaymentSchedule schedule = scheduleRepository.findById(installmentId)
                .orElseThrow(() -> new RuntimeException("Installment not found"));
        schedule.setStatus(status);
        scheduleRepository.save(schedule);
    }
    
    public void updateInstallmentStatusSecurely(Integer accountId, Integer installmentId, String status) {
        // 1. Fetch the installment
        RepaymentSchedule schedule = scheduleRepository.findById(installmentId)
                .orElseThrow(() -> new RuntimeException("Installment not found"));
        
        // 2. SECURITY CHECK: Does this installment actually belong to the provided account?
        if (!schedule.getLoanAccount().getId().equals(accountId)) {
            throw new RuntimeException("Security Error: This installment does not belong to Account ID " + accountId);
        }
        
        // 3. Update if everything is correct
        schedule.setStatus(status);
        scheduleRepository.save(schedule);
    }
}