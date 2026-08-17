package com.firstclub.membership.subscription.application.eligibility;

import com.firstclub.membership.catalog.domain.*;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class MinimumMonthlyOrderValueStrategy implements EligibilityRuleStrategy {
    @Override
    public EligibilityRuleType supportedType() {
        return EligibilityRuleType.MINIMUM_MONTHLY_ORDER_VALUE;
    }

    @Override
    public RuleEvaluation evaluate(TierEligibilityRule rule, EligibilityContext context) {
        BigDecimal expected = new BigDecimal(rule.getExpectedValue());
        BigDecimal actual = context.monthlyOrderValue();
        return new RuleEvaluation(
                supportedType(),
                expected.toPlainString(),
                actual.toPlainString(),
                actual.compareTo(expected) >= 0);
    }
}
