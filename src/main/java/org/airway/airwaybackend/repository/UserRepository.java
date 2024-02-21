package org.airway.airwaybackend.repository;

import org.airway.airwaybackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
