package com.enoch.bankledger.services;

import com.enoch.bankledger.dto.account.AccountRequest;
import com.enoch.bankledger.dto.account.AccountResponse;
import com.enoch.bankledger.entity.Account;
import com.enoch.bankledger.entity.Customer;
import com.enoch.bankledger.entity.User;
import com.enoch.bankledger.enums.AccountStatus;
import com.enoch.bankledger.repository.AccountRepository;
import com.enoch.bankledger.repository.CustomerRepository;
import com.enoch.bankledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;

    public AccountResponse createAccount(AccountRequest request) {
        User currentUser = getCurrentUser();

        Customer customer = customerRepository.findByIdAndUserId(request.getCustomerId(), currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found or access denied"));

        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountName(request.getAccountName())
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .customer(customer)
                .build();

        Account saved = accountRepository.save(account);
        return mapToResponse(saved);
    }

    public List<AccountResponse> getMyAccounts() {
        User currentUser = getCurrentUser();
        return accountRepository.findByCustomerUserId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccountById(Long id) {
        User currentUser = getCurrentUser();
        Account account = accountRepository.findByIdAndCustomerUserId(id, currentUser.getId())
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

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountName(account.getAccountName())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .customerId(account.getCustomer().getId())
                .createdAt(account.getCreatedAt())
                .build();
    }
}