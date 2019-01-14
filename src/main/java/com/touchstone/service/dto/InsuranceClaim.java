
package com.touchstone.service.dto;

public class InsuranceClaim {

	private String $class = "org.touchstone.basic.addInsuranceClaim";
	private InsuranceClaim_ insuranceClaim;
	private String health;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public InsuranceClaim_ getInsuranceClaim() {
		return insuranceClaim;
	}

	public void setInsuranceClaim(InsuranceClaim_ insuranceClaim) {
		this.insuranceClaim = insuranceClaim;
	}

	public String getHealth() {
		return health;
	}

	public void setHealth(String health) {
		this.health = health;
	}

}
