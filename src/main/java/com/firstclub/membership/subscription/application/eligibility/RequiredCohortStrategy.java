package com.firstclub.membership.subscription.application.eligibility;

import com.firstclub.membership.catalog.domain.*;
import org.springframework.stereotype.Component;

@Component
public class RequiredCohortStrategy implements EligibilityRuleStrategy {
    @Override
    public EligibilityRuleType supportedType() {
        return EligibilityRuleType.REQUIRED_COHORT;
    }

    @Override
    public RuleEvaluation evaluate(TierEligibilityRule rule, EligibilityContext context) {
        String actual = context.cohort() == null ? "" : context.cohort();
        return new RuleEvaluation(
                supportedType(),
                rule.getExpectedValue(),
                actual,
                rule.getExpectedValue().equalsIgnoreCase(actual));
    }
}
