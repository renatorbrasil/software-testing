package com.amigoscode.testing.customer;

import com.amigoscode.testing.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    public UUID registerNewCustomer(CustomerRegistrationRequest request) {
        Customer customerToInsert = request.getCustomer();
        String phoneNumber = request.getPhoneNumber();

        var customerOptional = customerRepository
                .findByPhoneNumber(phoneNumber);

        if (customerOptional.isEmpty()) {
            var customerId = UUID.randomUUID();
            customerToInsert.setId(customerId);
            customerRepository.save(customerToInsert);
            return customerId;
        } else if (customerToInsert.isDifferentFrom(customerOptional.get())) {
            throw new BusinessException
                    (String.format("Phone number %s is already taken.", phoneNumber));
        }

        return customerOptional.get().getId();
    }
}
