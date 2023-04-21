package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        var customer = new Customer(customerId, "Mordor", "0000000");
        var customerRegRequest = new CustomerRegistrationRequest(customer);

        ResultActions customerRegResultActions = mockMvc.perform(
                put("/api/v1/customer-registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectToJson(customerRegRequest))));

        var paymentId = UUID.randomUUID();
        var payment = new Payment(
                paymentId,
                customerId,
                new BigDecimal("100.00"),
                Currency.GBP,
                "x0x0x0",
                "Description");
        var paymentRequest = new PaymentRequest(payment);

        // When
        ResultActions paymentResultActions = mockMvc.perform(
                post("/api/v1/payment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Objects.requireNonNull(objectToJson(paymentRequest))));

        // Then
        customerRegResultActions.andExpect(status().isOk());
        paymentResultActions.andExpect(status().isOk());

        assertThat(paymentRepository.findById(paymentId))
                .isPresent()
                .hasValueSatisfying(p ->
                        assertThat(p).isEqualToComparingFieldByField(payment));
    }

    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Failed to convert object to JSON");
            return null;
        }
    }
}
