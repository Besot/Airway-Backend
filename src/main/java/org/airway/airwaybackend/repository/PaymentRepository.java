package org.airway.airwaybackend.repository;

import org.airway.airwaybackend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
