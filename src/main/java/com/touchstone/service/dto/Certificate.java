package com.touchstone.service.dto;

/**
 * A DTO
 */
public class Certificate {

	private String $class;
	
	private Certification certification;

	private String id;

	private String profile;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public Certification getCertification() {
		return certification;
	}

	public void setCertification(Certification certification) {
		this.certification = certification;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	@Override
	public String toString() {
		return "Certificate [$class=" + $class + ", certification=" + certification + ", id=" + id + ", profile="
				+ profile + "]";
	}
}
