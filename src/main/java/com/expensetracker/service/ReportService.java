package com.expensetracker.service;
import com.expensetracker.dto.Dtos.TxnView;
import com.expensetracker.entity.*;
import com.expensetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
@Service @RequiredArgsConstructor
public class ReportService {
    private final TransactionRepository txns;

    public Map<String, Object> dashboard(Long uid) {
        List<Transaction> all = txns.findByUserIdOrderByTxnDateDescIdDesc(uid);
        BigDecimal inc = sum(all, TxnType.INCOME), exp = sum(all, TxnType.EXPENSE);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalIncome", inc); m.put("totalExpenses", exp); m.put("balance", inc.subtract(exp));
        m.put("recent", all.stream().limit(5).map(TxnView::of).toList());
        m.put("categoryBreakdown", breakdown(all));
        m.put("monthly", monthly(uid, LocalDate.now().getYear()));
        return m;
    }
    public List<Map<String, Object>> monthly(Long uid, int year) {
        List<Transaction> l = txns.findByUserIdOrderByTxnDateDescIdDesc(uid).stream().filter(t -> t.getTxnDate().getYear() == year).toList();
        List<Map<String, Object>> out = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            int mm = m;
            List<Transaction> x = l.stream().filter(t -> t.getTxnDate().getMonthValue() == mm).toList();
            BigDecimal i = sum(x, TxnType.INCOME), e = sum(x, TxnType.EXPENSE);
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("month", Month.of(m).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            r.put("income", i); r.put("expenses", e); r.put("balance", i.subtract(e));
            out.add(r);
        }
        return out;
    }
    public List<Map<String, Object>> category(Long uid, LocalDate from, LocalDate to) {
        return breakdown(txns.findByUserIdOrderByTxnDateDescIdDesc(uid).stream()
                .filter(t -> from == null || !t.getTxnDate().isBefore(from)).filter(t -> to == null || !t.getTxnDate().isAfter(to)).toList());
    }
    private static BigDecimal sum(List<Transaction> l, TxnType ty) {
        return l.stream().filter(t -> t.getType() == ty).map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private static List<Map<String, Object>> breakdown(List<Transaction> l) {
        List<Transaction> ex = l.stream().filter(t -> t.getType() == TxnType.EXPENSE).toList();
        BigDecimal total = sum(ex, TxnType.EXPENSE);
        Map<String, BigDecimal> byCat = ex.stream().collect(Collectors.groupingBy(t -> t.getCategory().getName(),
                Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
        List<Map<String, Object>> out = new ArrayList<>();
        byCat.entrySet().stream().sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed()).forEach(e -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("category", e.getKey()); m.put("total", e.getValue());
            m.put("percent", total.signum() == 0 ? BigDecimal.ZERO : e.getValue().multiply(BigDecimal.valueOf(100)).divide(total, 1, RoundingMode.HALF_UP));
            out.add(m);
        });
        return out;
    }
}
