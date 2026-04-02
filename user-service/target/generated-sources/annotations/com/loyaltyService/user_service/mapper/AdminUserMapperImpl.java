package com.loyaltyService.user_service.mapper;

import com.loyaltyService.user_service.dto.AdminUserResponse;
import com.loyaltyService.user_service.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-02T22:37:39+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class AdminUserMapperImpl implements AdminUserMapper {

    @Override
    public AdminUserResponse toDto(User user) {
        if ( user == null ) {
            return null;
        }

        AdminUserResponse.AdminUserResponseBuilder adminUserResponse = AdminUserResponse.builder();

        adminUserResponse.id( user.getId() );
        adminUserResponse.name( user.getName() );
        adminUserResponse.email( user.getEmail() );
        adminUserResponse.phone( user.getPhone() );
        adminUserResponse.createdAt( user.getCreatedAt() );
        adminUserResponse.updatedAt( user.getUpdatedAt() );
        adminUserResponse.deletedAt( user.getDeletedAt() );

        adminUserResponse.status( user.getStatus().name() );
        adminUserResponse.role( user.getRole().name() );

        return adminUserResponse.build();
    }
}
