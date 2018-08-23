package com.touchstone.service.dto;

public class BankDetails {

	private String $class = "org.touchstone.basic.BankDetails";
	private String bankDetailsId;
	private String path;
	private String accholder;
	private String bankname;
	private String accountno;
	private String branch;
	private String ifsc;
	private Validation validation;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getBankDetailsId() {
		return bankDetailsId;
	}

	public void setBankDetailsId(String bankDetailsId) {
		this.bankDetailsId = bankDetailsId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAccholder() {
		return accholder;
	}

	public void setAccholder(String accholder) {
		this.accholder = accholder;
	}

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getAccountno() {
		return accountno;
	}

	public void setAccountno(String accountno) {
		this.accountno = accountno;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public Validation getValidation() {
		return validation;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;
	}

}
