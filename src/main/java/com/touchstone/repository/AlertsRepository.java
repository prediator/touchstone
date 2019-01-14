package com.touchstone.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.touchstone.service.dto.Alert;

/**
 * Spring Data MongoDB repository for the User entity.
 */
@Repository
public interface AlertsRepository extends MongoRepository<Alert, String> {
}
