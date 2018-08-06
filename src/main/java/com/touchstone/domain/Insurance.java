package com.touchstone.domain;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import com.touchstone.domain.Credit.Reportdate;

/**
 * tax
 */

@Document(collection = "insurance")
public class Insurance implements Serializable {

	private static final long serialVersionUID = 1L;

	private String path;

	private String template;

	private String type;

	private String amount;

	private String maturitydate;

	private Reportdate reportdate;

	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getMaturitydate() {
		return maturitydate;
	}

	public void setMaturitydate(String maturitydate) {
		this.maturitydate = maturitydate;
	}

	public Reportdate getReportdate() {
		return reportdate;
	}

	public void setReportdate(Reportdate reportdate) {
		this.reportdate = reportdate;
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
