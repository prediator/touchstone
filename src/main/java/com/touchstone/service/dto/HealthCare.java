
package com.touchstone.service.dto;

public class HealthCare {

	private String $class = "org.touchstone.basic.addHealthCare";
	private HealthCare_ HealthCare;
	private String health;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public HealthCare_ getHealthCare() {
		return HealthCare;
	}

	public void setHealthCare(HealthCare_ healthCare) {
		HealthCare = healthCare;
	}

	public String getHealth() {
		return health;
	}

	public void setHealth(String health) {
		this.health = health;
	}

	@Override
	public String toString() {
		return "HealthCare [$class=" + $class + ", HealthCare=" + HealthCare + ", health=" + health + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

	 

}
