package com.enoch.bankledger.entity;

import com.enoch.bankledger.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;                 // Unique transaction reference

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;             // DEPOSIT, WITHDRAWAL, TRANSFER

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(precision = 19, scale = 2)
    private BigDecimal fee = BigDecimal.ZERO;

    private String narration;                 // Description / remark

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;                  // Main account involved

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_account_id")
    private Account relatedAccount;           // Used for transfers (destination/source)

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;          // Balance after this transaction

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}