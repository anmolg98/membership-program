package com.firstclub.membership.config;

import com.firstclub.membership.catalog.domain.*;
import com.firstclub.membership.catalog.infrastructure.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@Configuration
public class SeedDataConfig {
    @Bean
    CommandLineRunner seed(MembershipPlanRepository plans, MembershipTierRepository tiers) {
        return args -> {
            plans.save(new MembershipPlan(PlanCode.MONTHLY, "Monthly", 1, new BigDecimal("299.00")));
            plans.save(new MembershipPlan(PlanCode.QUARTERLY, "Quarterly", 3, new BigDecimal("799.00")));
            plans.save(new MembershipPlan(PlanCode.YEARLY, "Yearly", 12, new BigDecimal("2499.00")));
            tiers.save(new MembershipTier("SILVER", "Silver", 1,
                    Map.of(BenefitType.FREE_DELIVERY, "eligible_orders", BenefitType.DISCOUNT_PERCENT, "2")));
            tiers.save(new MembershipTier("GOLD", "Gold", 2,
                    Map.of(BenefitType.FREE_DELIVERY, "all_orders", BenefitType.DISCOUNT_PERCENT, "5",
                            BenefitType.EARLY_SALE_ACCESS, "true"))
                    .addRule(EligibilityRuleType.MINIMUM_COMPLETED_ORDERS, "5")
                    .addRule(EligibilityRuleType.MINIMUM_MONTHLY_ORDER_VALUE, "5000.00"));
            tiers.save(new MembershipTier("PLATINUM", "Platinum", 3,
                    Map.of(BenefitType.FREE_DELIVERY, "express", BenefitType.DISCOUNT_PERCENT, "10",
                            BenefitType.EARLY_SALE_ACCESS, "true", BenefitType.PRIORITY_SUPPORT, "true",
                            BenefitType.EXCLUSIVE_COUPONS, "true"))
                    .addRule(EligibilityRuleType.MINIMUM_COMPLETED_ORDERS, "10")
                    .addRule(EligibilityRuleType.MINIMUM_MONTHLY_ORDER_VALUE, "15000.00")
                    .addRule(EligibilityRuleType.REQUIRED_COHORT, "PREMIUM"));
        };
    }
}
