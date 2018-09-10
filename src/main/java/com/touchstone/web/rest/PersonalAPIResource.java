package com.touchstone.web.rest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.spring4.SpringTemplateEngine;

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
import com.touchstone.config.Constants;
import com.touchstone.domain.Awards;
import com.touchstone.domain.Bank;
import com.touchstone.domain.Credit;
import com.touchstone.domain.Insurance;
import com.touchstone.domain.Ious;
import com.touchstone.domain.Miscellaneous;
import com.touchstone.domain.Personal;
import com.touchstone.domain.Property;
import com.touchstone.domain.Tax;
import com.touchstone.domain.User;
import com.touchstone.repository.AwardsRepository;
import com.touchstone.repository.BankRepository;
import com.touchstone.repository.CreditRepository;
import com.touchstone.repository.InsuranceRepository;
import com.touchstone.repository.IousRepository;
import com.touchstone.repository.MiscellaneousRepository;
import com.touchstone.repository.PersonalRepository;
import com.touchstone.repository.PropertyRepository;
import com.touchstone.repository.TaxRepository;
import com.touchstone.repository.UserRepository;
import com.touchstone.service.MailService;
import com.touchstone.service.PersonalService;
import com.touchstone.service.UserService;
import com.touchstone.service.dto.AddAwardsRecognitions;
import com.touchstone.service.dto.AddBankDetails;
import com.touchstone.service.dto.AddCreditReport;
import com.touchstone.service.dto.AddDocuments;
import com.touchstone.service.dto.AddInsuranceDetails;
import com.touchstone.service.dto.AddIous;
import com.touchstone.service.dto.AddMiscellaneousAssetDetails;
import com.touchstone.service.dto.AddPropertyDetails;
import com.touchstone.service.dto.AddTaxDetails;
import com.touchstone.service.dto.AwardsRecognitions;
import com.touchstone.service.dto.BankDetails;
import com.touchstone.service.dto.CreditDTO;
import com.touchstone.service.dto.CreditReport;
import com.touchstone.service.dto.Documents;
import com.touchstone.service.dto.InsuranceDetails_;
import com.touchstone.service.dto.MiscellaneousAssetDetails;
import com.touchstone.service.dto.PersonalRecords;
import com.touchstone.service.dto.PoliceVerification;
import com.touchstone.service.dto.PoliceVerificationDTO;
import com.touchstone.service.dto.PropertyDetails;
import com.touchstone.service.dto.TaxDetails;
import com.touchstone.service.dto.Validation;
import com.touchstone.service.util.RandomUtil;
import com.touchstone.web.rest.util.GenerateOTP;

import io.github.jhipster.config.JHipsterProperties;

/**
 * REST controller for adding certificate, Education, Experience, Project,
 * Skills.
 */
@RestController
@RequestMapping("/api")
public class PersonalAPIResource {

	private final Logger log = LoggerFactory.getLogger(PersonalAPIResource.class);
	private final UserService userService;
//	private final String tmpDir = "C:\\Users\\ABC\\Desktop\\Touch\\";
	private final String tmpDir = "/tmp/";
	private GenerateOTP generateOtp = new GenerateOTP();
	private final MailService mailService;

	private TaxRepository taxRepository;

	private PersonalRepository personalRepository;

	private CreditRepository creditRepository;

	private BankRepository bankRepository;

	private PropertyRepository propertyRepository;

	private IousRepository iousRepository;

	private AwardsRepository awardsRepository;

	private InsuranceRepository insuranceRepository;
	private final UserRepository userRepository;

	private MiscellaneousRepository miscellaneousRepository;

	private PersonalService personalService;
	private final SpringTemplateEngine templateEngine;
	private final MessageSource messageSource;
	private final JHipsterProperties jHipsterProperties;
	private final CacheManager cacheManager;
	private final JavaMailSender javaMailSender;

	public PersonalAPIResource(UserService userService, MailService mailService, TaxRepository taxRepository,
			CreditRepository creditRepository, BankRepository bankRepository, PropertyRepository propertyRepository,
			IousRepository iousRepository, AwardsRepository awardsRepository, InsuranceRepository insuranceRepository,
			MiscellaneousRepository miscellaneousRepository, PersonalRepository personalRepository,
			PersonalService personalService, SpringTemplateEngine templateEngine, MessageSource messageSource,
			JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender, UserRepository userRepository,
			CacheManager cacheManager) {
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
		this.personalRepository = personalRepository;
		this.personalService = personalService;
		this.templateEngine = templateEngine;
		this.messageSource = messageSource;
		this.jHipsterProperties = jHipsterProperties;
		this.javaMailSender = javaMailSender;
		this.userRepository = userRepository;
		this.cacheManager = cacheManager;
	}

	@PostMapping("/uploadimage")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public void UpdateName(@RequestParam(name = "file", required = false) MultipartFile file, Principal login)
			throws IOException {
		User user = userRepository.findOneByLogin(login.getName()).get();
		log.info("In add user:  " + user.getImage());
		File dir = new File(tmpDir);

		dir.mkdirs();
		if (dir.isDirectory() && file != null) {
			File serverFile = new File(dir, file.getOriginalFilename());
			serverFile.setReadable(true, false);
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
			stream.write(file.getBytes());
			stream.close();
			uploadFileToS3(file, user.getUserId(), "image");
			serverFile.delete();

			User usr = userRepository.findOneByEmailIgnoreCase(user.getEmail()).get();
			usr.setImage("https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/image/"
					+ file.getOriginalFilename());

			userRepository.save(usr);
			cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
			cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
		}
	}

	@GetMapping("/getimage")
	@Timed
	public ResponseEntity<User> getProfilePic(Principal login) throws IOException {
		User user = userRepository.findOneByLogin(login.getName()).get();
		log.info("user:  " + user.getImage());
		return new ResponseEntity<User>(user, HttpStatus.ACCEPTED);
	}

	@GetMapping("/getPersonalRecords")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<PersonalRecords>> getPersonalRecords(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		RestTemplate rt = new RestTemplate();
		String uri = new String(
				Constants.Url + "/queries/selectPersonalRecordByPersonalRecordId?personalId=" + user.getPersonalId());

		List<PersonalRecords> data = rt.getForObject(uri, List.class);

		return new ResponseEntity<List<PersonalRecords>>(data, HttpStatus.ACCEPTED);
	}

	@PostMapping("/aadharlicensetax")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Personal> getaadharlicensetax(@RequestParam(name = "type", required = true) String type,
			@RequestParam(name = "number", required = true) String number, Principal login)
			throws JsonProcessingException {
		try {
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			String id = RandomUtil.generateActivationKey();

			AddDocuments addDocuments = new AddDocuments();
			addDocuments.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
			Documents documents = new Documents();
			documents.setDocumentsId(id);
			addDocuments.setDocuments(documents);
			Personal personal = personalRepository.findByUserId(user.getUserId());
			if (personal == null) {
				personal = new Personal();
			}
			if (StringUtils.equals("aadhar", type)) {
				documents.setAadhar(number);
				personal.setAadhar(number);
				personal.setLicense(personal.getLicense());
				personal.setTaxno(personal.getTaxno());
			} else if (StringUtils.equals("license", type)) {
				documents.setLicense(number);
				personal.setLicense(number);
				personal.setTaxno(personal.getTaxno());
				personal.setAadhar(personal.getAadhar());
			} else {
				documents.setIncome(number);
				personal.setTaxno(number);
				personal.setAadhar(personal.getAadhar());
				personal.setLicense(personal.getLicense());
			}

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addDocuments);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addDocuments");
			rt.postForObject(uri, addDocuments, AddDocuments.class);

			personal.setUserId(user.getUserId());
			Personal personals = personalRepository.save(personal);

			return new ResponseEntity(personals, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/aadharlicensetax")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Personal> getaadharlicensetax(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		Personal data = personalRepository.findByUserId(user.getUserId());
		return new ResponseEntity<Personal>(data, HttpStatus.CREATED);
	}

	@PostMapping("/taxpaid")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> taxpaid(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("taxpaid") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();
			String id = RandomUtil.generateActivationKey();
			Tax tax = jsonParserClient.readValue(certi, Tax.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			String[] links = new String[1];

			if (file != null) {
				links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/taxpaid/"
						+ file.getOriginalFilename();
			} else {
				links[0] = "";
			}

			AddTaxDetails addTaxDetails = new AddTaxDetails();
			addTaxDetails.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());

			TaxDetails taxDetails = new TaxDetails();
			taxDetails.setPath(links[0]);
			taxDetails.setTaxDetailsId(id);
			taxDetails.setTaxPaid(tax.getTax_paid());

			Validation valid = new Validation();
			valid.set$class("org.touchstone.basic.Validation");
			valid.setValidationStatus("VALIDATE");
			valid.setValidationType("MANUAL");
			valid.setValidationBy("");
			valid.setValidationDate("");
			valid.setValidationEmail("");
			valid.setValidationNote("");
			taxDetails.setValidation(valid);

			addTaxDetails.setTaxDetails(taxDetails);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addTaxDetails);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addTaxDetails");
			rt.postForObject(uri, addTaxDetails, AddTaxDetails.class);

			tax.setUserId(user.getUserId());

			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory() && file != null) {
				File serverFile = new File(dir, file.getOriginalFilename());
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "taxpaid");
				serverFile.delete();
			}

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

	@PostMapping("/creditreport")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> creditreport(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("creditreport") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();
			String id = RandomUtil.generateActivationKey();
			Credit cre = jsonParserClient.readValue(certi, Credit.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			String[] links = new String[1];

			if (file != null) {
				links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId()
						+ "/creditreport/" + file.getOriginalFilename();
			} else {
				links[0] = "";
			}

			CreditDTO creditDTO = new CreditDTO();
			creditDTO.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
			creditDTO.set$class("org.touchstone.basic.addCredit");

			com.touchstone.service.dto.Credit credit = new com.touchstone.service.dto.Credit();
			credit.setDocumentReference(links[0]);
			credit.setCreditId(id);
			credit.setDateOfReport(cre.getReportdate());

			Validation valid = new Validation();
			valid.set$class("org.touchstone.basic.Validation");
			valid.setValidationStatus("VALIDATE");
			valid.setValidationType("MANUAL");
			valid.setValidationBy("");
			valid.setValidationDate("");
			valid.setValidationEmail("");
			valid.setValidationNote("");
			credit.setValidation(valid);

			creditDTO.setCredit(credit);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(creditDTO);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addCredit");
			rt.postForObject(uri, creditDTO, CreditDTO.class);

			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory() && file != null) {
				File serverFile = new File(dir, file.getOriginalFilename());
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "creditreport");
				serverFile.delete();
			}

			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/creditreport")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> creditreport(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String profileId = user.getProfileId();
		String uri = new String(Constants.Url + "/addCredit/" + profileId);
		String data = rt.getForObject(uri, String.class);

		return new ResponseEntity<String>(data, HttpStatus.CREATED);
	}

	@PostMapping("/addPoliceVerification")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addPoliceVerification(
			@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("policeverification") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();
			String id = RandomUtil.generateActivationKey();
			PoliceVerification cre = jsonParserClient.readValue(certi, PoliceVerification.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			String[] links = new String[1];

			if (file != null) {
				links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId()
						+ "/policeverification/" + file.getOriginalFilename();
			} else {
				links[0] = "";
			}

			PoliceVerificationDTO creditDTO = new PoliceVerificationDTO();
			creditDTO.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
			creditDTO.set$class("org.touchstone.basic.addPoliceVerification");

			PoliceVerification police = new PoliceVerification();
			police.setDocumentReference(links[0]);
			police.setPoliceVerificationId(id);
			police.setDateOfReport(cre.getDateOfReport());

			Validation valid = new Validation();
			valid.set$class("org.touchstone.basic.Validation");
			valid.setValidationStatus("VALIDATE");
			valid.setValidationType("MANUAL");
			valid.setValidationBy("");
			valid.setValidationDate("");
			valid.setValidationEmail("");
			valid.setValidationNote("");
			police.setValidation(valid);

			creditDTO.setPoliceVerification(police);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(creditDTO);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addPoliceVerification");
			rt.postForObject(uri, creditDTO, CreditDTO.class);

			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory() && file != null) {
				File serverFile = new File(dir, file.getOriginalFilename());
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "policeverification");
				serverFile.delete();
			}

			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/addPoliceVerification")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addPoliceVerification(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String profileId = user.getProfileId();
		String uri = new String(Constants.Url + "/addPoliceVerification/" + profileId);
		String data = rt.getForObject(uri, String.class);

		return new ResponseEntity<String>(data, HttpStatus.CREATED);
	}

	@PostMapping("/credit")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> credit(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("credit") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();
			String id = RandomUtil.generateActivationKey();

			Credit credit = jsonParserClient.readValue(certi, Credit.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			String[] links = new String[1];

			if (file != null) {
				links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/credit/"
						+ file.getOriginalFilename();
			} else {
				links[0] = "";
			}

			AddCreditReport addCreditReport = new AddCreditReport();
			addCreditReport.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());

			CreditReport creditReport = new CreditReport();
			creditReport.setCreditReportId(id);
			creditReport.setPath(links[0]);
			creditReport.setReportdate(credit.getReportdate());
			credit.setUserId(user.getUserId());

			Validation valid = new Validation();
			valid.set$class("org.touchstone.basic.Validation");
			valid.setValidationStatus("VALIDATE");
			valid.setValidationType("MANUAL");
			valid.setValidationBy("");
			valid.setValidationDate("");
			valid.setValidationEmail("");
			valid.setValidationNote("");
			creditReport.setValidation(valid);

			addCreditReport.setCreditReport(creditReport);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addCreditReport");
			rt.postForObject(uri, addCreditReport, AddCreditReport.class);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addCreditReport);
			System.out.println(jsonInString);
			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory() && file != null) {
				File serverFile = new File(dir, file.getOriginalFilename());
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "credit");
				serverFile.delete();
			}

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
	public ResponseEntity<List<Credit>> creditGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		List<Credit> data = creditRepository.findAllByUserId(user.getUserId());
		return new ResponseEntity<List<Credit>>(data, HttpStatus.CREATED);
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

			String id = RandomUtil.generateActivationKey();
			String[] links = new String[1];

			if (file != null) {
				links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/bank/"
						+ file.getOriginalFilename();
			} else {
				links[0] = "";
			}

			AddBankDetails addBankDetails = new AddBankDetails();
			addBankDetails.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());

			BankDetails bankDetails = new BankDetails();
			bankDetails.setAccholder(bank.getAccholder());
			bankDetails.setAccountno(bank.getAccountno());
			bankDetails.setBankDetailsId(id);
			bankDetails.setBankname(bank.getBankname());
			bankDetails.setBranch(bank.getBranch());
			bankDetails.setIfsc(bank.getIfsc());
			bankDetails.setPath(links[0]);

			Validation valid = new Validation();
			valid.set$class("org.touchstone.basic.Validation");
			valid.setValidationStatus("VALIDATE");
			valid.setValidationType("MANUAL");
			valid.setValidationBy("");
			valid.setValidationDate("");
			valid.setValidationEmail("");
			valid.setValidationNote("");
			bankDetails.setValidation(valid);
			addBankDetails.setBankDetails(bankDetails);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addBankDetails);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addBankDetails");
			rt.postForObject(uri, addBankDetails, AddBankDetails.class);

			bank.setUserId(user.getUserId());
			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory() && file != null) {
				File serverFile = new File(dir, file.getOriginalFilename());
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "bank");
				serverFile.delete();
			}
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
	public ResponseEntity<List<Bank>> bankGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		List<Bank> data = bankRepository.findAllByUserId(user.getUserId());
		return new ResponseEntity<List<Bank>>(data, HttpStatus.CREATED);
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

			String id = RandomUtil.generateActivationKey();
			String[] links = new String[1];
			if (file != null) {
				links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/property/"
						+ file.getOriginalFilename();
			} else {
				links[0] = "";
			}

			AddPropertyDetails addPropertyDetails = new AddPropertyDetails();
			addPropertyDetails
					.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());

			PropertyDetails propertyDetails = new PropertyDetails();
			propertyDetails.setAddress(property.getAddress());
			propertyDetails.setArea(property.getArea());
			propertyDetails.setPath(links[0]);
			propertyDetails.setPropertyDetailsId(id);
			propertyDetails.setType(property.getType());

			Validation valid = new Validation();
			valid.set$class("org.touchstone.basic.Validation");
			valid.setValidationStatus("VALIDATE");
			valid.setValidationType("MANUAL");
			valid.setValidationBy("");
			valid.setValidationDate("");
			valid.setValidationEmail("");
			valid.setValidationNote("");

			propertyDetails.setValidation(valid);
			addPropertyDetails.setPropertyDetails(propertyDetails);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addPropertyDetails);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addPropertyDetails");
			rt.postForObject(uri, addPropertyDetails, AddPropertyDetails.class);

			property.setUserId(user.getUserId());
			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory() && file != null) {
				File serverFile = new File(dir, file.getOriginalFilename());
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "property");
				serverFile.delete();
			}
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
	public ResponseEntity<List<Property>> propertyGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		List<Property> data = propertyRepository.findAllByUserId(user.getUserId());
		return new ResponseEntity<List<Property>>(data, HttpStatus.CREATED);
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

			String id = RandomUtil.generateActivationKey();
			String[] links = new String[1];

			if (file != null) {
				links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/ious/"
						+ file.getOriginalFilename();
			} else {
				links[0] = "";
			}

			AddIous addIous = new AddIous();
			addIous.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
			com.touchstone.service.dto.Ious ious = new com.touchstone.service.dto.Ious();

			ious.setAmount(property.getAmount());
			ious.setArea(property.getArea());
			ious.setPath(links[0]);
			ious.setType(property.getType());
			ious.setType(property.getType());
			ious.setIousId(id);

			Validation valid = new Validation();
			valid.set$class("org.touchstone.basic.Validation");
			valid.setValidationStatus("VALIDATE");
			valid.setValidationType("MANUAL");
			valid.setValidationBy("");
			valid.setValidationDate("");
			valid.setValidationEmail("");
			valid.setValidationNote("");

			ious.setValidation(valid);
			addIous.setIous(ious);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addIous);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addIous");
			rt.postForObject(uri, addIous, AddIous.class);

			property.setUserId(user.getUserId());
			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory() && file != null) {
				File serverFile = new File(dir, file.getOriginalFilename());
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "ious");
				serverFile.delete();
			}
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
	public ResponseEntity<List<Ious>> iousGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		List<Ious> data = iousRepository.findAllByUserId(user.getUserId());
		return new ResponseEntity<List<Ious>>(data, HttpStatus.CREATED);
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
			String id = RandomUtil.generateActivationKey();
			String[] links = new String[1];

			if (file != null) {
				links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/awards/"
						+ file.getOriginalFilename();
			} else {
				links[0] = "";
			}

			AddAwardsRecognitions addAwardsRecognitions = new AddAwardsRecognitions();
			addAwardsRecognitions
					.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());

			AwardsRecognitions awardsRecognitions = new AwardsRecognitions();
			awardsRecognitions.setAwardsRecognitionsId(id);
			awardsRecognitions.setIssuer(property.getIssuer());
			awardsRecognitions.setMonth(property.getMonth());
			awardsRecognitions.setNarration(property.getNarration());
			awardsRecognitions.setPath(links[0]);
			awardsRecognitions.setTitle(property.getTitle());
			awardsRecognitions.setYear(property.getYear());

			Validation valid = new Validation();
			valid.set$class("org.touchstone.basic.Validation");
			valid.setValidationStatus("VALIDATE");
			valid.setValidationType("MANUAL");
			valid.setValidationBy("");
			valid.setValidationDate("");
			valid.setValidationEmail("");
			valid.setValidationNote("");
			awardsRecognitions.setValidation(valid);

			addAwardsRecognitions.setAwardsRecognitions(awardsRecognitions);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addAwardsRecognitions);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addAwardsRecognitions");
			rt.postForObject(uri, addAwardsRecognitions, AddAwardsRecognitions.class);

			property.setUserId(user.getUserId());
			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory() && file != null) {
				File serverFile = new File(dir, file.getOriginalFilename());
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "awards");
				serverFile.delete();
			}
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
	public ResponseEntity<List<Awards>> awardsGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		List<Awards> data = awardsRepository.findAllByUserId(user.getUserId());
		return new ResponseEntity<List<Awards>>(data, HttpStatus.CREATED);
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
			String id = RandomUtil.generateActivationKey();
			String[] links = new String[1];

			if (file != null) {
				links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId()
						+ "/miscellaneous/" + file.getOriginalFilename();
			} else {
				links[0] = "";
			}

			AddMiscellaneousAssetDetails addMiscellaneousAssetDetails = new AddMiscellaneousAssetDetails();
			addMiscellaneousAssetDetails
					.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());

			MiscellaneousAssetDetails miscellaneousAssetDetails = new MiscellaneousAssetDetails();
			miscellaneousAssetDetails.setCost(property.getCost());
			miscellaneousAssetDetails.setMakeAndModel(property.getMakeAndModel());
			miscellaneousAssetDetails.setMiscellaneousAssetDetailsId(id);
			miscellaneousAssetDetails.setPath(links[0]);
			miscellaneousAssetDetails.setTemplate(Boolean.valueOf(property.getTemplate()));
			miscellaneousAssetDetails.setType(property.getType());

			Validation valid = new Validation();
			valid.set$class("org.touchstone.basic.Validation");
			valid.setValidationStatus("VALIDATE");
			valid.setValidationType("MANUAL");
			valid.setValidationBy("");
			valid.setValidationDate("");
			valid.setValidationEmail("");
			valid.setValidationNote("");
			miscellaneousAssetDetails.setValidation(valid);

			addMiscellaneousAssetDetails.setMiscellaneousAssetDetails(miscellaneousAssetDetails);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addMiscellaneousAssetDetails);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addMiscellaneousAssetDetails");
			rt.postForObject(uri, addMiscellaneousAssetDetails, AddMiscellaneousAssetDetails.class);
			property.setUserId(user.getUserId());
			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory() && file != null) {
				File serverFile = new File(dir, file.getOriginalFilename());
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "miscellaneous");
				serverFile.delete();
			}
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
	public ResponseEntity<List<Miscellaneous>> miscellaneousGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		List<Miscellaneous> data = miscellaneousRepository.findAllByUserId(user.getUserId());
		return new ResponseEntity<List<Miscellaneous>>(data, HttpStatus.CREATED);
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

			String id = RandomUtil.generateActivationKey();
			String[] links = new String[1];

			if (file != null) {
				links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/insurance/"
						+ file.getOriginalFilename();
			} else {
				links[0] = "";
			}

			AddInsuranceDetails addInsuranceDetails = new AddInsuranceDetails();

			addInsuranceDetails
					.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());

			InsuranceDetails_ insuranceDetails_ = new InsuranceDetails_();
			insuranceDetails_.setAmount(property.getAmount());
			insuranceDetails_.setInsuranceDetailsId(id);
			insuranceDetails_.setMaturitydate(property.getMaturitydate());
			insuranceDetails_.setPath(links[0]);
			insuranceDetails_.setTemplate(Boolean.valueOf(property.getTemplate()));
			insuranceDetails_.setType(property.getType());

			Validation valid = new Validation();
			valid.set$class("org.touchstone.basic.Validation");
			valid.setValidationStatus("VALIDATE");
			valid.setValidationType("MANUAL");
			valid.setValidationBy("");
			valid.setValidationDate("");
			valid.setValidationEmail("");
			valid.setValidationNote("");
			insuranceDetails_.setValidation(valid);
			addInsuranceDetails.setInsuranceDetails(insuranceDetails_);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addInsuranceDetails);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addInsuranceDetails");
			rt.postForObject(uri, addInsuranceDetails, AddInsuranceDetails.class);

			property.setUserId(user.getUserId());
			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory() && file != null) {
				File serverFile = new File(dir, file.getOriginalFilename());
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "insurance");
				serverFile.delete();
			}
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
	public ResponseEntity<List<Insurance>> insuranceGet(Principal login) {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		List<Insurance> data = insuranceRepository.findAllByUserId(user.getUserId());
		return new ResponseEntity<List<Insurance>>(data, HttpStatus.CREATED);
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

	@Async
	public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart,
				isHtml, to, subject, content);

		// Prepare message using a Spring helper
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
			message.setTo(to);
			message.setFrom(jHipsterProperties.getMail().getFrom());
			message.setSubject(subject);
			message.setText(content, isHtml);
			javaMailSender.send(mimeMessage);
			log.debug("Sent email to User '{}'", to);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.warn("Email could not be sent to user '{}'", to, e);
			} else {
				log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
			}
		}
	}
}
