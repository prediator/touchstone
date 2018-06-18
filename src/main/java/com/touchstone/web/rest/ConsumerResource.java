package com.touchstone.web.rest;

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
import com.touchstone.service.dto.Consumer;
import com.touchstone.service.dto.Enterprise;
import com.touchstone.service.dto.Validation;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class ConsumerResource {

	private final Logger log = LoggerFactory.getLogger(ConsumerResource.class);

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

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/Consumer");
		rt.postForObject(uri, consumer, Consumer.class);
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

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/Enterprise");
		rt.postForObject(uri, enterprise, Enterprise.class);
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
