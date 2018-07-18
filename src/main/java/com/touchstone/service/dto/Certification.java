package com.touchstone.service.dto;

import java.util.Arrays;

/**
 * Certification
 */
public class Certification {

	private String $class;

	private String certification_slno;

	private String courseName;

	private String institute;

	private String yearOfPassing;

	private String[] supportingDocumentLinks;

	private CertificateValidation validation;
	
	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getCertification_slno() {
		return certification_slno;
	}

	public void setCertification_slno(String certification_slno) {
		this.certification_slno = certification_slno;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getInstitute() {
		return institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	public String getYearOfPassing() {
		return yearOfPassing;
	}

	public void setYearOfPassing(String yearOfPassing) {
		this.yearOfPassing = yearOfPassing;
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
		return "Certification [$class=" + $class + ", certification_slno=" + certification_slno + ", courseName="
				+ courseName + ", institute=" + institute + ", yearOfPassing=" + yearOfPassing
				+ ", supportingDocumentLinks=" + Arrays.toString(supportingDocumentLinks) + ", validation=" + validation
				+ "]";
	}
}
