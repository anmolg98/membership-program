package com.firstclub.membership.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "tier_eligibility_rules")
public class TierEligibilityRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EligibilityRuleType type;

    @Column(nullable = false)
    private String expectedValue;

    @Column(nullable = false)
    private boolean active = true;

    protected TierEligibilityRule() {
    }

    TierEligibilityRule(MembershipTier tier, EligibilityRuleType type, String expectedValue) {
        this.tier = tier;
        this.type = type;
        this.expectedValue = expectedValue;
    }

    public Long getId() {
        return id;
    }

    public EligibilityRuleType getType() {
        return type;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public boolean isActive() {
        return active;
    }
}
