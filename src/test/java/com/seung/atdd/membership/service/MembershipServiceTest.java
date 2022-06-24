package com.seung.atdd.membership.service;

import com.seung.atdd.membership.dto.MembershipAddResponse;
import com.seung.atdd.membership.dto.MembershipDetailResponse;
import com.seung.atdd.membership.entity.Membership;
import com.seung.atdd.membership.enums.MembershipType;
import com.seung.atdd.membership.exception.MembershipErrorResult;
import com.seung.atdd.membership.exception.MembershipException;
import com.seung.atdd.membership.repository.MembershipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {

    @InjectMocks
    private MembershipService target;

    @Mock
    private RatePointService ratePointService;

    @Mock
    private MembershipRepository membershipRepository;

    private final String userId = "userId";
    private final MembershipType membershipType = MembershipType.NAVER;
    private final Integer point = 10000;
    private final Long membershipId = -1L;

    /*
    TODO TDD로 멤버십 삭제 및 포인트 적립 API 구현 예제 - (5/5)
    https://mangkyu.tistory.com/186
     */

    @Test
    public void 멤버십삭제실패_존재하지않음() {
        // given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);

        // when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.removeMembership(membershipId, userId));

        // then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십삭제실패_본인이아님() {
        // given
        final Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        // when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.removeMembership(membershipId, "notowner"));

        // then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
    }

    @Test
    public void 멤버십삭제성공() {
        // given
        final Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        // when
        target.removeMembership(membershipId, userId);

        // then
    }

    @Test
    public void _10000원의적립은100원() {
        // given
        final int price = 10000;

        // when
        final int result = ratePointService.calculateAmount(price);

        // then
        assertThat(result).isEqualTo(100);
    }

    @Test
    public void _20000원의적립은200원() {
        // given
        final int price = 20000;

        // when
        final int result = ratePointService.calculateAmount(price);

        // then
        assertThat(result).isEqualTo(200);
    }

    @Test
    public void _30000원의적립은300원() {
        // given
        final int price = 30000;

        // when
        final int result = ratePointService.calculateAmount(price);

        // then
        assertThat(result).isEqualTo(300);
    }

    @Test
    public void 멤버십적립일때_존재하지않음() {
        // given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);

        // when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.accumulateMembershipPoint(membershipId, userId, 10000));

        // then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십적립일때_본인이아님() {
        // given
        final Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        // when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.accumulateMembershipPoint(membershipId, "notowner", 10000));

        // then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
    }

    @Test
    public void 멤버십적립성공() {
        // given
        final Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        // when
        target.accumulateMembershipPoint(membershipId, userId, 10000);

        // then
    }

    /*
    TODO TDD로 멤버십 전체/상세 조회 API 구현 예제 - (4/5)
    https://mangkyu.tistory.com/185
     */

    @Test
    public void 멤버십목록조회() {
        // given
        doReturn(Arrays.asList(
                Membership.builder().build(),
                Membership.builder().build(),
                Membership.builder().build()
        )).when(membershipRepository).findAllByUserId(userId);

        // when
        final List<MembershipDetailResponse> result = target.getMembershipList(userId);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 멤버십상세조회실패_존재하지않음() {
        // given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);

        // when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.getMembership(membershipId, userId));

        // then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십상세조회실패_본인이아님() {
        // given
        doReturn(Arrays.asList(
                Membership.builder().build(),
                Membership.builder().build(),
                Membership.builder().build()
        )).when(membershipRepository).findAllByUserId(userId);

        // when
        final List<MembershipDetailResponse> result = target.getMembershipList(userId);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 멤버십상세조회성공() {
        // given
        doReturn(Arrays.asList(
                Membership.builder().build(),
                Membership.builder().build(),
                Membership.builder().build()
        )).when(membershipRepository).findAllByUserId(userId);

        // when
        final List<MembershipDetailResponse> result = target.getMembershipList(userId);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    /*
    TODO TDD로 멤버십 등록 API 구현 예제 - (3/5)
    https://mangkyu.tistory.com/184?category=761302
     */

    @Test
    public void 멤버십등록실패_이미존재함() {
        // given
        doReturn(Membership.builder().build()).when(membershipRepository).findByUserIdAndMembershipType(userId, membershipType);

        // when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.addMembership(userId, membershipType, point));

        // then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
    }

    @Test
    public void 멤버십등록성공() {
        // given
        doReturn(null).when(membershipRepository).findByUserIdAndMembershipType(userId, membershipType);
        doReturn(membership()).when(membershipRepository).save(any(Membership.class));

        // when
        final MembershipAddResponse result = target.addMembership(userId, membershipType, point);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.NAVER);

        // verify
        verify(membershipRepository, times(1)).findByUserIdAndMembershipType(userId, membershipType);
        verify(membershipRepository, times(1)).save(any(Membership.class));
    }

    private Membership membership() {
        return Membership.builder()
                .id(-1L)
                .userId(userId)
                .point(point)
                .membershipType(MembershipType.NAVER)
                .build();
    }
}
