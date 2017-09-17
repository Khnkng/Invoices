package com.qount.invoice.servlets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.qount.invoice.common.Log4jLoder;
import com.qount.invoice.common.PropertiesLoader;
import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.schedular.SchedularService;
import com.qount.invoice.utils.Constants;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * 
 * @author Ravikiran
 *
 */
public class ConfigurationLoaderServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	/**
	 * this method is called while the server startup or on deploying of the
	 * application to the server
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		loadSwaggerConfiguration(config);
		Log4jLoder.getLog4jLoder().initilializeLogging();
		PropertiesLoader.getPropertiesLoader().loadProjectProperties();
		try {
			Class.forName(MySQLManager.class.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (PropertyManager.getProperty("invoice.remainder.start.onload").equals("true")) {
			try {
				Class.forName(SchedularService.class.getName());
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}
	}

	

	public static void loadSwaggerConfiguration(ServletConfig config) {
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion(Constants.SWAGGER_API_SPEC_VERSION);
		beanConfig.setSchemes(new String[] { Constants.SWAGGER_API_HTTP });
		beanConfig.setBasePath(config.getServletContext().getContextPath());
		beanConfig.setResourcePackage(Constants.SWAGGER_API_PACKAGE);
		beanConfig.setScan(true);
	}

}