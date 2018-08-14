package com.touchstone.service.dto;

public class Ailment {

	private String $class = "org.touchstone.basic.addAilment";
	private Ailment_ ailment;
	private String health;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public Ailment_ getAilment() {
		return ailment;
	}

	public void setAilment(Ailment_ ailment) {
		this.ailment = ailment;
	}

	public String getHealth() {
		return health;
	}

	public void setHealth(String health) {
		this.health = health;
	}
}