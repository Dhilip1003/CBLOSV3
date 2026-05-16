package com.cblos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cblos.model.LoanApplication;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Integer> {

}
