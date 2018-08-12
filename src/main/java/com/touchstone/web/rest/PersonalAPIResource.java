package com.touchstone.web.rest;

import java.io.File;
import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
import com.touchstone.domain.Awards;
import com.touchstone.domain.Bank;
import com.touchstone.domain.Credit;
import com.touchstone.domain.Insurance;
import com.touchstone.domain.Ious;
import com.touchstone.domain.Miscellaneous;
import com.touchstone.domain.Property;
import com.touchstone.domain.Tax;
import com.touchstone.domain.User;
import com.touchstone.repository.AwardsRepository;
import com.touchstone.repository.BankRepository;
import com.touchstone.repository.CreditRepository;
import com.touchstone.repository.InsuranceRepository;
import com.touchstone.repository.IousRepository;
import com.touchstone.repository.MiscellaneousRepository;
import com.touchstone.repository.PropertyRepository;
import com.touchstone.repository.TaxRepository;
import com.touchstone.service.MailService;
import com.touchstone.service.UserService;
import com.touchstone.web.rest.util.GenerateOTP;

/**
 * REST controller for adding certificate, Education, Experience, Project,
 * Skills.
 */
@RestController
@RequestMapping("/api")
public class PersonalAPIResource {

	private final Logger log = LoggerFactory.getLogger(PersonalAPIResource.class);
	private final UserService userService;
	// private final String tmpDir =
	// "C:\\Users\\Kadri\\Desktop\\Touch\\build\\libs\\";
	private final String tmpDir = "/tmp/";
	private GenerateOTP generateOtp = new GenerateOTP();
	private final MailService mailService;

	private TaxRepository taxRepository;

	private CreditRepository creditRepository;

	private BankRepository bankRepository;

	private PropertyRepository propertyRepository;

	private IousRepository iousRepository;

	private AwardsRepository awardsRepository;

	private InsuranceRepository insuranceRepository;

	private MiscellaneousRepository miscellaneousRepository;

	public PersonalAPIResource(UserService userService, MailService mailService, TaxRepository taxRepository,
			CreditRepository creditRepository, BankRepository bankRepository, PropertyRepository propertyRepository,
			IousRepository iousRepository, AwardsRepository awardsRepository, InsuranceRepository insuranceRepository,
			MiscellaneousRepository miscellaneousRepository) {
		this.userService = userService;
		this.mailService = mailService;
		this.taxRepository = taxRepository;
		this.creditRepository = creditRepository;
		this.bankRepository = bankRepository;
		this.propertyRepository = propertyRepository;
		this.iousRepository = iousRepository;
		this.awardsRepository = awardsRepository;
		this.insuranceRepository = insuranceRepository;
		this.miscellaneousRepository = miscellaneousRepository;
	}

	@PostMapping("/taxpaid")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> taxpaid(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("taxpaid") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();

			Tax tax = jsonParserClient.readValue(certi, Tax.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			tax.setUserId(user.getUserId());

			taxRepository.save(tax);

			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/taxpaid")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<Tax>> taxpaidGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		List<Tax> data = taxRepository.findAllByUserId(user.getUserId());
		return new ResponseEntity<List<Tax>>(data, HttpStatus.CREATED);
	}

	@PostMapping("/credit")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> credit(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("credit") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();

			Credit credit = jsonParserClient.readValue(certi, Credit.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			credit.setUserId(user.getUserId());

			creditRepository.save(credit);

			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/credit")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Credit> creditGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		Credit data = creditRepository.findOne(user.getUserId());
		return new ResponseEntity<Credit>(data, HttpStatus.CREATED);
	}

	@PostMapping("/bank")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> bank(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("bank") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();

			Bank bank = jsonParserClient.readValue(certi, Bank.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			bank.setUserId(user.getUserId());

			bankRepository.save(bank);

			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/bank")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Bank> bankGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		Bank data = bankRepository.findOne(user.getUserId());
		return new ResponseEntity<Bank>(data, HttpStatus.CREATED);
	}

	@PostMapping("/property")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> property(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("property") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();

			Property property = jsonParserClient.readValue(certi, Property.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			property.setUserId(user.getUserId());

			propertyRepository.save(property);

			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/property")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Property> propertyGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		Property data = propertyRepository.findOne(user.getUserId());
		return new ResponseEntity<Property>(data, HttpStatus.CREATED);
	}

	@PostMapping("/ious")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> ious(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("ious") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();

			Ious property = jsonParserClient.readValue(certi, Ious.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			property.setUserId(user.getUserId());

			iousRepository.save(property);

			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/ious")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Ious> iousGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		Ious data = iousRepository.findOne(user.getUserId());
		return new ResponseEntity<Ious>(data, HttpStatus.CREATED);
	}

	@PostMapping("/awards")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> awards(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("awards") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();

			Awards property = jsonParserClient.readValue(certi, Awards.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			property.setUserId(user.getUserId());

			awardsRepository.save(property);

			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/awards")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Awards> awardsGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		Awards data = awardsRepository.findOne(user.getUserId());
		return new ResponseEntity<Awards>(data, HttpStatus.CREATED);
	}

	@PostMapping("/miscellaneous")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> miscellaneous(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("miscellaneous") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();

			Miscellaneous property = jsonParserClient.readValue(certi, Miscellaneous.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			property.setUserId(user.getUserId());

			miscellaneousRepository.save(property);

			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/miscellaneous")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Miscellaneous> miscellaneousGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		Miscellaneous data = miscellaneousRepository.findOne(user.getUserId());
		return new ResponseEntity<Miscellaneous>(data, HttpStatus.CREATED);
	}

	@PostMapping("/insurance")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> insurance(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("insurance") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();

			Insurance property = jsonParserClient.readValue(certi, Insurance.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			property.setUserId(user.getUserId());

			insuranceRepository.save(property);

			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/insurance")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Insurance> insuranceGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		Insurance data = insuranceRepository.findOne(user.getUserId());
		return new ResponseEntity<Insurance>(data, HttpStatus.CREATED);
	}

	public void uploadFileToS3(MultipartFile file, String id, String type) {
		AWSCredentials credentials = null;
		try {
			credentials = new BasicAWSCredentials("AKIAJJ2K72I5Y3U5F4WQ", "JmNXnTymXEUTMFnroK5mSzMrt79Q6jzveXqAfhbt");
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (~/.aws/credentials), and is in valid format.", e);
		}

		AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion("ap-south-1").build();

		String bucketName = "touchstonebackend";

		String fileName = file.getOriginalFilename();
		// changeg to /tmp/ whhen done
		s3.putObject(new PutObjectRequest(bucketName, id + "/" + type + "/" + fileName, new File(tmpDir + fileName))
				.withCannedAcl(CannedAccessControlList.PublicRead));

	}
}
