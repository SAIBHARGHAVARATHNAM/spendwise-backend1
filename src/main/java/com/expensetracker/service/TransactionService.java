package com.expensetracker.service;
import com.expensetracker.dto.Dtos.*;
import com.expensetracker.entity.*;
import com.expensetracker.exception.ApiException;
import com.expensetracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
@Service @RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository txns; private final CategoryRepository cats; private final UserRepository users;

    public List<TxnView> search(Long uid, String q, TxnType type, Long categoryId, LocalDate from, LocalDate to, String sortBy, String dir) {
        Comparator<Transaction> cmp = switch (sortBy == null ? "date" : sortBy) {
            case "amount" -> Comparator.comparing(Transaction::getAmount);
            case "category" -> Comparator.comparing((Transaction t) -> t.getCategory().getName().toLowerCase());
            default -> Comparator.comparing(Transaction::getTxnDate);
        };
        if (!"asc".equalsIgnoreCase(dir)) cmp = cmp.reversed();
        String k = q == null ? "" : q.trim().toLowerCase();
        return txns.findByUserIdOrderByTxnDateDescIdDesc(uid).stream()
                .filter(t -> type == null || t.getType() == type)
                .filter(t -> categoryId == null || t.getCategory().getId().equals(categoryId))
                .filter(t -> from == null || !t.getTxnDate().isBefore(from))
                .filter(t -> to == null || !t.getTxnDate().isAfter(to))
                .filter(t -> k.isEmpty() || (t.getDescription() != null && t.getDescription().toLowerCase().contains(k)) || t.getCategory().getName().toLowerCase().contains(k))
                .sorted(cmp).map(TxnView::of).toList();
    }
    public TxnView get(Long uid, Long id) { return TxnView.of(own(uid, id)); }
    @Transactional
    public TxnView create(Long uid, TxnReq r) {
        Transaction t = new Transaction(); t.setUser(users.getReferenceById(uid)); apply(t, r, uid);
        return TxnView.of(txns.save(t));
    }
    @Transactional
    public TxnView update(Long uid, Long id, TxnReq r) { Transaction t = own(uid, id); apply(t, r, uid); return TxnView.of(txns.save(t)); }
    @Transactional
    public void delete(Long uid, Long id) { txns.delete(own(uid, id)); }

    private Transaction own(Long uid, Long id) { return txns.findByIdAndUserId(id, uid).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Transaction not found")); }
    private void apply(Transaction t, TxnReq r, Long uid) {
        Category c = cats.findByIdAndUserId(r.categoryId(), uid).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid category"));
        if (c.getType() != r.type()) throw new ApiException(HttpStatus.BAD_REQUEST, "Category type does not match transaction type");
        t.setCategory(c); t.setType(r.type()); t.setAmount(r.amount());
        t.setDescription(r.description() == null ? null : r.description().trim()); t.setTxnDate(r.date());
    }
}
