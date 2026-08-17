package com.firstclub.membership.subscription.application.eligibility;

import com.firstclub.membership.catalog.domain.EligibilityRuleType;

public record RuleEvaluation(
        EligibilityRuleType type,
        String expected,
        String actual,
        boolean passed) {
}
