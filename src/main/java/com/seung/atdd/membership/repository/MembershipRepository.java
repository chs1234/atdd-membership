package com.seung.atdd.membership.repository;

import com.seung.atdd.membership.entity.Membership;
import com.seung.atdd.membership.enums.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Membership findByUserIdAndMembershipType(final String userId, final MembershipType membershipType);
}
