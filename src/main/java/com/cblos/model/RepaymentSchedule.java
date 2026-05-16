package com.cblos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "repayment_schedule")
public class RepaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Links to the LoanAccount
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private LoanAccount loanAccount;

    @Column(name = "installment_number")
    private Integer installmentNumber; // e.g., 1, 2, 3...

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "installment_amount", precision = 18, scale = 2)
    private BigDecimal installmentAmount; // Total amount due for this month

    @Column(name = "principal_component", precision = 18, scale = 2)
    private BigDecimal principalComponent; // How much goes to principal

    @Column(name = "interest_component", precision = 18, scale = 2)
    private BigDecimal interestComponent; // How much goes to interest

    @Column(length = 20)
    private String status; // e.g., "Pending", "Paid", "Overdue"

    public RepaymentSchedule() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LoanAccount getLoanAccount() { return loanAccount; }
    public void setLoanAccount(LoanAccount loanAccount) { this.loanAccount = loanAccount; }

    public Integer getInstallmentNumber() { return installmentNumber; }
    public void setInstallmentNumber(Integer installmentNumber) { this.installmentNumber = installmentNumber; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public BigDecimal getInstallmentAmount() { return installmentAmount; }
    public void setInstallmentAmount(BigDecimal installmentAmount) { this.installmentAmount = installmentAmount; }

    public BigDecimal getPrincipalComponent() { return principalComponent; }
    public void setPrincipalComponent(BigDecimal principalComponent) { this.principalComponent = principalComponent; }

    public BigDecimal getInterestComponent() { return interestComponent; }
    public void setInterestComponent(BigDecimal interestComponent) { this.interestComponent = interestComponent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}