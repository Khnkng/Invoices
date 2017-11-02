package com.qount.invoice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.glassfish.jersey.message.internal.ReaderWriter;

public class CustomLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
    @Context
    private ResourceInfo resourceInfo;

    private static final Logger log = Logger.getLogger(CustomLoggingFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        //Note down the start request time...we will use to calculate the total
        //execution time
        MDC.put("start-time", String.valueOf(System.currentTimeMillis()));

        log.debug("\n\n\n\n\n\n\nEntering in Resource : /{} "+ requestContext.getUriInfo().getPath());
        log.debug("\n\n\nMethod Name : {} "+ resourceInfo.getResourceMethod().getName());
        log.debug("\n\n\nClass : {} "+ resourceInfo.getResourceClass().getCanonicalName());
        logQueryParameters(requestContext);
        logMethodAnnotations();
        logRequestHeader(requestContext);

        //log entity stream...
        String entity = readEntityStream(requestContext);
        if(null != entity && entity.trim().length() > 0) {
            log.debug("Entity Stream : {}"+ entity);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("User: ").append(requestContext.getSecurityContext().getUserPrincipal() == null ? "unknown"
                        : requestContext.getSecurityContext().getUserPrincipal());
        sb.append(" - Path: ").append(requestContext.getUriInfo().getPath());
        sb.append(" - Header: ").append(requestContext.getHeaders());
        sb.append(" - Entity: ").append(entity);
        log.debug("\n\n\nHTTP REQUEST : " + sb.toString());
    }

	private void logQueryParameters(ContainerRequestContext requestContext) {
        Iterator<String> iterator = requestContext.getUriInfo().getPathParameters().keySet().iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            List<String> obj = requestContext.getUriInfo().getPathParameters().get(name);
            String value = null;
            if(null != obj && obj.size() > 0) {
                value = obj.get(0);
            }
            log.debug("\n\nQuery Parameter Name: {}, Value :{}" +  name+"," +value);
        }
    }

    private void logMethodAnnotations() {
        Annotation[] annotations = resourceInfo.getResourceMethod().getDeclaredAnnotations();
        if (annotations != null && annotations.length > 0) {
            log.debug("\n\n\n----Start Annotations of resource ----");
            for (Annotation annotation : annotations) {
                log.debug(annotation.toString());
            }
            log.debug("----End Annotations of resource----");
        }
    }

    private void logRequestHeader(ContainerRequestContext requestContext) {
        Iterator<String> iterator;
        log.debug("\n\n\n----Start Header Section of request ----");
        log.debug("Method Type : {}"+ requestContext.getMethod());
        iterator = requestContext.getHeaders().keySet().iterator();
        while (iterator.hasNext()) {
            String headerName = iterator.next();
            String headerValue = requestContext.getHeaderString(headerName);
            log.debug("Header Name: "+headerName+" , Header Value : "+ headerValue);
        }
        
        log.debug("----End Header Section of request ----");
    }

    private String readEntityStream(ContainerRequestContext requestContext)
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final InputStream inputStream = requestContext.getEntityStream();
        final StringBuilder builder = new StringBuilder();
        try
        {
            ReaderWriter.writeTo(inputStream, outStream);
            byte[] requestEntity = outStream.toByteArray();
            if (requestEntity.length == 0) {
                builder.append("").append("\n");;
            } else {
                builder.append(new String(requestEntity)).append("\n");;
            }
            requestContext.setEntityStream(new ByteArrayInputStream(requestEntity) );
        } catch (IOException ex) {
            log.error("----Exception occurred while reading entity stream :{}", ex);
        }
        return builder.toString();
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
    	StringBuilder sb = new StringBuilder();
        sb.append("\n\nHeader: ").append(responseContext.getHeaders());
        sb.append("\n\n - Entity: ").append(responseContext.getEntity());
        log.debug("\n\n\n\n\nHTTP RESPONSE : " + sb.toString());
    	
    	String stTime = (String)MDC.get("start-time");
        if(null == stTime || stTime.length() == 0) {
        	return;
        }
    	long startTime = Long.parseLong(stTime);
        long executionTime = System.currentTimeMillis() - startTime;
        log.debug("Total request execution time : {} milliseconds" + executionTime);
        
        //clear the context on exit
        MDC.clear();
    }
    
    public static void main(String[] args) {
//    	String[] companyIds = {"495a05f7-4b01-421d-9f64-16d73618a38d"};
    	String[] companyIds = {"9987b686-9352-4f32-9958-a8cec3d74cf9","02a38414-0fa8-49fc-99b5-8f7d10ef77d0","041537bf-301c-47e3-ba12-f66d36c810ff","05ec0553-991a-41eb-a14c-383925164d10","062abb74-b3cb-4e26-a7c3-d6d9ee2fe3f9","0e446642-75e7-455f-999b-2c7426be282a","178e7a68-6f54-46f2-b04c-759ec2f118e1","23bb2e22-a730-4d3d-9a02-b728ff176296","26893ec3-83de-4ca4-b25a-d9ce0fc151ce","2a594e4f-574b-48b3-b288-a70ae712fba6","2f96697b-3e0c-40d8-96e2-a28e327b4e38","3c81c7f3-bccb-47d4-8ae2-7492e6d4c061","43ca0080-3dc5-4f95-8d61-c9a98ee73963","4ac61639-7a8b-49c9-a98e-f60d2c855deb","4c84510c-49ea-4eb7-b564-41d80c0b0fe8","5dbbba4e-80a0-49b2-a56f-b13721e8da8e","5eacf0e3-0ae7-4d60-a1f2-bbfc9315fc6a","6bcbd215-4061-4f9c-a215-c60d9a8829ab","72f9ea00-488a-4775-b657-553860341fe2","792ece48-340f-49c3-a6af-2c0c10ec4be4","7dd9930c-04cd-443d-a724-4feb63296a87","80f02bdc-53a7-4a94-8625-d1ca38763b19","88c6fa3f-52ca-4343-a0db-d9c80beb00ac","a2f60837-0561-4d87-8f32-228fe81cc981","a31b69be-8810-4214-aec9-4dfa0b335fbc","b032ea03-7953-4af0-89a7-85551c6033e9","b35f4744-3507-4de9-a891-879249e9c797","b66ce43f-7508-47e9-a9e1-6ca88d351177","b69e4ebd-0618-4245-aa99-ccc2576a2e06","b91ec123-0c33-4f9a-adec-1917c4f3cffd","c47ed8af-3127-4972-b780-e7bdf9b5c743","c6ca2629-cc9f-4aa1-9d95-f3b4ce70dfbb","cbac8e38-e638-43a6-87ec-bb2d03f9bd5e","e1a393d1-8518-47e5-a94f-c1ba7359e5ff","e4dc70ff-ae7f-478f-8d0a-66449f117d00","e725eeda-ade1-4a84-868b-f955c258759a","eb310cbe-6036-4bf3-a462-44fbd2343ea1","f6d03830-e956-4a9a-bf36-00f13c3211b8","f8047200-bdcb-446a-9b91-ebdafd42f47e","a525bf7f-3017-40dd-9f1a-86e4386dfcb4","9caebd3b-2b5f-4c99-9599-61ee9c5f3180"};
    	for(int i=0;i<companyIds.length;i++){
			String query = "INSERT INTO `invoice_preferences` (`id`, `template_type`, `document_id`, `display_logo`, `accent_color`, `default_payment_terms`, `default_title`, `default_sub_heading`, `default_footer`, `standard_memo`, `items`, `units`, `price`, `amount`, `hide_item_name`, `hide_item_description`, `hide_units`, `hide_price`, `hide_amount`, `company_id`, `company_logo`) VALUES"
					+ "('"+UUID.randomUUID().toString()+"','Default',NULL,'0',NULL,NULL,NULL,NULL,NULL,NULL,'Item','Quantity','Price','Amount','0','0','0','0','0',"
							+ "'"+companyIds[i]+"',NULL);";
			System.out.println(query);
    	}
	}
}
