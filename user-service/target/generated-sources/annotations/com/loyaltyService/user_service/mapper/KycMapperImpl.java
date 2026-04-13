package com.loyaltyService.user_service.mapper;

import com.loyaltyService.user_service.dto.KycStatusResponse;
import com.loyaltyService.user_service.entity.KycDetail;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-13T19:33:56+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class KycMapperImpl implements KycMapper {

    @Override
    public KycStatusResponse toResponse(KycDetail entity) {
        if ( entity == null ) {
            return null;
        }

        KycStatusResponse.KycStatusResponseBuilder kycStatusResponse = KycStatusResponse.builder();

        kycStatusResponse.kycId( entity.getId() );
        kycStatusResponse.docNumber( entity.getDocNumber() );
        kycStatusResponse.rejectionReason( entity.getRejectionReason() );
        kycStatusResponse.submittedAt( entity.getSubmittedAt() );
        kycStatusResponse.updatedAt( entity.getUpdatedAt() );

        kycStatusResponse.userId( entity.getUser() != null ? entity.getUser().getId() : null );
        kycStatusResponse.userName( entity.getUser() != null ? entity.getUser().getName() : null );
        kycStatusResponse.userEmail( entity.getUser() != null ? entity.getUser().getEmail() : null );
        kycStatusResponse.docType( entity.getDocType().name() );
        kycStatusResponse.status( entity.getStatus().name() );
        kycStatusResponse.docFilePath( entity.getDocFilePath() );

        return kycStatusResponse.build();
    }
}
