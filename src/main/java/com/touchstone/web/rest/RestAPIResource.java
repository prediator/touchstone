package com.touchstone.web.rest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import com.google.common.io.Files;
import com.touchstone.config.Constants;
import com.touchstone.domain.User;
import com.touchstone.service.UserService;
import com.touchstone.service.dto.Certificate;
import com.touchstone.service.dto.CertificateValidation;
import com.touchstone.service.dto.Certification;
import com.touchstone.service.dto.Education;
import com.touchstone.service.dto.EducationDTO;
import com.touchstone.service.dto.Experience;
import com.touchstone.service.dto.ExperienceDTO;
import com.touchstone.service.dto.Project;
import com.touchstone.service.dto.ProjectDTO;
import com.touchstone.service.dto.SkillDTO;
import com.touchstone.service.dto.Skills;
import com.touchstone.service.util.RandomUtil;

import afu.org.checkerframework.framework.qual.FieldIsExpression;

/**
 * REST controller for adding certificate, Education, Experience, Project,
 * Skills.
 */
@RestController
@RequestMapping("/api")
public class RestAPIResource {

	private final Logger log = LoggerFactory.getLogger(RestAPIResource.class);
	private final UserService userService;
	// private final String tmpDir =
	// "C:\\Users\\Kadri\\Desktop\\Touch\\build\\libs\\";
	private final String tmpDir = "/tmp/";

	public RestAPIResource(UserService userService) {
		this.userService = userService;
	}

	/**
	 * POST /addCertification : Add certification.
	 *
	 * @param Certificate
	 *            the certificate data
	 * @throws JsonProcessingException
	 */
	@PostMapping("/addCertification")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addCertification(@RequestParam(name = "file",required=false) MultipartFile file,
			@RequestParam("certificate") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();

			Certificate certificate = jsonParserClient.readValue(certi, Certificate.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			String[] links = new String[1];

			links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/certificate/"
					+ file.getOriginalFilename();
			String fileName = file.getOriginalFilename();
			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory()) {
				File serverFile = new File(dir, fileName);
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "certificate");
				serverFile.delete();
			}

			certificate.set$class("org.touchstone.basic.addCertification");
			certificate.getCertification().set$class("org.touchstone.basic.Certification");
			certificate.getCertification().getValidation().set$class("org.touchstone.basic.Validation");
			certificate.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			certificate.getCertification().getValidation().setValidationStatus("VALIDATE");
			certificate.getCertification().getValidation().setValidationType("MANUAL");

			String sl_no = RandomUtil.generateActivationKey();
			certificate.getCertification().setCertification_slno(sl_no);

			certificate.getCertification().setSupportingDocumentLinks(links);
			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(certificate);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addCertification");
			rt.postForObject(uri, certificate, Certificate.class);
			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * POST /addCertification : Add certification.
	 *
	 * @param Certificate
	 *            the certificate data
	 * @throws JsonProcessingException
	 */
	@GetMapping("/validateCertification/{slno}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> validateCertification(@PathVariable String slno, Principal login)
			throws JsonProcessingException {
		try {
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			Certificate certificate = new Certificate();
			certificate.set$class("org.touchstone.basic.validateCertification");
			certificate.setCertification(new Certification());
			certificate.getCertification().setCertification_slno(slno);
			certificate.getCertification().set$class("org.touchstone.basic.Certification");
			certificate.getCertification().setValidation(new CertificateValidation());
			certificate.getCertification().getValidation().set$class("org.touchstone.basic.Validation");
			certificate.getCertification().getValidation().setValidationStatus("VALIDATED");
			certificate.getCertification().getValidation().setValidationType("MANUAL");
			certificate.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			certificate.getCertification().setSupportingDocumentLinks(new String[0]);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(certificate);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateCertification");
			rt.postForObject(uri, certificate, Certificate.class);
			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * POST /addEducation : add education.
	 *
	 * @param EducationDTO
	 *            the education details
	 * @throws JsonProcessingException
	 */
	@PostMapping("/addEducation")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addEducation(@RequestParam(name="file[]",required=false) MultipartFile[] files,
			@RequestParam("education") String education, Principal login) throws JsonProcessingException {
		try {

			ObjectMapper jsonParserClient = new ObjectMapper();

			EducationDTO edu = jsonParserClient.readValue(education, EducationDTO.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			String sl_no = RandomUtil.generateActivationKey();
			edu.getEducation().setEducation_slno(sl_no);
			
			if(files != null && files.length>0) {
				String[] links = new String[files.length];
				String fileName =null;
				File dir = new File(tmpDir);
				dir.mkdirs();
				for(int i=0;i<files.length;i++) {
					links[i] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/education/"+ files[i].getOriginalFilename();
					 fileName = files[0].getOriginalFilename();
					 if (dir.isDirectory()) {
							File serverFile = new File(dir, fileName);
							serverFile.setReadable(true, false);
							BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
							stream.write(files[0].getBytes());
							stream.close();
							uploadFileToS3(files[0], user.getUserId(), "education");
							serverFile.delete();
						}
				
				}
				edu.getEducation().setSupportingDocumentLinks(links);
			}else {
				edu.getEducation().setSupportingDocumentLinks(new String[0]);
			}
			
			

//			String[] links = new String[1];
//			links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/education/"
//					+ files[0].getOriginalFilename();
//			edu.getEducation().setSupportingDocumentLinks(links);
//
//			String fileName = files[0].getOriginalFilename();
//			File dir = new File(tmpDir);
//
//			dir.mkdirs();
//			if (dir.isDirectory()) {
//				File serverFile = new File(dir, fileName);
//				serverFile.setReadable(true, false);
//				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
//				stream.write(file[0].getBytes());
//				stream.close();
//				uploadFileToS3(file[0], user.getUserId(), "education");
//				serverFile.delete();
//			}

			edu.set$class("org.touchstone.basic.addEducation");
			edu.getEducation().set$class("org.touchstone.basic.Education");
			edu.getEducation().getValidation().set$class("org.touchstone.basic.Validation");
			edu.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			edu.getEducation().getValidation().setValidationStatus("VALIDATE");
			edu.getEducation().getValidation().setValidationType("MANUAL");

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(edu);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addEducation");
			rt.postForObject(uri, edu, EducationDTO.class);
			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/validateEducation/{slno}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> validateEducation(@PathVariable String slno, Principal login)
			throws JsonProcessingException {

		try {
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			EducationDTO education = new EducationDTO();
			education.set$class("org.touchstone.basic.validateEducation");
			education.setEducation(new Education());
			education.getEducation().set$class("org.touchstone.basic.Education");
			education.getEducation().setValidation(new CertificateValidation());
			education.getEducation().getValidation().set$class("org.touchstone.basic.Validation");
			education.getEducation().getValidation().setValidationStatus("VALIDATED");
			education.getEducation().getValidation().setValidationType("MANUAL");
			education.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			education.getEducation().setSupportingDocumentLinks(new String[0]);
			education.getEducation().setEducation_slno(slno);
			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(education);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateEducation");
			rt.postForObject(uri, education, Certificate.class);
			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/validateExperience/{slno}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> validateExperience(@PathVariable String slno, Principal login)
			throws JsonProcessingException {

		try {
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			ExperienceDTO education = new ExperienceDTO();
			education.set$class("org.touchstone.basic.validateExperience");
			education.setExperience(new Experience());
			education.getExperience().set$class("org.touchstone.basic.Experience");
			education.getExperience().setValidation(new CertificateValidation());
			education.getExperience().getValidation().set$class("org.touchstone.basic.Validation");
			education.getExperience().getValidation().setValidationStatus("VALIDATED");
			education.getExperience().getValidation().setValidationType("MANUAL");
			education.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			education.getExperience().setSupportingDocumentLinks(new String[0]);
			education.getExperience().setExperience_slno(slno);
			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(education);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateExperience");
			rt.postForObject(uri, education, Certificate.class);
			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/validateProject/{slno}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> validateProject(@PathVariable String slno, Principal login)
			throws JsonProcessingException {

		try {
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			ProjectDTO education = new ProjectDTO();
			education.set$class("org.touchstone.basic.validateProject");
			education.setProject(new Project());
			education.getProject().set$class("org.touchstone.basic.Project");
			education.getProject().setValidation(new CertificateValidation());
			education.getProject().getValidation().set$class("org.touchstone.basic.Validation");
			education.getProject().getValidation().setValidationStatus("VALIDATED");
			education.getProject().getValidation().setValidationType("MANUAL");
			education.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			education.getProject().setSupportingDocumentLinks(new String[0]);
			education.getProject().setProject_slno(slno);
			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(education);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateProject");
			rt.postForObject(uri, education, Certificate.class);
			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/validateSkills/{slno}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> validateSkills(@PathVariable String slno, Principal login)
			throws JsonProcessingException {

		try {
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			SkillDTO education = new SkillDTO();
			education.set$class("org.touchstone.basic.validateSkills");
			education.setSkills(new Skills());
			education.getSkills().set$class("org.touchstone.basic.Skills");
			education.getSkills().setValidation(new CertificateValidation());
			education.getSkills().getValidation().set$class("org.touchstone.basic.Validation");
			education.getSkills().getValidation().setValidationStatus("VALIDATED");
			education.getSkills().getValidation().setValidationType("MANUAL");
			education.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			education.getSkills().setSupportingDocumentLinks(new String[0]);
			education.getSkills().setSkill_slno(slno);
			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(education);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/validateSkills");
			rt.postForObject(uri, education, Certificate.class);
			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * POST /addExperience : add experience.
	 *
	 * @param ExperienceDTO
	 *            the experience details
	 * @throws JsonProcessingException
	 */
	@PostMapping("/addExperience")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addExperience(@RequestParam(name="file",required=false) MultipartFile file,
			@RequestParam("experience") String education, Principal login) throws JsonProcessingException {
		try {

			ObjectMapper jsonParserClient = new ObjectMapper();

			ExperienceDTO exp = jsonParserClient.readValue(education, ExperienceDTO.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			String[] links = new String[1];

			links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/experience/"
					+ file.getOriginalFilename();
			exp.getExperience().setSupportingDocumentLinks(links);

			String fileName = file.getOriginalFilename();
			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory()) {
				File serverFile = new File(dir, fileName);
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "experience");
				serverFile.delete();
			}

			String sl_no = RandomUtil.generateActivationKey();
			exp.getExperience().setExperience_slno(sl_no);

			exp.set$class("org.touchstone.basic.addExperience");
			exp.getExperience().set$class("org.touchstone.basic.Experience");
			exp.getExperience().getValidation().set$class("org.touchstone.basic.Validation");
			exp.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			exp.getExperience().getValidation().setValidationStatus("VALIDATE");
			exp.getExperience().getValidation().setValidationType("MANUAL");

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(exp);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addExperience");
			rt.postForObject(uri, exp, ExperienceDTO.class);
			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * POST /addProject : add Project.
	 *
	 * @param ProjectDTO
	 *            the project details
	 * @throws JsonProcessingException
	 */
	@PostMapping("/addProject")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addProject(@RequestParam(name="file",required=false) MultipartFile file,
			@RequestParam("project") String prj, Principal login) throws JsonProcessingException {
		try {

			ObjectMapper jsonParserClient = new ObjectMapper();

			ProjectDTO project = jsonParserClient.readValue(prj, ProjectDTO.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			String[] links = new String[1];

			links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/project/"
					+ file.getOriginalFilename();
			project.getProject().setSupportingDocumentLinks(links);

			String fileName = file.getOriginalFilename();
			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory()) {
				File serverFile = new File(dir, fileName);
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "project");
				serverFile.delete();
			}

			String sl_no = RandomUtil.generateActivationKey();
			project.getProject().setProject_slno(sl_no);

			project.set$class("org.touchstone.basic.addProject");
			project.getProject().set$class("org.touchstone.basic.Project");
			project.getProject().getValidation().set$class("org.touchstone.basic.Validation");
			project.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			project.getProject().getValidation().setValidationStatus("VALIDATE");
			project.getProject().getValidation().setValidationType("MANUAL");

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(project);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addProject");
			rt.postForObject(uri, project, ProjectDTO.class);
			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * POST /addSkills : add Skills.
	 *
	 * @param ProjectDTO
	 *            the project details
	 * @throws JsonProcessingException
	 */
	@PostMapping("/addSkills")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addSkill(@RequestParam(name="file",required=false) MultipartFile file,
			@RequestParam("skills") String skil, Principal login) throws JsonProcessingException {
		try {

			ObjectMapper jsonParserClient = new ObjectMapper();

			SkillDTO skill = jsonParserClient.readValue(skil, SkillDTO.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			String[] links = new String[1];

			links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/skills/"
					+ file.getOriginalFilename();
			skill.getSkills().setSupportingDocumentLinks(links);

			String fileName = file.getOriginalFilename();
			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory()) {
				File serverFile = new File(dir, fileName);
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "skills");
				serverFile.delete();
			}

			String sl_no = RandomUtil.generateActivationKey();
			skill.getSkills().setSkill_slno(sl_no);

			skill.set$class("org.touchstone.basic.addSkills");

			skill.getSkills().set$class("org.touchstone.basic.Skills");
			skill.getSkills().getValidation().set$class("org.touchstone.basic.Validation");
			skill.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			skill.getSkills().getValidation().setValidationStatus("VALIDATE");
			skill.getSkills().getValidation().setValidationType("MANUAL");
			skill.getSkills().setExpertiseLevel("BEGINNER");
			skill.getSkills().setSupportingDocumentLinks(new String[0]);

			ObjectMapper mappers = new ObjectMapper();
			String jsonInString = mappers.writeValueAsString(skill);
			System.out.println(jsonInString);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addSkills");
			rt.postForObject(uri, skill, SkillDTO.class);
			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/getProfile")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> getProfile(Principal principal) {
		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
		User user = userService.getUserWithAuthoritiesByLogin(principal.getName()).get();
		String profileId = user.getProfileId();
		String uri = new String(Constants.Url + "/Profile/" + profileId);
		String data = rt.getForObject(uri, String.class);

		return new ResponseEntity<String>(data, HttpStatus.CREATED);

	}

	public void uploadFileToS3(MultipartFile file, String id, String type) {
		AWSCredentials credentials = null;
		try {
			credentials = new BasicAWSCredentials("AKIAJTTKGKU3EU5OH6OA", "J9kwzybAitcVBROx4HogeNXSsYiNy3j7vOMvZPPI");
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
