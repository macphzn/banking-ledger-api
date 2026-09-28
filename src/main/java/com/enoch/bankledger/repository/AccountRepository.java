package com.enoch.bankledger.repository;

import com.enoch.bankledger.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByCustomerId(Long customerId);

    List<Account> findByCustomerUserId(Long userId);

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByIdAndCustomerUserId(Long id, Long userId);

    boolean existsByAccountNumber(String accountNumber);
}