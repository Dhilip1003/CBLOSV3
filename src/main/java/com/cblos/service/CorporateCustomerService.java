package com.cblos.service;

import com.cblos.model.CorporateCustomer;
import com.cblos.repository.CorporateCustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CorporateCustomerService {

    @Autowired
    private CorporateCustomerRepository customerRepository;

    public CorporateCustomer onboardCustomer(CorporateCustomer customer) {
        // Optional: Check if Tax ID already exists to prevent duplicates
        if (customerRepository.findByTaxId(customer.getTaxId()).isPresent()) {
            throw new RuntimeException("Customer with this Tax ID already exists.");
        }
        return customerRepository.save(customer);
    }

    public CorporateCustomer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public List<CorporateCustomer> getAllCustomers() {
        return customerRepository.findAll();
    }
}