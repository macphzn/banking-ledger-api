package com.enoch.bankledger.controller;

import com.enoch.bankledger.dto.transaction.*;
import com.enoch.bankledger.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(transactionService.deposit(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawalRequest request) {
        return ResponseEntity.ok(transactionService.withdraw(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transactionService.transfer(request));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getMyTransactions() {
        return ResponseEntity.ok(transactionService.getMyTransactions());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactions(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getAccountTransactions(accountId));
    }
}