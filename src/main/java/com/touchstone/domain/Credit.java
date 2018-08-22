package com.touchstone.domain;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * tax
 */

@Document(collection = "credit")
public class Credit implements Serializable {

	private static final long serialVersionUID = 1L;

	private String path;

	private String reportdate;

	private String userId;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getReportdate() {
		return reportdate;
	}

	public void setReportdate(String reportdate) {
		this.reportdate = reportdate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
