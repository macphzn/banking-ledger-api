package com.enoch.bankledger.dto.customer;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomerResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String bvn;
    private String nin;
    private LocalDateTime createdAt;
}