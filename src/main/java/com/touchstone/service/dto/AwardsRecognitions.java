package com.touchstone.service.dto;

public class AwardsRecognitions {

	private String $class = "org.touchstone.basic.AwardsRecognitions";
	private String awardsRecognitionsId;
	private String path;
	private String year;
	private String month;
	private String title;
	private String narration;
	private String issuer;
	private Validation validation;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getAwardsRecognitionsId() {
		return awardsRecognitionsId;
	}

	public void setAwardsRecognitionsId(String awardsRecognitionsId) {
		this.awardsRecognitionsId = awardsRecognitionsId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public Validation getValidation() {
		return validation;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;
	}

}
