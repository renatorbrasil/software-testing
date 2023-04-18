package com.amigoscode.testing.customer;

import com.amigoscode.testing.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        Customer customerToInsert = request.getCustomer();
        String phoneNumber = customerToInsert.getPhoneNumber();

        var customerOptional = customerRepository
                .findByPhoneNumber(phoneNumber);

        if (customerOptional.isEmpty()) {
            customerRepository.save(customerToInsert);
        } else if (!customerToInsert.getName()
                .equalsIgnoreCase(customerOptional.get().getName())) {
            throw new BusinessException
                    (String.format("Phone number %s is already taken.", phoneNumber));
        }

    }
}
