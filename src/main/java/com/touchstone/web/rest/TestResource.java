package com.touchstone.web.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchstone.config.Constants;
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
import com.touchstone.service.dto.AddInsuranceDetails;
import com.touchstone.service.dto.AddIous;
import com.touchstone.service.dto.AddMiscellaneousAssetDetails;
import com.touchstone.service.dto.AddPropertyDetails;
import com.touchstone.service.dto.AddTaxDetails;
import com.touchstone.service.dto.AwardsRecognitions;
import com.touchstone.service.dto.BankDetails;
import com.touchstone.service.dto.Certificate;
import com.touchstone.service.dto.CertificateValidation;
import com.touchstone.service.dto.Certification;
import com.touchstone.service.dto.CreditReport;
import com.touchstone.service.dto.Education;
import com.touchstone.service.dto.EducationDTO;
import com.touchstone.service.dto.Experience;
import com.touchstone.service.dto.ExperienceDTO;
import com.touchstone.service.dto.InsuranceDetails_;
import com.touchstone.service.dto.MedicalValidation;
import com.touchstone.service.dto.MiscellaneousAssetDetails;
import com.touchstone.service.dto.Project;
import com.touchstone.service.dto.ProjectDTO;
import com.touchstone.service.dto.PropertyDetails;
import com.touchstone.service.dto.SkillDTO;
import com.touchstone.service.dto.Skills;
import com.touchstone.service.dto.TaxDetails;
import com.touchstone.service.dto.Validation;
import com.touchstone.service.dto.ValidationEmail;
import com.touchstone.web.rest.util.GenerateOTP;

import io.github.jhipster.config.JHipsterProperties;

/**
 * REST controller for adding certificate, Education, Experience, Project,
 * Skills.
 */
@Controller
@RequestMapping("/api")
public class TestResource {

	private final Logger log = LoggerFactory.getLogger(TestResource.class);
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

	private final JavaMailSender javaMailSender;

	public TestResource(UserService userService, MailService mailService, TaxRepository taxRepository,
			CreditRepository creditRepository, BankRepository bankRepository, PropertyRepository propertyRepository,
			IousRepository iousRepository, AwardsRepository awardsRepository, InsuranceRepository insuranceRepository,
			MiscellaneousRepository miscellaneousRepository, PersonalRepository personalRepository,
			PersonalService personalService, SpringTemplateEngine templateEngine, MessageSource messageSource,
			JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender, UserRepository userRepository) {
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
	}

	@RequestMapping("/personalrecordsvalidate/{otp}/{slno}/{uname}/{email}/{type}/{desc}/{by}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public void validateMedicalNew(HttpServletResponse httpServletResponse, @PathVariable Integer otp,
			@PathVariable String slno, @PathVariable String uname, @PathVariable String email,
			@PathVariable String type, @PathVariable String desc, @PathVariable String by) throws IOException {

		User user = userService.getUserWithAuthoritiesByLogin(uname).get();
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());

		Validation valid = new Validation();
		valid.set$class("org.touchstone.basic.Validation");
		valid.setValidationStatus("VALIDATED");
		valid.setValidationType("MANUAL");
		valid.setValidationBy(by);
		valid.setValidationDate(date);
		valid.setValidationEmail(email);
		valid.setValidationNote(desc);

		if (generateOtp.checkOTP(email, otp) == 1) {
			if (StringUtils.equals(type, "taxPaid")) {
				AddTaxDetails addTaxDetails = new AddTaxDetails();
				addTaxDetails
						.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
				addTaxDetails.set$class("org.touchstone.basic.validateTaxDetails");
				TaxDetails taxDetails = new TaxDetails();
				taxDetails.setPath("");
				taxDetails.setTaxDetailsId(slno);
				taxDetails.setTaxPaid("");

				taxDetails.setValidation(valid);

				addTaxDetails.setTaxDetails(taxDetails);

				ObjectMapper mappers = new ObjectMapper();
				String jsonInString = mappers.writeValueAsString(addTaxDetails);
				System.out.println(jsonInString);

				RestTemplate rt = new RestTemplate();
				rt.getMessageConverters().add(new StringHttpMessageConverter());
				String uri = new String(Constants.Url + "/validateTaxDetails");
				rt.postForObject(uri, addTaxDetails, AddTaxDetails.class);
				;

			} else if (StringUtils.equals(type, "creditReport")) {
				AddCreditReport addCreditReport = new AddCreditReport();
				addCreditReport
						.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
				addCreditReport.set$class("org.touchstone.basic.validateCreditReport");
				CreditReport creditReport = new CreditReport();
				creditReport.setCreditReportId(slno);
				creditReport.setPath("");
				creditReport.setReportdate("");

				creditReport.setValidation(valid);

				addCreditReport.setCreditReport(creditReport);

				RestTemplate rt = new RestTemplate();
				rt.getMessageConverters().add(new StringHttpMessageConverter());
				String uri = new String(Constants.Url + "/validateCreditReport");
				rt.postForObject(uri, addCreditReport, AddCreditReport.class);

				ObjectMapper mappers = new ObjectMapper();
				String jsonInString = mappers.writeValueAsString(addCreditReport);
				System.out.println(jsonInString);
			} else if (StringUtils.equals(type, "bank")) {
				AddBankDetails addBankDetails = new AddBankDetails();
				addBankDetails
						.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
				addBankDetails.set$class("org.touchstone.basic.validateBankDetails");
				BankDetails bankDetails = new BankDetails();
				bankDetails.setAccholder("");
				bankDetails.setAccountno("");
				bankDetails.setBankDetailsId(slno);
				bankDetails.setBankname("");
				bankDetails.setBranch("");
				bankDetails.setIfsc("");
				bankDetails.setPath("");

				bankDetails.setValidation(valid);
				addBankDetails.setBankDetails(bankDetails);

				ObjectMapper mappers = new ObjectMapper();
				String jsonInString = mappers.writeValueAsString(addBankDetails);
				System.out.println(jsonInString);

				RestTemplate rt = new RestTemplate();
				rt.getMessageConverters().add(new StringHttpMessageConverter());
				String uri = new String(Constants.Url + "/validateBankDetails");
				rt.postForObject(uri, addBankDetails, AddBankDetails.class);
			} else if (StringUtils.equals(type, "property")) {
				AddPropertyDetails addPropertyDetails = new AddPropertyDetails();
				addPropertyDetails
						.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
				addPropertyDetails.set$class("org.touchstone.basic.validatePropertyDetails");
				PropertyDetails propertyDetails = new PropertyDetails();
				propertyDetails.setAddress("");
				propertyDetails.setArea("");
				propertyDetails.setPath("");
				propertyDetails.setPropertyDetailsId(slno);
				propertyDetails.setType("");

				propertyDetails.setValidation(valid);
				addPropertyDetails.setPropertyDetails(propertyDetails);

				ObjectMapper mappers = new ObjectMapper();
				String jsonInString = mappers.writeValueAsString(addPropertyDetails);
				System.out.println(jsonInString);

				RestTemplate rt = new RestTemplate();
				rt.getMessageConverters().add(new StringHttpMessageConverter());
				String uri = new String(Constants.Url + "/validatePropertyDetails");
				rt.postForObject(uri, addPropertyDetails, AddPropertyDetails.class);
			} else if (StringUtils.equals(type, "ious")) {
				AddIous addIous = new AddIous();
				addIous.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
				com.touchstone.service.dto.Ious ious = new com.touchstone.service.dto.Ious();
				addIous.set$class("org.touchstone.basic.validateIous");
				ious.setAmount("");
				ious.setArea("");
				ious.setPath("");
				ious.setType("");
				ious.setIousId(slno);

				ious.setValidation(valid);
				addIous.setIous(ious);

				ObjectMapper mappers = new ObjectMapper();
				String jsonInString = mappers.writeValueAsString(addIous);
				System.out.println(jsonInString);

				RestTemplate rt = new RestTemplate();
				rt.getMessageConverters().add(new StringHttpMessageConverter());
				String uri = new String(Constants.Url + "/validateIous");
				rt.postForObject(uri, addIous, AddIous.class);
			} else if (StringUtils.equals(type, "awards")) {
				AddAwardsRecognitions addAwardsRecognitions = new AddAwardsRecognitions();
				addAwardsRecognitions
						.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
				addAwardsRecognitions.set$class("org.touchstone.basic.validateAwardsRecognitions");
				AwardsRecognitions awardsRecognitions = new AwardsRecognitions();
				awardsRecognitions.setAwardsRecognitionsId(slno);
				awardsRecognitions.setIssuer("");
				awardsRecognitions.setMonth("");
				awardsRecognitions.setNarration("");
				awardsRecognitions.setPath("");
				awardsRecognitions.setTitle("");
				awardsRecognitions.setYear("");

				awardsRecognitions.setValidation(valid);

				addAwardsRecognitions.setAwardsRecognitions(awardsRecognitions);

				ObjectMapper mappers = new ObjectMapper();
				String jsonInString = mappers.writeValueAsString(addAwardsRecognitions);
				System.out.println(jsonInString);

				RestTemplate rt = new RestTemplate();
				rt.getMessageConverters().add(new StringHttpMessageConverter());
				String uri = new String(Constants.Url + "/validateAwardsRecognitions");
				rt.postForObject(uri, addAwardsRecognitions, AddAwardsRecognitions.class);
			} else if (StringUtils.equals(type, "insurance")) {
				AddInsuranceDetails addInsuranceDetails = new AddInsuranceDetails();

				addInsuranceDetails
						.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
				addInsuranceDetails.set$class("org.touchstone.basic.validateInsuranceDetails");
				InsuranceDetails_ insuranceDetails_ = new InsuranceDetails_();
				insuranceDetails_.setAmount("");
				insuranceDetails_.setInsuranceDetailsId(slno);
				insuranceDetails_.setMaturitydate("");
				insuranceDetails_.setPath("");
				insuranceDetails_.setTemplate(Boolean.valueOf(""));
				insuranceDetails_.setType("");

				insuranceDetails_.setValidation(valid);
				addInsuranceDetails.setInsuranceDetails(insuranceDetails_);

				ObjectMapper mappers = new ObjectMapper();
				String jsonInString = mappers.writeValueAsString(addInsuranceDetails);
				System.out.println(jsonInString);

				RestTemplate rt = new RestTemplate();
				rt.getMessageConverters().add(new StringHttpMessageConverter());
				String uri = new String(Constants.Url + "/validateInsuranceDetails");
				rt.postForObject(uri, addInsuranceDetails, AddInsuranceDetails.class);
			} else if (StringUtils.equals(type, "miscellaneous")) {
				AddMiscellaneousAssetDetails addMiscellaneousAssetDetails = new AddMiscellaneousAssetDetails();
				addMiscellaneousAssetDetails
						.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
				addMiscellaneousAssetDetails.set$class("org.touchstone.basic.validateMiscellaneousAssetDetails");

				MiscellaneousAssetDetails miscellaneousAssetDetails = new MiscellaneousAssetDetails();
				miscellaneousAssetDetails.setCost("");
				miscellaneousAssetDetails.setMakeAndModel("");
				miscellaneousAssetDetails.setMiscellaneousAssetDetailsId(slno);
				miscellaneousAssetDetails.setPath("");
				miscellaneousAssetDetails.setTemplate(false);
				miscellaneousAssetDetails.setType("");

				miscellaneousAssetDetails.setValidation(valid);

				addMiscellaneousAssetDetails.setMiscellaneousAssetDetails(miscellaneousAssetDetails);

				ObjectMapper mappers = new ObjectMapper();
				String jsonInString = mappers.writeValueAsString(addMiscellaneousAssetDetails);
				System.out.println(jsonInString);

				RestTemplate rt = new RestTemplate();
				rt.getMessageConverters().add(new StringHttpMessageConverter());
				String uri = new String(Constants.Url + "/validateMiscellaneousAssetDetails");
				rt.postForObject(uri, addMiscellaneousAssetDetails, AddMiscellaneousAssetDetails.class);
				generateOtp.removeOtp(email);

				Map<String, String> data = new HashMap<>();
				data.put("status", "success");

			} else {

			}
			httpServletResponse.sendRedirect(Constants.live);
		}
	}

	@PostMapping("/personalrecordsvalidate")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public void validateMedical(@RequestBody MedicalValidation medicalValidation, Principal login)
			throws JsonProcessingException {
		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		String date = simpleDateFormat.format(new Date());

		Validation valid = new Validation();
		valid.set$class("org.touchstone.basic.Validation");
		valid.setValidationStatus("IN_PROGRESS");
		valid.setValidationType("MANUAL");
		valid.setValidationBy(medicalValidation.getValidationBy());
		valid.setValidationDate(date);
		valid.setValidationEmail(medicalValidation.getEmail());
		valid.setValidationNote(medicalValidation.getDescription());

		if (StringUtils.equals(medicalValidation.getType(), "taxPaid")) {
			AddTaxDetails addTaxDetails = new AddTaxDetails();
			addTaxDetails.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
			addTaxDetails.set$class("org.touchstone.basic.validateTaxDetails");
			TaxDetails taxDetails = new TaxDetails();
			taxDetails.setPath("");
			taxDetails.setTaxDetailsId(medicalValidation.getSlno());
			taxDetails.setTaxPaid("");

			taxDetails.setValidation(valid);

			addTaxDetails.setTaxDetails(taxDetails);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addTaxDetails);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateTaxDetails");
			rt.postForObject(uri, addTaxDetails, AddTaxDetails.class);

		} else if (StringUtils.equals(medicalValidation.getType(), "creditReport")) {
			AddCreditReport addCreditReport = new AddCreditReport();
			addCreditReport.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
			addCreditReport.set$class("org.touchstone.basic.validateCreditReport");
			CreditReport creditReport = new CreditReport();
			creditReport.setCreditReportId(medicalValidation.getSlno());
			creditReport.setPath("");
			creditReport.setReportdate("");

			creditReport.setValidation(valid);

			addCreditReport.setCreditReport(creditReport);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateCreditReport");
			rt.postForObject(uri, addCreditReport, AddCreditReport.class);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addCreditReport);
			System.out.println(jsonInString);
		} else if (StringUtils.equals(medicalValidation.getType(), "bank")) {
			AddBankDetails addBankDetails = new AddBankDetails();
			addBankDetails.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
			addBankDetails.set$class("org.touchstone.basic.validateBankDetails");
			BankDetails bankDetails = new BankDetails();
			bankDetails.setAccholder("");
			bankDetails.setAccountno("");
			bankDetails.setBankDetailsId(medicalValidation.getSlno());
			bankDetails.setBankname("");
			bankDetails.setBranch("");
			bankDetails.setIfsc("");
			bankDetails.setPath("");

			bankDetails.setValidation(valid);
			addBankDetails.setBankDetails(bankDetails);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addBankDetails);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateBankDetails");
			rt.postForObject(uri, addBankDetails, AddBankDetails.class);
		} else if (StringUtils.equals(medicalValidation.getType(), "property")) {
			AddPropertyDetails addPropertyDetails = new AddPropertyDetails();
			addPropertyDetails
					.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
			addPropertyDetails.set$class("org.touchstone.basic.validatePropertyDetails");
			PropertyDetails propertyDetails = new PropertyDetails();
			propertyDetails.setAddress("");
			propertyDetails.setArea("");
			propertyDetails.setPath("");
			propertyDetails.setPropertyDetailsId(medicalValidation.getSlno());
			propertyDetails.setType("");

			propertyDetails.setValidation(valid);
			addPropertyDetails.setPropertyDetails(propertyDetails);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addPropertyDetails);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validatePropertyDetails");
			rt.postForObject(uri, addPropertyDetails, AddPropertyDetails.class);
		} else if (StringUtils.equals(medicalValidation.getType(), "ious")) {
			AddIous addIous = new AddIous();
			addIous.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
			com.touchstone.service.dto.Ious ious = new com.touchstone.service.dto.Ious();
			addIous.set$class("org.touchstone.basic.validateIous");
			ious.setAmount("");
			ious.setArea("");
			ious.setPath("");
			ious.setType("");
			ious.setIousId(medicalValidation.getSlno());

			ious.setValidation(valid);
			addIous.setIous(ious);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addIous);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateIous");
			rt.postForObject(uri, addIous, AddIous.class);
		} else if (StringUtils.equals(medicalValidation.getType(), "awards")) {
			AddAwardsRecognitions addAwardsRecognitions = new AddAwardsRecognitions();
			addAwardsRecognitions
					.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
			addAwardsRecognitions.set$class("org.touchstone.basic.validateAwardsRecognitions");
			AwardsRecognitions awardsRecognitions = new AwardsRecognitions();
			awardsRecognitions.setAwardsRecognitionsId(medicalValidation.getSlno());
			awardsRecognitions.setIssuer("");
			awardsRecognitions.setMonth("");
			awardsRecognitions.setNarration("");
			awardsRecognitions.setPath("");
			awardsRecognitions.setTitle("");
			awardsRecognitions.setYear("");

			awardsRecognitions.setValidation(valid);

			addAwardsRecognitions.setAwardsRecognitions(awardsRecognitions);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addAwardsRecognitions);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateAwardsRecognitions");
			rt.postForObject(uri, addAwardsRecognitions, AddAwardsRecognitions.class);
		} else if (StringUtils.equals(medicalValidation.getType(), "insurance")) {
			AddInsuranceDetails addInsuranceDetails = new AddInsuranceDetails();

			addInsuranceDetails
					.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
			addInsuranceDetails.set$class("org.touchstone.basic.validateInsuranceDetails");
			InsuranceDetails_ insuranceDetails_ = new InsuranceDetails_();
			insuranceDetails_.setAmount("");
			insuranceDetails_.setInsuranceDetailsId(medicalValidation.getSlno());
			insuranceDetails_.setMaturitydate("");
			insuranceDetails_.setPath("");
			insuranceDetails_.setTemplate(Boolean.valueOf(""));
			insuranceDetails_.setType("");

			insuranceDetails_.setValidation(valid);
			addInsuranceDetails.setInsuranceDetails(insuranceDetails_);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addInsuranceDetails);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateInsuranceDetails");
			rt.postForObject(uri, addInsuranceDetails, AddInsuranceDetails.class);
		} else if (StringUtils.equals(medicalValidation.getType(), "miscellaneous")) {
			AddMiscellaneousAssetDetails addMiscellaneousAssetDetails = new AddMiscellaneousAssetDetails();
			addMiscellaneousAssetDetails
					.setPersonalRecords("resource:org.touchstone.basic.PersonalRecords#" + user.getPersonalId());
			addMiscellaneousAssetDetails.set$class("org.touchstone.basic.validateMiscellaneousAssetDetails");

			MiscellaneousAssetDetails miscellaneousAssetDetails = new MiscellaneousAssetDetails();
			miscellaneousAssetDetails.setCost("");
			miscellaneousAssetDetails.setMakeAndModel("");
			miscellaneousAssetDetails.setMiscellaneousAssetDetailsId(medicalValidation.getSlno());
			miscellaneousAssetDetails.setPath("");
			miscellaneousAssetDetails.setTemplate(false);
			miscellaneousAssetDetails.setType("");

			miscellaneousAssetDetails.setValidation(valid);

			addMiscellaneousAssetDetails.setMiscellaneousAssetDetails(miscellaneousAssetDetails);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(addMiscellaneousAssetDetails);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateMiscellaneousAssetDetails");
			rt.postForObject(uri, addMiscellaneousAssetDetails, AddMiscellaneousAssetDetails.class);
		}

		Locale locale = Locale.forLanguageTag("en");
		Context context = new Context(locale);

		context.setVariable("doc", medicalValidation.getPath());
		context.setVariable("consumer", user.getFirstName() + " " + user.getLastName());
		context.setVariable("desc", medicalValidation.getDescription());
		context.setVariable("name", medicalValidation.getValidationBy());
		context.setVariable("type",
				medicalValidation.getType().substring(0, 1).toUpperCase() + medicalValidation.getType().substring(1));
		context.setVariable("url",
				"http://ridgelift.io:8080/api/personalrecordsvalidate/"
						+ generateOtp.storeOTP(medicalValidation.getEmail()) + "/" + medicalValidation.getSlno() + "/"
						+ user.getLogin() + "/" + medicalValidation.getEmail() + "/" + medicalValidation.getType() + "/"
						+ medicalValidation.getDescription() + "/" + medicalValidation.getValidationBy());
		String content = templateEngine.process("education", context);
		String subject = messageSource.getMessage("email.activation.title", null, locale);
		sendEmail(medicalValidation.getEmail(), subject, content, false, true);

	}

	/**
	 * POST /addCertification : Add certification.
	 *
	 * @param Certificate the certificate data
	 * @throws JsonProcessingException
	 */
	@GetMapping("/validateCertification/{otp}/{slno}/{uname}/{email}/{type}/{desc}/{by}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public void validateCertification(HttpServletResponse httpServletResponse, @PathVariable Integer otp,
			@PathVariable String slno, @PathVariable String uname, @PathVariable String email,
			@PathVariable String type, @PathVariable String desc, @PathVariable String by)
			throws JsonProcessingException {
		try {
			User user = userService.getUserWithAuthoritiesByLogin(uname).get();
			String pattern = "yyyy-MM-dd HH:mm:ss";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			String date = simpleDateFormat.format(new Date());
			if (generateOtp.checkOTP(email, otp) == 1) {

				if (StringUtils.contains(type, "education")) {
					EducationDTO education = new EducationDTO();
					education.set$class("org.touchstone.basic.validateEducation");
					education.setEducation(new Education());
					education.getEducation().set$class("org.touchstone.basic.Education");
					education.getEducation().setValidation(new CertificateValidation());
					education.getEducation().getValidation().set$class("org.touchstone.basic.Validation");
					education.getEducation().getValidation().setValidationStatus("VALIDATED");
					education.getEducation().getValidation().setValidationType("MANUAL");
					education.getEducation().getValidation().setValidationDate(date);
					education.getEducation().getValidation().setValidationEmail(email);
					education.getEducation().getValidation().setValidationNote(desc);
					education.getEducation().getValidation().setValidationBy(by);
					education.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
					education.getEducation().setSupportingDocumentLinks(new String[0]);
					education.getEducation().setEducation_slno(slno);
					ObjectMapper mappers = new ObjectMapper();
					String jsonInString = mappers.writeValueAsString(education);
					System.out.println(jsonInString);

					RestTemplate rt = new RestTemplate();
					rt.getMessageConverters().add(new StringHttpMessageConverter());
					String uri = new String(Constants.Url + "/validateEducation");
					rt.postForObject(uri, education, EducationDTO.class);
				} else if (StringUtils.contains(type, "experience")) {

					ExperienceDTO education = new ExperienceDTO();
					education.set$class("org.touchstone.basic.validateExperience");
					education.setExperience(new Experience());
					education.getExperience().set$class("org.touchstone.basic.Experience");
					education.getExperience().setValidation(new CertificateValidation());
					education.getExperience().getValidation().set$class("org.touchstone.basic.Validation");
					education.getExperience().getValidation().setValidationStatus("VALIDATED");
					education.getExperience().getValidation().setValidationType("MANUAL");
					education.getExperience().getValidation().setValidationDate(date);
					education.getExperience().getValidation().setValidationEmail(email);
					education.getExperience().getValidation().setValidationNote(desc);
					education.getExperience().getValidation().setValidationBy(by);
					education.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
					education.getExperience().setSupportingDocumentLinks(new String[0]);
					education.getExperience().setExperience_slno(slno);
					ObjectMapper mappers = new ObjectMapper();
					String jsonInString = mappers.writeValueAsString(education);
					System.out.println(jsonInString);

					RestTemplate rt = new RestTemplate();
					rt.getMessageConverters().add(new StringHttpMessageConverter());
					String uri = new String(Constants.Url + "/validateExperience");
					rt.postForObject(uri, education, ExperienceDTO.class);

				} else if (StringUtils.contains(type, "certificate")) {
					Certificate certificate = new Certificate();
					certificate.set$class("org.touchstone.basic.validateCertification");
					certificate.setCertification(new Certification());
					certificate.getCertification().setCertification_slno(slno);
					certificate.getCertification().set$class("org.touchstone.basic.Certification");
					certificate.getCertification().setValidation(new CertificateValidation());
					certificate.getCertification().getValidation().set$class("org.touchstone.basic.Validation");
					certificate.getCertification().getValidation().setValidationStatus("VALIDATED");
					certificate.getCertification().getValidation().setValidationType("MANUAL");
					certificate.getCertification().getValidation().setValidationDate(date);
					certificate.getCertification().getValidation().setValidationEmail(email);
					certificate.getCertification().getValidation().setValidationNote(desc);
					certificate.getCertification().getValidation().setValidationBy(by);
					certificate.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
					certificate.getCertification().setSupportingDocumentLinks(new String[0]);

					ObjectMapper mappers = new ObjectMapper();
					String jsonInString = mappers.writeValueAsString(certificate);
					System.out.println(jsonInString);

					RestTemplate rt = new RestTemplate();
					rt.getMessageConverters().add(new StringHttpMessageConverter());
					String uri = new String(Constants.Url + "/validateCertification");
					rt.postForObject(uri, certificate, Certificate.class);
				} else if (StringUtils.contains(type, "skill")) {
					SkillDTO education = new SkillDTO();
					education.set$class("org.touchstone.basic.validateSkills");
					education.setSkills(new Skills());
					education.getSkills().set$class("org.touchstone.basic.Skills");
					education.getSkills().setValidation(new CertificateValidation());
					education.getSkills().getValidation().set$class("org.touchstone.basic.Validation");
					education.getSkills().getValidation().setValidationStatus("VALIDATED");
					education.getSkills().getValidation().setValidationType("MANUAL");
					education.getSkills().getValidation().setValidationDate(date);
					education.getSkills().getValidation().setValidationEmail(email);
					education.getSkills().getValidation().setValidationNote(desc);
					education.getSkills().getValidation().setValidationBy(by);
					education.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
					education.getSkills().setSupportingDocumentLinks(new String[0]);
					education.getSkills().setSkill_slno(slno);
					ObjectMapper mappers = new ObjectMapper();
					String jsonInString = mappers.writeValueAsString(education);
					System.out.println(jsonInString);

					RestTemplate rt = new RestTemplate();
					rt.getMessageConverters().add(new StringHttpMessageConverter());
					String uri = new String(Constants.Url + "/validateSkills");
					rt.postForObject(uri, education, SkillDTO.class);
				} else {
					ProjectDTO education = new ProjectDTO();
					education.set$class("org.touchstone.basic.validateProject");
					education.setProject(new Project());
					education.getProject().set$class("org.touchstone.basic.Project");
					education.getProject().setValidation(new CertificateValidation());
					education.getProject().getValidation().set$class("org.touchstone.basic.Validation");
					education.getProject().getValidation().setValidationStatus("VALIDATED");
					education.getProject().getValidation().setValidationType("MANUAL");
					education.getProject().getValidation().setValidationDate(date);
					education.getProject().getValidation().setValidationEmail(email);
					education.getProject().getValidation().setValidationNote(desc);
					education.getProject().getValidation().setValidationBy(by);
					education.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
					education.getProject().setSupportingDocumentLinks(new String[0]);
					education.getProject().setProject_slno(slno);
					ObjectMapper mappers = new ObjectMapper();
					String jsonInString = mappers.writeValueAsString(education);
					System.out.println(jsonInString);

					RestTemplate rt = new RestTemplate();
					rt.getMessageConverters().add(new StringHttpMessageConverter());
					String uri = new String(Constants.Url + "/validateProject");
					rt.postForObject(uri, education, ProjectDTO.class);
				}

				generateOtp.removeOtp(email);

				httpServletResponse.sendRedirect(Constants.live);
			} else {
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PostMapping("/validate")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public void validateCertification(@RequestBody ValidationEmail validEmail, Principal login)
			throws JsonProcessingException {

		System.out.println(validEmail);
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		String date = simpleDateFormat.format(new Date());

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		// education.experience,certificate,skill,project
		if (StringUtils.contains(validEmail.getType(), "education")) {
			EducationDTO education = new EducationDTO();
			education.set$class("org.touchstone.basic.validateEducation");
			education.setEducation(new Education());
			education.getEducation().set$class("org.touchstone.basic.Education");
			education.getEducation().setValidation(new CertificateValidation());
			education.getEducation().getValidation().set$class("org.touchstone.basic.Validation");
			education.getEducation().getValidation().setValidationStatus("IN_PROGRESS");
			education.getEducation().getValidation().setValidationType("MANUAL");
			education.getEducation().getValidation().setValidationDate(date);
			education.getEducation().getValidation().setValidationEmail(validEmail.getEmail());
			education.getEducation().getValidation().setValidationNote(validEmail.getDescription());
			education.getEducation().getValidation().setValidationBy(validEmail.getValidationBy());
			education.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			education.getEducation().setSupportingDocumentLinks(new String[0]);
			education.getEducation().setEducation_slno(validEmail.getSlno());
			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(education);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateEducation");
			rt.postForObject(uri, education, EducationDTO.class);
		} else if (StringUtils.contains(validEmail.getType(), "experience")) {

			ExperienceDTO education = new ExperienceDTO();
			education.set$class("org.touchstone.basic.validateExperience");
			education.setExperience(new Experience());
			education.getExperience().set$class("org.touchstone.basic.Experience");
			education.getExperience().setValidation(new CertificateValidation());
			education.getExperience().getValidation().set$class("org.touchstone.basic.Validation");
			education.getExperience().getValidation().setValidationStatus("IN_PROGRESS");
			education.getExperience().getValidation().setValidationType("MANUAL");
			education.getExperience().getValidation().setValidationDate(date);
			education.getExperience().getValidation().setValidationEmail(validEmail.getEmail());
			education.getExperience().getValidation().setValidationNote(validEmail.getDescription());
			education.getExperience().getValidation().setValidationBy(validEmail.getValidationBy());
			education.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			education.getExperience().setSupportingDocumentLinks(new String[0]);
			education.getExperience().setExperience_slno(validEmail.getSlno());
			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(education);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateExperience");
			rt.postForObject(uri, education, ExperienceDTO.class);

		} else if (StringUtils.contains(validEmail.getType(), "certificate")) {
			Certificate certificate = new Certificate();
			certificate.set$class("org.touchstone.basic.validateCertification");
			certificate.setCertification(new Certification());
			certificate.getCertification().setCertification_slno(validEmail.getSlno());
			certificate.getCertification().set$class("org.touchstone.basic.Certification");
			certificate.getCertification().setValidation(new CertificateValidation());
			certificate.getCertification().getValidation().set$class("org.touchstone.basic.Validation");
			certificate.getCertification().getValidation().setValidationStatus("IN_PROGRESS");
			certificate.getCertification().getValidation().setValidationType("MANUAL");
			certificate.getCertification().getValidation().setValidationDate(date);
			certificate.getCertification().getValidation().setValidationEmail(validEmail.getEmail());
			certificate.getCertification().getValidation().setValidationNote(validEmail.getDescription());
			certificate.getCertification().getValidation().setValidationBy(validEmail.getValidationBy());
			certificate.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			certificate.getCertification().setSupportingDocumentLinks(new String[0]);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(certificate);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateCertification");
			rt.postForObject(uri, certificate, Certificate.class);
		} else if (StringUtils.contains(validEmail.getType(), "skill")) {
			SkillDTO education = new SkillDTO();
			education.set$class("org.touchstone.basic.validateSkills");
			education.setSkills(new Skills());
			education.getSkills().set$class("org.touchstone.basic.Skills");
			education.getSkills().setValidation(new CertificateValidation());
			education.getSkills().setExpertiseLevel("BEGINNER");
			education.getSkills().getValidation().set$class("org.touchstone.basic.Validation");
			education.getSkills().getValidation().setValidationStatus("IN_PROGRESS");
			education.getSkills().getValidation().setValidationType("MANUAL");
			education.getSkills().getValidation().setValidationDate(date);
			education.getSkills().getValidation().setValidationEmail(validEmail.getEmail());
			education.getSkills().getValidation().setValidationNote(validEmail.getDescription());
			education.getSkills().getValidation().setValidationBy(validEmail.getValidationBy());
			education.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			education.getSkills().setSupportingDocumentLinks(new String[0]);
			education.getSkills().setSkill_slno(validEmail.getSlno());
			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(education);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateSkills");
			rt.postForObject(uri, education, SkillDTO.class);
		} else {
			ProjectDTO education = new ProjectDTO();
			education.set$class("org.touchstone.basic.validateProject");
			education.setProject(new Project());
			education.getProject().set$class("org.touchstone.basic.Project");
			education.getProject().setValidation(new CertificateValidation());
			education.getProject().setProject_slno("");
			education.getProject().getValidation().set$class("org.touchstone.basic.Validation");
			education.getProject().getValidation().setValidationStatus("IN_PROGRESS");
			education.getProject().getValidation().setValidationType("MANUAL");
			education.getProject().getValidation().setValidationDate(date);
			education.getProject().getValidation().setValidationEmail(validEmail.getEmail());
			education.getProject().getValidation().setValidationNote(validEmail.getDescription());
			education.getProject().getValidation().setValidationBy(validEmail.getValidationBy());
			education.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			education.getProject().setSupportingDocumentLinks(new String[0]);
			education.getProject().setProject_slno(validEmail.getSlno());
			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(education);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateProject");
			rt.postForObject(uri, education, ProjectDTO.class);
		}

		// {otp}/{slno}/{uname}/{email}/{type}

		Locale locale = Locale.forLanguageTag("en");
		Context context = new Context(locale);

		context.setVariable("doc", validEmail.getPath());
		context.setVariable("consumer", user.getFirstName() + " " + user.getLastName());
		context.setVariable("desc", validEmail.getDescription());
		context.setVariable("name", validEmail.getValidationBy());
		context.setVariable("type",
				validEmail.getType().substring(0, 1).toUpperCase() + validEmail.getType().substring(1));
		context.setVariable("url",
				"http://ridgelift.io:8080/api/validateCertification/" + generateOtp.storeOTP(validEmail.getEmail())
						+ "/" + validEmail.getSlno() + "/" + user.getLogin() + "/" + validEmail.getEmail() + "/"
						+ validEmail.getType() + "/" + validEmail.getDescription() + "/"
						+ validEmail.getValidationBy());
		String content = templateEngine.process("education", context);
		String subject = messageSource.getMessage("email.activation.title", null, locale);
		sendEmail(validEmail.getEmail(), subject, content, false, true);

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
