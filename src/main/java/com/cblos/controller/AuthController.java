package com.cblos.controller;

import com.cblos.security.AppUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal AppUserDetails user) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("email", user.getUsername());
        body.put("role", user.getRole().name());
        body.put("corporateCustomerId", user.getCorporateCustomerId());
        body.put("loanOfficerId", user.getLoanOfficerId());
        return ResponseEntity.ok(body);
    }
}
