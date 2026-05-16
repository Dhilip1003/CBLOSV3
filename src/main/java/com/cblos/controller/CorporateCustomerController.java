package com.cblos.controller;

import com.cblos.model.CorporateCustomer;
import com.cblos.service.CorporateCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CorporateCustomerController {

    @Autowired
    private CorporateCustomerService customerService;

    // 1. Onboard a new customer
    @PostMapping("/onboard")
    public ResponseEntity<CorporateCustomer> onboardCustomer(@RequestBody CorporateCustomer customer) {
        return ResponseEntity.ok(customerService.onboardCustomer(customer));
    }

    // 2. Get a specific customer
    @GetMapping("/{id}")
    public ResponseEntity<CorporateCustomer> getCustomer(@PathVariable Integer id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    // 3. Get all customers
    @GetMapping("/all")
    public ResponseEntity<List<CorporateCustomer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
}