package com.amigoscode.testing.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PaymentRequest {

    private final UUID customerId;

    private final BigDecimal amount;

    private final Currency currency;

    private final String source;

    private final String description;

    public PaymentRequest(Payment payment) {
        this.customerId = payment.getCustomerId();
        this.amount = payment.getAmount();
        this.currency = payment.getCurrency();
        this.source = payment.getSource();
        this.description = payment.getDescription();
    }

    public Payment getPayment() {
        return new Payment
                (null, customerId, amount, currency, source, description);
    }
}
