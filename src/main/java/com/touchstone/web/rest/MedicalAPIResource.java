package com.touchstone.web.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Principal;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.touchstone.config.Constants;
import com.touchstone.domain.User;
import com.touchstone.service.MailService;
import com.touchstone.service.UserService;
import com.touchstone.service.dto.Ailment;
import com.touchstone.service.dto.Ailment_;
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
	private final String tmpDir = "D:\\";
	// private final String tmpDir = "/tmp/";
	private GenerateOTP generateOtp = new GenerateOTP();
	private final MailService mailService;

	public MedicalAPIResource(UserService userService, MailService mailService) {
		this.userService = userService;
		this.mailService = mailService;
	}

	@PostMapping("/addailment")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Ailment> addailment(@RequestParam(name = "file", required = false) MultipartFile files,
			@RequestParam("ailmentDetails") String ailmentDetails, Principal login) throws JsonProcessingException {

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

			ailment_.setAilmentReference("medical/" + file.getName());
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

	@PostMapping("/addhealthCare")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<HealthCare> addhealthCare(@RequestBody HealthCare healthCare, Principal login)
			throws JsonProcessingException {

		try {

			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			healthCare.setHealth("resource:org.touchstone.basic.Health#" + user.getHealthId());
			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(healthCare);
			System.out.println(jsonInString);
			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addHealthCare");
			rt.postForObject(uri, healthCare, HealthCare.class);

			return new ResponseEntity(healthCare, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/addhealthCareReport")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addhealthCareReport(@RequestParam("healthReportDetails") String healthReportDetails , Principal login)
			throws JsonProcessingException {

		try {
			HealthCareReport healthCareReport = new HealthCareReport();
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			healthCareReport.setHealth("resource:org.touchstone.basic.Health#" + user.getHealthId());
			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(healthCareReport);
			System.out.println(jsonInString);
			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addhealthCareReport");
			rt.postForObject(uri, healthCareReport, HealthCareReport.class);

			return new ResponseEntity(healthCareReport, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/addInsuranceClaim")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addinsuranceClaim(@RequestBody InsuranceClaim insuranceClaim, Principal login)
			throws JsonProcessingException {

		try {

			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			insuranceClaim.setHealth("resource:org.touchstone.basic.health#" + user.getHealthId());

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addInsuranceClaim");
			rt.postForObject(uri, insuranceClaim, InsuranceClaim.class);

			return new ResponseEntity(insuranceClaim, HttpStatus.CREATED);
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
}
