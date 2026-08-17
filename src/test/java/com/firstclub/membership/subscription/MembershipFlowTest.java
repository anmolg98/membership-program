package com.firstclub.membership.subscription;

import com.firstclub.membership.catalog.domain.*;
import com.firstclub.membership.catalog.infrastructure.*;
import com.firstclub.membership.subscription.application.SubscriptionService;
import com.firstclub.membership.subscription.application.TierEligibilityService;
import com.firstclub.membership.subscription.domain.MembershipSubscription;
import com.firstclub.membership.common.exception.BusinessRuleException;
import com.firstclub.membership.subscription.domain.SubscriptionStatus;
import com.firstclub.membership.user.application.CustomerService;
import com.firstclub.membership.user.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MembershipFlowTest {
    @Autowired
    CustomerService customers;

    @Autowired
    SubscriptionService subscriptions;

    @Autowired
    MembershipPlanRepository plans;

    @Autowired
    MembershipTierRepository tiers;

    @Autowired
    TierEligibilityService eligibility;

    @Test
    void userProgressesFromSilverToGoldAfterMeetingConfiguredRules() {
        Customer user = customers.create("Aarav", "aarav@example.com", "REGULAR");
        MembershipPlan monthly = plans.findByActiveTrueOrderByDurationMonths().get(0);
        MembershipTier silver = tiers.findByActiveTrueOrderByRank().get(0);

        MembershipSubscription membership = subscriptions.subscribe(user.getId(), monthly.getId(), silver.getId());
        assertThat(membership.getTier().getCode()).isEqualTo("SILVER");

        for (int i = 0; i < 5; i++) {
            customers.recordOrder(user.getId(), new BigDecimal("1000.00"));
        }
        membership = subscriptions.evaluateAndApply(user.getId());

        assertThat(membership.getTier().getCode()).isEqualTo("GOLD");
    }

    @Test
    void frontendCanDisplayIneligibleTierButCannotSelectIt() {
        Customer user = customers.create("Mira", "mira@example.com", "REGULAR");
        MembershipPlan monthly = plans.findByActiveTrueOrderByDurationMonths().get(0);
        MembershipTier gold = tiers.findByActiveTrueOrderByRank().get(1);

        TierEligibilityService.TierResult goldOption = eligibility.evaluate(user.getId()).tiers().stream()
                .filter(tier -> tier.tier().equals("GOLD"))
                .findFirst().orElseThrow();

        assertThat(goldOption.eligible()).isFalse();
        assertThat(goldOption.ruleResults()).allMatch(rule -> !rule.passed());
        assertThatThrownBy(() -> subscriptions.subscribe(user.getId(), monthly.getId(), gold.getId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("not eligible");
    }

    @Test
    void cancelledUserCanCreateANewSubscriptionPeriod() {
        Customer user = customers.create("Kabir", "kabir@example.com", "REGULAR");
        MembershipPlan monthly = plans.findByActiveTrueOrderByDurationMonths().get(0);
        MembershipTier silver = tiers.findByActiveTrueOrderByRank().get(0);

        MembershipSubscription first = subscriptions.subscribe(user.getId(), monthly.getId(), silver.getId());
        subscriptions.cancel(user.getId());
        MembershipSubscription second = subscriptions.subscribe(user.getId(), monthly.getId(), silver.getId());

        assertThat(first.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
        assertThat(second.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(second.getId()).isNotEqualTo(first.getId());
        assertThat(subscriptions.get(user.getId()).getId()).isEqualTo(second.getId());
    }
}
