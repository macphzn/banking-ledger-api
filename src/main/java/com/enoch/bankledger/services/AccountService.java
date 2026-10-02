package com.enoch.bankledger.services;

import com.enoch.bankledger.dto.account.AccountRequest;
import com.enoch.bankledger.dto.account.AccountResponse;
import com.enoch.bankledger.entity.Account;
import com.enoch.bankledger.entity.AuditLog;
import com.enoch.bankledger.entity.Customer;
import com.enoch.bankledger.entity.Product;
import com.enoch.bankledger.enums.AccountStatus;
import com.enoch.bankledger.repository.AccountRepository;
import com.enoch.bankledger.repository.CustomerRepository;
import com.enoch.bankledger.repository.ProductRepository;
import com.enoch.bankledger.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;


    public AccountResponse createAccount(AccountRequest request) {
        Long userId = currentUserService.getCurrentUserId();

        // Validate customer belongs to current user
        Customer customer = customerRepository.findByIdAndUserId(request.getCustomerId(), userId)
                .orElseThrow(() -> new RuntimeException("Customer not found or access denied"));

        // Validate product exists and is active
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.isActive()) {
            throw new RuntimeException("Product is not active");
        }

        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountName(request.getAccountName())
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .customer(customer)
                .product(product)                    // Link to product
                .build();

        Account saved = accountRepository.save(account);
        auditService.log(
                "CREATE_ACCOUNT",
                "ACCOUNT",
                saved.getId(),
                "Created account " + saved.getAccountNumber() + " for customer ID " + customer.getId()
        );
        return mapToResponse(saved);
    }

    public List<AccountResponse> getMyAccounts() {
        Long userId = currentUserService.getCurrentUserId();

        return accountRepository.findByCustomerUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccountById(Long id) {
        Long userId = currentUserService.getCurrentUserId();

        Account account = accountRepository.findByIdAndCustomerUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Account not found or access denied"));

        return mapToResponse(account);
    }

    private String generateAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            accountNumber = String.format("%010d", random.nextInt(1_000_000_000));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountName(account.getAccountName())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .customerId(account.getCustomer().getId())
                .productId(account.getProduct().getId())
                .productCode(account.getProduct().getCode())
                .productName(account.getProduct().getName())
                .createdAt(account.getCreatedAt())
                .build();
    }
}