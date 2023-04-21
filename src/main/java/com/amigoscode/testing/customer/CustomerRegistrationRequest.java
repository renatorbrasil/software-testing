package com.amigoscode.testing.customer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomerRegistrationRequest {

    private final String name;
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
