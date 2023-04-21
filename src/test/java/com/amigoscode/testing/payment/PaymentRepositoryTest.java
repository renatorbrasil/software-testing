package com.amigoscode.testing.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
})
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository underTest;

    Payment defaultPayment() {
        return new Payment(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("10.00"),
                Currency.USD, "card123",
                "Donation"
        );
    }

    @Test
    void itShouldInsertPayment() {
        // Given
        var payment = defaultPayment();
        // When
        underTest.save(payment);
        // Then
        var paymentOptional = underTest.findById(payment.getId());
        assertThat(paymentOptional)
                .isPresent()
                .hasValueSatisfying(p ->
                        assertThat(p).isEqualToComparingFieldByField(payment));
    }

    @Test
    void itShouldFindPaymentByIdAndCustomerId() {
        // Given
        var payment = defaultPayment();
        // When
        underTest.save(payment);

        // Then
        var paymentOptional = underTest.findByIdAndCustomerId
                (payment.getId(), payment.getCustomerId());

        assertThat(paymentOptional)
                .isPresent()
                .hasValueSatisfying(p ->
                        assertThat(p).isEqualToComparingFieldByField(payment));
    }
}