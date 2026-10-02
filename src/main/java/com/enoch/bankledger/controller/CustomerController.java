package com.enoch.bankledger.controller;

import com.enoch.bankledger.dto.customer.CustomerRequest;
import com.enoch.bankledger.dto.customer.CustomerResponse;
import com.enoch.bankledger.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getMyCustomers() {
        return ResponseEntity.ok(customerService.getMyCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }
}