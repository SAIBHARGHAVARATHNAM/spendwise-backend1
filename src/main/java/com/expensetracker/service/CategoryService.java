package com.expensetracker.service;
import com.expensetracker.dto.Dtos.*;
import com.expensetracker.entity.*;
import com.expensetracker.exception.ApiException;
import com.expensetracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository cats; private final TransactionRepository txns; private final UserRepository users;
    public List<CatView> list(Long uid) { return cats.findByUserIdOrderByNameAsc(uid).stream().map(CatView::of).toList(); }
    @Transactional
    public CatView create(Long uid, CatReq r) {
        String n = r.name().trim();
        if (cats.existsByUserIdAndNameIgnoreCaseAndType(uid, n, r.type())) throw new ApiException(HttpStatus.CONFLICT, "Category already exists");
        Category c = new Category(); c.setUser(users.getReferenceById(uid)); c.setName(n); c.setType(r.type());
        return CatView.of(cats.save(c));
    }
    @Transactional
    public CatView update(Long uid, Long id, CatReq r) {
        Category c = own(uid, id); String n = r.name().trim();
        boolean same = c.getName().equalsIgnoreCase(n) && c.getType() == r.type();
        if (!same && cats.existsByUserIdAndNameIgnoreCaseAndType(uid, n, r.type())) throw new ApiException(HttpStatus.CONFLICT, "Category already exists");
        if (c.getType() != r.type() && txns.existsByCategoryId(id)) throw new ApiException(HttpStatus.CONFLICT, "Cannot change type of a category that is in use");
        c.setName(n); c.setType(r.type());
        return CatView.of(cats.save(c));
    }
    @Transactional
    public void delete(Long uid, Long id) {
        Category c = own(uid, id);
        if (txns.existsByCategoryId(id)) throw new ApiException(HttpStatus.CONFLICT, "Category is used by transactions and cannot be deleted");
        cats.delete(c);
    }
    private Category own(Long uid, Long id) { return cats.findByIdAndUserId(id, uid).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found")); }
}
