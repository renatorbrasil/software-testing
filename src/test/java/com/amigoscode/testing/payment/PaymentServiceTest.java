package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import com.amigoscode.testing.exception.BusinessException;
import com.amigoscode.testing.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class PaymentServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private CardPaymentCharger cardPaymentCharger;

    @Captor private ArgumentCaptor<Payment> paymentCaptor;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger);
    }

    Payment defaultPayment(UUID customerId) {
        return new Payment(
                null,
                customerId,
                new BigDecimal("10.00"),
                Currency.USD, "card123",
                "Donation"
        );
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        // Given
        UUID customerId = UUID.randomUUID();
        Payment payment = defaultPayment(customerId);
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(mock(Customer.class)));
        given(cardPaymentCharger.chargeCard(
                payment.getSource(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription()))
                .willReturn(new CardPaymentCharge(true));

        // When
        underTest.chargeCard(paymentRequest);

        then(paymentRepository).should().save(paymentCaptor.capture());

        Payment paymentCaptured = paymentCaptor.getValue();
        assertThat(paymentCaptured).isEqualToIgnoringGivenFields
                (paymentRequest.getPayment(), "id");
        assertThat(paymentCaptured.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void itShouldThrowWhenCardIsNotCharged() {
        // Given
        UUID customerId = UUID.randomUUID();
        Payment payment = defaultPayment(customerId);
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(mock(Customer.class)));
        given(cardPaymentCharger.chargeCard(
                payment.getSource(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription()))
                .willReturn(new CardPaymentCharge(false));

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(paymentRequest))
                .isInstanceOf(BusinessException.class);
        then(paymentRepository).should(never()).save(any());
    }

    @Test
    void itShouldThrowWhenCurrencyNotSupported() {
        // Given
        UUID customerId = UUID.randomUUID();
        Payment payment = defaultPayment(customerId);
        payment.setCurrency(Currency.EUR);
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(mock(Customer.class)));

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(paymentRequest))
                .isInstanceOf(BusinessException.class);
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).should(never()).save(any());
    }

    @Test
    void itShouldThrowWhenCustomerIsNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        Payment payment = defaultPayment(customerId);
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        given(customerRepository.findById(customerId))
                .willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(paymentRequest))
                .isInstanceOf(BusinessException.class);
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).should(never()).save(any());
    }

    @Test
    void itShouldFindPaymentByIdAndCustomerId() {
        // Given
        UUID paymentId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Payment payment = new Payment(
            paymentId,
            customerId,
            new BigDecimal("10.00"),
            Currency.USD, "card123",
            "Donation"
        );

        given(paymentRepository.findByIdAndCustomerId(paymentId, customerId))
                .willReturn(Optional.of(payment));

        // When
        Payment result = underTest.findByIdAndCustomerId(paymentId, customerId);

        // Then
        assertThat(result).isEqualToComparingFieldByField(payment);
    }

    @Test
    void itShouldThrowWhenPaymentNotFound() {
        // Given
        UUID paymentId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        given(paymentRepository.findByIdAndCustomerId(paymentId, customerId))
                .willReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.findByIdAndCustomerId(paymentId, customerId))
                .isInstanceOf(NotFoundException.class);

    }
}