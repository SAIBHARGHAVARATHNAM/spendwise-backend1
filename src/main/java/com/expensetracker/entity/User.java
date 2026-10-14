package com.expensetracker.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "users") @Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 80) private String name;
    @Column(nullable = false, unique = true, length = 120) private String email;
    @Column(nullable = false) private String passwordHash;
}
