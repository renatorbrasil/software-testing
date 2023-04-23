package com.amigoscode.testing.customer;

import com.amigoscode.testing.exception.BusinessException;
import com.amigoscode.testing.utils.PhoneNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;
    private final PhoneNumberValidator phoneNumberValidator;

    public UUID registerNewCustomer(CustomerRegistrationRequest request) {
        Customer customerToInsert = request.getCustomer();
        String phoneNumber = request.getPhoneNumber();

        if (!phoneNumberValidator.test(phoneNumber)) {
            throw new BusinessException("Phone number is not valid");
        }

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
