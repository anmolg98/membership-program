package com.firstclub.membership.subscription.application.eligibility;

import com.firstclub.membership.catalog.domain.*;

public interface EligibilityRuleStrategy {
    EligibilityRuleType supportedType();

    RuleEvaluation evaluate(TierEligibilityRule rule, EligibilityContext context);
}
