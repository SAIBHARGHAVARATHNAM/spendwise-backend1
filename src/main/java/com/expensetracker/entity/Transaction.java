package com.expensetracker.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity @Table(name = "transactions", indexes = {@Index(name = "idx_txn_user_date", columnList = "user_id,txn_date"), @Index(name = "idx_txn_user_cat", columnList = "user_id,category_id")})
@Getter @Setter @NoArgsConstructor
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id") private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "category_id") private Category category;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10) private TxnType type;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal amount;
    @Column(length = 255) private String description;
    @Column(name = "txn_date", nullable = false) private LocalDate txnDate;
}
