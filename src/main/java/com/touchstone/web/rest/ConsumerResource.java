package com.touchstone.web.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.touchstone.config.Constants;
import com.touchstone.domain.User;
import com.touchstone.domain.UserType;
import com.touchstone.service.UserService;
import com.touchstone.service.dto.Consumer;
import com.touchstone.service.dto.ConsumerDTO;
import com.touchstone.service.dto.Enterprise;
import com.touchstone.service.dto.EnterpriseDTO;
import com.touchstone.service.dto.OtpDto;
import com.touchstone.service.dto.Update;
import com.touchstone.service.dto.Validation;
import com.touchstone.service.util.RandomUtil;
import com.touchstone.web.rest.util.GenerateOTP;

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

	private GenerateOTP generateOtp = new GenerateOTP();

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
		data.setLangKey(consumer.getLangKey());
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
		data.setLangKey(enterprise.getLangKey());
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
	 * POST /ValidateMobile : to validate otp
	 *
	 * @param phone
	 *            the phone number
	 * @param id
	 *            the otp
	 */
	@GetMapping("/ValidateMobile/{id}/{phone}/{user}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Map<String, String>> ValidateMobile(@PathVariable Integer id, @PathVariable String phone,
			@PathVariable String user) {

		if (generateOtp.checkOTP(phone, id) == 1) {
			Validation validPhone = new Validation();
			validPhone.set$class("org.touchstone.basic.ValidateMobile");
			validPhone.setIsMobileValidated(true);
			validPhone.setConsumer("resource:org.touchstone.basic.Consumer#" + user);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/ValidateMobile");
			rt.postForObject(uri, validPhone, Validation.class);

			generateOtp.removeOtp(phone);

			Map<String, String> data = new HashMap<>();

			data.put("status", "success");
			return new ResponseEntity<Map<String, String>>(data, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<Map<String, String>>(HttpStatus.UNAUTHORIZED);
		}
	}

	/**
	 * POST /sentOtp : to sent otp
	 *
	 * @param phone
	 *            the phone data
	 */
	@GetMapping("/sentOtp/{phone}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Map<String, String>> sentOtp(@PathVariable String phone) {

		Map<String, String> data = new HashMap<>();

		try {
			Unirest.post("http://api.msg91.com/api/v2/sendsms").header("authkey", "221184AdSEVa5f3aK5b28933d")
					.header("content-type", "application/json")
					.body("{ \"sender\": \"SOCKET\", \"route\": \"4\", \"country\": \"91\", \"sms\": [ { \"message\": \" OTP "
							+ generateOtp.storeOTP(phone) + "\", \"to\": [ \"" + phone + "\" ] }] }")
					.asString();
			data.put("status", "success");
		} catch (UnirestException e) {
			data.put("status", "error");
			return new ResponseEntity<Map<String, String>>(HttpStatus.BAD_REQUEST);

		}
		return new ResponseEntity<Map<String, String>>(HttpStatus.CREATED);

	}

	/**
	 * POST /ValidateAddress : Address validation
	 *
	 * @param address
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

	/**
	 * POST /UpdateMobile : Address validation
	 *
	 * @param address
	 *            the phone data
	 */
	@PostMapping("/UpdateMobile")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Validation> UpdateMobile(@RequestBody Update update) {

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/UpdateMobile");
		Validation response = rt.postForObject(uri, update, Validation.class);
		return new ResponseEntity<Validation>(response, HttpStatus.CREATED);

	}

	/**
	 * POST /ValidateAddress : Address validation
	 *
	 * @param address
	 *            the phone data
	 */
	@PostMapping("/UpdateAddress")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Validation> UpdateAddress(@RequestBody Update update) {

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/UpdateAddress");
		Validation response = rt.postForObject(uri, update, Validation.class);
		return new ResponseEntity<Validation>(response, HttpStatus.CREATED);
	}

	/**
	 * POST /UpdateEmail : Address validation
	 *
	 * @param address
	 *            the phone data
	 */
	@PostMapping("/UpdateEmail")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Validation> UpdateEmail(@RequestBody Update update) {

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/UpdateEmail");
		Validation response = rt.postForObject(uri, update, Validation.class);
		return new ResponseEntity<Validation>(response, HttpStatus.CREATED);
	}

	/**
	 * POST /UpdateName : Address validation
	 *
	 * @param address
	 *            the phone data
	 */
	@PostMapping("/UpdateName")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Validation> UpdateName(@RequestBody Update update) {

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/UpdateName");
		Validation response = rt.postForObject(uri, update, Validation.class);
		return new ResponseEntity<Validation>(response, HttpStatus.CREATED);
	}

	/**
	 * POST /Consumer/{userid} : To get Consumer
	 *
	 * @param userid
	 *            the user id
	 */
	@GetMapping("/Consumer/{userid}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<Consumer>> getConsumer(@PathVariable String userid) {

		if (userid != null) {

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/queries/selectConsumerByUserId?userId=" + userid);
			List<Consumer> data = rt.getForObject(uri, List.class);

			return new ResponseEntity<List<Consumer>>(data, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<List<Consumer>>(HttpStatus.UNAUTHORIZED);
		}
	}

	/**
	 * POST /Consumer/email/{email} : To get Consumer by Email
	 *
	 * @param email
	 *            the user email
	 */
	@GetMapping("/Consumer/email/{email:.+}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<Consumer>> getConsumerByEmail(@PathVariable String email) {
		if (email != null) {
			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/queries/selectConsumerByEmail?email=" + email);
			List<Consumer> data = rt.getForObject(uri, List.class);

			return new ResponseEntity<List<Consumer>>(data, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<List<Consumer>>(HttpStatus.UNAUTHORIZED);
		}
	}

	/**
	 * POST /Enterprise/{userid} : To get Enterprise
	 *
	 * @param userid
	 *            the id
	 */
	@GetMapping("/Enterprise/{userid}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<Enterprise>> getEnterprise(@RequestParam(value = "userid") String userid) {

		if (userid != null) {

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/queries/selectEnterpriseByUserId?id=" + userid);
			List<Enterprise> data = rt.getForObject(uri, List.class);

			return new ResponseEntity<List<Enterprise>>(data, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<List<Enterprise>>(HttpStatus.UNAUTHORIZED);
		}
	}

	/**
	 * POST /Enterprise/email/{email} : To get Enterprise by Email
	 *
	 * @param email
	 *            the user email
	 */
	@GetMapping("/Enterprise/email/{email:.+}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<Enterprise>> getEnterpriseByEmail(@PathVariable String email) {
		if (email != null) {
			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/queries/selectEnterpriseByEmail?email=" + email);
			List<Enterprise> data = rt.getForObject(uri, List.class);

			return new ResponseEntity<List<Enterprise>>(data, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<List<Enterprise>>(HttpStatus.UNAUTHORIZED);
		}
	}

}
