package com.cblos.config;

import com.cblos.model.AppUser;
import com.cblos.model.CorporateCustomer;
import com.cblos.model.LoanOfficer;
import com.cblos.model.UserRole;
import com.cblos.repository.AppUserRepository;
import com.cblos.repository.CorporateCustomerRepository;
import com.cblos.repository.LoanOfficerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CorporateCustomerRepository customerRepository;

    @Autowired
    private LoanOfficerRepository officerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (appUserRepository.count() > 0) {
            return;
        }

        CorporateCustomer customer = customerRepository.findAll().stream().findFirst().orElseGet(() -> {
            CorporateCustomer c = new CorporateCustomer();
            c.setTaxId("TAX-DEMO-001");
            c.setCompanyName("Demo Corp Ltd");
            c.setIndustryType("Technology");
            return customerRepository.save(c);
        });

        LoanOfficer officer = officerRepository.findAll().stream().findFirst().orElseGet(() -> {
            LoanOfficer o = new LoanOfficer();
            o.setEmployeeId("EMP-DEMO-001");
            o.setName("Demo Officer");
            o.setDesignation("Loan Officer");
            return officerRepository.save(o);
        });

        LoanOfficer manager = officerRepository.findAll().stream()
                .filter(o -> o.getId() != null && !o.getId().equals(officer.getId()))
                .findFirst()
                .orElseGet(() -> {
                    LoanOfficer m = new LoanOfficer();
                    m.setEmployeeId("EMP-DEMO-002");
                    m.setName("Demo Manager");
                    m.setDesignation("Manager");
                    return officerRepository.save(m);
                });

        String encoded = passwordEncoder.encode("password");

        saveUser("customer@demo.com", encoded, UserRole.CUSTOMER, customer.getId(), null);
        saveUser("officer@bank.com", encoded, UserRole.OFFICER, null, officer.getId());
        saveUser("manager@bank.com", encoded, UserRole.MANAGER, null, manager.getId());

        System.out.println("=== Demo users created (password: password) ===");
        System.out.println("  customer@demo.com  -> CUSTOMER (company id " + customer.getId() + ")");
        System.out.println("  officer@bank.com   -> OFFICER");
        System.out.println("  manager@bank.com   -> MANAGER");
    }

    private void saveUser(String email, String encodedPassword, UserRole role,
                          Integer corporateCustomerId, Integer loanOfficerId) {
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(role);
        user.setCorporateCustomerId(corporateCustomerId);
        user.setLoanOfficerId(loanOfficerId);
        appUserRepository.save(user);
    }
}
