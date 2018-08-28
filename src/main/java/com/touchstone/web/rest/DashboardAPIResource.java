package com.touchstone.web.rest;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

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
import com.touchstone.service.MailService;
import com.touchstone.service.PersonalService;
import com.touchstone.service.UserService;
import com.touchstone.service.dto.AddIous;
import com.touchstone.service.dto.AwardsRecognitions;
import com.touchstone.service.dto.BankDetails;
import com.touchstone.service.dto.CreditReport;
import com.touchstone.service.dto.DashboardStats;
import com.touchstone.service.dto.Documents;
import com.touchstone.service.dto.InsuranceDetails_;
import com.touchstone.service.dto.MiscellaneousAssetDetails;
import com.touchstone.service.dto.PersonalRecords;
import com.touchstone.service.dto.PropertyDetails;
import com.touchstone.web.rest.util.GenerateOTP;

import io.github.jhipster.config.JHipsterProperties;

/**
 * REST controller for adding certificate, Education, Experience, Project,
 * Skills.
 */
@RestController
@RequestMapping("/api")
public class DashboardAPIResource {

	private final Logger log = LoggerFactory.getLogger(DashboardAPIResource.class);
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

	private MiscellaneousRepository miscellaneousRepository;

	private PersonalService personalService;
	private final SpringTemplateEngine templateEngine;
	private final MessageSource messageSource;
	private final JHipsterProperties jHipsterProperties;

	private final JavaMailSender javaMailSender;

	public DashboardAPIResource(UserService userService, MailService mailService, TaxRepository taxRepository,
			CreditRepository creditRepository, BankRepository bankRepository, PropertyRepository propertyRepository,
			IousRepository iousRepository, AwardsRepository awardsRepository, InsuranceRepository insuranceRepository,
			MiscellaneousRepository miscellaneousRepository, PersonalRepository personalRepository,
			PersonalService personalService, SpringTemplateEngine templateEngine, MessageSource messageSource,
			JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender) {
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
	}

	@GetMapping("/getDashboard")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<DashboardStats> getPersonalRecords() throws IOException {

		RestTemplate rt = new RestTemplate();
		String uri = new String(
				Constants.Url + "/queries/selectPersonalRecordByPersonalRecordId?personalId=63015916596579988701");

		List<PersonalRecords> data = rt.getForObject(uri, List.class);

		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(data.get(0));
		PersonalRecords personalRecords = mapper.readValue(jsonInString, PersonalRecords.class);

		int count = personalRecords.getDocuments().size() + personalRecords.getTaxDetails().size()
				+ personalRecords.getCreditReport().size() + personalRecords.getBankDetails().size()
				+ personalRecords.getPropertyDetails().size() + personalRecords.getIous().size()
				+ personalRecords.getAwardsRecognitions().size() + personalRecords.getInsuranceDetails().size()
				+ personalRecords.getMiscellaneousAssetDetails().size();

		DashboardStats dashboardStats = new DashboardStats();
		dashboardStats.setTotalRecords(count);
		System.out.println(count);
		return new ResponseEntity<DashboardStats>(dashboardStats, HttpStatus.ACCEPTED);
	}

}
