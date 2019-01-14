package com.touchstone.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.touchstone.domain.Miscellaneous;

/**
 * Spring Data MongoDB repository for the User entity.
 */
@Repository
public interface MiscellaneousRepository extends MongoRepository<Miscellaneous, String> {
	List<Miscellaneous> findAllByUserId(String userId);
}
