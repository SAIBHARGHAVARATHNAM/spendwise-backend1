package com.expensetracker.controller;
import com.expensetracker.dto.Dtos.*;
import com.expensetracker.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/categories") @RequiredArgsConstructor
public class CategoryController {
    private final CategoryService s;
    @GetMapping public List<CatView> list(@AuthenticationPrincipal Long uid) { return s.list(uid); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public CatView create(@AuthenticationPrincipal Long uid, @Valid @RequestBody CatReq r) { return s.create(uid, r); }
    @PutMapping("/{id}")
    public CatView update(@AuthenticationPrincipal Long uid, @PathVariable Long id, @Valid @RequestBody CatReq r) { return s.update(uid, id, r); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long uid, @PathVariable Long id) { s.delete(uid, id); }
}
