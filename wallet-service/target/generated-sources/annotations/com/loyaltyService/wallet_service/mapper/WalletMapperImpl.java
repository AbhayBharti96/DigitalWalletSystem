package com.loyaltyService.wallet_service.mapper;

import com.loyaltyService.wallet_service.dto.WalletBalanceResponse;
import com.loyaltyService.wallet_service.entity.WalletAccount;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-02T22:38:01+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class WalletMapperImpl implements WalletMapper {

    @Override
    public WalletBalanceResponse toResponse(WalletAccount account, Long userId) {
        if ( account == null && userId == null ) {
            return null;
        }

        WalletBalanceResponse.WalletBalanceResponseBuilder walletBalanceResponse = WalletBalanceResponse.builder();

        if ( account != null ) {
            walletBalanceResponse.balance( account.getBalance() );
        }
        walletBalanceResponse.userId( userId );
        walletBalanceResponse.status( account.getStatus().name() );

        return walletBalanceResponse.build();
    }
}
