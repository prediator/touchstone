package com.touchstone.web.rest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.touchstone.config.Constants;
import com.touchstone.domain.Inquiry;
import com.touchstone.domain.User;
import com.touchstone.domain.UserType;
import com.touchstone.repository.DocsRepository;
import com.touchstone.repository.InquiryRepository;
import com.touchstone.repository.UserRepository;
import com.touchstone.service.MailService;
import com.touchstone.service.UserService;
import com.touchstone.service.dto.Consumer;
import com.touchstone.service.dto.ConsumerDTO;
import com.touchstone.service.dto.Enterprise;
import com.touchstone.service.dto.EnterpriseDTO;
import com.touchstone.service.dto.Update;
import com.touchstone.service.dto.Validation;
import com.touchstone.service.util.RandomUtil;
import com.touchstone.web.rest.errors.EmailAlreadyUsedException;
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

	private final MailService mailService;

	private final UserRepository userRepository;

	private final DocsRepository docsRepository;

	private final InquiryRepository inquiryRepository;

	private GenerateOTP generateOtp = new GenerateOTP();

	public ConsumerResource(UserService userService, MailService mailService, UserRepository userRepository,
			DocsRepository docsRepository, InquiryRepository inquiryRepository) {
		super();
		this.userService = userService;
		this.mailService = mailService;
		this.userRepository = userRepository;
		this.docsRepository = docsRepository;
		this.inquiryRepository = inquiryRepository;
	}

	/**
	 * POST /register : register Consumer.
	 *
	 * @param consumer
	 *            the consumer data
	 * @throws JsonProcessingException 
	 */
	@PostMapping("/Consumer")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> registerAccount(@RequestBody Consumer consumer) throws JsonProcessingException {

		User data = new User();

		data.setUserId(RandomUtil.generateActivationKey());
		data.setEmail(consumer.getEmail().toLowerCase());
		data.setFirstName(consumer.getFirstName());
		data.setLastName(consumer.getLastName());
		data.setPassword(consumer.getPassword());
		data.setUserType(UserType.CONSUMER.name());
		data.setLangKey(consumer.getLangKey());

		userRepository.findOneByEmailIgnoreCase(data.getEmail()).ifPresent(u -> {
			throw new EmailAlreadyUsedException();
		});

		userService.registerConsumer(data);

		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.classMap(Consumer.class, ConsumerDTO.class);
		MapperFacade mapper = mapperFactory.getMapperFacade();
		ConsumerDTO dest = mapper.map(consumer, ConsumerDTO.class);

		dest.setUserId(data.getUserId());
		dest.set$class("org.touchstone.basic.Consumer");
		dest.getAddress().set$class("org.touchstone.basic.Address");
		
		ObjectMapper mappers = new ObjectMapper();
		String jsonInString = mappers.writeValueAsString(dest);
		System.out.println(jsonInString);
		
		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/Consumer");
		rt.postForObject(uri, dest, ConsumerDTO.class);
		mailService.sendEmail(
				data.getEmail(), "Account Created", "http://ridgelift.io:8080/api/verifyc/"
						+ generateOtp.storeOTP(data.getUserId()) + "/" + data.getEmail() + "/" + data.getUserId(),
				false, true);
		return new ResponseEntity(HttpStatus.CREATED);

	}

	@GetMapping("/verifyc/{code}/{id:.+}/{uid}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public String validedEmail(@PathVariable int code, @PathVariable String id, @PathVariable String uid) {

		if (generateOtp.checkOTP(id, code) == 1) {
			Validation valid = new Validation();
			valid.set$class("org.touchstone.basic.ValidateEmail");
			valid.setIsEmailValidated(true);
			valid.setConsumer("resource:org.touchstone.basic.Consumer#" + uid);
			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/ValidateEmail");
			rt.postForObject(uri, valid, Validation.class);

			generateOtp.removeOtp(id);
			return "Success";
		}

		return "Failure";
	}

	@GetMapping("/verifye/{code}/{id:.+}/{uid}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public String validedEmailEnterprise(@PathVariable int code, @PathVariable String id, @PathVariable String uid) {

		if (generateOtp.checkOTP(id, code) == 1) {
			Validation valid = new Validation();
			valid.set$class("org.touchstone.basic.ValidateEmail");
			valid.setIsEmailValidated(true);
			valid.setConsumer("resource:org.touchstone.basic.Consumer#" + uid);
			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/ValidateEmail");
			rt.postForObject(uri, valid, Validation.class);

			generateOtp.removeOtp(id);
			return "Success";
		}

		return "Failure";
	}

	/**
	 * POST /register : register Enterprise.
	 *
	 * @param consumer
	 *            the consumer data
	 * @throws JsonProcessingException
	 */
	@PostMapping("/Enterprise")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> registerAccount(@RequestBody Enterprise enterprise) throws JsonProcessingException {
		User data = new User();

		data.setUserId(RandomUtil.generateActivationKey());
		data.setEmail(enterprise.getEmail().toLowerCase());
		data.setPassword(enterprise.getPassword());
		data.setUserType(UserType.ENTERPRISE.name());
		data.setLangKey(enterprise.getLangKey());

		userRepository.findOneByEmailIgnoreCase(data.getEmail()).ifPresent(u -> {
			throw new EmailAlreadyUsedException();
		});

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

		ObjectMapper mappers = new ObjectMapper();
		String jsonInString = mappers.writeValueAsString(dest);
		System.out.println(jsonInString);

		rt.postForObject(uri, dest, EnterpriseDTO.class);
		userService.registerEnterprise(data);
		mailService.sendEmail(
				data.getEmail(), "Account Created", "http://ridgelift.io:8080/api/verifye/"
						+ generateOtp.storeOTP(data.getUserId()) + "/" + data.getEmail() + "/" + data.getUserId(),
				false, true);
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
	public ResponseEntity<Update> UpdateMobile(@RequestBody Update update) {

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/UpdateMobile");
		Update response = rt.postForObject(uri, update, Update.class);
		return new ResponseEntity<Update>(response, HttpStatus.CREATED);

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
	public ResponseEntity<Update> UpdateAddress(@RequestBody Update update) {

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/UpdateAddress");
		Update response = rt.postForObject(uri, update, Update.class);
		return new ResponseEntity<Update>(response, HttpStatus.CREATED);
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
	public ResponseEntity<Update> UpdateEmail(@RequestBody Update update) {

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/UpdateEmail");
		Update response = rt.postForObject(uri, update, Update.class);
		return new ResponseEntity<Update>(response, HttpStatus.CREATED);
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
	public ResponseEntity<Update> UpdateName(@RequestBody Update update) {

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/UpdateName");
		Update response = rt.postForObject(uri, update, Update.class);
		return new ResponseEntity<Update>(response, HttpStatus.CREATED);
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

	@GetMapping("/testing")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public void test() {
		mailService.sendEmail("my3d3d@gmail.com", "Account Created", "http://ridgelift.io:8080/api/validedEmail/"
				+ generateOtp.storeOTP("my3d3d@gmail.com") + "/" + "my3d3d@gmail.com", false, true);
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
	public ResponseEntity<List<Enterprise>> getEnterprise(@PathVariable(value = "userid") String userid) {

		if (userid != null) {

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/queries/selectEnterpriseByUserId?userId=" + userid);
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

	/**
	 * POST /Enterprise/email/{email} : To get Enterprise by Email
	 *
	 * @param email
	 *            the user email
	 */
	@GetMapping("/documents/{id}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<com.touchstone.domain.AmazonS3>> getDocsById(@PathVariable String id) {

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());

		List<com.touchstone.domain.AmazonS3> dta = docsRepository.findAllByUser(null, id);

		return new ResponseEntity<List<com.touchstone.domain.AmazonS3>>(dta, HttpStatus.ACCEPTED);

	}

	@PostMapping("/upload")
	@Timed
	public void uploadDocs(@RequestParam("file") MultipartFile file, @RequestParam("education") String education)
			throws IOException {
		com.touchstone.domain.AmazonS3 data = new com.touchstone.domain.AmazonS3();
		try {
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(education);

			String fileName = file.getOriginalFilename();
			File dir = new File("/tmp/");

			dir.mkdirs();
			if (dir.isDirectory()) {
				File serverFile = new File(dir, fileName);
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();

				data.setFileName(fileName);
				data.setUser(obj.get("id").toString());
				data.setName(obj.get("name").toString());
				data.setQualification(obj.get("qualification").toString());
				docsRepository.save(data);
				uploadFileToS3(file, obj.get("id").toString());
				serverFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * POST /UpdateName : Address validation
	 *
	 * @param address
	 *            the phone data
	 */
	@PostMapping("/inquiry")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> inquiry(@RequestBody Inquiry inquiry) {
		System.out.println(inquiry);
		inquiryRepository.save(inquiry);
		mailService.sendEmail("my3d3d@gmail.com", "Inquiry", "Name: " + inquiry.getName() + "\n Phone " + inquiry.getPhone()
				+ "\n Email: " + inquiry.getEmail() + "\n Message " + inquiry.getMessage(), false, true);
		return new ResponseEntity(HttpStatus.CREATED);

	}

	public void uploadFileToS3(MultipartFile file, String id) {
		AWSCredentials credentials = null;
		try {
			credentials = new BasicAWSCredentials("AKIAJ3V3PHYVKBNK3KEA", "cv7xkpJkTY4oVRrwuL7EKHnkrc3NUlDzV60rnduy");
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (~/.aws/credentials), and is in valid format.", e);
		}

		AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion("ap-south-1").build();

		String bucketName = "touchstonebackend";

		String fileName = file.getOriginalFilename();

		s3.putObject(new PutObjectRequest(bucketName, id + "/" + fileName, new File("/tmp/" + fileName))
				.withCannedAcl(CannedAccessControlList.PublicRead));

	}

}
