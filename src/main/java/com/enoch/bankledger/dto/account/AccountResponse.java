package com.enoch.bankledger.dto.account;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private String accountName;
    private BigDecimal balance;
    private String status;
    private Long customerId;
    private LocalDateTime createdAt;
    private Long productId;
    private String productCode;
    private String productName;
}