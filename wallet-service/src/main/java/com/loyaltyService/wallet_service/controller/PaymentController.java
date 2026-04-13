package com.loyaltyService.wallet_service.controller;

import com.loyaltyService.wallet_service.dto.PaymentFailureRequest;
import com.loyaltyService.wallet_service.dto.PaymentVerifyRequest;
import com.loyaltyService.wallet_service.service.RazorpayService;
import com.razorpay.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam BigDecimal amount) throws Exception {

        Order order = razorpayService.createOrder(userId, amount);
        return ResponseEntity.ok(Map.of(
                "orderId", order.get("id"),
                "amount", order.get("amount"),
                "currency", order.get("currency")));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody PaymentVerifyRequest request) {

        try {
            razorpayService.verifyPayment(userId, request);
            return ResponseEntity.ok("Payment verified & wallet credited");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/fail")
    public ResponseEntity<?> markFailed(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody PaymentFailureRequest request) {

        try {
            razorpayService.markPaymentFailed(userId, request);
            return ResponseEntity.ok("Payment marked as failed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
