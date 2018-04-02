package com.qount.invoice.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.qount.invoice.database.dao.ConfigDAO;


public class ConfigService {


	private static final ConfigService CONFIG = new ConfigService();
	private Map<String, String> configParamaters = null;

	private ConfigService() {
		configParamaters = load();
	}

	private Map<String, String> load() {
		ConfigDAO configDaoImpl = new ConfigDAO();
		return configDaoImpl.get();
	}

	public static ConfigService getInstance() {
		return CONFIG;
	}

	/**
	 * 
	 * @param name
	 * @return
	 * returns the value associated with key from the database. Returns null if key does not exist
	 */
	public String getValue(String name) {
		String value = null;
		if (StringUtils.isNotBlank(name)) {
			if (configParamaters != null) {
				value = configParamaters.get(name.trim()).trim();
			}
		}
		return value;
	}



}
