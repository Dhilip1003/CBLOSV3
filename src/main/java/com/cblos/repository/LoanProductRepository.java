package com.cblos.repository;

import com.cblos.model.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, Integer> {
    
    /**
     * Custom Query Definition: Allows us to search for a product 
     * by its exact textual name string if needed down the line.
     */
    Optional<LoanProduct> findByProductName(String productName);
}