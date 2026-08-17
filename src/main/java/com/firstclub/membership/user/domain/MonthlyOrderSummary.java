package com.firstclub.membership.user.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(
        name = "monthly_order_summaries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "orderMonth"}))
public class MonthlyOrderSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Customer customer;

    @Column(nullable = false)
    private String orderMonth;

    @Column(nullable = false)
    private int completedOrderCount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalOrderValue = BigDecimal.ZERO;

    @Version
    private long version;

    protected MonthlyOrderSummary() {
    }

    public MonthlyOrderSummary(Customer customer, YearMonth month) {
        this.customer = customer;
        this.orderMonth = month.toString();
    }

    public void record(BigDecimal amount) {
        completedOrderCount++;
        totalOrderValue = totalOrderValue.add(amount);
    }

    public int getCompletedOrderCount() {
        return completedOrderCount;
    }

    public BigDecimal getTotalOrderValue() {
        return totalOrderValue;
    }

    public String getOrderMonth() {
        return orderMonth;
    }
}
