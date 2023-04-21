package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.CustomerRepository;
import com.amigoscode.testing.exception.BusinessException;
import com.amigoscode.testing.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
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

    public UUID chargeCard(PaymentRequest paymentRequest) {
        var payment = paymentRequest.getPayment();
        var customerId = payment.getCustomerId();

        customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException
                        (String.format("Customer of id %s does not exist.", customerId)));

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

        var paymentId = UUID.randomUUID();
        payment.setId(paymentId);
        paymentRepository.save(payment);

        // TODO: send sms

        return paymentId;
    }

    public Payment findByIdAndCustomerId(UUID paymentId, UUID customerId) {
        return paymentRepository
                .findByIdAndCustomerId(paymentId, customerId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Payment of id [%s] and customerId [%s] not found",
                                paymentId, customerId)));
    }
}
