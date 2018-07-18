package com.touchstone.service.dto;

import java.util.Arrays;

/**
 * Experience
 */
public class Experience {

	private String $class;

	private String experience_slno;

	private String from;

	private String to;

	private String position;
	private String organizationName;
	private String[] skills;
	private String jobRole;
	private String[] supportingDocumentLinks;

	private CertificateValidation validation;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getExperience_slno() {
		return experience_slno;
	}

	public void setExperience_slno(String experience_slno) {
		this.experience_slno = experience_slno;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String[] getSkills() {
		return skills;
	}

	public void setSkills(String[] skills) {
		this.skills = skills;
	}

	public String getJobRole() {
		return jobRole;
	}

	public void setJobRole(String jobRole) {
		this.jobRole = jobRole;
	}

	public String[] getSupportingDocumentLinks() {
		return supportingDocumentLinks;
	}

	public void setSupportingDocumentLinks(String[] supportingDocumentLinks) {
		this.supportingDocumentLinks = supportingDocumentLinks;
	}

	public CertificateValidation getValidation() {
		return validation;
	}

	public void setValidation(CertificateValidation validation) {
		this.validation = validation;
	}

	@Override
	public String toString() {
		return "Experience [$class=" + $class + ", experience_slno=" + experience_slno + ", from=" + from + ", to=" + to
				+ ", position=" + position + ", organizationName=" + organizationName + ", skills="
				+ Arrays.toString(skills) + ", jobRole=" + jobRole + ", supportingDocumentLinks="
				+ Arrays.toString(supportingDocumentLinks) + ", validation=" + validation + "]";
	}

}
