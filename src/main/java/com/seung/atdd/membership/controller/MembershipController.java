package com.seung.atdd.membership.controller;

import com.seung.atdd.membership.dto.MembershipDetailResponse;
import com.seung.atdd.membership.dto.MembershipRequest;
import com.seung.atdd.membership.dto.MembershipAddResponse;
import com.seung.atdd.membership.service.MembershipService;
import com.seung.atdd.membership.validation.ValidationGroups;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static com.seung.atdd.membership.common.MembershipConstants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/api/v1/memberships")
    public ResponseEntity<MembershipAddResponse> addMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @RequestBody @Validated(ValidationGroups.MembershipAddMarker.class) final MembershipRequest membershipRequest) {

        final MembershipAddResponse membershipResponse = membershipService.addMembership(userId, membershipRequest.getMembershipType(), membershipRequest.getPoint());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipResponse);
    }

    @GetMapping("/api/v1/memberships")
    public ResponseEntity<List<MembershipDetailResponse>> getMembershipList(
            @RequestHeader(USER_ID_HEADER) final String userId) {

        return ResponseEntity.ok(membershipService.getMembershipList(userId));
    }

    @GetMapping("/api/v1/memberships/{id}")
    public ResponseEntity<MembershipDetailResponse> getMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @PathVariable final Long id) {

        return ResponseEntity.ok(membershipService.getMembership(id, userId));
    }

    @DeleteMapping("/api/v1/memberships/{id}")
    public ResponseEntity<Void> removeMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @PathVariable final Long id) {

        membershipService.removeMembership(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/memberships/{id}/accumulate")
    public ResponseEntity<Void> accumulateMembershipPoint(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @PathVariable final Long id,
            @RequestBody @Validated(ValidationGroups.MembershipAccumulateMarker.class) final MembershipRequest membershipRequest) {

        membershipService.accumulateMembershipPoint(id, userId, membershipRequest.getPoint());

        return ResponseEntity.noContent().build();
    }
}
