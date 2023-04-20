package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.CustomerRepository;
import com.amigoscode.testing.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.GBP, Currency.USD);

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;

    void chargeCard(UUID customerId, PaymentRequest paymentRequest) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException
                        (String.format("Customer of id %s does not exist.", customerId)));

        var payment = paymentRequest.getPayment();

        boolean isCurrencySupported = ACCEPTED_CURRENCIES.contains(payment.getCurrency());
        if (!isCurrencySupported) {
            throw new BusinessException("Currency not supported");
        }

        var charge = cardPaymentCharger.chargeCard(
                payment.getSource(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription());

        if (!charge.isCardDebited()) {
            throw new BusinessException(String.format(
                    "Payment was not debited on card %s",
                    payment.getSource()));
        }

        payment.setCustomerId(customerId);
        paymentRepository.save(payment);

        // TODO: send sms
    }
}
