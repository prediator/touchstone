package com.touchstone.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * docs
 */

@org.springframework.data.mongodb.core.mapping.Document(collection = "docs")
public class AmazonS3 implements Serializable {

	private static final long serialVersionUID = 1L;


	private String user;

	private String name;

	private String qualification;

	private String fileName;

	 

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "AmazonS3 [ user=" + user + ", name=" + name + ", qualification=" + qualification
				+ ", fileName=" + fileName + "]";
	}

}