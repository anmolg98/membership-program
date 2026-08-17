package com.firstclub.membership.catalog.infrastructure;

import com.firstclub.membership.catalog.domain.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {
    List<MembershipTier> findByActiveTrueOrderByRank();
}
