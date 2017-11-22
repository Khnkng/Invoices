package com.qount.invoice.utils;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import com.qount.invoice.common.PropertyManager;



public class LTMUtils {
	private static final Map<String, String> LTMCACHE = new HashMap<>();

	/**
	 * 
	 * @param hostVaribale
	 * @param portVariable
	 * @param GTMVariable
	 * @return
	 */
	public static String getHostAddress(String hostVaribale, String portVariable, @NotNull String GTMVariable) {
		String path = null;
		if (StringUtils.isAnyBlank(hostVaribale, portVariable)) {
			return PropertyManager.getProperty(GTMVariable);
		}
		path = LTMCACHE.get(hostVaribale);
		if (StringUtils.isNotBlank(path)) {
			return path;
		}
		String internalLinkingAddress = System.getenv(PropertyManager.getProperty(hostVaribale));
		String internalLinkingPort = System.getenv(PropertyManager.getProperty(portVariable));
		if (StringUtils.isAnyBlank(internalLinkingAddress, internalLinkingPort)) {
			return PropertyManager.getProperty(GTMVariable);
		}
		
		path = "http://" + internalLinkingAddress + ":" + internalLinkingPort + "/";
		LTMCACHE.put(hostVaribale, path);
		System.out.println(path);
		return path;
	}
	
	
	public static String getHostAddress(String hostVaribale, String portVariable) {
		String path = null;
		if (StringUtils.isAnyBlank(hostVaribale, portVariable)) {
			return null;
		}
		path = LTMCACHE.get(hostVaribale);
		if (StringUtils.isNotBlank(path)) {
			return path;
		}
		String internalLinkingAddress = System.getenv(PropertyManager.getProperty(hostVaribale));
		String internalLinkingPort = System.getenv(PropertyManager.getProperty(portVariable));
		if (StringUtils.isAnyBlank(internalLinkingAddress, internalLinkingPort)) {
			return null;
		}
		path = "http://" + internalLinkingAddress + ":" + internalLinkingPort + "/";
		LTMCACHE.put(hostVaribale, path);
		System.out.println(path);
		return path;
	}

}
