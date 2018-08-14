package com.touchstone.web.rest;

import java.io.File;
import java.security.Principal;

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
import com.touchstone.config.Constants;
import com.touchstone.domain.User;
import com.touchstone.service.MailService;
import com.touchstone.service.UserService;
import com.touchstone.service.dto.Ailment;
import com.touchstone.service.dto.Health;
import com.touchstone.service.dto.HealthCare;
import com.touchstone.service.dto.HealthCareReport;
import com.touchstone.service.dto.InsuranceClaim;
import com.touchstone.web.rest.util.GenerateOTP;

/**
 * REST controller for adding certificate, Education, Experience, Project,
 * Skills.
 */
@RestController
@RequestMapping("/api")
public class MedicalAPIResource {

	private final Logger log = LoggerFactory.getLogger(MedicalAPIResource.class);
	private final UserService userService;
	// private final String tmpDir =
	// "C:\\Users\\Kadri\\Desktop\\Touch\\build\\libs\\";
	private final String tmpDir = "/tmp/";
	private GenerateOTP generateOtp = new GenerateOTP();
	private final MailService mailService;

	public MedicalAPIResource(UserService userService, MailService mailService) {
		this.userService = userService;
		this.mailService = mailService;
	}

	@PostMapping("/addailment")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Ailment> addailment(@RequestBody Ailment ailment, Principal login)
			throws JsonProcessingException {

		try {
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			ailment.setHealth("resource:org.touchstone.basic.health#" + user.getHealthId());

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addailment");
			rt.postForObject(uri, ailment, Ailment.class);

			return new ResponseEntity(ailment, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(ailment, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/addhealthCare")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<HealthCare> addhealthCare(@RequestBody HealthCare healthCare) throws JsonProcessingException {

		try {

			// User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			// healthCare.setHealth("resource:org.touchstone.basic.health#" +
			// user.getHealthId());

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addhealthCare");
			rt.postForObject(uri, healthCare, HealthCare.class);

			return new ResponseEntity(healthCare, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(healthCare, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/addhealthCareReport")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addhealthCareReport(@RequestBody HealthCareReport healthCareReport, Principal login)
			throws JsonProcessingException {

		try {

			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			healthCareReport.setHealth("resource:org.touchstone.basic.health#" + user.getHealthId());

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addhealthCareReport");
			rt.postForObject(uri, healthCareReport, HealthCareReport.class);

			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/addinsuranceClaim")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addinsuranceClaim(@RequestBody InsuranceClaim insuranceClaim, Principal login)
			throws JsonProcessingException {

		try {

			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			insuranceClaim.setHealth("resource:org.touchstone.basic.health#" + user.getHealthId());

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/insuranceClaim");
			rt.postForObject(uri, insuranceClaim, InsuranceClaim.class);

			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
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
}
