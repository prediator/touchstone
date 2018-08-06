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

	private Reportdate reportdate;

	private String userId;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Reportdate getReportdate() {
		return reportdate;
	}

	public void setReportdate(Reportdate reportdate) {
		this.reportdate = reportdate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public class Reportdate {
		private String year;
		private String month;
		private String day;

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

		public String getDay() {
			return day;
		}

		public void setDay(String day) {
			this.day = day;
		}

		@Override
		public String toString() {
			return "Reportdate [year=" + year + ", month=" + month + ", day=" + day + "]";
		}

	}

}
