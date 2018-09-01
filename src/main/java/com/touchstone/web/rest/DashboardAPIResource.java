package com.touchstone.web.rest;

import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchstone.config.Constants;
import com.touchstone.domain.User;
import com.touchstone.repository.AlertsRepository;
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
import com.touchstone.service.dto.Alert;
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
import com.touchstone.service.dto.Ious;
import com.touchstone.service.dto.MiscellaneousAssetDetails;
import com.touchstone.service.dto.PersonalRecords;
import com.touchstone.service.dto.ProfileDTO;
import com.touchstone.service.dto.Project;
import com.touchstone.service.dto.PropertyDetails;
import com.touchstone.service.dto.Skills;
import com.touchstone.service.dto.TaxDetails;
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

	private AlertsRepository alertsRepository;

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
			JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender, AlertsRepository alertsRepository) {
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
		this.alertsRepository = alertsRepository;
	}

	@GetMapping("/getDashboard")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<DashboardStats> getPersonalRecords(Principal login) throws IOException {
		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		RestTemplate rt = new RestTemplate();
		int countValid = 0;
		String uri = new String(
				Constants.Url + "/queries/selectPersonalRecordByPersonalRecordId?personalId=" + user.getPersonalId());

		List<PersonalRecords> data = rt.getForObject(uri, List.class);

		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(data.get(0));
		PersonalRecords personalRecords = mapper.readValue(jsonInString, PersonalRecords.class);

		for (TaxDetails d : personalRecords.getTaxDetails()) {
			if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
				countValid++;
			}
		}
		for (CreditReport d : personalRecords.getCreditReport()) {
			if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
				countValid++;
			}
		}
		for (BankDetails d : personalRecords.getBankDetails()) {
			if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
				countValid++;
			}
		}
		for (PropertyDetails d : personalRecords.getPropertyDetails()) {
			if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
				countValid++;
			}
		}
		for (Ious d : personalRecords.getIous()) {
			if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
				countValid++;
			}
		}
		for (AwardsRecognitions d : personalRecords.getAwardsRecognitions()) {
			if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
				countValid++;
			}
		}
		for (InsuranceDetails_ d : personalRecords.getInsuranceDetails()) {
			if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
				countValid++;
			}
		}
		for (MiscellaneousAssetDetails d : personalRecords.getMiscellaneousAssetDetails()) {
			if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
				countValid++;
			}
		}
		int count = personalRecords.getDocuments().size() + personalRecords.getTaxDetails().size()
				+ personalRecords.getCreditReport().size() + personalRecords.getBankDetails().size()
				+ personalRecords.getPropertyDetails().size() + personalRecords.getIous().size()
				+ personalRecords.getAwardsRecognitions().size() + personalRecords.getInsuranceDetails().size()
				+ personalRecords.getMiscellaneousAssetDetails().size();

		mapper = new ObjectMapper();
		rt = new RestTemplate();
		uri = new String(Constants.Url + "/Profile/" + user.getProfileId());

		ProfileDTO data1 = rt.getForObject(uri, ProfileDTO.class);
		jsonInString = mapper.writeValueAsString(data1);
		ProfileDTO profileDTO = mapper.readValue(jsonInString, ProfileDTO.class);

		for (Education d : profileDTO.getEducation()) {
			if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
				countValid++;
			}
		}

		for (Certification d : profileDTO.getCertification()) {
			if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
				countValid++;
			}
		}

		for (Skills d : profileDTO.getSkills()) {
			if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
				countValid++;
			}
		}

		for (Experience d : profileDTO.getExperience()) {
			if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
				countValid++;
			}
		}

		for (Project d : profileDTO.getProject()) {
			if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
				countValid++;
			}
		}

		count += profileDTO.getEducation().size() + profileDTO.getCertification().size()
				+ profileDTO.getExperience().size() + profileDTO.getProject().size() + profileDTO.getSkills().size();

		rt = new RestTemplate();
		uri = new String(Constants.Url + "/queries/selectHealthByHealthId?healthId=" + user.getHealthId());

		List<Health> data2 = rt.getForObject(uri, List.class);
		jsonInString = mapper.writeValueAsString(data2.get(0));
		Health health = mapper.readValue(jsonInString, Health.class);

		try {
			count += StringUtils.isEmpty(health.getAilment().get(0).getAilmentId()) ? 0 : health.getAilment().size();
		} catch (Exception e) {
		}
		try {
			for (HealthCare_ d : health.getHealthCare()) {
				if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
					countValid++;
				}
			}
			count += (StringUtils.isEmpty(health.getHealthCare().get(0).getHealthCareId()) ? 0
					: health.getHealthCare().size());
		} catch (Exception e) {
		}
		try {
			for (HealthCareReport_ d : health.getHealthCareReport()) {
				if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
					countValid++;
				}
			}
			count += (StringUtils.isEmpty(health.getHealthCareReport().get(0).getHealthCareReportId()) ? 0
					: health.getHealthCareReport().size());
		} catch (Exception e) {
		}
		try {

			for (InsuranceClaim_ d : health.getInsuranceClaim()) {
				if (!StringUtils.equals(d.getValidation().getValidationStatus(), "VALIDATE")) {
					countValid++;
				}
			}
			count += (StringUtils.isEmpty(health.getInsuranceClaim().get(0).getInsuranceClaimId()) ? 0
					: health.getInsuranceClaim().size());
		} catch (Exception e) {
		}

		DashboardStats dashboardStats = new DashboardStats();
		dashboardStats.setTotalRecords(count);
		dashboardStats.setValidRecords(countValid);
		dashboardStats.setTotalDocuments(getList(user.getUserId()));

		return new ResponseEntity<DashboardStats>(dashboardStats, HttpStatus.ACCEPTED);
	}

	@GetMapping("/getAlerts")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<Alert>> getAlerts(Principal login ) throws IOException {
		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		RestTemplate rt = new RestTemplate();
		String uri = new String(Constants.Url + "/queries/selectHealthByHealthId?healthId=10022764702391465219");

		List<Health> data = rt.getForObject(uri, List.class);

		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(data.get(0));
		Health health = mapper.readValue(jsonInString, Health.class);

		List<Alert> alert = new ArrayList<>();
		for (HealthCare_ h : health.getHealthCare()) {
			Alert a = new Alert();
			if (h.getNeedCheckUpReminder() == true) {
				try {
					if (alertsRepository.findOne(h.getHealthCareId()).isStatus()) {
						a.setDate(h.getDateOfReport());
						a.setId(h.getHealthCareId());
						a.setHealthReportTypeName(h.getHealthReportTypeName());
						alert.add(a);
					}

				} catch (Exception e) {
					a.setDate(h.getDateOfReport());
					a.setId(h.getHealthCareId());
					a.setHealthReportTypeName(h.getHealthReportTypeName());
					alert.add(a);
				}
				
			}
		}

		return new ResponseEntity<List<Alert>>(alert, HttpStatus.ACCEPTED);

	}

	@PostMapping("/setAlerts")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Alert> getAlertsPost(@RequestParam("alert") String alerts, Principal login)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper jsonParserClient = new ObjectMapper();
		Alert alert = jsonParserClient.readValue(alerts, Alert.class);

		if (alert.getId() != null) {
			alert.setStatus(false);
			alertsRepository.save(alert);

		}
		return new ResponseEntity<Alert>(alert, HttpStatus.ACCEPTED);

	}

	public static String compareDates(String d1, String d2) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = sdf.parse(d1);
			Date date2 = sdf.parse(d2);

			if (date1.after(date2)) {
				return "after";
			}
			if (date1.before(date2)) {
				return "before";
			}

			if (date1.equals(date2)) {
				return "equal";
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public int getList(String id) {
		AWSCredentials credentials = null;
		int count = 0;

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

		ObjectListing objectListing = s3.listObjects(bucketName);
		for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
			if (StringUtils.contains(os.getKey(), id)) {
				System.out.println(os.getKey());
				count++;
			}

		}

		return count;

	}

}
