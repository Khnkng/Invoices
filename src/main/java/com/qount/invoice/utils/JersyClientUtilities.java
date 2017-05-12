package com.qount.invoice.utils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.SyncInvoker;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

/**
 * @author Mateen
 * @version 1.0 Feb 27 2016
 */
public class JersyClientUtilities {

	private static final Logger LOGGER = Logger.getLogger(JersyClientUtilities.class);
	
	public static SyncInvoker constructMultipartRequest(String url,String header){
		Client client = ClientBuilder.newBuilder()
		        .register(MultiPartFeature.class)
		        .build();
		return client.target(url).request().header("Authorization", header);
	}
	
	public static SyncInvoker constructRequest(String url,String header,String contentType){
		Client client = ClientBuilder.newBuilder()
		        .build();
		return client.target(url).request().header("Authorization", header).header("Content-Type", contentType);
	}
	
	public static void closeMultiPart(MultiPart multiPart){
		try {
			if(multiPart!=null){
				multiPart.close();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}
	
	public static void closeFormDataMultiPart(FormDataMultiPart formDataMultiPart){
		try {
			if(formDataMultiPart!=null){
				formDataMultiPart.close();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

}