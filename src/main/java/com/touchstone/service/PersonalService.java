package com.touchstone.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.touchstone.domain.Personal;
import com.touchstone.repository.PersonalRepository;
import com.touchstone.repository.UserRepository;
import com.touchstone.security.SecurityUtils;

/**
 * Service class for managing users.
 */
@Service
public class PersonalService {

	private final Logger log = LoggerFactory.getLogger(PersonalService.class);

	private final UserRepository userRepository;

	private PersonalRepository personalRepository;

	public PersonalService(UserRepository userRepository, PersonalRepository personalRepository) {
		this.userRepository = userRepository;
		this.personalRepository = personalRepository;
	}

	public void saveTx(Personal personal) {
		
		personalRepository.save(personal);
		log.debug("Created Information for User: {}", personal);
	}

}
