package com.loyaltyService.wallet_service.service;

import com.loyaltyService.wallet_service.entity.Payment;
import com.loyaltyService.wallet_service.repository.PaymentRepository;
import com.loyaltyService.wallet_service.repository.TransactionRepository;
import com.loyaltyService.wallet_service.service.impl.RazorpayServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RazorpayServiceTest {

    @Mock
    private PaymentRepository paymentRepo;
    @Mock
    private TransactionRepository transactionRepo;
    @Mock
    private WalletCommandService walletCommandService;
    @Mock
    private WalletQueryService walletQueryService;
    @Mock
    private KafkaProducerService kafkaProducer;

    @InjectMocks
    private RazorpayServiceImpl razorpayService;

    @Test
    void createOrderWithoutCredentialsThrows() {
        ReflectionTestUtils.setField(razorpayService, "key", null);
        ReflectionTestUtils.setField(razorpayService, "secret", null);

        assertThrows(Exception.class, () -> razorpayService.createOrder(1L, new BigDecimal("100.00")));
    }

    @Test
    void paymentStatusConstantsUseExpectedLifecycleValues() {
        assertEquals("PENDING", Payment.STATUS_PENDING);
        assertEquals("SUCCESS", Payment.STATUS_SUCCESS);
        assertEquals("FAILED", Payment.STATUS_FAILED);
    }
}
