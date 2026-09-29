package com.enoch.bankledger.repository;

import com.enoch.bankledger.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);

    List<Transaction> findByAccountCustomerUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByReference(String reference);
}