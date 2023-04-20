package com.amigoscode.testing.customer;

import com.amigoscode.testing.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        Customer customerToInsert = request.getCustomer();
        String phoneNumber = customerToInsert.getPhoneNumber();

        var customerOptional = customerRepository
                .findByPhoneNumber(phoneNumber);

        if (customerOptional.isEmpty()) {
            customerRepository.save(customerToInsert);
        } else if (customerToInsert.isDifferentFrom(customerOptional.get())) {
            throw new BusinessException
                    (String.format("Phone number %s is already taken.", phoneNumber));
        }

    }
}
