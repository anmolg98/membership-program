package com.firstclub.membership.subscription.application;

import com.firstclub.membership.catalog.domain.*;
import com.firstclub.membership.catalog.infrastructure.MembershipTierRepository;
import com.firstclub.membership.subscription.application.eligibility.*;
import com.firstclub.membership.user.application.CustomerService;
import com.firstclub.membership.user.domain.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TierEligibilityService {
    private final MembershipTierRepository tiers;
    private final CustomerService customers;
    private final EligibilityStrategyRegistry strategies;

    public TierEligibilityService(
            MembershipTierRepository tiers,
            CustomerService customers,
            EligibilityStrategyRegistry strategies) {
        this.tiers = tiers;
        this.customers = customers;
        this.strategies = strategies;
    }

    public Evaluation evaluate(Long customerId) {
        Customer customer = customers.get(customerId);
        MonthlyOrderSummary summary = customers.currentSummary(customerId);
        EligibilityContext context = new EligibilityContext(
                summary.getCompletedOrderCount(),
                summary.getTotalOrderValue(),
                customer.getCohort());
        List<MembershipTier> activeTiers = tiers.findByActiveTrueOrderByRank();

        List<TierResult> results = activeTiers.stream()
                .map(tier -> evaluate(tier, context))
                .toList();
        MembershipTier best = activeTiers.stream()
                .filter(tier -> results.stream()
                        .anyMatch(result -> result.tierId().equals(tier.getId()) && result.eligible()))
                .max(Comparator.comparingInt(MembershipTier::getRank))
                .orElseThrow(() -> new IllegalStateException("At least one base tier must be eligible"));

        return new Evaluation(best.getId(), best.getCode(), results);
    }

    private TierResult evaluate(MembershipTier tier, EligibilityContext context) {
        List<RuleEvaluation> ruleResults = tier.getEligibilityRules().stream()
                .filter(TierEligibilityRule::isActive)
                .map(rule -> strategies.strategyFor(rule.getType()).evaluate(rule, context))
                .toList();
        boolean eligible = ruleResults.stream().allMatch(RuleEvaluation::passed);
        return new TierResult(
                tier.getId(),
                tier.getCode(),
                tier.getName(),
                tier.getRank(),
                eligible,
                tier.getBenefits(),
                ruleResults);
    }

    public record TierResult(
            Long tierId,
            String tier,
            String name,
            int rank,
            boolean eligible,
            Map<BenefitType, String> benefits,
            List<RuleEvaluation> ruleResults) {
    }

    public record Evaluation(
            Long recommendedTierId,
            String recommendedTier,
            List<TierResult> tiers) {
    }
}
