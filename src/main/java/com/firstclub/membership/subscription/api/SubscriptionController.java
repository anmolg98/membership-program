package com.firstclub.membership.subscription.api;

import com.firstclub.membership.subscription.application.SubscriptionService;
import com.firstclub.membership.subscription.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/{userId}")
public class SubscriptionController {
    private final SubscriptionService service;

    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    @PostMapping("/subscriptions")
    @ResponseStatus(HttpStatus.CREATED)
    public View subscribe(@PathVariable Long userId, @Valid @RequestBody SubscribeRequest request) {
        return View.from(service.subscribe(userId, request.planId(), request.tierId()));
    }

    @GetMapping("/membership")
    public View current(@PathVariable Long userId) {
        return View.from(service.get(userId));
    }

    @PatchMapping("/subscription/tier")
    public View tier(@PathVariable Long userId, @Valid @RequestBody TierRequest request) {
        return View.from(service.changeTier(userId, request.tierId()));
    }

    @PostMapping("/subscription/evaluate-tier")
    public View evaluate(@PathVariable Long userId) {
        return View.from(service.evaluateAndApply(userId));
    }

    @DeleteMapping("/subscription")
    public View cancel(@PathVariable Long userId) {
        return View.from(service.cancel(userId));
    }

    public record SubscribeRequest(@NotNull Long planId, @NotNull Long tierId) {
    }

    public record TierRequest(@NotNull Long tierId) {
    }

    public record View(
            Long id,
            Long userId,
            String plan,
            String tier,
            SubscriptionStatus status,
            Instant startedAt,
            Instant expiresAt,
            Instant cancelledAt,
            Map<?, ?> benefits,
            long version) {

        static View from(MembershipSubscription subscription) {
            return new View(
                    subscription.getId(),
                    subscription.getCustomer().getId(),
                    subscription.getPlan().getCode().name(),
                    subscription.getTier().getCode(),
                    subscription.getStatus(),
                    subscription.getStartedAt(),
                    subscription.getExpiresAt(),
                    subscription.getCancelledAt(),
                    subscription.getTier().getBenefits(),
                    subscription.getVersion());
        }
    }
}
