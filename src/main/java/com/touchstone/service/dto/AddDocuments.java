
package com.touchstone.service.dto;

public class AddDocuments {

	private String $class = "org.touchstone.basic.addDocuments";
	private Documents documents;
	private String personalRecords;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public Documents getDocuments() {
		return documents;
	}

	public void setDocuments(Documents documents) {
		this.documents = documents;
	}

	public String getPersonalRecords() {
		return personalRecords;
	}

	public void setPersonalRecords(String personalRecords) {
		this.personalRecords = personalRecords;
	}

}
