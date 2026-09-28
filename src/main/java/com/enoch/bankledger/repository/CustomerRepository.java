package com.enoch.bankledger.repository;

import com.enoch.bankledger.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByUserId(Long userId);

    Optional<Customer> findByIdAndUserId(Long id, Long userId);

    boolean existsByEmail(String email);
}