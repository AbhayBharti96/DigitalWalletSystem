package com.loyaltyService.wallet_service.service.impl;

import com.loyaltyService.wallet_service.dto.PaymentFailureRequest;
import com.loyaltyService.wallet_service.dto.PaymentVerifyRequest;
import com.loyaltyService.wallet_service.entity.Payment;
import com.loyaltyService.wallet_service.entity.Transaction;
import com.loyaltyService.wallet_service.repository.PaymentRepository;
import com.loyaltyService.wallet_service.repository.TransactionRepository;
import com.loyaltyService.wallet_service.service.RazorpayService;
import com.loyaltyService.wallet_service.service.WalletCommandService;
import com.loyaltyService.wallet_service.service.WalletQueryService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {

    private final PaymentRepository paymentRepo;
    private final TransactionRepository transactionRepo;
    private final WalletCommandService walletCommandService;
    private final WalletQueryService walletQueryService;
    private final com.loyaltyService.wallet_service.service.KafkaProducerService kafkaProducer;

    @Value("${razorpay.key}")
    private String key;

    @Value("${razorpay.secret}")
    private String secret;

    @Override
    @Transactional
    public Order createOrder(Long userId, BigDecimal amount) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(key, secret);

        JSONObject options = new JSONObject();
        options.put("amount", amount.multiply(BigDecimal.valueOf(100)));
        options.put("currency", "INR");
        options.put("receipt", "wallet_" + System.currentTimeMillis());

        Order order = client.orders.create(options);

        paymentRepo.save(
                Payment.builder()
                        .orderId(order.get("id"))
                        .userId(userId)
                        .amount(amount)
                        .status(Payment.STATUS_PENDING)
                        .build()
        );

        transactionRepo.save(
                Transaction.builder()
                        .receiverId(userId)
                        .amount(amount)
                        .status(Transaction.TxnStatus.PENDING)
                        .type(Transaction.TxnType.TOPUP)
                        .referenceId(order.get("id"))
                        .idempotencyKey(order.get("id"))
                        .description("Wallet top-up initiated")
                        .build()
        );

        return order;
    }

    @Override
    @Transactional
    public void verifyPayment(Long userId, PaymentVerifyRequest req) throws RazorpayException {
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", req.getRazorpayOrderId());
        options.put("razorpay_payment_id", req.getRazorpayPaymentId());
        options.put("razorpay_signature", req.getRazorpaySignature());

        boolean isValid = Utils.verifyPaymentSignature(options, secret);
        if (!isValid) {
            markPaymentAsFailed(req.getRazorpayOrderId(), req.getRazorpayPaymentId(), "Invalid signature");
            throw new RuntimeException("Invalid signature");
        }

        Payment payment = paymentRepo.findById(req.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!payment.getUserId().equals(userId)) {
            throw new RuntimeException("Payment does not belong to this user");
        }
        if (Payment.STATUS_SUCCESS.equals(payment.getStatus())) {
            throw new RuntimeException("Payment already processed");
        }
        if (Payment.STATUS_FAILED.equals(payment.getStatus())) {
            throw new RuntimeException("Payment already marked as failed");
        }

        walletCommandService.topup(userId, payment.getAmount(), payment.getOrderId());

        payment.setPaymentId(req.getRazorpayPaymentId());
        payment.setStatus(Payment.STATUS_SUCCESS);
        paymentRepo.save(payment);

        BigDecimal updatedBalance = walletQueryService.getBalance(userId).getBalance();
        kafkaProducer.send("payment-events", Map.of(
                "event", "PAYMENT_SUCCESS",
                "userId", payment.getUserId(),
                "amount", payment.getAmount(),
                "orderId", payment.getOrderId(),
                "balance", updatedBalance
        ));
    }

    @Override
    @Transactional
    public void markPaymentFailed(Long userId, PaymentFailureRequest req) {
        Payment payment = paymentRepo.findById(req.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!payment.getUserId().equals(userId)) {
            throw new RuntimeException("Payment does not belong to this user");
        }
        if (Payment.STATUS_SUCCESS.equals(payment.getStatus())) {
            throw new RuntimeException("Successful payment cannot be marked as failed");
        }

        markPaymentAsFailed(req.getRazorpayOrderId(), req.getRazorpayPaymentId(), req.getReason());
    }

    private void markPaymentAsFailed(String orderId, String paymentId, String reason) {
        Payment payment = paymentRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (Payment.STATUS_SUCCESS.equals(payment.getStatus())) {
            return;
        }

        payment.setPaymentId(paymentId);
        payment.setStatus(Payment.STATUS_FAILED);
        paymentRepo.save(payment);

        transactionRepo.findByReferenceId(orderId).ifPresent(txn -> {
            if (txn.getStatus() != Transaction.TxnStatus.SUCCESS) {
                txn.setStatus(Transaction.TxnStatus.FAILED);
                txn.setDescription(reason != null && !reason.isBlank() ? reason : "Payment failed");
                transactionRepo.save(txn);
            }
        });

        log.info("Payment marked as failed: orderId={}, paymentId={}, reason={}", orderId, paymentId, reason);
    }
}
