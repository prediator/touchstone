package com.touchstone.web.rest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
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
import org.thymeleaf.context.Context;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.touchstone.config.Constants;
import com.touchstone.domain.User;
import com.touchstone.service.MailService;
import com.touchstone.service.UserService;
import com.touchstone.service.dto.AddHealthCare;
import com.touchstone.service.dto.Ailment;
import com.touchstone.service.dto.Ailment_;
import com.touchstone.service.dto.Health;
import com.touchstone.service.dto.HealthCare;
import com.touchstone.service.dto.HealthCareReport;
import com.touchstone.service.dto.HealthCareReport_;
import com.touchstone.service.dto.InsuranceClaim;
import com.touchstone.service.dto.InsuranceClaim_;
import com.touchstone.service.dto.MedicalValidation;
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
public class MedicalAPIResource {

	private final Logger log = LoggerFactory.getLogger(MedicalAPIResource.class);
	private final UserService userService;
//	private final String tmpDir = "D:\\";
	private final String tmpDir = "/tmp/";
	private GenerateOTP generateOtp = new GenerateOTP();
	private final MailService mailService;
	private final SpringTemplateEngine templateEngine;
	private final MessageSource messageSource;
	private final JHipsterProperties jHipsterProperties;

	private final JavaMailSender javaMailSender;

	public MedicalAPIResource(UserService userService, MailService mailService, SpringTemplateEngine templateEngine,
			MessageSource messageSource, JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender) {
		this.userService = userService;
		this.mailService = mailService;
		this.templateEngine = templateEngine;
		this.messageSource = messageSource;
		this.jHipsterProperties = jHipsterProperties;
		this.javaMailSender = javaMailSender;
	}

	@GetMapping("/selectHealthByHealthId")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<Health>> validedEmail(Principal login) {
		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

		RestTemplate rt = new RestTemplate();
		String uri = new String(Constants.Url + "/queries/selectHealthByHealthId?healthId=" + user.getHealthId());

		List<Health> data = rt.getForObject(uri, List.class);

		System.out.println(data);
		return new ResponseEntity<List<Health>>(data, HttpStatus.ACCEPTED);

	}

	@GetMapping("/healthvalidate/{otp}/{slno}/{uname}/{email}/{type}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Map<String, String>> healthvalidate(@PathVariable Integer otp, @PathVariable String slno,
			@PathVariable String uname, @PathVariable String email, @PathVariable String type) {
		User user = userService.getUserWithAuthoritiesByLogin(uname).get();

		try {
			if (generateOtp.checkOTP(email, otp) == 1) {
				if (StringUtils.equals(type, "healthInfo")) {
					AddHealthCare addHealthCare = new AddHealthCare();
					addHealthCare.set$class("org.touchstone.basic.validateHealthCare");
					addHealthCare.setHealth("resource:org.touchstone.basic.Health#" + user.getHealthId());

					HealthCare health = new HealthCare();
					health.setHealthCareId(slno);
					health.setReportReference(new ArrayList<>());
					Validation valid = new Validation();
					valid.set$class("org.touchstone.basic.Validation");
					valid.setValidationStatus("VALIDATED");
					valid.setValidationType("MANUAL");
					valid.setValidationBy("");
					valid.setValidationDate("");
					valid.setValidationEmail("");
					valid.setValidationNote("");
					health.setValidation(valid);

					addHealthCare.setHealthCare(health);

					ObjectMapper mappers = new ObjectMapper();
					String jsonInString = mappers.writeValueAsString(addHealthCare);
					System.out.println(jsonInString);

					RestTemplate rt = new RestTemplate();
					rt.getMessageConverters().add(new StringHttpMessageConverter());
					String uri = new String(Constants.Url + "/validateHealthCare");
					rt.postForObject(uri, addHealthCare, AddHealthCare.class);
				} else if (StringUtils.equals(type, "healthReport")) {
					HealthCareReport_ health = new HealthCareReport_();

					health.setReportReference(new ArrayList<>());
					health.setHealthCareReportId(slno);
					HealthCareReport healthCareReport = new HealthCareReport();
					healthCareReport.set$class("org.touchstone.basic.validateHealthCareReport");
					healthCareReport.setHealth("resource:org.touchstone.basic.Health#" + user.getHealthId());
					healthCareReport.setHealthCareReport(health);
					Validation valid = new Validation();
					valid.set$class("org.touchstone.basic.Validation");
					valid.setValidationStatus("VALIDATED");
					valid.setValidationType("MANUAL");
					valid.setValidationBy("");
					valid.setValidationDate("");
					valid.setValidationEmail("");
					valid.setValidationNote("");
					health.setValidation(valid);

					ObjectMapper mappers = new ObjectMapper();
					String jsonInString = mappers.writeValueAsString(healthCareReport);
					System.out.println(jsonInString);
					RestTemplate rt = new RestTemplate();
					rt.getMessageConverters().add(new StringHttpMessageConverter());
					String uri = new String(Constants.Url + "/validateHealthCareReport");
					rt.postForObject(uri, healthCareReport, HealthCareReport.class);
				} else if (StringUtils.equals(type, "insuranceClaim")) {
					InsuranceClaim_ ic = new InsuranceClaim_();

					InsuranceClaim insuranceClaim = new InsuranceClaim();
					insuranceClaim.set$class("org.touchstone.basic.validateInsuranceDetails");
					insuranceClaim.setHealth("resource:org.touchstone.basic.Health#" + user.getHealthId());
					ic.setInsuranceClaimId(slno);
					ic.setClaimreports(new ArrayList<>());
					ic.setAilment(new ArrayList());
					Validation valid = new Validation();
					valid.set$class("org.touchstone.basic.Validation");
					valid.setValidationStatus("VALIDATED");
					valid.setValidationType("MANUAL");
					valid.setValidationBy("");
					valid.setValidationDate("");
					valid.setValidationEmail("");
					valid.setValidationNote("");

					ic.setValidation(valid);

					insuranceClaim.setInsuranceClaim(ic);
					ObjectMapper mappers = new ObjectMapper();
					String jsonInString = mappers.writeValueAsString(insuranceClaim);
					System.out.println(jsonInString);

					RestTemplate rt = new RestTemplate();
					rt.getMessageConverters().add(new StringHttpMessageConverter());
					String uri = new String(Constants.Url + "/validateInsuranceClaim");
					rt.postForObject(uri, insuranceClaim, InsuranceClaim.class);
				}

				generateOtp.removeOtp(email);

				Map<String, String> data = new HashMap<>();
				data.put("status", "success");
				return new ResponseEntity<Map<String, String>>(data, HttpStatus.ACCEPTED);
			} else {
				return new ResponseEntity<Map<String, String>>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, String>>(HttpStatus.UNAUTHORIZED);
		}

	}

	@PostMapping("/healthrecordsvalidate")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public void validateMedical(@RequestBody MedicalValidation medicalValidation, Principal login) {
		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

		try {
			if (StringUtils.equals(medicalValidation.getType(), "healthInfo")) {
				AddHealthCare addHealthCare = new AddHealthCare();
				addHealthCare.set$class("org.touchstone.basic.validateHealthCare");
				addHealthCare.setHealth("resource:org.touchstone.basic.Health#" + user.getHealthId());

				HealthCare health = new HealthCare();
				health.setHealthCareId(medicalValidation.getSlno());
				health.setReportReference(new ArrayList<>());
				Validation valid = new Validation();
				valid.set$class("org.touchstone.basic.Validation");
				valid.setValidationStatus("IN_PROGRESS");
				valid.setValidationType("MANUAL");
				valid.setValidationBy("");
				valid.setValidationDate("");
				valid.setValidationEmail("");
				valid.setValidationNote("");
				health.setValidation(valid);

				addHealthCare.setHealthCare(health);

				ObjectMapper mappers = new ObjectMapper();
				String jsonInString = mappers.writeValueAsString(addHealthCare);
				System.out.println(jsonInString);

				RestTemplate rt = new RestTemplate();
				rt.getMessageConverters().add(new StringHttpMessageConverter());
				String uri = new String(Constants.Url + "/validateHealthCare");
				rt.postForObject(uri, addHealthCare, AddHealthCare.class);
			} else if (StringUtils.equals(medicalValidation.getType(), "healthReport")) {
				HealthCareReport_ health = new HealthCareReport_();
				health.setReportReference(new ArrayList<>());
				health.setHealthCareReportId(medicalValidation.getSlno());
				HealthCareReport healthCareReport = new HealthCareReport();
				healthCareReport.set$class("org.touchstone.basic.validateHealthCareReport");
				healthCareReport.setHealth("resource:org.touchstone.basic.Health#" + user.getHealthId());
				healthCareReport.setHealthCareReport(health);
				Validation valid = new Validation();
				valid.set$class("org.touchstone.basic.Validation");
				valid.setValidationStatus("IN_PROGRESS");
				valid.setValidationType("MANUAL");
				valid.setValidationBy("");
				valid.setValidationDate("");
				valid.setValidationEmail("");
				valid.setValidationNote("");
				health.setValidation(valid);

				ObjectMapper mappers = new ObjectMapper();
				String jsonInString = mappers.writeValueAsString(healthCareReport);
				System.out.println(jsonInString);
				RestTemplate rt = new RestTemplate();
				rt.getMessageConverters().add(new StringHttpMessageConverter());
				String uri = new String(Constants.Url + "/validateHealthCareReport");
				rt.postForObject(uri, healthCareReport, HealthCareReport.class);
			} else if (StringUtils.equals(medicalValidation.getType(), "insuranceClaim")) {
				InsuranceClaim_ ic = new InsuranceClaim_();

				InsuranceClaim insuranceClaim = new InsuranceClaim();
				insuranceClaim.set$class("org.touchstone.basic.validateInsuranceClaim");
				insuranceClaim.setHealth("resource:org.touchstone.basic.Health#" + user.getHealthId());
				ic.setInsuranceClaimId(medicalValidation.getSlno());
				ic.setClaimreports(new ArrayList<>());
				ic.setAilment(new ArrayList());
				Validation valid = new Validation();
				valid.set$class("org.touchstone.basic.Validation");
				valid.setValidationStatus("IN_PROGRESS");
				valid.setValidationType("MANUAL");
				valid.setValidationBy("");
				valid.setValidationDate("");
				valid.setValidationEmail("");
				valid.setValidationNote("");

				ic.setValidation(valid);

				insuranceClaim.setInsuranceClaim(ic);
				ObjectMapper mappers = new ObjectMapper();
				String jsonInString = mappers.writeValueAsString(insuranceClaim);
				System.out.println(jsonInString);

				RestTemplate rt = new RestTemplate();
				rt.getMessageConverters().add(new StringHttpMessageConverter());
				String uri = new String(Constants.Url + "/validateInsuranceClaim");
				rt.postForObject(uri, insuranceClaim, InsuranceClaim.class);
			}

			// {otp}/{slno}/{uname}/{email}/{type}

			Locale locale = Locale.forLanguageTag("en");
			Context context = new Context(locale);
			context.setVariable("by", "Hi " + medicalValidation.getValidationBy());
			context.setVariable("desc", medicalValidation.getDescription());
			System.out.println("---------" + medicalValidation);
			context.setVariable("document", medicalValidation.getUrl());
			context.setVariable("url",
					" http://ridgelift.io:8080/api/healthvalidate/" + generateOtp.storeOTP(medicalValidation.getEmail())
							+ "/" + medicalValidation.getSlno() + "/" + user.getLogin() + "/"
							+ medicalValidation.getEmail() + "/" + medicalValidation.getType());
			String content = templateEngine.process("medical", context);
			String subject = messageSource.getMessage("email.activation.title", null, locale);
			sendEmail(user.getEmail(), subject, content, false, true);

//			String message = "Dear " + medicalValidation.getValidationBy() + " " + medicalValidation.getDescription()
//					+ " http://ridgelift.io:8080/api/healthvalidate/"
//					+ generateOtp.storeOTP(medicalValidation.getEmail()) + "/" + medicalValidation.getSlno() + "/"
//					+ user.getLogin() + "/" + medicalValidation.getEmail() + "/" + medicalValidation.getType();
//			mailService.sendEmail(medicalValidation.getEmail(), "validation", message, false, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@GetMapping("/tet")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public void tet() {
		Locale locale = Locale.forLanguageTag("en");
		Context context = new Context(locale);
		context.setVariable("by", "Hi " + "asdfasdfasdf");
		context.setVariable("desc", "asdfasdfasdf");
		context.setVariable("url", " http://ridgelift.io:8080/api/healthvalidate/");
		String content = templateEngine.process("medical", context);
		String subject = messageSource.getMessage("email.activation.title", null, locale);
		sendEmail("my3d3d@gmail.com", subject, content, false, true);

	}

	@PostMapping("/addHeathCare")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public void addHealthCare(@RequestParam(name = "file", required = false) MultipartFile files,
			@RequestParam("healthCare") String healthCare, @RequestParam("reference") String reference, Principal login)
			throws IOException {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		ObjectMapper jsonParserClient = new ObjectMapper();
		String id = RandomUtil.generateActivationKey();

		String[] links = new String[1];
		if (files != null) {
			links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/healthcare/"
					+ files.getOriginalFilename();
		} else {
			links[0] = "";
		}

		AddHealthCare addHealthCare = new AddHealthCare();
		addHealthCare.set$class("org.touchstone.basic.addHealthCare");
		addHealthCare.setHealth("resource:org.touchstone.basic.Health#" + user.getHealthId());

		HealthCare health = jsonParserClient.readValue(healthCare, HealthCare.class);
		health.setHealthCareId(id);

		List<String> ref = new ArrayList<>();
		ref.add(links[0]);
		health.setReportReference(ref);
		addHealthCare.setHealthCare(health);

		Validation valid = new Validation();
		valid.set$class("org.touchstone.basic.Validation");
		valid.setValidationStatus("VALIDATE");
		valid.setValidationType("MANUAL");
		valid.setValidationBy("");
		valid.setValidationDate("");
		valid.setValidationEmail("");
		valid.setValidationNote("");
		health.setValidation(valid);
		ObjectMapper mappers = new ObjectMapper();
		String jsonInString = mappers.writeValueAsString(addHealthCare);
		System.out.println(jsonInString);
		// uploadFileToS3(files, user.getUserId(), "HeathCare");

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String(Constants.Url + "/addHealthCare");
		rt.postForObject(uri, addHealthCare, AddHealthCare.class);

		File dir = new File(tmpDir);

		dir.mkdirs();
		if (dir.isDirectory() && files != null) {
			File serverFile = new File(dir, files.getOriginalFilename());
			serverFile.setReadable(true, false);
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
			stream.write(files.getBytes());
			stream.close();
			uploadFileToS3(files, user.getUserId(), "healthcare");
			serverFile.delete();
		}

	}

	@PostMapping("/addHealthCareReport")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public void addhealthCareReport(@RequestParam(name = "file", required = false) MultipartFile files,
			@RequestParam("healthReport") String healthReport, @RequestParam("reference") String reference,
			Principal login) {
		try {

			ObjectMapper jsonParserClient = new ObjectMapper();
			String id = RandomUtil.generateActivationKey();
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			String[] links = new String[1];
			if (files != null) {
				links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId()
						+ "/healthcarereport/" + files.getOriginalFilename();
			} else {
				links[0] = "";
			}

			HealthCareReport_ health = jsonParserClient.readValue(healthReport, HealthCareReport_.class);
			List<String> ref = new ArrayList<>();
			ref.add(links[0]);
			health.setReportReference(ref);
			health.setHealthCareReportId(id);
			HealthCareReport healthCareReport = new HealthCareReport();

			healthCareReport.setHealth("resource:org.touchstone.basic.Health#" + user.getHealthId());
			healthCareReport.setHealthCareReport(health);
			Validation valid = new Validation();
			valid.set$class("org.touchstone.basic.Validation");
			valid.setValidationStatus("VALIDATE");
			valid.setValidationType("MANUAL");
			valid.setValidationBy("");
			valid.setValidationDate("");
			valid.setValidationEmail("");
			valid.setValidationNote("");
			health.setValidation(valid);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(healthCareReport);
			System.out.println(jsonInString);
			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addhealthCareReport");
			rt.postForObject(uri, healthCareReport, HealthCareReport.class);

			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory() && files != null) {
				File serverFile = new File(dir, files.getOriginalFilename());
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(files.getBytes());
				stream.close();
				uploadFileToS3(files, user.getUserId(), "healthcarereport");
				serverFile.delete();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PostMapping("/addInsuranceClaim")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addinsuranceClaim(@RequestParam(name = "file", required = false) MultipartFile files,
			@RequestParam("healthInsurance") String healthInsurance, @RequestParam("reference") String reference,
			Principal login) throws JsonProcessingException {
		try {
			String id = RandomUtil.generateActivationKey();

			ObjectMapper jsonParserClient = new ObjectMapper();
			InsuranceClaim_ ic = jsonParserClient.readValue(healthInsurance, InsuranceClaim_.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			String[] links = new String[1];
			if (files != null) {
				links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/insurance/"
						+ files.getOriginalFilename();
			} else {
				links[0] = "";
			}

			InsuranceClaim insuranceClaim = new InsuranceClaim();

			insuranceClaim.setHealth("resource:org.touchstone.basic.Health#" + user.getHealthId());
			ic.setInsuranceClaimId(id);

			List<String> ref = new ArrayList<>();
			ref.add(links[0]);
			ic.setClaimreports(ref);

			ic.setAilment(new ArrayList());
			Validation valid = new Validation();
			valid.set$class("org.touchstone.basic.Validation");
			valid.setValidationStatus("VALIDATE");
			valid.setValidationType("MANUAL");
			valid.setValidationBy("");
			valid.setValidationDate("");
			valid.setValidationEmail("");
			valid.setValidationNote("");

			ic.setValidation(valid);

			insuranceClaim.setInsuranceClaim(ic);
			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(insuranceClaim);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addInsuranceClaim");
			rt.postForObject(uri, insuranceClaim, InsuranceClaim.class);

			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory() && files != null) {
				File serverFile = new File(dir, files.getOriginalFilename());
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(files.getBytes());
				stream.close();
				uploadFileToS3(files, user.getUserId(), "insurance");
				serverFile.delete();
			}

			return new ResponseEntity(insuranceClaim, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/addailment")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Ailment> addailment(@RequestParam("ailmentDetails") String ailmentDetails, Principal login)
			throws JsonProcessingException {

		try {
			Ailment ailment = new Ailment();
			ailment.set$class("org.touchstone.basic.addAilment");
			Ailment_ ailment_ = new Ailment_();
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			ailment.setHealth("resource:org.touchstone.basic.Health#" + user.getHealthId());

			String name = RandomStringUtils.random(8, 0, 20, true, true, "bj81G5RDED3DC6142kasok".toCharArray())
					.toString();

			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(tmpDir + name + ".pdf"));
			document.open();
			document.add(new Paragraph(ailmentDetails));
			document.close();

			File file = new File(tmpDir + name + ".pdf");
			FileInputStream input = new FileInputStream(file);
			MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain",
					IOUtils.toByteArray(input));
			uploadFileToS3(multipartFile, user.getUserId(), "medical");

			ailment_.setAilmentReference("https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId()
					+ "/medical/" + file.getName());
			ailment_.set$class("org.touchstone.basic.Ailment");
			ailment_.setAilmentId(name);

			ailment.setAilment(ailment_);
			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addailment");

			rt.postForObject(uri, ailment, Ailment.class);

			return new ResponseEntity(ailment, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
		}
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
