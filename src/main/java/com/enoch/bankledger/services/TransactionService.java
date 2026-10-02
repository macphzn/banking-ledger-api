package com.enoch.bankledger.services;

import com.enoch.bankledger.dto.transaction.*;
import com.enoch.bankledger.entity.Account;
import com.enoch.bankledger.entity.Transaction;
import com.enoch.bankledger.enums.AccountStatus;
import com.enoch.bankledger.enums.TransactionType;
import com.enoch.bankledger.repository.AccountRepository;
import com.enoch.bankledger.repository.TransactionRepository;
import com.enoch.bankledger.security.CurrentUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    // ==================== DEPOSIT ====================
    @Transactional
    public TransactionResponse deposit(DepositRequest request) {
        Account account = getOwnedAccount(request.getAccountId());
        validateAccountIsActive(account);

        BigDecimal amount = request.getAmount();
        BigDecimal newBalance = account.getBalance().add(amount);

        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .reference(generateReference())
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .fee(BigDecimal.ZERO)
                .narration(request.getNarration() != null ? request.getNarration() : "Deposit")
                .account(account)
                .balanceAfter(newBalance)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        auditService.log(
                "DEPOSIT",
                "TRANSACTION",
                saved.getId(),
                "Deposited " + amount + " into account " + account.getAccountNumber()
        );


        return mapToResponse(saved);
    }

    // ==================== WITHDRAWAL ====================
    @Transactional
    public TransactionResponse withdraw(WithdrawalRequest request) {
        Account account = getOwnedAccount(request.getAccountId());
        validateAccountIsActive(account);

        BigDecimal amount = request.getAmount();
        BigDecimal fee = account.getProduct().getTransactionFee() != null
                ? account.getProduct().getTransactionFee()
                : BigDecimal.ZERO;

        BigDecimal totalDebit = amount.add(fee);

        if (account.getBalance().compareTo(totalDebit) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal balanceAfter = account.getBalance().subtract(totalDebit);
        if (balanceAfter.compareTo(account.getProduct().getMinimumBalance()) < 0) {
            throw new RuntimeException("Transaction would breach minimum balance");
        }

        account.setBalance(balanceAfter);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .reference(generateReference())
                .type(TransactionType.WITHDRAWAL)
                .amount(amount)
                .fee(fee)
                .narration(request.getNarration() != null ? request.getNarration() : "Withdrawal")
                .account(account)
                .balanceAfter(balanceAfter)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        auditService.log(
                "WITHDRAWAL",
                "TRANSACTION",
                saved.getId(),
                "Withdrew " + amount + " (+ fee: " + fee + ") from account " + account.getAccountNumber()
        );


        return mapToResponse(saved);
    }

    // ==================== TRANSFER ====================
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        Account sourceAccount = getOwnedAccount(request.getSourceAccountId());
        validateAccountIsActive(sourceAccount);

        Account destinationAccount = accountRepository.findById(request.getDestinationAccountId())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));
        validateAccountIsActive(destinationAccount);

        BigDecimal amount = request.getAmount();
        BigDecimal fee = sourceAccount.getProduct().getTransactionFee() != null
                ? sourceAccount.getProduct().getTransactionFee()
                : BigDecimal.ZERO;

        BigDecimal totalDebit = amount.add(fee);

        if (sourceAccount.getBalance().compareTo(totalDebit) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Debit source
        BigDecimal sourceBalanceAfter = sourceAccount.getBalance().subtract(totalDebit);
        sourceAccount.setBalance(sourceBalanceAfter);
        accountRepository.save(sourceAccount);

        // Credit destination
        BigDecimal destBalanceAfter = destinationAccount.getBalance().add(amount);
        destinationAccount.setBalance(destBalanceAfter);
        accountRepository.save(destinationAccount);

        // Record transaction for source account
        Transaction transaction = Transaction.builder()
                .reference(generateReference())
                .type(TransactionType.TRANSFER)
                .amount(amount)
                .fee(fee)
                .narration(request.getNarration() != null ? request.getNarration() : "Transfer")
                .account(sourceAccount)
                .relatedAccount(destinationAccount)
                .balanceAfter(sourceBalanceAfter)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        auditService.log(
                "TRANSFER",
                "TRANSACTION",
                saved.getId(),
                "Transferred " + amount + " from " + sourceAccount.getAccountNumber() +
                        " to " + destinationAccount.getAccountNumber()
        );


        return mapToResponse(saved);
    }

    // ==================== GET TRANSACTIONS ====================
    public List<TransactionResponse> getMyTransactions() {
        Long userId = currentUserService.getCurrentUserId();
        return transactionRepository.findByAccountCustomerUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getAccountTransactions(Long accountId) {
        Account account = getOwnedAccount(accountId);
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(account.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==================== HELPER METHODS ====================
    private Account getOwnedAccount(Long accountId) {
        Long userId = currentUserService.getCurrentUserId();
        return accountRepository.findByIdAndCustomerUserId(accountId, userId)
                .orElseThrow(() -> new RuntimeException("Account not found or access denied"));
    }

    private void validateAccountIsActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }
    }

    private String generateReference() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .reference(transaction.getReference())
                .type(transaction.getType().name())
                .amount(transaction.getAmount())
                .fee(transaction.getFee())
                .narration(transaction.getNarration())
                .accountId(transaction.getAccount().getId())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .relatedAccountId(transaction.getRelatedAccount() != null ? transaction.getRelatedAccount().getId() : null)
                .relatedAccountNumber(transaction.getRelatedAccount() != null ? transaction.getRelatedAccount().getAccountNumber() : null)
                .balanceAfter(transaction.getBalanceAfter())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}