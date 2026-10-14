package com.expensetracker.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name", "type"}))
@Getter @Setter @NoArgsConstructor
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 50) private String name;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10) private TxnType type;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id") private User user;
}
