package com.touchstone.service.dto;

import java.util.Arrays;

/**
 * Skills
 */
public class Skills {

	private String $class;

	private String skill_slno;

	private String skillName;

	private String yearOfExp;

	private String expertiseLevel;
	private String[] supportingDocumentLinks;

	private CertificateValidation validation;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}
	
	public String getSkill_slno() {
		return skill_slno;
	}

	public void setSkill_slno(String skill_slno) {
		this.skill_slno = skill_slno;
	}

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public String getYearOfExp() {
		return yearOfExp;
	}

	public void setYearOfExp(String yearOfExp) {
		this.yearOfExp = yearOfExp;
	}

	public String getExpertiseLevel() {
		return expertiseLevel;
	}

	public void setExpertiseLevel(String expertiseLevel) {
		this.expertiseLevel = expertiseLevel;
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
		return "Skills [$class=" + $class + ", skill_slno=" + skill_slno + ", skillName=" + skillName + ", yearOfExp="
				+ yearOfExp + ", expertiseLevel=" + expertiseLevel + ", supportingDocumentLinks="
				+ Arrays.toString(supportingDocumentLinks) + ", validation=" + validation + "]";
	}
}
