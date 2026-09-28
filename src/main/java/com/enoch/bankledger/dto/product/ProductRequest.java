package com.enoch.bankledger.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "Product code is required")
    private String code;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Interest rate is required")
    private BigDecimal interestRate;

    @NotNull(message = "Transaction fee is required")
    private BigDecimal transactionFee;

    @NotNull(message = "Minimum balance is required")
    private BigDecimal minimumBalance;

    private String description;
}