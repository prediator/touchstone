package com.touchstone.web.rest;

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

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.touchstone.config.Constants;
import com.touchstone.service.dto.Certificate;
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

	public RestAPIResource() {
		super();
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
	public ResponseEntity<String> addCertification(@RequestBody Certificate certificate)
			throws JsonProcessingException {

		try {
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
	 * POST /addEducation : add education.
	 *
	 * @param EducationDTO
	 *            the education details
	 * @throws JsonProcessingException
	 */
	@PostMapping("/addEducation")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addEducation(@RequestBody EducationDTO education) throws JsonProcessingException {
		try {
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
	public ResponseEntity<String> addEducation(@RequestBody ExperienceDTO experience) throws JsonProcessingException {
		try {
			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addExperience");
			rt.postForObject(uri, experience, ExperienceDTO.class);
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
	public ResponseEntity<String> addEducation(@RequestBody ProjectDTO project) throws JsonProcessingException {
		try {
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
	public ResponseEntity<String> addEducation(@RequestBody SkillDTO skills) throws JsonProcessingException {
		try {
			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());
			String uri = new String(Constants.Url + "/addSkills");
			rt.postForObject(uri, skills, SkillDTO.class);
			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}
}
