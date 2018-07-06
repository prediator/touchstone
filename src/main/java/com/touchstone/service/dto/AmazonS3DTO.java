package com.touchstone.service.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 *  docs
 */
@Document(collection = "docs")

public class AmazonS3DTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(max = 50)
	@Id
	private Integer id;

	private Long userId;

	private String name;

	private String qualification;

	private String fileName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
		return "AmazonS3 [id=" + id + ", userId=" + userId + ", name=" + name + ", qualification=" + qualification
				+ ", fileName=" + fileName + "]";
	}

}
