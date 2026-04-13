package com.loyaltyService.user_service.service.impl;

import com.loyaltyService.user_service.dto.TransferRecipientResponse;
import com.loyaltyService.user_service.dto.UserProfileResponse;
import com.loyaltyService.user_service.entity.KycDetail;
import com.loyaltyService.user_service.entity.User;
import com.loyaltyService.user_service.exception.ResourceNotFoundException;
import com.loyaltyService.user_service.mapper.UserMapper;
import com.loyaltyService.user_service.repository.KycRepository;
import com.loyaltyService.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserQueryServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private KycRepository kycRepo;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserQueryServiceImpl userQueryService;

    private User testUser;
    private UserProfileResponse testResponse;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).name("Test User").role(User.Role.USER).build();
        testResponse = new UserProfileResponse();
        testResponse.setId(1L);
        testResponse.setName("Test User");
        testResponse.setKycStatus("APPROVED");
    }

    @Test
    void testGetProfile_Success() {
        // Arrange
        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));
        
        KycDetail kyc = KycDetail.builder()
                .status(KycDetail.KycStatus.APPROVED)
                .build();
        when(kycRepo.findFirstByUserIdOrderBySubmittedAtDesc(1L)).thenReturn(Optional.of(kyc));
        
        when(userMapper.toUserProfile(testUser, "APPROVED")).thenReturn(testResponse);

        // Act
        UserProfileResponse res = userQueryService.getProfile(1L);

        // Assert
        assertNotNull(res);
        assertEquals("Test User", res.getName());
        assertEquals("APPROVED", res.getKycStatus());
    }

    @Test
    void testGetProfile_UserNotFound() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userQueryService.getProfile(99L));
    }

    @Test
    void testGetProfile_NoKyc() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));
        when(kycRepo.findFirstByUserIdOrderBySubmittedAtDesc(1L)).thenReturn(Optional.empty());
        when(userMapper.toUserProfile(testUser, "NOT_SUBMITTED")).thenReturn(testResponse);

        UserProfileResponse res = userQueryService.getProfile(1L);
        
        assertNotNull(res);
        verify(userMapper, times(1)).toUserProfile(testUser, "NOT_SUBMITTED");
    }

    @Test
    void testGetUserProfile_Success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));
        when(kycRepo.findFirstByUserIdOrderBySubmittedAtDesc(1L)).thenReturn(Optional.empty());
        when(userMapper.toUserProfile(testUser, "NOT_SUBMITTED")).thenReturn(testResponse);

        UserProfileResponse res = userQueryService.getUserProfile(1L);
        
        assertNotNull(res);
    }

    @Test
    void searchTransferRecipientsFindsByNameAndExcludesRequester() {
        User otherUser = User.builder()
                .id(2L)
                .name("Test Receiver")
                .phone("9999999998")
                .status(User.UserStatus.ACTIVE)
                .build();
        when(userRepo.searchByKeyword("test", PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(testUser, otherUser), PageRequest.of(0, 10), 2));
        when(kycRepo.findFirstByUserIdOrderBySubmittedAtDesc(2L))
                .thenReturn(Optional.of(KycDetail.builder().status(KycDetail.KycStatus.APPROVED).build()));

        List<TransferRecipientResponse> recipients = userQueryService.searchTransferRecipients(1L, "test", 10);

        assertEquals(1, recipients.size());
        assertEquals(2L, recipients.getFirst().getId());
        assertEquals("APPROVED", recipients.getFirst().getKycStatus());
    }

    @Test
    void searchTransferRecipientsFindsByExactId() {
        User otherUser = User.builder()
                .id(22L)
                .name("Receiver")
                .phone("9999999997")
                .status(User.UserStatus.ACTIVE)
                .build();
        when(userRepo.findById(22L)).thenReturn(Optional.of(otherUser));
        when(userRepo.searchByKeyword("22", PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));
        when(kycRepo.findFirstByUserIdOrderBySubmittedAtDesc(22L)).thenReturn(Optional.empty());

        List<TransferRecipientResponse> recipients = userQueryService.searchTransferRecipients(1L, "22", 10);

        assertEquals(1, recipients.size());
        assertEquals(22L, recipients.getFirst().getId());
        assertEquals("NOT_SUBMITTED", recipients.getFirst().getKycStatus());
    }
}
