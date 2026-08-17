package com.firstclub.membership.subscription.application;

import com.firstclub.membership.catalog.domain.*;
import com.firstclub.membership.catalog.infrastructure.*;
import com.firstclub.membership.common.exception.*;
import com.firstclub.membership.subscription.domain.*;
import com.firstclub.membership.subscription.infrastructure.*;
import com.firstclub.membership.user.application.CustomerService;
import com.firstclub.membership.user.domain.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptions;
    private final MembershipPlanRepository plans;
    private final MembershipTierRepository tiers;
    private final CustomerService customers;
    private final TierEligibilityService eligibility;
    private final Clock clock;

    public SubscriptionService(SubscriptionRepository subscriptions, MembershipPlanRepository plans,
            MembershipTierRepository tiers, CustomerService customers,
            TierEligibilityService eligibility, Clock clock) {
        this.subscriptions = subscriptions;
        this.plans = plans;
        this.tiers = tiers;
        this.customers = customers;
        this.eligibility = eligibility;
        this.clock = clock;
    }

    @Transactional
    public MembershipSubscription subscribe(Long userId, Long planId, Long tierId) {
        Customer customer = customers.getForUpdate(userId);
        subscriptions.findFirstByCustomerIdAndStatusOrderByStartedAtDescIdDesc(userId, SubscriptionStatus.ACTIVE)
                .ifPresent(existing -> {
                    existing.markExpired(clock.instant());
                    if (existing.getStatus() == SubscriptionStatus.ACTIVE) {
                        throw new ConflictException("User already has an active subscription");
                    }
                });

        MembershipPlan plan = plan(planId);
        MembershipTier tier = tier(tierId);
        ensureEligible(userId, tier);
        return subscriptions.save(new MembershipSubscription(customer, plan, tier, clock.instant()));
    }

    @Transactional
    public MembershipSubscription get(Long userId) {
        MembershipSubscription subscription = find(userId);
        subscription.markExpired(clock.instant());
        return subscription;
    }

    @Transactional
    public MembershipSubscription changeTier(Long userId, Long tierId) {
        MembershipSubscription subscription = activeForUpdate(userId);
        MembershipTier target = tier(tierId);

        if (target.getRank() > subscription.getTier().getRank()) {
            ensureEligible(userId, target);
        }

        subscription.changeTier(target);
        return subscription;
    }

    @Transactional
    public MembershipSubscription evaluateAndApply(Long userId) {
        MembershipSubscription subscription = activeForUpdate(userId);
        TierEligibilityService.Evaluation result = eligibility.evaluate(userId);
        MembershipTier recommended = tier(result.recommendedTierId());

        if (!recommended.getId().equals(subscription.getTier().getId())) {
            subscription.changeTier(recommended);
        }

        return subscription;
    }

    @Transactional
    public MembershipSubscription cancel(Long userId) {
        customers.getForUpdate(userId);
        MembershipSubscription subscription = subscriptions
                .findFirstByCustomerIdAndStatusOrderByStartedAtDescIdDesc(userId, SubscriptionStatus.ACTIVE)
                .orElseGet(() -> find(userId));
        subscription.markExpired(clock.instant());

        if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            subscription.cancel(clock.instant());
        }

        return subscription;
    }

    private void ensureEligible(Long userId, MembershipTier tier) {
        boolean eligible = eligibility.evaluate(userId)
                .tiers()
                .stream()
                .anyMatch(result -> result.tierId().equals(tier.getId()) && result.eligible());

        if (!eligible) {
            throw new BusinessRuleException("User is not eligible for tier " + tier.getCode());
        }
    }

    private MembershipSubscription activeForUpdate(Long userId) {
        customers.getForUpdate(userId);
        MembershipSubscription subscription = subscriptions
                .findFirstByCustomerIdAndStatusOrderByStartedAtDescIdDesc(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new BusinessRuleException("Subscription is not active"));
        subscription.markExpired(clock.instant());

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new BusinessRuleException("Subscription is not active");
        }

        return subscription;
    }

    private MembershipSubscription find(Long userId) {
        return subscriptions.findFirstByCustomerIdOrderByStartedAtDescIdDesc(userId)
                .orElseThrow(() -> new NotFoundException("Subscription not found for user: " + userId));
    }

    private MembershipPlan plan(Long planId) {
        return plans.findById(planId)
                .filter(MembershipPlan::isActive)
                .orElseThrow(() -> new NotFoundException("Active plan not found: " + planId));
    }

    private MembershipTier tier(Long tierId) {
        return tiers.findById(tierId)
                .filter(MembershipTier::isActive)
                .orElseThrow(() -> new NotFoundException("Active tier not found: " + tierId));
    }
}
