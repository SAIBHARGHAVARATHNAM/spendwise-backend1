package com.expensetracker.controller;
import com.expensetracker.dto.Dtos.*;
import com.expensetracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class AuthController {
    private final AuthService s;
    @PostMapping("/auth/register") @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@Valid @RequestBody RegisterReq r) { return s.register(r); }
    @PostMapping("/auth/login")
    public Map<String, Object> login(@Valid @RequestBody LoginReq r) { return s.login(r); }
    @PostMapping("/auth/logout")
    public Map<String, String> logout() { return Map.of("message", "Logged out. Discard the token on the client."); }
    @GetMapping("/profile")
    public Map<String, Object> profile(@AuthenticationPrincipal Long uid) { return s.profile(uid); }
    @PutMapping("/profile")
    public Map<String, Object> rename(@AuthenticationPrincipal Long uid, @Valid @RequestBody NameReq r) { return s.rename(uid, r.name()); }
}
