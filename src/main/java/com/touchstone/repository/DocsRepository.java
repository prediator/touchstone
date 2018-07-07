package com.touchstone.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.touchstone.domain.AmazonS3;

/**
 * Spring Data MongoDB repository for the User entity.
 */
@Repository
public interface DocsRepository extends MongoRepository<AmazonS3, String> {
	
	List<AmazonS3> findAllByUser(Pageable pageable, String user);
}