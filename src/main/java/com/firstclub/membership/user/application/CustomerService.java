package com.firstclub.membership.user.application;

import com.firstclub.membership.common.exception.NotFoundException;
import com.firstclub.membership.user.domain.*;
import com.firstclub.membership.user.infrastructure.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.YearMonth;

@Service
public class CustomerService {
    private final CustomerRepository customers;
    private final MonthlyOrderSummaryRepository summaries;

    public CustomerService(CustomerRepository customers, MonthlyOrderSummaryRepository summaries) {
        this.customers = customers;
        this.summaries = summaries;
    }

    public Customer create(String name, String email, String cohort) {
        return customers.save(new Customer(name, email, cohort));
    }

    public Customer get(Long id) {
        return customers.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Transactional
    public Customer getForUpdate(Long id) {
        return customers.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Transactional
    public MonthlyOrderSummary recordOrder(Long customerId, BigDecimal amount) {
        Customer customer = get(customerId);
        String month = YearMonth.now().toString();
        MonthlyOrderSummary summary = summaries.findForUpdate(customerId, month)
                .orElseGet(() -> new MonthlyOrderSummary(customer, YearMonth.now()));
        summary.record(amount);

        try {
            return summaries.save(summary);
        } catch (DataIntegrityViolationException conflict) {
            throw new IllegalStateException(
                    "Concurrent first order detected; retry the request",
                    conflict);
        }
    }

    public MonthlyOrderSummary currentSummary(Long customerId) {
        get(customerId);
        return summaries.findByCustomerIdAndOrderMonth(customerId, YearMonth.now().toString())
                .orElseGet(() -> new MonthlyOrderSummary(get(customerId), YearMonth.now()));
    }
}
