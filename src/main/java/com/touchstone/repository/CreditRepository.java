package com.touchstone.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.touchstone.domain.Credit;

/**
 * Spring Data MongoDB repository for the Tax entity.
 */
@Repository
public interface CreditRepository extends MongoRepository<Credit, String> {
	
}
