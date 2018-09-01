package com.touchstone.web.rest;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.mail.internet.MimeMessage;

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
import com.touchstone.config.Constants;
import com.touchstone.domain.User;
import com.touchstone.service.MailService;
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
import com.touchstone.service.dto.ValidationEmail;
import com.touchstone.service.util.RandomUtil;
import com.touchstone.web.rest.util.GenerateOTP;

import io.github.jhipster.config.JHipsterProperties;

/**
 * REST controller for adding certificate, Education, Experience, Project,
 * Skills.
 */
@RestController
@RequestMapping("/api")
public class RestAPIResource {

	private final Logger log = LoggerFactory.getLogger(RestAPIResource.class);
	private final UserService userService;
//	private final String tmpDir = "d:\\link\\";
	private final String tmpDir = "/tmp/";
	private GenerateOTP generateOtp = new GenerateOTP();
	private final MailService mailService;
	private final SpringTemplateEngine templateEngine;
	private final MessageSource messageSource;
	private final JHipsterProperties jHipsterProperties;

	private final JavaMailSender javaMailSender;

	public RestAPIResource(UserService userService, MailService mailService, SpringTemplateEngine templateEngine,
			MessageSource messageSource, JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender) {
		this.userService = userService;
		this.mailService = mailService;
		this.templateEngine = templateEngine;
		this.messageSource = messageSource;
		this.jHipsterProperties = jHipsterProperties;
		this.javaMailSender = javaMailSender;
	}

	public File convert(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	private static void unzip(FileInputStream fis, String destDir) {
		File dir = new File(destDir);
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		// buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try {
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(destDir + File.separator + fileName);
				System.out.println("Unzipping to " + newFile.getAbsolutePath());
				// create directories for sub directories in zip
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				// close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * POST /addCertification : Add certification.
	 *
	 * @param Certificate the certificate data
	 * @throws JsonProcessingException
	 */
	@PostMapping("/uploadlinkdin")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> uploadlinkdin(@RequestParam(name = "file", required = false) MultipartFile file,
			Principal login) throws JsonProcessingException {
		try {
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			FileInputStream fs = new FileInputStream(convert(file));
			unzip(fs, tmpDir + "/" + user.getProfileId() + "/");
			if (file != null) {
				BufferedReader br = null;
				String line = "";
				String cvsSplitBy = ",";

				try {
					int i = 0;

					String sl_no = RandomUtil.generateActivationKey();
					br = new BufferedReader(new FileReader(tmpDir + "/" + user.getProfileId() + "/Certifications.csv"));
					while ((line = br.readLine()) != null) {

						if (i > 0) {
							String[] data = line.split(cvsSplitBy);
							Certificate certificate = new Certificate();
							certificate.set$class("org.touchstone.basic.addCertification");
							certificate.setCertification(new Certification());
							certificate.getCertification().setCertification_slno(sl_no);
							certificate.getCertification().set$class("org.touchstone.basic.Certification");
							certificate.getCertification().setValidation(new CertificateValidation());
							certificate.getCertification().getValidation().set$class("org.touchstone.basic.Validation");
							certificate.getCertification().getValidation().setValidationStatus("VALIDATE");
							certificate.getCertification().getValidation().setValidationType("MANUAL");
							certificate.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());

							certificate.getCertification().setCourseName(data[0]);
							certificate.getCertification().setInstitute(data[2]);

							// certificate.getCertification().setSupportingDocumentLinks(links);
							ObjectMapper mappers = new ObjectMapper();
							String jsonInString = mappers.writeValueAsString(certificate);
							System.out.println(jsonInString);

							RestTemplate rt = new RestTemplate();
							rt.getMessageConverters().add(new StringHttpMessageConverter());
							String uri = new String(Constants.Url + "/addCertification");
							rt.postForObject(uri, certificate, Certificate.class);

							System.out.println(data[0]);
							System.out.println(data[2]);
						}
						i++;

					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
			Files.walk(Paths.get(tmpDir + "/" + user.getProfileId())).filter(Files::isRegularFile).map(Path::toFile)
					.forEach(File::delete);

			return new ResponseEntity(HttpStatus.CREATED);
		} catch (

		Exception e) {

			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * POST /addCertification : Add certification.
	 *
	 * @param Certificate the certificate data
	 * @throws JsonProcessingException
	 */
	@PostMapping("/addCertification")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addCertification(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("certificate") String certi, Principal login) throws JsonProcessingException {
		try {
			ObjectMapper jsonParserClient = new ObjectMapper();

			Certificate certificate = jsonParserClient.readValue(certi, Certificate.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();
			String[] links = new String[1];

			if (file != null) {
				links[0] = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/certificate/"
						+ file.getOriginalFilename();
				certificate.getCertification().setSupportingDocumentLinks(links);
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
			}

			certificate.set$class("org.touchstone.basic.addCertification");
			certificate.getCertification().set$class("org.touchstone.basic.Certification");
			certificate.getCertification().getValidation().set$class("org.touchstone.basic.Validation");
			certificate.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			certificate.getCertification().getValidation().setValidationStatus("VALIDATE");
			certificate.getCertification().getValidation().setValidationType("MANUAL");

			String sl_no = RandomUtil.generateActivationKey();
			certificate.getCertification().setCertification_slno(sl_no);

			// certificate.getCertification().setSupportingDocumentLinks(links);
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
	 * @param Certificate the certificate data
	 * @throws JsonProcessingException
	 */
	@GetMapping("/validateCertification/{otp}/{slno}/{uname}/{email}/{type}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Map<String, String>> validateCertification(@PathVariable Integer otp,
			@PathVariable String slno, @PathVariable String uname, @PathVariable String email,
			@PathVariable String type) throws JsonProcessingException {
		try {
			User user = userService.getUserWithAuthoritiesByLogin(uname).get();

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

	@PostMapping("/validate")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public void validateCertification(@RequestBody ValidationEmail validEmail, Principal login)
			throws JsonProcessingException {

		System.out.println(validEmail);

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
						+ validEmail.getType());
		String content = templateEngine.process("education", context);
		String subject = messageSource.getMessage("email.activation.title", null, locale);
		sendEmail(validEmail.getEmail(), subject, content, false, true);

	}

	/**
	 * POST /addEducation : add education.
	 *
	 * @param EducationDTO the education details
	 * @throws JsonProcessingException
	 */
	@PostMapping("/addEducation")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addEducation(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("education") String education, Principal login) throws JsonProcessingException {
		try {

			ObjectMapper jsonParserClient = new ObjectMapper();

			EducationDTO edu = jsonParserClient.readValue(education, EducationDTO.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			String sl_no = RandomUtil.generateActivationKey();
			edu.getEducation().setEducation_slno(sl_no);

			if (file != null) {
				String links = null;
				String fileName = null;
				File dir = new File(tmpDir);
				dir.mkdirs();
				links = "https://s3.ap-south-1.amazonaws.com/touchstonebackend/" + user.getUserId() + "/education/"
						+ file.getOriginalFilename();
				fileName = file.getOriginalFilename();
				if (dir.isDirectory()) {
					File serverFile = new File(dir, fileName);
					serverFile.setReadable(true, false);
					BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
					stream.write(file.getBytes());
					stream.close();
					uploadFileToS3(file, user.getUserId(), "education");
					serverFile.delete();
				}
				edu.getEducation().setSupportingDocumentLinks(links.split("@@@##$!!!"));
			} else {
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
			rt.postForObject(uri, education, EducationDTO.class);
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
			rt.postForObject(uri, education, ExperienceDTO.class);
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
			rt.postForObject(uri, education, ProjectDTO.class);
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
			rt.postForObject(uri, education, SkillDTO.class);
			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * POST /addExperience : add experience.
	 *
	 * @param ExperienceDTO the experience details
	 * @throws JsonProcessingException
	 */
	@PostMapping("/addExperience")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addExperience(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("experience") String education, Principal login) throws JsonProcessingException {
		try {

			ObjectMapper jsonParserClient = new ObjectMapper();

			ExperienceDTO exp = jsonParserClient.readValue(education, ExperienceDTO.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			String[] links = new String[1];

			if (file != null) {
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
			}

			String sl_no = RandomUtil.generateActivationKey();
			exp.getExperience().setExperience_slno(sl_no);

			exp.set$class("org.touchstone.basic.addExperience");
			exp.getExperience().set$class("org.touchstone.basic.Experience");
			exp.getExperience().getValidation().set$class("org.touchstone.basic.Validation");
			exp.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			exp.getExperience().getValidation().setValidationStatus("VALIDATE");
			exp.getExperience().getValidation().setValidationType("MANUAL");
			// exp.getExperience().setSupportingDocumentLinks(links);

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
	 * @param ProjectDTO the project details
	 * @throws JsonProcessingException
	 */
	@PostMapping("/addProject")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addProject(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("project") String prj, Principal login) throws JsonProcessingException {
		try {

			ObjectMapper jsonParserClient = new ObjectMapper();

			ProjectDTO project = jsonParserClient.readValue(prj, ProjectDTO.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			String[] links = new String[1];

			if (file != null) {
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
			}

			String sl_no = RandomUtil.generateActivationKey();
			project.getProject().setProject_slno(sl_no);

			project.set$class("org.touchstone.basic.addProject");
			project.getProject().set$class("org.touchstone.basic.Project");
			project.getProject().getValidation().set$class("org.touchstone.basic.Validation");
			project.setProfile("resource:org.touchstone.basic.Profile#" + user.getProfileId());
			project.getProject().getValidation().setValidationStatus("VALIDATE");
			project.getProject().getValidation().setValidationType("MANUAL");
			// project.getProject().setSupportingDocumentLinks(links);

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
	 * @param ProjectDTO the project details
	 * @throws JsonProcessingException
	 */
	@PostMapping("/addSkills")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addSkill(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("skills") String skil, Principal login) throws JsonProcessingException {
		try {

			ObjectMapper jsonParserClient = new ObjectMapper();

			SkillDTO skill = jsonParserClient.readValue(skil, SkillDTO.class);
			User user = userService.getUserWithAuthoritiesByLogin(login.getName()).get();

			String[] links = new String[1];

			if (file != null) {
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
			// skill.getSkills().setSupportingDocumentLinks(links);

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
