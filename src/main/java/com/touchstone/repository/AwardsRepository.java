package com.touchstone.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.touchstone.domain.Awards;

/**
 * Spring Data MongoDB repository for the User entity.
 */
@Repository
public interface AwardsRepository extends MongoRepository<Awards, String> {
	List<Awards> findAllByUserId(String userId);
}
