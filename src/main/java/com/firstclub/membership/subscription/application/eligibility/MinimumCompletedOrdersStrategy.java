package com.firstclub.membership.subscription.application.eligibility;

import com.firstclub.membership.catalog.domain.*;
import org.springframework.stereotype.Component;

@Component
public class MinimumCompletedOrdersStrategy implements EligibilityRuleStrategy {
    @Override
    public EligibilityRuleType supportedType() {
        return EligibilityRuleType.MINIMUM_COMPLETED_ORDERS;
    }

    @Override
    public RuleEvaluation evaluate(TierEligibilityRule rule, EligibilityContext context) {
        int expected = Integer.parseInt(rule.getExpectedValue());
        int actual = context.completedOrderCount();
        return new RuleEvaluation(
                supportedType(),
                String.valueOf(expected),
                String.valueOf(actual),
                actual >= expected);
    }
}
