package com.firstclub.membership.subscription.application.eligibility;

import com.firstclub.membership.catalog.domain.EligibilityRuleType;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EligibilityStrategyRegistry {
    private final Map<EligibilityRuleType, EligibilityRuleStrategy> strategies;

    public EligibilityStrategyRegistry(List<EligibilityRuleStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(
                        EligibilityRuleStrategy::supportedType,
                        Function.identity()));
    }

    public EligibilityRuleStrategy strategyFor(EligibilityRuleType type) {
        EligibilityRuleStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalStateException("No eligibility strategy registered for " + type);
        }

        return strategy;
    }
}
