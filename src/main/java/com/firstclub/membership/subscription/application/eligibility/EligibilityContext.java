package com.firstclub.membership.subscription.application.eligibility;

import java.math.BigDecimal;

public record EligibilityContext(
        int completedOrderCount,
        BigDecimal monthlyOrderValue,
        String cohort) {
}
