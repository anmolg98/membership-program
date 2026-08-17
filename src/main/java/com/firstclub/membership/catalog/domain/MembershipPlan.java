package com.firstclub.membership.catalog.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "membership_plans", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class MembershipPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanCode code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int durationMonths;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active = true;

    protected MembershipPlan() {
    }

    public MembershipPlan(PlanCode code, String name, int durationMonths, BigDecimal price) {
        this.code = code;
        this.name = name;
        this.durationMonths = durationMonths;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public PlanCode getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isActive() {
        return active;
    }
}
