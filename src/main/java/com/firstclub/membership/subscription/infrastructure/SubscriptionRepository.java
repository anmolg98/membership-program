package com.firstclub.membership.subscription.infrastructure;

import com.firstclub.membership.subscription.domain.MembershipSubscription;
import com.firstclub.membership.subscription.domain.SubscriptionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<MembershipSubscription, Long> {
    Optional<MembershipSubscription> findFirstByCustomerIdOrderByStartedAtDescIdDesc(Long customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<MembershipSubscription> findFirstByCustomerIdAndStatusOrderByStartedAtDescIdDesc(
            Long customerId, SubscriptionStatus status);
}
