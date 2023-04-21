package com.amigoscode.testing.customer;

import com.amigoscode.testing.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class CustomerRegistrationServiceTest {

    private final String MAIN_NAME = "Frodo";
    private final String ALT_NAME = "0001";
    private final String PHONE_NUMBER = "0000";

    @Mock private CustomerRepository customerRepository;

    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer() {
        // Given a customer
        Customer customer = new Customer(UUID.randomUUID(), MAIN_NAME, PHONE_NUMBER);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // No customer is returned
        given(customerRepository.findByPhoneNumber(PHONE_NUMBER))
                .willReturn(Optional.empty());

        // When
        underTest.registerNewCustomer(request);

        // Then should call save method
        then(customerRepository).should().save(any());
    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {
        // Given a customer
        Customer customer = new Customer(UUID.randomUUID(), MAIN_NAME, PHONE_NUMBER);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.findByPhoneNumber(PHONE_NUMBER))
                .willReturn(Optional.of(customer));

        // When
        underTest.registerNewCustomer(request);

        // Then
        then(customerRepository).should(never()).save(any());
    }

    @Test
    void itShouldThrowWhenPhoneNumberIsTaken() {
        // Given a customer
        Customer customer = new Customer(UUID.randomUUID(), MAIN_NAME, PHONE_NUMBER);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... an existing customer is returned
        Customer existingCustomer = new Customer(UUID.randomUUID(), ALT_NAME, PHONE_NUMBER);
        given(customerRepository.findByPhoneNumber(PHONE_NUMBER))
                .willReturn(Optional.of(existingCustomer));

        // When
        // Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(BusinessException.class);

        then(customerRepository).should(never()).save(any());
    }
}