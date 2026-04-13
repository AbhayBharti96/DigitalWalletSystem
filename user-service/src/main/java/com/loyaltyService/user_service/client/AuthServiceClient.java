package com.loyaltyService.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", fallback = AuthServiceClientFallback.class)
public interface AuthServiceClient {

    @PutMapping("/api/auth/internal/update-profile")
    void updateProfile(@RequestBody UpdateProfileRequest request);

    @PutMapping("/api/auth/internal/status")   // 🔥 NEW
    void updateStatus(@RequestBody StatusUpdateRequest request);

    @PutMapping("/api/auth/internal/role")
    void updateRole(@RequestBody RoleUpdateRequest request);

    @PutMapping("/api/auth/internal/kyc-status")
    void updateKycStatus(@RequestBody KycStatusUpdateRequest request);

    record UpdateProfileRequest(Long userId, String name, String phone) {}

    record StatusUpdateRequest(Long userId, String status) {} // 🔥 NEW
    record RoleUpdateRequest(Long userId, String role) {}
    record KycStatusUpdateRequest(Long userId, String kycStatus) {}
}
