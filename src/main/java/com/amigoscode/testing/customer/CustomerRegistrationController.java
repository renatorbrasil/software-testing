package com.amigoscode.testing.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/customer-registration")
@RequiredArgsConstructor
public class CustomerRegistrationController {

    private final CustomerRegistrationService customerRegistrationService;

    @PutMapping
    public CustomerRegistrationResponse registerNewCustomer(
            @Valid @RequestBody CustomerRegistrationRequest request
    ) {
        var customerId = customerRegistrationService.registerNewCustomer(request);
        return new CustomerRegistrationResponse(customerId);
    }

}
