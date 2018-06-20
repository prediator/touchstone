package com.touchstone.web.rest;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;
import com.touchstone.config.Constants;
import com.touchstone.domain.User;
import com.touchstone.domain.UserType;
import com.touchstone.service.UserService;
import com.touchstone.service.dto.Consumer;
import com.touchstone.service.dto.ConsumerDTO;
import com.touchstone.service.dto.Enterprise;
import com.touchstone.service.dto.EnterpriseDTO;
import com.touchstone.service.dto.Validation;
import com.touchstone.service.util.RandomUtil;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class ConsumerResource {

	private final Logger log = LoggerFactory.getLogger(ConsumerResource.class);

	private final UserService userService;

	public ConsumerResource(UserService userService) {
		this.userService = userService;
	}

	/**
	 * POST /register : register Consumer.
	 *
	 * @param consumer
	 *            the consumer data
	 */
	@PostMapping("/Consumer")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> registerAccount(@RequestBody Consumer consumer) {

		User data = new User();

		data.setUserId(RandomUtil.generateActivationKey());
		data.setEmail(consumer.getEmail());
		data.setFirstName(consumer.getFirstName());
		data.setLastName(consumer.getLastName());
		data.setPassword(consumer.getPassword());
		data.setUserType(UserType.CONSUMER.name());
		userService.registerConsumer(data);

		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.classMap(Consumer.class, ConsumerDTO.class);
		MapperFacade mapper = mapperFactory.getMapperFacade();
		ConsumerDTO dest = mapper.map(consumer, ConsumerDTO.class);

		dest.setUserId(data.getUserId());
		dest.set$class("org.touchstone.basic.Consumer");
		dest.getAddress().set$class("org.touchstone.basic.Address");

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/Consumer");
		rt.postForObject(uri, dest, ConsumerDTO.class);
		return new ResponseEntity(HttpStatus.CREATED);

	}

	/**
	 * POST /register : register Enterprise.
	 *
	 * @param consumer
	 *            the consumer data
	 */
	@PostMapping("/Enterprise")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> registerAccount(@RequestBody Enterprise enterprise) {
		
		User data = new User();

		data.setUserId(RandomUtil.generateActivationKey());
		data.setEmail(enterprise.getEmail());
		data.setPassword(enterprise.getPassword());
		data.setUserType(UserType.ENTERPRISE.name());
		userService.registerConsumer(data);

		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.classMap(Enterprise.class, EnterpriseDTO.class);
		MapperFacade mapper = mapperFactory.getMapperFacade();
		EnterpriseDTO dest = mapper.map(enterprise, EnterpriseDTO.class);

		dest.setUserId(data.getUserId());
		dest.set$class("org.touchstone.basic.Enterprise");
		dest.getAddress().set$class("org.touchstone.basic.Address");
		
		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/Enterprise");
		rt.postForObject(uri, dest, EnterpriseDTO.class);
		return new ResponseEntity(HttpStatus.CREATED);

	}

	/**
	 * POST /ValidateEmail : email validation
	 *
	 * @param email
	 *            the email data
	 */
	@PostMapping("/ValidateEmail")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Validation> ValidateEmail(@RequestBody Validation email) {

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/ValidateEmail");
		Validation response = rt.postForObject(uri, email, Validation.class);
		return new ResponseEntity<Validation>(response, HttpStatus.CREATED);

	}

	/**
	 * POST /ValidateMobile : phone validation
	 *
	 * @param phone
	 *            the phone data
	 */
	@PostMapping("/ValidateMobile")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Validation> ValidateMobile(@RequestBody Validation phone) {

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/ValidateMobile");
		Validation response = rt.postForObject(uri, phone, Validation.class);
		return new ResponseEntity<Validation>(response, HttpStatus.CREATED);

	}

	/**
	 * POST /ValidateMobile : phone validation
	 *
	 * @param phone
	 *            the phone data
	 */
	@PostMapping("/ValidateAddress")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Validation> ValidateAddress(@RequestBody Validation address) {

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/ValidateAddress");
		Validation response = rt.postForObject(uri, address, Validation.class);
		return new ResponseEntity<Validation>(response, HttpStatus.CREATED);

	}

}
