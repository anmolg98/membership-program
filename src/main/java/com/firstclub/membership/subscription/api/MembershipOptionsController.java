package com.firstclub.membership.subscription.api;

import com.firstclub.membership.catalog.domain.MembershipPlan;
import com.firstclub.membership.catalog.infrastructure.MembershipPlanRepository;
import com.firstclub.membership.subscription.application.TierEligibilityService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/membership-options")
public class MembershipOptionsController {
    private final MembershipPlanRepository plans;
    private final TierEligibilityService eligibility;

    public MembershipOptionsController(MembershipPlanRepository plans, TierEligibilityService eligibility) {
        this.plans = plans;
        this.eligibility = eligibility;
    }

    @GetMapping
    public Options options(@PathVariable Long userId) {
        TierEligibilityService.Evaluation evaluation = eligibility.evaluate(userId);
        return new Options(plans.findByActiveTrueOrderByDurationMonths(), evaluation.tiers());
    }

    public record Options(
            List<MembershipPlan> plans,
            List<TierEligibilityService.TierResult> tiers) {
    }
}
