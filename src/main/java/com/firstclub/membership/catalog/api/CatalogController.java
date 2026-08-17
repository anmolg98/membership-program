package com.firstclub.membership.catalog.api;

import com.firstclub.membership.catalog.domain.MembershipPlan;
import com.firstclub.membership.catalog.domain.MembershipTier;
import com.firstclub.membership.catalog.infrastructure.MembershipPlanRepository;
import com.firstclub.membership.catalog.infrastructure.MembershipTierRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CatalogController {
    private final MembershipPlanRepository plans;
    private final MembershipTierRepository tiers;

    public CatalogController(
            MembershipPlanRepository plans,
            MembershipTierRepository tiers) {
        this.plans = plans;
        this.tiers = tiers;
    }

    @GetMapping("/plans")
    public List<MembershipPlan> plans() {
        return plans.findByActiveTrueOrderByDurationMonths();
    }

    @GetMapping("/tiers")
    public List<MembershipTier> tiers() {
        return tiers.findByActiveTrueOrderByRank();
    }

    @GetMapping("/membership-options")
    public Options options() {
        return new Options(plans(), tiers());
    }

    public record Options(
            List<MembershipPlan> plans,
            List<MembershipTier> tiers) {
    }
}
