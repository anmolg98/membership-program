package com.firstclub.membership.subscription.domain;

import com.firstclub.membership.catalog.domain.*;
import com.firstclub.membership.user.domain.Customer;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "subscriptions")
public class MembershipSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Customer customer;

    @ManyToOne(optional = false)
    private MembershipPlan plan;

    @ManyToOne(optional = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(nullable = false)
    private Instant startedAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant cancelledAt;

    @Version
    private long version;

    protected MembershipSubscription() {
    }

    public MembershipSubscription(Customer customer, MembershipPlan plan, MembershipTier tier, Instant now) {
        this.customer = customer;
        this.plan = plan;
        this.tier = tier;
        this.status = SubscriptionStatus.ACTIVE;
        this.startedAt = now;
        this.expiresAt = now.atZone(java.time.ZoneOffset.UTC)
                .plusMonths(plan.getDurationMonths())
                .toInstant();
    }

    public void changeTier(MembershipTier tier) {
        this.tier = tier;
    }

    public void cancel(Instant now) {
        if (status == SubscriptionStatus.ACTIVE) {
            status = SubscriptionStatus.CANCELLED;
            cancelledAt = now;
        }
    }

    public void markExpired(Instant now) {
        if (status == SubscriptionStatus.ACTIVE && !expiresAt.isAfter(now)) {
            status = SubscriptionStatus.EXPIRED;
        }
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public MembershipPlan getPlan() {
        return plan;
    }

    public MembershipTier getTier() {
        return tier;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public long getVersion() {
        return version;
    }
}
