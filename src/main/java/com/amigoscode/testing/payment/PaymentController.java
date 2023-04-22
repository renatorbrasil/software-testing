package com.amigoscode.testing.payment;

import com.amigoscode.testing.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse makePayment(
            @RequestBody @Valid PaymentRequest request
    ) {
        var paymentId = paymentService.chargeCard(request);
        return new PaymentResponse(paymentId);
    }

    @GetMapping("/{customerId}/{paymentId}")
    public ResponseEntity<Payment> getPaymentById(
            @PathVariable("customerId") UUID customerId,
            @PathVariable("paymentId") UUID paymentId
    ) {
        try {
            var payment = paymentService.findByIdAndCustomerId(paymentId, customerId);
            return ResponseEntity.ok(payment);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
