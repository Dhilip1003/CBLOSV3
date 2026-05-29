package com.cblos.service;

import com.cblos.model.LoanProduct;
import com.cblos.repository.LoanProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class LoanProductService {

    @Autowired
    private LoanProductRepository productRepository;

    /**
     * Pulls the active credit catalog list to draw the dashboard selection cards on the frontend screen.
     */
    public List<LoanProduct> getAllAvailableProducts() {
        return productRepository.findAll();
    }

    /**
     * Looks up a single product rule configuration by its database row ID.
     */
    public LoanProduct getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Selected Credit Product option not found in database catalog."));
    }

    /**
     * 🚀 AUTOMATED DATABASE SEEDER
     * Runs automatically every time your Spring Boot application starts up.
     * If your database table is completely empty, it populates all 3 core products.
     */
    @PostConstruct
    public void seedInitialLoanProducts() {
        if (productRepository.count() == 0) {
            
            // Product 1: Commercial Term Loan
            LoanProduct termLoan = new LoanProduct();
            termLoan.setProductName("COMMERCIAL_TERM_LOAN");
            termLoan.setDescription("Designed for massive infrastructure capital acquisitions, plant machinery procurement, or business site structural expansion.");
            termLoan.setMinLoanAmount(1000000.00);    // ₹10 Lakhs Minimum
            termLoan.setMaxLoanAmount(50000000.00);   // ₹5 Crores Maximum
            termLoan.setMinTenureMonths(12);          // 1 Year Minimum
            termLoan.setMaxTenureMonths(60);          // 5 Years Maximum
            termLoan.setDefaultInterestRate(9.25);
            productRepository.save(termLoan);

            // Product 2: Working Capital Line
            LoanProduct workingCapital = new LoanProduct();
            workingCapital.setProductName("WORKING_CAPITAL_LINE");
            workingCapital.setDescription("Short-term operating credit asset to manage day-to-day transaction flows, raw materials ledger, and emergency employee payroll gaps.");
            workingCapital.setMinLoanAmount(200000.00);    // ₹2 Lakhs Minimum
            workingCapital.setMaxLoanAmount(10000000.00);  // ₹1 Crore Maximum
            workingCapital.setMinTenureMonths(6);          // 6 Months Minimum
            workingCapital.setMaxTenureMonths(24);         // 2 Years Maximum
            workingCapital.setDefaultInterestRate(11.50);
            productRepository.save(workingCapital);

            // Product 3: Commercial Line of Credit
            LoanProduct lineOfCredit = new LoanProduct();
            lineOfCredit.setProductName("COMMERCIAL_LINE_OF_CREDIT");
            lineOfCredit.setDescription("Flexible corporate safety net. Draw funds up to your limit at any time, repay them, and draw again. Interest is charged only on the exact amount used.");
            lineOfCredit.setMinLoanAmount(500000.00);    // ₹5 Lakhs Minimum
            lineOfCredit.setMaxLoanAmount(25000000.00);  // ₹2.5 Crores Maximum
            lineOfCredit.setMinTenureMonths(12);         // 1 Year Minimum
            lineOfCredit.setMaxTenureMonths(36);         // 3 Years Maximum
            lineOfCredit.setDefaultInterestRate(10.75);
            productRepository.save(lineOfCredit);

            System.out.println("🌱 [CB-LOS] System Alert: Seeded all 3 core financial credit product models successfully into empty database records.");
        }
    }
}