package com.expensetracker.controller;
import com.expensetracker.dto.Dtos.*;
import com.expensetracker.entity.TxnType;
import com.expensetracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
@RestController @RequestMapping("/api/transactions") @RequiredArgsConstructor
public class TransactionController {
    private final TransactionService s;
    @GetMapping
    public List<TxnView> list(@AuthenticationPrincipal Long uid, @RequestParam(required = false) String q, @RequestParam(required = false) TxnType type,
                              @RequestParam(required = false) Long categoryId,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                              @RequestParam(required = false) String sortBy, @RequestParam(required = false) String dir) {
        return s.search(uid, q, type, categoryId, from, to, sortBy, dir);
    }
    @GetMapping("/{id}") public TxnView get(@AuthenticationPrincipal Long uid, @PathVariable Long id) { return s.get(uid, id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public TxnView create(@AuthenticationPrincipal Long uid, @Valid @RequestBody TxnReq r) { return s.create(uid, r); }
    @PutMapping("/{id}")
    public TxnView update(@AuthenticationPrincipal Long uid, @PathVariable Long id, @Valid @RequestBody TxnReq r) { return s.update(uid, id, r); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long uid, @PathVariable Long id) { s.delete(uid, id); }
}
