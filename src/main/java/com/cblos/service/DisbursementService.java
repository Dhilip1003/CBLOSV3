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
        BigDecimal principalPerMonth = account.getPrincipalAmount().divide(new BigDecimal(months), 2, RoundingMode.HALF_UP);
        
        for(int i = 1; i <= months; i++) {
            RepaymentSchedule schedule = new RepaymentSchedule();
            schedule.setLoanAccount(account);
            schedule.setInstallmentNumber(i);
            schedule.setDueDate(LocalDate.now().plusMonths(i));
            schedule.setPrincipalComponent(principalPerMonth);
            // Simplified logic: Total amount is just principal for this example
            schedule.setInstallmentAmount(principalPerMonth); 
            schedule.setStatus("Pending");
            
            scheduleRepository.save(schedule);
        }
    }

    public String generateDisbursementReport() {
        List<Disbursement> allDisbursements = disbursementRepository.findAll();
        StringBuilder report = new StringBuilder("--- Disbursement Compliance Report ---\n");
        for (Disbursement d : allDisbursements) {
            report.append("ID: ").append(d.getId())
                  .append(" | AccountDbId: ").append(d.getLoanAccount().getId())
                  .append(" | Account: ").append(d.getLoanAccount().getAccountNumber())
                  .append(" | Amount: ").append(d.getDisbursedAmount())
                  .append(" | Date: ").append(d.getDisbursementDate()).append("\n");
        }
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