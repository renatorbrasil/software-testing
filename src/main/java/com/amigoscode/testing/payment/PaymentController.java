package com.amigoscode.testing.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse makePayment(
            @RequestBody PaymentRequest request
    ) {
        var paymentId = paymentService.chargeCard(request);
        return new PaymentResponse(paymentId);
    }

/*    @GetMapping("/{customerId}/{paymentId}")
    public Payment getPaymentById(
            @PathVariable("customerId") UUID customerId,
            @PathVariable("paymentId") UUID paymentId
    ) {

    }*/
}
