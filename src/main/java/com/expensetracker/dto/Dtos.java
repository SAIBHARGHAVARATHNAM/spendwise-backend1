package com.expensetracker.dto;
import com.expensetracker.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public final class Dtos {
    private Dtos() {}
    public record RegisterReq(@NotBlank(message = "Name is required") @Size(max = 80) String name,
                              @NotBlank(message = "Email is required") @Email(message = "Invalid email") String email,
                              @NotBlank(message = "Password is required") @Size(min = 6, max = 100, message = "Password must be at least 6 characters") String password) {}
    public record LoginReq(@NotBlank(message = "Email is required") String email, @NotBlank(message = "Password is required") String password) {}
    public record TxnReq(@NotNull(message = "Type is required") TxnType type,
                         @NotNull(message = "Category is required") Long categoryId,
                         @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than 0") @Digits(integer = 10, fraction = 2, message = "Invalid amount") BigDecimal amount,
                         @Size(max = 255) String description,
                         @NotNull(message = "Date is required") LocalDate date) {}
    public record CatReq(@NotBlank(message = "Category name is required") @Size(max = 50) String name, @NotNull(message = "Type is required") TxnType type) {}
    public record NameReq(@NotBlank(message = "Name is required") @Size(max = 80) String name) {}
    public record CatView(Long id, String name, TxnType type) {
        public static CatView of(Category c) { return new CatView(c.getId(), c.getName(), c.getType()); }
    }
    public record TxnView(Long id, TxnType type, Long categoryId, String category, BigDecimal amount, String description, LocalDate date) {
        public static TxnView of(Transaction t) {
            return new TxnView(t.getId(), t.getType(), t.getCategory().getId(), t.getCategory().getName(), t.getAmount(), t.getDescription(), t.getTxnDate());
        }
    }
}
