package com.amigoscode.testing.customer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class CustomerRegistrationRequest {

    @NotBlank
    private final String name;

    @NotBlank
    private final String phoneNumber;

    public CustomerRegistrationRequest(
            Customer customer
    ) {
        this.name = customer.getName();
        this.phoneNumber = customer.getPhoneNumber();
    }

    public Customer getCustomer() {
        return new Customer(null, name, phoneNumber);
    }
}
