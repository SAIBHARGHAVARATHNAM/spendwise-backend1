package com.expensetracker.exception;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<Map<String, Object>> body(HttpStatus s, String msg, Object errors) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("status", s.value()); m.put("message", msg);
        if (errors != null) m.put("errors", errors);
        return ResponseEntity.status(s).body(m);
    }
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> api(ApiException e) { return body(e.getStatus(), e.getMessage(), null); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
        Map<String, String> errs = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> errs.putIfAbsent(f.getField(), f.getDefaultMessage()));
        return body(HttpStatus.BAD_REQUEST, errs.values().iterator().next(), errs);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> bad(Exception e) { return body(HttpStatus.BAD_REQUEST, "Invalid input (check amount, date, type or id format)", null); }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> other(Exception e) { e.printStackTrace(); return body(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", null); }
}
