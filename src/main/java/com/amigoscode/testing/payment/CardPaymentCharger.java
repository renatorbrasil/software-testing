package com.amigoscode.testing.payment;

import com.amigoscode.testing.exception.IntegrationException;

import java.math.BigDecimal;

public interface CardPaymentCharger {

    CardPaymentCharge chargeCard(
            String cardSource,
            BigDecimal amount,
            Currency currency,
            String description
    ) throws IntegrationException;

}
