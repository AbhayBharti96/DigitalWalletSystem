package com.loyaltyService.user_service.service.impl;

import com.loyaltyService.user_service.dto.TransferRecipientResponse;
import com.loyaltyService.user_service.dto.UserProfileResponse;
import com.loyaltyService.user_service.entity.User;
import com.loyaltyService.user_service.exception.ResourceNotFoundException;
import com.loyaltyService.user_service.mapper.UserMapper;
import com.loyaltyService.user_service.repository.KycRepository;
import com.loyaltyService.user_service.repository.UserRepository;
import com.loyaltyService.user_service.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepo;
    private final KycRepository kycRepo;
    private final UserMapper userMapper;

    @Override
    @Cacheable(value = "user-profile", key = "#userId")
    public UserProfileResponse getProfile(Long userId) {
        log.debug("Cache miss - loading user profile from DB for userId={}", userId);
        return buildUserProfile(userId);
    }

    @Override
    @Cacheable(value = "user-profile", key = "#userId")
    public UserProfileResponse getUserProfile(Long userId) {
        return buildUserProfile(userId);
    }

    @Override
    @Cacheable(value = "user-status", key = "#userId")
    public String getUserStatus(Long userId) {
        log.debug("Cache miss - loading user status from DB for userId={}", userId);

        User user = findUser(userId);
        return user.getStatus() != null ? user.getStatus().name() : "ACTIVE";
    }

    @Override
    public List<TransferRecipientResponse> searchTransferRecipients(Long requesterId, String query, int limit) {
        String normalizedQuery = query == null ? "" : query.trim();
        if (normalizedQuery.isBlank()) {
            return List.of();
        }

        int safeLimit = Math.max(1, Math.min(limit, 20));
        Map<Long, TransferRecipientResponse> recipientsById = new LinkedHashMap<>();

        if (normalizedQuery.chars().allMatch(Character::isDigit)) {
            Long exactUserId = Long.valueOf(normalizedQuery);
            userRepo.findById(exactUserId)
                    .filter(user -> isTransferCandidate(user, requesterId))
                    .ifPresent(user -> recipientsById.put(user.getId(), toTransferRecipient(user)));
        }

        userRepo.searchByKeyword(normalizedQuery, PageRequest.of(0, safeLimit))
                .stream()
                .filter(user -> isTransferCandidate(user, requesterId))
                .map(this::toTransferRecipient)
                .forEach(recipient -> recipientsById.putIfAbsent(recipient.getId(), recipient));

        List<TransferRecipientResponse> recipients = new ArrayList<>(recipientsById.values());
        return recipients.subList(0, Math.min(recipients.size(), safeLimit));
    }

    private UserProfileResponse buildUserProfile(Long userId) {
        User user = findUser(userId);
        String kycStatus = kycRepo.findFirstByUserIdOrderBySubmittedAtDesc(userId)
                .map(k -> k.getStatus().name())
                .orElse("NOT_SUBMITTED");
        return userMapper.toUserProfile(user, kycStatus);
    }

    private User findUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private boolean isTransferCandidate(User user, Long requesterId) {
        return !user.getId().equals(requesterId) && user.isActive();
    }

    private TransferRecipientResponse toTransferRecipient(User user) {
        String kycStatus = kycRepo.findFirstByUserIdOrderBySubmittedAtDesc(user.getId())
                .map(k -> k.getStatus().name())
                .orElse("NOT_SUBMITTED");

        return TransferRecipientResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .kycStatus(kycStatus)
                .build();
    }
}
