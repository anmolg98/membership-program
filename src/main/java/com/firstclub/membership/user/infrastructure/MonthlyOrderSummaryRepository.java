package com.firstclub.membership.user.infrastructure;

import com.firstclub.membership.user.domain.MonthlyOrderSummary;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface MonthlyOrderSummaryRepository extends JpaRepository<MonthlyOrderSummary, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from MonthlyOrderSummary s where s.customer.id = :customerId and s.orderMonth = :month")
    Optional<MonthlyOrderSummary> findForUpdate(
            @Param("customerId") Long customerId,
            @Param("month") String month);

    Optional<MonthlyOrderSummary> findByCustomerIdAndOrderMonth(Long customerId, String month);
}
