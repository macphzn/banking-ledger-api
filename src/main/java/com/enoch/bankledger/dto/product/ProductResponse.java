package com.enoch.bankledger.dto.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {

    private Long id;
    private String code;
    private String name;
    private BigDecimal interestRate;
    private BigDecimal transactionFee;
    private BigDecimal minimumBalance;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
}