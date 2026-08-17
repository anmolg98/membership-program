package com.firstclub.membership.common.api;

import com.firstclub.membership.common.exception.*;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<?> notFound(RuntimeException exception) {
        return error(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(BusinessRuleException.class)
    ResponseEntity<?> rule(RuntimeException exception) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, exception);
    }

    @ExceptionHandler({ConflictException.class, OptimisticLockingFailureException.class, IllegalStateException.class})
    ResponseEntity<?> conflict(RuntimeException exception) {
        return error(HttpStatus.CONFLICT, exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(field -> field.getField() + " " + field.getDefaultMessage())
                .orElse("Invalid request");

        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now(),
                "status", 400,
                "message", message));
    }

    private ResponseEntity<?> error(HttpStatus status, RuntimeException exception) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now(),
                "status", status.value(),
                "message", exception.getMessage()));
    }
}
