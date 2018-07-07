package com.touchstone.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.touchstone.domain.Inquiry;

/**
 * Spring Data MongoDB repository for the User entity.
 */
@Repository
public interface InquiryRepository extends MongoRepository<Inquiry, String> {
	
	List<Inquiry> findAllByEmail(Pageable pageable, String user);
}
