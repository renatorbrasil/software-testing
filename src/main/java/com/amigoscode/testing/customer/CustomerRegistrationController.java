package com.amigoscode.testing.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/customer-registration")
@RequiredArgsConstructor
public class CustomerRegistrationController {

    private final CustomerRegistrationService customerRegistrationService;

    @PostMapping
    public CustomerRegistrationResponse registerNewCustomer(
            @Valid @RequestBody CustomerRegistrationRequest request
    ) {
        var customerId = customerRegistrationService.registerNewCustomer(request);
        return new CustomerRegistrationResponse(customerId);
    }

}
