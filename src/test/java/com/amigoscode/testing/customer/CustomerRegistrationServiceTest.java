package com.amigoscode.testing.customer;

import com.amigoscode.testing.exception.BusinessException;
import com.amigoscode.testing.utils.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    private final String MAIN_NAME = "Frodo";
    private final String ALT_NAME = "0001";
    private final String PHONE_NUMBER = "0000";

    @Mock private CustomerRepository customerRepository;
    @Mock private PhoneNumberValidator phoneNumberValidator;

    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository, phoneNumberValidator);
    }

    @Test
    void itShouldSaveNewCustomer() {
        // Given a customer
        Customer customer = new Customer(null, MAIN_NAME, PHONE_NUMBER);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // No customer is returned
        given(customerRepository.findByPhoneNumber(PHONE_NUMBER))
                .willReturn(Optional.empty());

        // Phone number is valid
        given(phoneNumberValidator.test(any())).willReturn(true);

        // When
        underTest.registerNewCustomer(request);

        // Then should call save method
        then(customerRepository).should().save(any());
    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {
        // Given a customer
        Customer customer = new Customer(null, MAIN_NAME, PHONE_NUMBER);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.findByPhoneNumber(PHONE_NUMBER))
                .willReturn(Optional.of(customer));

        // Phone number is valid
        given(phoneNumberValidator.test(any())).willReturn(true);

        // When
        underTest.registerNewCustomer(request);

        // Then
        then(customerRepository).should(never()).save(any());
    }

    @Test
    void itShouldThrowWhenPhoneNumberIsTaken() {
        // Given a customer
        Customer customer = new Customer(null, MAIN_NAME, PHONE_NUMBER);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... an existing customer is returned
        Customer existingCustomer = new Customer(null, ALT_NAME, PHONE_NUMBER);
        given(customerRepository.findByPhoneNumber(PHONE_NUMBER))
                .willReturn(Optional.of(existingCustomer));

        // Phone number is valid
        given(phoneNumberValidator.test(any())).willReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Phone number");

        then(customerRepository).should(never()).save(any());
    }

    @Test
    void itShouldThrowWhenPhoneNumberIsNotValid() {
        // Given a customer
        Customer customer = new Customer(null, MAIN_NAME, PHONE_NUMBER);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // No customer is returned
        given(customerRepository.findByPhoneNumber(PHONE_NUMBER))
                .willReturn(Optional.empty());

        // Phone number is valid
        given(phoneNumberValidator.test(any())).willReturn(false);

        // When
        assertThatThrownBy(() ->  underTest.registerNewCustomer(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Phone number");

        // Then should not call save method
        then(customerRepository).shouldHaveNoInteractions();
    }
}