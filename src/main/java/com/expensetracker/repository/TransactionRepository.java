package com.expensetracker.repository;
import com.expensetracker.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByTxnDateDescIdDesc(Long userId);
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
    boolean existsByCategoryId(Long categoryId);
}
