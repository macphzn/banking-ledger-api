package com.enoch.bankledger.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;              // e.g. CREATE_ACCOUNT, DEPOSIT, WITHDRAWAL, LOGIN

    private String entityType;          // e.g. ACCOUNT, TRANSACTION, CUSTOMER

    private Long entityId;

    private String performedBy;         // username

    private String details;             // additional info (JSON or plain text)

    private String ipAddress;           // optional

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}