package com.touchstone.service.dto;

/**
 * A DTO
 */
public class ProjectDTO {

	private String $class="";
	
	private Project project;

	private String id;

	private String profile="";

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	public String toString() {
		return "ProjectDTO [$class=" + $class + ", id=" + id + ", profile=" + profile + "]";
	}

}
