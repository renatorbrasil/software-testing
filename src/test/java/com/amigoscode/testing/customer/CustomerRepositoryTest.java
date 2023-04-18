package com.amigoscode.testing.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    private final String NAME = "Frodo";
    private final String PHONE_NUMBER = "0000";

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldFindCustomerByPhoneNumber() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, NAME, PHONE_NUMBER);
        // When
        underTest.save(customer);
        // Then
        Optional<Customer> optionalCustomer = underTest.findByPhoneNumber(PHONE_NUMBER);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c ->
                        assertThat(c).isEqualToComparingFieldByField(customer));
    }

    @Test
    void itShouldNotSelectCustomerByPhoneNumberWhenNumberDoesNotExist() {
        // Given
        // When
        Optional<Customer> optionalCustomer = underTest.findByPhoneNumber(PHONE_NUMBER);
        // Then
        assertThat(optionalCustomer).isNotPresent();
    }

    @Test
    void itShouldSaveCustomer() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, NAME, PHONE_NUMBER);
        // When
        underTest.save(customer);
        // Then
        Optional<Customer> optionalCustomer = underTest.findById(id);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c ->
                    assertThat(c).isEqualToComparingFieldByField(customer));
    }
}