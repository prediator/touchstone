package com.touchstone.web.rest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.codahale.metrics.annotation.Timed;
import com.touchstone.domain.User;
import com.touchstone.service.MailService;
import com.touchstone.service.UserService;
import com.touchstone.service.dto.Download;
import com.touchstone.service.dto.ListFiles;

/**
 * REST controller for adding certificate, Education, Experience, Project,
 * Skills.
 */
@RestController
@RequestMapping("/api")
public class POCResource {

	private final Logger logger = LoggerFactory.getLogger(POCResource.class);

	private final UserService userService;
	// private final String tmpDir =
	// "C:\\Users\\Kadri\\Desktop\\Touch\\build\\libs\\";
	private final MailService mailService;

	private AmazonS3 s3client;

	@Value("${amazonProperties.endpointUrl}")
	private String endpointUrl;
	@Value("${amazonProperties.bucketName}")
	private String bucketName;
	@Value("${amazonProperties.accessKey}")
	private String accessKey;
	@Value("${amazonProperties.secretKey}")
	private String secretKey;

	public POCResource(UserService userService, MailService mailService) {
		this.userService = userService;
		this.mailService = mailService;
	}

	@PostConstruct
	private void initializeAmazon() throws IOException {
		BasicAWSCredentials creds = new BasicAWSCredentials(this.accessKey, this.secretKey);
		this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(creds))
				.withRegion("us-east-1").build();

	}

	@GetMapping("/poc/{id}")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<ListFiles>> getFile(@PathVariable String id) throws IOException {

		User user = userService.getUserWithAuthoritiesByLogin(id).get();

		List<ListFiles> data = new ArrayList<>();
		try {

			if (StringUtils.equals(user.getUserId(), id)) {

				ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(id)
						.withMaxKeys(2);
				ListObjectsV2Result result;

				do {
					result = s3client.listObjectsV2(req);

					for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
						ListFiles file = new ListFiles();
						file.setName(objectSummary.getKey());
						file.setSize(objectSummary.getSize() + "");
						data.add(file);
						System.out.printf(" - %s (size: %d)\n", objectSummary.getKey(), objectSummary.getSize());
					}
					// If there are more than maxKeys keys in the bucket, get a continuation token
					// and list the next objects.
					String token = result.getNextContinuationToken();
					System.out.println("Next Continuation Token: " + token);
					req.setContinuationToken(token);
				} while (result.isTruncated());
				return new ResponseEntity<List<ListFiles>>(data, HttpStatus.ACCEPTED);

			}

		} catch (AmazonServiceException ase) {
			logger.info("Caught an AmazonServiceException from GET requests, rejected reasons:");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			logger.info("Caught an AmazonClientException: ");
			logger.info("Error Message: " + ace.getMessage());
		}

		return null;

	}

	@PostMapping("/poc")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> download(@RequestBody Download download, Principal name)
			throws IOException {

		// "74109955991806253176"
		System.out.println("--"+name.getName());
		User user = userService.getUserWithAuthoritiesByLogin(name.getName()).get();
		byte[] content = null;
		try {
			if (StringUtils.equals(user.getUserId(), download.getPath())) {
				S3Object object = s3client.getObject(bucketName, download.getPath() + "/" + download.getFilename());
				S3ObjectInputStream s3is = object.getObjectContent();

				content = FileCopyUtils.copyToByteArray(s3is);
				HttpHeaders headers = new HttpHeaders();
				 headers.setContentType(MediaType.parseMediaType(object.getObjectMetadata().getContentType()));
				  
//				return ResponseEntity.ok()
//						.cacheControl(CacheControl.noCache())
//						.header("Content-Disposition", "attachment; filename=" + download.getFilename())
//						.contentLength(object.getObjectMetadata().getContentLength())
//						
//						.body(new InputStreamResource(s3is));
//				
				return new ResponseEntity<byte[]>(content, headers, HttpStatus.OK);
			}

		} catch (AmazonServiceException ase) {
			logger.info("Caught an AmazonServiceException from GET requests, rejected reasons:");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			logger.info("Caught an AmazonClientException: ");
			logger.info("Error Message: " + ace.getMessage());
		}

		return null;

	}
	
	@PostMapping("/download")
	public void downloadFile1(HttpServletRequest request, HttpServletResponse response, Principal name,@RequestBody Download download) throws FileNotFoundException, IOException {
		String path = download.getPath();
		String filename = download.getFilename();
		User user = userService.getUserWithAuthoritiesByLogin(name.getName()).get();
		try {
			if (StringUtils.equals(user.getUserId(),path)) {
				S3Object object = s3client.getObject(bucketName, path + "/" + filename);
				S3ObjectInputStream s3is = object.getObjectContent();
				response.setContentType(object.getObjectMetadata().getContentType());
				response.setContentLength((int) object.getObjectMetadata().getContentLength());
				String headerKey = "Content-Disposition";
		        String headerValue = String.format("attachment; filename=\"%s\"",filename);
		        response.setHeader(headerKey, headerValue);
		        OutputStream outStream = response.getOutputStream();
		        byte[] buffer = new byte[4096];
		        int bytesRead = -1;
		        while ((bytesRead = s3is.read(buffer)) != -1) {
		            outStream.write(buffer, 0, bytesRead);
		        }
		        s3is.close();
		        outStream.close();
			}
		} catch (AmazonServiceException ase) {
			logger.info("Caught an AmazonServiceException from GET requests, rejected reasons:");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			logger.info("Caught an AmazonClientException: ");
			logger.info("Error Message: " + ace.getMessage());
		}
	}

}
