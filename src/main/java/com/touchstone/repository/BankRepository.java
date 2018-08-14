package com.touchstone.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.touchstone.domain.Bank;

/**
 * Spring Data MongoDB repository for the User entity.
 */
@Repository
public interface BankRepository extends MongoRepository<Bank, String> {
	List<Bank> findAllByUserId(String userId);
}
