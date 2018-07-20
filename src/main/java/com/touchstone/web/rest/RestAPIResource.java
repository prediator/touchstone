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
import com.touchstone.config.Constants;
import com.touchstone.domain.User;
import com.touchstone.service.UserService;
import com.touchstone.service.dto.Certificate;
import com.touchstone.service.dto.CertificateValidation;
import com.touchstone.service.dto.Certification;
import com.touchstone.service.dto.EducationDTO;
import com.touchstone.service.dto.ExperienceDTO;
import com.touchstone.service.dto.ProjectDTO;
import com.touchstone.service.dto.SkillDTO;

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
	public ResponseEntity<String> addCertification(@RequestParam("file") MultipartFile file,
			@RequestParam("certificate") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();

			Certificate certificate = jsonParserClient.readValue(certi, Certificate.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

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
	@GetMapping("/validateCertification")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> validateCertification(Principal login) throws JsonProcessingException {

		try {
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			Certificate certificate = new Certificate();
			certificate.set$class("org.touchstone.basic.validateCertification");
			certificate.setCertification(new Certification());
			certificate.getCertification().set$class("org.touchstone.basic.Certification");
			certificate.getCertification().setValidation(new CertificateValidation());
			certificate.getCertification().getValidation().set$class("org.touchstone.basic.Validation");
			certificate.getCertification().getValidation().setValidationStatus("VALIDATE");
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
	public ResponseEntity<String> addEducation(@RequestParam("file") MultipartFile file,
			@RequestParam("education") String education, Principal login) throws JsonProcessingException {
		try {

			ObjectMapper jsonParserClient = new ObjectMapper();

			EducationDTO edu = jsonParserClient.readValue(education, EducationDTO.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			String fileName = file.getOriginalFilename();
			File dir = new File(tmpDir);

			dir.mkdirs();
			if (dir.isDirectory()) {
				File serverFile = new File(dir, fileName);
				serverFile.setReadable(true, false);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(file.getBytes());
				stream.close();
				uploadFileToS3(file, user.getUserId(), "education");
				serverFile.delete();
			}

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
			rt.postForObject(uri, education, EducationDTO.class);
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
	public ResponseEntity<String> addExperience(@RequestParam("file") MultipartFile file,
			@RequestParam("experience") String education, Principal login) throws JsonProcessingException {
		try {

			ObjectMapper jsonParserClient = new ObjectMapper();

			ExperienceDTO exp = jsonParserClient.readValue(education, ExperienceDTO.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

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
	public ResponseEntity<String> addProject(@RequestParam("file") MultipartFile file,
			@RequestParam("project") String prj, Principal login) throws JsonProcessingException {
		try {

			ObjectMapper jsonParserClient = new ObjectMapper();

			ProjectDTO project = jsonParserClient.readValue(prj, ProjectDTO.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

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
	public ResponseEntity<String> addSkill(@RequestParam("file") MultipartFile file,
			@RequestParam("skills") String skil, Principal login) throws JsonProcessingException {
		try {

			ObjectMapper jsonParserClient = new ObjectMapper();

			SkillDTO skill = jsonParserClient.readValue(skil, SkillDTO.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

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

			skill.set$class("org.touchstone.basic.addProject");
			skill.getSkills().set$class("org.touchstone.basic.Project");
			skill.getSkills().getValidation().set$class("org.touchstone.basic.Validation");
			skill.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			skill.getSkills().getValidation().setValidationStatus("VALIDATE");
			skill.getSkills().getValidation().setValidationType("MANUAL");

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

	@GetMapping("/getProfile/{profileId}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> ggetProfile(@PathVariable String profileId) {
		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());
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
