package com.touchstone.service.dto;

import java.util.Arrays;

/**
 * Education
 */
public class Education {

	private String $class;

	private String education_slno = "";

	private String course = "";

	private String institute = "";

	private String specialization = "";
	private String yearOfPassing = "";
	private String score = "";
	private String courseType = "FULL_TIME";
	private String[] supportingDocumentLinks;

	private CertificateValidation validation;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getEducation_slno() {
		return education_slno;
	}

	public void setEducation_slno(String education_slno) {
		this.education_slno = education_slno;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getInstitute() {
		return institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getYearOfPassing() {
		return yearOfPassing;
	}

	public void setYearOfPassing(String yearOfPassing) {
		this.yearOfPassing = yearOfPassing;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getCourseType() {
		return courseType;
	}

	public void setCourseType(String courseType) {
		this.courseType = courseType;
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
		return "Education [$class=" + $class + ", education_slno=" + education_slno + ", course=" + course
				+ ", institute=" + institute + ", specialization=" + specialization + ", yearOfPassing=" + yearOfPassing
				+ ", score=" + score + ", courseType=" + courseType + ", supportingDocumentLinks="
				+ Arrays.toString(supportingDocumentLinks) + ", validation=" + validation + "]";
	}

}
