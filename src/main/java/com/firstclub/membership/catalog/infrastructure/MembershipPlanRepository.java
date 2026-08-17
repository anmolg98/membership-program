package com.firstclub.membership.catalog.infrastructure;

import com.firstclub.membership.catalog.domain.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    List<MembershipPlan> findByActiveTrueOrderByDurationMonths();
}
