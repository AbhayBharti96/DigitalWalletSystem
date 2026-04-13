package com.loyaltyService.user_service.mapper;

import com.loyaltyService.user_service.dto.AdminUserResponse;
import com.loyaltyService.user_service.dto.UpdateUserRequest;
import com.loyaltyService.user_service.dto.UserProfileResponse;
import com.loyaltyService.user_service.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-13T19:33:56+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserProfileResponse toUserProfile(User user, String kycStatus) {
        if ( user == null && kycStatus == null ) {
            return null;
        }

        UserProfileResponse.UserProfileResponseBuilder userProfileResponse = UserProfileResponse.builder();

        if ( user != null ) {
            userProfileResponse.id( user.getId() );
            userProfileResponse.name( user.getName() );
            userProfileResponse.email( user.getEmail() );
            userProfileResponse.phone( user.getPhone() );
            userProfileResponse.createdAt( user.getCreatedAt() );
        }
        userProfileResponse.status( user.getStatus().name() );
        userProfileResponse.kycStatus( kycStatus != null ? kycStatus : "NOT_SUBMITTED" );

        return userProfileResponse.build();
    }

    @Override
    public AdminUserResponse toAdminResponse(User user) {
        if ( user == null ) {
            return null;
        }

        AdminUserResponse.AdminUserResponseBuilder adminUserResponse = AdminUserResponse.builder();

        adminUserResponse.id( user.getId() );
        adminUserResponse.name( user.getName() );
        adminUserResponse.email( user.getEmail() );
        adminUserResponse.phone( user.getPhone() );
        if ( user.getStatus() != null ) {
            adminUserResponse.status( user.getStatus().name() );
        }
        if ( user.getRole() != null ) {
            adminUserResponse.role( user.getRole().name() );
        }
        adminUserResponse.createdAt( user.getCreatedAt() );
        adminUserResponse.updatedAt( user.getUpdatedAt() );
        adminUserResponse.deletedAt( user.getDeletedAt() );

        return adminUserResponse.build();
    }

    @Override
    public void updateUserFromDto(UpdateUserRequest dto, User user) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            user.setName( dto.getName() );
        }
        if ( dto.getPhone() != null ) {
            user.setPhone( dto.getPhone() );
        }
    }
}
