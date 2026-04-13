package com.loyaltyService.user_service.service;

import com.loyaltyService.user_service.dto.UserProfileResponse;
import com.loyaltyService.user_service.dto.TransferRecipientResponse;

import java.util.List;

/**
 * CQRS — Query side: all read operations for User.
 * Results are cached in Redis.
 */
public interface UserQueryService {

    UserProfileResponse getProfile(Long userId);

    UserProfileResponse getUserProfile(Long userId);

    String getUserStatus(Long userId);

    List<TransferRecipientResponse> searchTransferRecipients(Long requesterId, String query, int limit);
}
