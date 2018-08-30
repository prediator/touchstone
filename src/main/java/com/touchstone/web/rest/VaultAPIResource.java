package com.touchstone.web.rest;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.codahale.metrics.annotation.Timed;
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
import com.touchstone.service.MailService;
import com.touchstone.service.PersonalService;
import com.touchstone.service.UserService;
import com.touchstone.service.dto.AddIous;
import com.touchstone.service.dto.AwardsRecognitions;
import com.touchstone.service.dto.BankDetails;
import com.touchstone.service.dto.Certification;
import com.touchstone.service.dto.CreditReport;
import com.touchstone.service.dto.DashboardStats;
import com.touchstone.service.dto.Education;
import com.touchstone.service.dto.Experience;
import com.touchstone.service.dto.Health;
import com.touchstone.service.dto.HealthCareReport_;
import com.touchstone.service.dto.HealthCare_;
import com.touchstone.service.dto.InsuranceClaim_;
import com.touchstone.service.dto.InsuranceDetails_;
import com.touchstone.service.dto.MiscellaneousAssetDetails;
import com.touchstone.service.dto.PersonalRecords;
import com.touchstone.service.dto.ProfileDTO;
import com.touchstone.service.dto.Project;
import com.touchstone.service.dto.PropertyDetails;
import com.touchstone.service.dto.Skills;
import com.touchstone.service.dto.TaxDetails;
import com.touchstone.service.dto.Alert;
import com.touchstone.web.rest.util.GenerateOTP;

import io.github.jhipster.config.JHipsterProperties;

/**
 * REST controller for adding certificate, Education, Experience, Project,
 * Skills.
 */
@RestController
@RequestMapping("/api")
public class VaultAPIResource {

	private final Logger log = LoggerFactory.getLogger(VaultAPIResource.class);
	private final UserService userService;

	public VaultAPIResource(UserService userService ) {
		this.userService = userService;

	}

	@GetMapping("/getVaultProfessional")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<ProfileDTO> getProfessionalt(Principal login) throws IOException {

		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

		ObjectMapper mapper = new ObjectMapper();
		RestTemplate rt = new RestTemplate();
		String uri = new String(Constants.Url + "/Profile/" + user.getProfileId());

		ProfileDTO data1 = rt.getForObject(uri, ProfileDTO.class);
		String jsonInString = mapper.writeValueAsString(data1);
		ProfileDTO profileDTO = mapper.readValue(jsonInString, ProfileDTO.class);

		return new ResponseEntity(profileDTO, HttpStatus.CREATED);
	}

	@GetMapping("/getVaultPersonal")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<PersonalRecords> getVault(Principal login) throws IOException {
		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

		RestTemplate rt = new RestTemplate();
		String uri = new String(
				Constants.Url + "/queries/selectPersonalRecordByPersonalRecordId?personalId=" + user.getPersonalId());

		List<PersonalRecords> data = rt.getForObject(uri, List.class);

		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(data.get(0));
		PersonalRecords personalRecords = mapper.readValue(jsonInString, PersonalRecords.class);

		return new ResponseEntity(personalRecords, HttpStatus.CREATED);
	}

	@GetMapping("/getVaultPersonalHealth")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Health> getHealth(Principal login) throws IOException {
		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

		RestTemplate rt = new RestTemplate();
		String uri = new String(Constants.Url + "/queries/selectHealthByHealthId?healthId=" + user.getHealthId());
		ObjectMapper mapper = new ObjectMapper();

		List<Health> data2 = rt.getForObject(uri, List.class);
		String jsonInString = mapper.writeValueAsString(data2.get(0));
		Health health = mapper.readValue(jsonInString, Health.class);

		return new ResponseEntity(health, HttpStatus.CREATED);
	}

}
