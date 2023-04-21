package com.amigoscode.testing.payment;

import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("from Payment p where p.id = :paymentId and p.customerId = :customerId")
    Optional<Payment> findByIdAndCustomerId(
            @Param("paymentId") UUID paymentId,
            @Param("customerId") UUID customerId);

}
