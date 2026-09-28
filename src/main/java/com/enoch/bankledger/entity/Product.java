package com.enoch.bankledger.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product code is required")
    @Column(unique = true, nullable = false)
    private String code;                  // e.g. SAVINGS, CURRENT

    @NotBlank(message = "Product name is required")
    private String name;                  // e.g. Savings Account

    @DecimalMin(value = "0.0", inclusive = true)
    @NotNull
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestRate;      // e.g. 0.04 = 4%

    @DecimalMin(value = "0.0", inclusive = true)
    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal transactionFee;    // Fee charged on transactions

    @DecimalMin(value = "0.0", inclusive = true)
    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal minimumBalance;    // Minimum balance required

    private String description;

    private boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}