package com.enoch.bankledger.dto.transaction;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {

    private Long id;
    private String reference;
    private String type;
    private BigDecimal amount;
    private BigDecimal fee;
    private String narration;
    private Long accountId;
    private String accountNumber;
    private Long relatedAccountId;
    private String relatedAccountNumber;
    private BigDecimal balanceAfter;
    private LocalDateTime createdAt;
}