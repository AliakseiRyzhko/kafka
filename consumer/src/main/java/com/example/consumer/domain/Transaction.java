package com.example.consumer.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction {

    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bank")
    private String bank;
    @Column(name="transaction_type", updatable = false, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TransactionType transactionType;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "price")
    private Double price;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name="total_cost")
    private BigDecimal totalCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="client_id")
    @Fetch(FetchMode.JOIN)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Client client;
}
