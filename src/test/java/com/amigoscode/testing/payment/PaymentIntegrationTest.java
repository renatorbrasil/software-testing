package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationRequest;
import com.amigoscode.testing.customer.CustomerRegistrationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private UUID registerCustomerAndReturnId() throws Exception {
        var customer = new Customer(null, "Mordor", "0000000");
        var customerRegRequest = new CustomerRegistrationRequest(customer);

        ResultActions customerRegResultActions = mockMvc.perform(
                post("/api/v1/customer-registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectToJson(customerRegRequest))));

        CustomerRegistrationResponse customerResponse =
                new ObjectMapper().readValue(customerRegResultActions
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), CustomerRegistrationResponse.class);

        return customerResponse.getId();
    }

    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return fail("Failed to convert object to JSON");
        }
    }

    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {
        // Given
        var customerId = registerCustomerAndReturnId();

        var payment = new Payment(
                null,
                customerId,
                new BigDecimal("100.00"),
                Currency.GBP,
                "x0x0x0",
                "Description");
        var paymentRequest = new PaymentRequest(payment);

        // When
        ResultActions newPaymentResultActions = mockMvc.perform(
                post("/api/v1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectToJson(paymentRequest))));

        PaymentResponse paymentResponse =
                new ObjectMapper().readValue(newPaymentResultActions
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), PaymentResponse.class);

        var paymentId = paymentResponse.getId();

        ResultActions paymentActions = mockMvc.perform(
                get("/api/v1/payment/{customerId}/{paymentId}", customerId, paymentId)
                        .contentType(MediaType.APPLICATION_JSON));

        Payment paymentReturned =
                new ObjectMapper().readValue(paymentActions
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), Payment.class);

        // Then
        assertThat(paymentReturned).isEqualToIgnoringGivenFields(payment, "id");
    }

    @Test
    void itShouldReturnErrorWhenAmountIsNull() throws Exception {
        // Given a customer
        var customerId = registerCustomerAndReturnId();

        // ... and null amount
        var payment = new Payment(
                null,
                customerId,
                null,
                Currency.GBP,
                "x0x0x0",
                "Description");
        var paymentRequest = new PaymentRequest(payment);

        // When
        ResultActions newPaymentResultActions = mockMvc.perform(
                post("/api/v1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectToJson(paymentRequest))));

        // Then
        newPaymentResultActions.andExpect(status().is4xxClientError());

    }
}
