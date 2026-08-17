package com.firstclub.membership.catalog.domain;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "membership_tiers", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class MembershipTier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int rank;

    @Column(nullable = false)
    private boolean active = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tier_benefits", joinColumns = @JoinColumn(name = "tier_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "benefit_type")
    @Column(name = "benefit_value", nullable = false)
    private Map<BenefitType, String> benefits = new LinkedHashMap<>();

    @OneToMany(mappedBy = "tier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<TierEligibilityRule> eligibilityRules = new ArrayList<>();

    protected MembershipTier() {
    }

    public MembershipTier(String code, String name, int rank, Map<BenefitType, String> benefits) {
        this.code = code;
        this.name = name;
        this.rank = rank;
        this.benefits.putAll(benefits);
    }

    public MembershipTier addRule(EligibilityRuleType type, String expectedValue) {
        eligibilityRules.add(new TierEligibilityRule(this, type, expectedValue));
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getRank() {
        return rank;
    }

    public boolean isActive() {
        return active;
    }

    public Map<BenefitType, String> getBenefits() {
        return Map.copyOf(benefits);
    }

    public List<TierEligibilityRule> getEligibilityRules() {
        return List.copyOf(eligibilityRules);
    }
}
