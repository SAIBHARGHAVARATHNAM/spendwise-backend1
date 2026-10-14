package com.expensetracker.service;
import com.expensetracker.dto.Dtos.*;
import com.expensetracker.entity.*;
import com.expensetracker.exception.ApiException;
import com.expensetracker.repository.*;
import com.expensetracker.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service @RequiredArgsConstructor
public class AuthService {
    private static final List<String> EXPENSE = List.of("Food", "Transportation", "Education", "Shopping", "Bills", "Entertainment", "Healthcare", "Other");
    private static final List<String> INCOME = List.of("Salary", "Freelance", "Gift", "Other");
    private final UserRepository users; private final CategoryRepository cats; private final PasswordEncoder encoder; private final JwtUtil jwt;

    @Transactional
    public Map<String, Object> register(RegisterReq r) {
        String email = r.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        User u = new User(); u.setName(r.name().trim()); u.setEmail(email); u.setPasswordHash(encoder.encode(r.password()));
        users.save(u);
        EXPENSE.forEach(n -> seed(u, n, TxnType.EXPENSE)); INCOME.forEach(n -> seed(u, n, TxnType.INCOME));
        return auth(u);
    }
    public Map<String, Object> login(LoginReq r) {
        User u = users.findByEmail(r.email().trim().toLowerCase()).filter(x -> encoder.matches(r.password(), x.getPasswordHash()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return auth(u);
    }
    public Map<String, Object> profile(Long uid) { return view(find(uid)); }
    @Transactional
    public Map<String, Object> rename(Long uid, String name) { User u = find(uid); u.setName(name.trim()); return view(users.save(u)); }

    private User find(Long uid) { return users.findById(uid).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found")); }
    private void seed(User u, String n, TxnType t) { Category c = new Category(); c.setUser(u); c.setName(n); c.setType(t); cats.save(c); }
    private Map<String, Object> view(User u) { return Map.of("name", u.getName(), "email", u.getEmail()); }
    private Map<String, Object> auth(User u) { return Map.of("token", jwt.generate(u.getId()), "name", u.getName(), "email", u.getEmail()); }
}
