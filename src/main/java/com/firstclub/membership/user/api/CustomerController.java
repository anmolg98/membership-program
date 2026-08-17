package com.firstclub.membership.user.api;

import com.firstclub.membership.user.application.CustomerService;
import com.firstclub.membership.user.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/users")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@Valid @RequestBody CreateUser request) {
        return service.create(request.name(), request.email(), request.cohort());
    }

    @PostMapping("/{userId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderSummary record(@PathVariable Long userId, @Valid @RequestBody RecordOrder request) {
        MonthlyOrderSummary summary = service.recordOrder(userId, request.amount());
        return new OrderSummary(
                summary.getOrderMonth(),
                summary.getCompletedOrderCount(),
                summary.getTotalOrderValue());
    }

    public record CreateUser(
            @NotBlank String name,
            @Email @NotBlank String email,
            String cohort) {
    }

    public record RecordOrder(@NotNull @DecimalMin("0.01") BigDecimal amount) {
    }

    public record OrderSummary(
            String month,
            int completedOrders,
            BigDecimal totalOrderValue) {
    }
}
