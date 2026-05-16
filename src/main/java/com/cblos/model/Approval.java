package com.cblos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval")
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private LoanApplication loanApplication;

    // Relates directly to the LoanOfficer table instead of just an Integer ID
    @ManyToOne
    @JoinColumn(name = "approved_by", nullable = false)
    private LoanOfficer approvedBy;

    // MISSING FIELD ADDED HERE
    @Column(name = "approval_level")
    private Integer approvalLevel;

    @Column(name = "approval_status", length = 20)
    private String approvalStatus; // "Approved", "Rejected", etc.

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    public Approval() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LoanApplication getLoanApplication() { return loanApplication; }
    public void setLoanApplication(LoanApplication loanApplication) { this.loanApplication = loanApplication; }

    public LoanOfficer getApprovedBy() { return approvedBy; }
    public void setApprovedBy(LoanOfficer approvedBy) { this.approvedBy = approvedBy; }

    // MISSING GETTER/SETTER ADDED HERE
    public Integer getApprovalLevel() { return approvalLevel; }
    public void setApprovalLevel(Integer approvalLevel) { this.approvalLevel = approvalLevel; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }
}