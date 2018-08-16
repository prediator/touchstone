package com.touchstone.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.touchstone.domain.Personal;

/**
 * Spring Data MongoDB repository for the Tax entity.
 */
@Repository
public interface PersonalRepository extends MongoRepository<Personal, String> {

	Personal findByUserId(String userId);

}
