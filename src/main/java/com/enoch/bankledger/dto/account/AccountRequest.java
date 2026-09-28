package com.enoch.bankledger.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountRequest {

    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotNull(message = "Customer ID is required")
    private Long customerId;
}