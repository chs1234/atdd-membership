package com.seung.atdd.membership.service;

import com.seung.atdd.membership.dto.MembershipAddResponse;
import com.seung.atdd.membership.dto.MembershipDetailResponse;
import com.seung.atdd.membership.entity.Membership;
import com.seung.atdd.membership.enums.MembershipType;
import com.seung.atdd.membership.exception.MembershipErrorResult;
import com.seung.atdd.membership.exception.MembershipException;
import com.seung.atdd.membership.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipAddResponse addMembership(final String userId, final MembershipType membershipType, final Integer point) {
        final Membership result = membershipRepository.findByUserIdAndMembershipType(userId, membershipType);
        if (result != null)
            throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);

        final Membership membership = Membership.builder()
                .userId(userId)
                .point(point)
                .membershipType(membershipType)
                .build();

        final Membership savedMembership = membershipRepository.save(membership);

        return MembershipAddResponse.builder()
                .id(savedMembership.getId())
                .membershipType(savedMembership.getMembershipType())
                .build();
    }

    public List<MembershipDetailResponse> getMembershipList(final String userId) {
        final List<Membership> membershipList = membershipRepository.findAllByUserId(userId);

        return membershipList.stream()
                .map(v -> MembershipDetailResponse.builder()
                        .id(v.getId())
                        .membershipType(v.getMembershipType())
                        .point(v.getPoint())
                        .createdAt(v.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
