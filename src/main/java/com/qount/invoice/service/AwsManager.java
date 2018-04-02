package com.qount.invoice.service;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

/**
 * 
 * @author Ravi
 *
 */
public class AwsManager {

	private static final Logger LOGGER = Logger.getLogger(AwsManager.class);

	private static final AWSStaticCredentialsProvider CREDENTIALS_PROVIDER = createCredentialsProvider();

	private static AWSStaticCredentialsProvider createCredentialsProvider() {
		LOGGER.info("Creating aws creadentials Instance");
		AWSStaticCredentialsProvider credentialsProvider = null;
		try {
			ConfigService config = ConfigService.getInstance();
			String secret = config.getValue("encryption_key");
			String accessID = AESCrypt.decrypt(config.getValue("aws_access_key_id"), secret);
			String awsSecret = AESCrypt.decrypt(config.getValue("aws_secret_access_key"), secret);
			System.out.println("accessID = " + accessID + " awsSecret = " + awsSecret );
			AWSCredentials credentials = new BasicAWSCredentials(accessID, awsSecret);
			credentialsProvider = new AWSStaticCredentialsProvider(credentials);
			LOGGER.debug("credentialsProvider [ " + credentialsProvider.getCredentials().toString());
		} catch (Exception e) {
			LOGGER.error("Error creating aws client", e);
		}
		return credentialsProvider;
	}

	public static AWSStaticCredentialsProvider getCredentialsProvider() {
		return CREDENTIALS_PROVIDER;
	}

}
