package com.touchstone.web.rest;

import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchstone.config.Constants;
import com.touchstone.domain.User;
import com.touchstone.service.UserService;
import com.touchstone.service.dto.Ailment_;
import com.touchstone.service.dto.AwardsRecognitions;
import com.touchstone.service.dto.BankDetails;
import com.touchstone.service.dto.Certification;
import com.touchstone.service.dto.CreditReport;
import com.touchstone.service.dto.Education;
import com.touchstone.service.dto.Experience;
import com.touchstone.service.dto.Health;
import com.touchstone.service.dto.HealthCareReport_;
import com.touchstone.service.dto.HealthCare_;
import com.touchstone.service.dto.InsuranceClaim_;
import com.touchstone.service.dto.InsuranceDetails_;
import com.touchstone.service.dto.Ious;
import com.touchstone.service.dto.MiscellaneousAssetDetails;
import com.touchstone.service.dto.MyVaultDTO;
import com.touchstone.service.dto.PersonalRecords;
import com.touchstone.service.dto.ProfileDTO;
import com.touchstone.service.dto.Project;
import com.touchstone.service.dto.PropertyDetails;
import com.touchstone.service.dto.Skills;
import com.touchstone.service.dto.TaxDetails;

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
		List<MyVaultDTO> list = new ArrayList<>();
		MyVaultDTO temp = new MyVaultDTO();
		URL url = null;
		String temp_filename = null;

		ObjectMapper mapper = new ObjectMapper();
		RestTemplate rt = new RestTemplate();
		String uri = new String(Constants.Url + "/Profile/" + user.getProfileId());

		ProfileDTO data1 = rt.getForObject(uri, ProfileDTO.class);
		String jsonInString = mapper.writeValueAsString(data1);
		ProfileDTO profileDTO = mapper.readValue(jsonInString, ProfileDTO.class);
		
		List<Education> education = profileDTO.getEducation();
		List<Certification> certificate = profileDTO.getCertification();
		List<Skills> skill = profileDTO.getSkills();
		List<Experience> exp = profileDTO.getExperience();
		List<Project> project= profileDTO.getProject();
		
		
		if(project!=null && !project.isEmpty()) {
			for(Project t : project) {
				temp = new MyVaultDTO();
				if(t.getSupportingDocumentLinks()!=null && t.getSupportingDocumentLinks().length > 0) {
					try {
						url = new URL(t.getSupportingDocumentLinks()[0]);
						temp_filename = FilenameUtils.getName(url.getPath());
					}catch (Exception e) {
						temp_filename = null;
					}
				}else {
					temp_filename = null;
				}
				
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("Project");
					temp.setPath(t.getSupportingDocumentLinks()[0]);
					list.add(temp);
				}
			}	
		}
		if(exp!=null && !exp.isEmpty()) {
			for(Experience t : exp) {
				temp = new MyVaultDTO();
				if(t.getSupportingDocumentLinks()!=null && t.getSupportingDocumentLinks().length > 0) {
					try {
						url = new URL(t.getSupportingDocumentLinks()[0]);
						temp_filename = FilenameUtils.getName(url.getPath());
					}catch (Exception e) {
						temp_filename = null;
					}
				}else {
					temp_filename = null;
				}
				
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("Experience");
					temp.setPath(t.getSupportingDocumentLinks()[0]);
					list.add(temp);
				}
			}	
		}
		
		if(skill!=null && !skill.isEmpty()) {
			for(Skills t : skill) {
				temp = new MyVaultDTO();
				if(t.getSupportingDocumentLinks()!=null && t.getSupportingDocumentLinks().length > 0) {
					try {
						url = new URL(t.getSupportingDocumentLinks()[0]);
						temp_filename = FilenameUtils.getName(url.getPath());
					}catch (Exception e) {
						temp_filename = null;
					}
				}else {
					temp_filename = null;
				}
				
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("Skills");
					temp.setPath(t.getSupportingDocumentLinks()[0]);
					list.add(temp);
				}
			}	
		}
		
		if(certificate!=null && !certificate.isEmpty()) {
			for(Certification t : certificate) {
				temp = new MyVaultDTO();
				if(t.getSupportingDocumentLinks()!=null && t.getSupportingDocumentLinks().length > 0) {
					try {
						url = new URL(t.getSupportingDocumentLinks()[0]);
						temp_filename = FilenameUtils.getName(url.getPath());
					}catch (Exception e) {
						temp_filename = null;
					}
				}else {
					temp_filename = null;
				}
				
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("InsuranceClaim_");
					temp.setPath(t.getSupportingDocumentLinks()[0]);
					list.add(temp);
				}
			}	
		}
		
		if(education!=null && !education.isEmpty()) {
			for(Education t : education) {
				temp = new MyVaultDTO();
				if(t.getSupportingDocumentLinks()!=null && t.getSupportingDocumentLinks().length > 0) {
					try {
						url = new URL(t.getSupportingDocumentLinks()[0]);
						temp_filename = FilenameUtils.getName(url.getPath());
					}catch (Exception e) {
						temp_filename = null;
					}
				}else {
					temp_filename = null;
				}
				
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("Education");
					temp.setPath(t.getSupportingDocumentLinks()[0]);
					list.add(temp);
				}
			}	
		}
		
		

		return new ResponseEntity(list, HttpStatus.CREATED);
	}

	@GetMapping("/getVaultPersonal")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<PersonalRecords> getVault(Principal login) throws IOException {
		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		List<MyVaultDTO> list = new ArrayList<>();
		MyVaultDTO temp = new MyVaultDTO();
		URL url = null;
		String temp_filename = null;
		
		RestTemplate rt = new RestTemplate();
		String uri = new String(
				Constants.Url + "/queries/selectPersonalRecordByPersonalRecordId?personalId=" + user.getPersonalId());
		List<PersonalRecords> data = rt.getForObject(uri, List.class);

		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(data.get(0));
		PersonalRecords personalRecords = mapper.readValue(jsonInString, PersonalRecords.class);

		List<TaxDetails> tax = personalRecords.getTaxDetails();
		List<CreditReport> credit = personalRecords.getCreditReport();
		List<BankDetails> bank = personalRecords.getBankDetails();
		List<PropertyDetails> property = personalRecords.getPropertyDetails();
		List<Ious> ious = personalRecords.getIous();
		List<AwardsRecognitions> awards = personalRecords.getAwardsRecognitions();
		List<InsuranceDetails_> insurance = personalRecords.getInsuranceDetails();
		List<MiscellaneousAssetDetails> misc = personalRecords.getMiscellaneousAssetDetails();
		
		if(misc!=null && !misc.isEmpty()) {
			for(MiscellaneousAssetDetails t : misc) {
				temp = new MyVaultDTO();
				try {
					url = new URL(t.getPath());
					temp_filename = FilenameUtils.getName(url.getPath());
				}catch (Exception e) {
					temp_filename = null;
				}
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("MiscellaneousAssetDetails");
					temp.setPath(t.getPath());
					list.add(temp);
				}
			}	
		}
		
		if(insurance!=null && !insurance.isEmpty()) {
			for(InsuranceDetails_ t : insurance) {
				temp = new MyVaultDTO();
				try {
					url = new URL(t.getPath());
					temp_filename = FilenameUtils.getName(url.getPath());
				}catch (Exception e) {
					temp_filename = null;
				}
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("InsuranceDetails");
					temp.setPath(t.getPath());
					list.add(temp);
				}
			}	
		}
		
		if(awards!=null && !awards.isEmpty()) {
			for(AwardsRecognitions t : awards) {
				temp = new MyVaultDTO();
				try {
					url = new URL(t.getPath());
					temp_filename = FilenameUtils.getName(url.getPath());
				}catch (Exception e) {
					temp_filename = null;
				}
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("AwardsRecognitions");
					temp.setPath(t.getPath());
					list.add(temp);
				}
			}	
		}
		
		if(ious!=null && !ious.isEmpty()) {
			for(Ious t : ious) {
				temp = new MyVaultDTO();
				try {
					url = new URL(t.getPath());
					temp_filename = FilenameUtils.getName(url.getPath());
				}catch (Exception e) {
					temp_filename = null;
				}
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("ious");
					temp.setPath(t.getPath());
					list.add(temp);
				}
			}	
		}
		
		if(property!=null && !property.isEmpty()) {
			for(PropertyDetails t : property) {
				temp = new MyVaultDTO();
				try {
					url = new URL(t.getPath());
					temp_filename = FilenameUtils.getName(url.getPath());
				}catch (Exception e) {
					temp_filename = null;
				}
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("PropertyDetails");
					temp.setPath(t.getPath());
					list.add(temp);
				}
			}	
		}
		
		if(bank!=null && !bank.isEmpty()) {
			for(BankDetails t : bank) {
				temp = new MyVaultDTO();
				try {
					url = new URL(t.getPath());
					temp_filename = FilenameUtils.getName(url.getPath());
				}catch (Exception e) {
					temp_filename = null;
				}
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("BankDetails");
					temp.setPath(t.getPath());
					list.add(temp);
				}
			}	
		}
		
		if(credit!=null && !credit.isEmpty()) {
			for(CreditReport t : credit) {
				temp = new MyVaultDTO();
				try {
					url = new URL(t.getPath());
					temp_filename = FilenameUtils.getName(url.getPath());
				}catch (Exception e) {
					temp_filename = null;
				}
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("CreditReport");
					temp.setPath(t.getPath());
					list.add(temp);
				}
			}	
		}
		
		if(tax!=null && !tax.isEmpty()) {
			for(TaxDetails t : tax) {
				temp = new MyVaultDTO();
				try {
					url = new URL(t.getPath());
					temp_filename = FilenameUtils.getName(url.getPath());
				}catch (Exception e) {
					temp_filename = null;
				}
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("TaxDetails");
					temp.setPath(t.getPath());
					list.add(temp);
				}
			}	
		}
		
		
		return new ResponseEntity(list, HttpStatus.CREATED);
	}

	@GetMapping("/getVaultPersonalHealth")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Health> getHealth(Principal login) throws IOException {
		User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
		List<MyVaultDTO> list = new ArrayList<>();
		MyVaultDTO temp = new MyVaultDTO();
		URL url = null;
		String temp_filename = null;

		RestTemplate rt = new RestTemplate();
		String uri = new String(Constants.Url + "/queries/selectHealthByHealthId?healthId=" + user.getHealthId());
		ObjectMapper mapper = new ObjectMapper();

		List<Health> data2 = rt.getForObject(uri, List.class);
		String jsonInString = mapper.writeValueAsString(data2.get(0));
		Health health = mapper.readValue(jsonInString, Health.class);

		List<HealthCare_> healthcare = health.getHealthCare();
		List<HealthCareReport_> healthcare_report = health.getHealthCareReport();
		List<InsuranceClaim_> insurance = health.getInsuranceClaim();
		List<Ailment_> ailment = health.getAilment();
		
		if(ailment!=null && !ailment.isEmpty()) {
			for(Ailment_ t : ailment) {
				temp = new MyVaultDTO();
				try {
					url = new URL(t.getAilmentReference());
					temp_filename = FilenameUtils.getName(url.getPath());
				}catch (Exception e) {
					temp_filename = null;
				}
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("Ailment");
					temp.setPath(t.getAilmentReference());
					list.add(temp);
				}
			}	
		}
		
		if(insurance!=null && !insurance.isEmpty()) {
			for(InsuranceClaim_ t : insurance) {
				temp = new MyVaultDTO();
				if(!t.getClaimreports().isEmpty()) {
					try {
						url = new URL(t.getClaimreports().get(0));
						temp_filename = FilenameUtils.getName(url.getPath());
					}catch (Exception e) {
						temp_filename = null;
					}
				}else {
					temp_filename = null;
				}
				
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("InsuranceClaim_");
					temp.setPath(t.getClaimreports().get(0));
					list.add(temp);
				}
			}	
		}
		
		if(healthcare_report!=null && !healthcare_report.isEmpty()) {
			for(HealthCareReport_ t : healthcare_report) {
				temp = new MyVaultDTO();
				if(!t.getReportReference().isEmpty()) {
					try {
						url = new URL(t.getReportReference().get(0));
						temp_filename = FilenameUtils.getName(url.getPath());
					}catch (Exception e) {
						temp_filename = null;
					}
				}else {
					temp_filename = null;
				}
				
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("HealthCareReport");
					temp.setPath(t.getReportReference().get(0));
					list.add(temp);
				}
			}	
		}
		
		if(healthcare!=null &&  !healthcare.isEmpty()) {
			for(HealthCare_ t : healthcare) {
				temp = new MyVaultDTO();
				if(!t.getReportReference().isEmpty()) {
					try {
						url = new URL(t.getReportReference().get(0));
						temp_filename = FilenameUtils.getName(url.getPath());
					}catch (Exception e) {
						temp_filename = null;
					}
				}else {
					temp_filename = null;
				}
				
				if(temp_filename!=null) {
					temp.setFilename(temp_filename);
					temp.setType("HealthCare");
					temp.setPath(t.getReportReference().get(0));
					list.add(temp);
				}
			}	
		}
		return new ResponseEntity(list, HttpStatus.CREATED);
	}

}
