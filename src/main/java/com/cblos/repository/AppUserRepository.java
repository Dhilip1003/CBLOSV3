package com.cblos.repository;

import com.cblos.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByEmail(String email);
    
    // ─── NEW: Used during the Admin approval gate to find and unlock the login profile ───
    Optional<AppUser> findByCorporateCustomerId(Integer corporateCustomerId);
}