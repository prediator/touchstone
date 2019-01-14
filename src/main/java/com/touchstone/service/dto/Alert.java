package com.touchstone.service.dto;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "alerts")
public class Alert {

	@NotNull
	@Id
	private String id;

	private String date;
	private String healthReportTypeName;
	private boolean status;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getHealthReportTypeName() {
		return healthReportTypeName;
	}

	public void setHealthReportTypeName(String healthReportTypeName) {
		this.healthReportTypeName = healthReportTypeName;
	}

	@Override
	public String toString() {
		return "Alert [id=" + id + ", date=" + date + ", healthReportTypeName=" + healthReportTypeName + ", status="
				+ status + "]";
	}

}
