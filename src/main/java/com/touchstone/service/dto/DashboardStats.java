package com.touchstone.service.dto;

public class DashboardStats {

	private Integer totalRecords;

	private Integer validRecords;

	private Integer totalDocuments;
	
	private Integer percentage;

	public Integer getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	public Integer getValidRecords() {
		return validRecords;
	}

	public void setValidRecords(Integer validRecords) {
		this.validRecords = validRecords;
	}

	public Integer getTotalDocuments() {
		return totalDocuments;
	}

	public void setTotalDocuments(Integer totalDocuments) {
		this.totalDocuments = totalDocuments;
	}

	public Integer getPercentage() {
		return percentage;
	}

	public void setPercentage(Integer percentage) {
		this.percentage = percentage;
	}

}
