package com.touchstone.service.dto;

public class AddHealthCare {

	private String $class;
	private HealthCare healthCare;
	private String health;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public HealthCare getHealthCare() {
		return healthCare;
	}

	public void setHealthCare(HealthCare healthCare) {
		this.healthCare = healthCare;
	}

	public String getHealth() {
		return health;
	}

	public void setHealth(String health) {
		this.health = health;
	}

}
