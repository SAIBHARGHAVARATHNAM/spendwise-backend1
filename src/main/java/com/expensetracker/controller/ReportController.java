package com.expensetracker.controller;
import com.expensetracker.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class ReportController {
    private final ReportService s;
    @GetMapping("/dashboard") public Map<String, Object> dashboard(@AuthenticationPrincipal Long uid) { return s.dashboard(uid); }
    @GetMapping("/reports/monthly")
    public List<Map<String, Object>> monthly(@AuthenticationPrincipal Long uid, @RequestParam(required = false) Integer year) {
        return s.monthly(uid, year == null ? LocalDate.now().getYear() : year);
    }
    @GetMapping("/reports/category")
    public List<Map<String, Object>> category(@AuthenticationPrincipal Long uid,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) { return s.category(uid, from, to); }
}
