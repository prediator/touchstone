package com.touchstone.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.touchstone.domain.Insurance;

/**
 * Spring Data MongoDB repository for the User entity.
 */
@Repository
public interface InsuranceRepository extends MongoRepository<Insurance, String> {
	List<Insurance> findAllByUserId(String userId);
}
