package com.amigoscode.testing.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerRegistrationService {

    @Autowired
    private CustomerRepository customerRepository;

    public void registerNewCustomer(CustomerRegistrationRequest request) {

    }
}
