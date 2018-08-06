package com.touchstone.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.touchstone.domain.Property;

/**
 * Spring Data MongoDB repository for the User entity.
 */
@Repository
public interface PropertyRepository extends MongoRepository<Property, String> {
	
}
