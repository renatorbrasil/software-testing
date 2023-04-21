package com.amigoscode.testing.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("from Payment p where p.id = :paymentId and p.customerId = :customerId")
    Optional<Payment> findByIdAndCustomerId(UUID paymentId, UUID customerId);

}
