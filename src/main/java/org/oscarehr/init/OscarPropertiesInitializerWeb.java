/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.init;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;

/**
 * Load the instance properties file as early as possible so the values can be used during startup.
 * Properties file search order (value must exist and file must exist and be readable):
 * - command line argument org.junoemr.junoPropertiesFilename
 * - the environment variable JUNO_PROPERTIES_FILENAME
 * - the file $CATALINA_HOME/{context path name}.properties
 * - the file /usr/share/tomcat/{context path name}.properties
 * - none
 *
 * TODO: SPRINGUPGRADE: split properties into startup vs application.  Perhaps distinguish between ones that can be
 *                      refreshed while the app is running vs ones that can't (i.e. value is read and stored)
 */
@Component
public class OscarPropertiesInitializerWeb implements ApplicationContextInitializer<ConfigurableWebApplicationContext>
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final oscar.OscarProperties oscarProperties = oscar.OscarProperties.getInstance();

	private static final String TOMCAT_LOCATION_ENVIRONMENT_VARIABLE_NAME = "CATALINA_HOME";
	private static final String TOMCAT_DEFAULT_LOCATION = "/usr/share/tomcat";
	private static final String ENVIRONMENT_VARIABLE_NAME = "JUNO_PROPERTIES_FILENAME";
	private static final String PROPERTIES_FILE_ARGUMENT_NAME = "juno.propertiesFilename";

	@Autowired
	private Environment environment;

	public void initialize(ConfigurableWebApplicationContext context)
	{
		// Check for command line argument
		String propertiesFilenameCli = StringUtils.trimToNull(System.getProperty(PROPERTIES_FILE_ARGUMENT_NAME));

		// Check for environment variable
		String propertiesFilenameEnvironment =
				StringUtils.trimToNull(context.getEnvironment().getProperty(ENVIRONMENT_VARIABLE_NAME));

		// Check for context path (only if ServletContext exists, which likely means it is using external Tomcat)
		String contextPath = getContextPath(context);
		String propertiesFilenameTomcatEnvironment = null;
		String propertiesFilenameTomcatDefault = null;
		if(contextPath != null)
		{
			String propertiesContextFilename = contextPath + ".properties";

			// Check for $CATALINA_HOME
			propertiesFilenameTomcatEnvironment =
					StringUtils.trimToNull(context.getEnvironment().getProperty(TOMCAT_LOCATION_ENVIRONMENT_VARIABLE_NAME));

			if(propertiesFilenameTomcatEnvironment != null)
			{
				propertiesFilenameTomcatEnvironment += propertiesContextFilename;
			}

			// Otherwise use default path
			propertiesFilenameTomcatDefault = TOMCAT_DEFAULT_LOCATION + propertiesContextFilename;
		}

		logger.info("Loading properties file for OscarProperties");
		logger.info("Command line argument (-D" + PROPERTIES_FILE_ARGUMENT_NAME + "): " + propertiesFilenameCli);
		logger.info("Environment variable (" + ENVIRONMENT_VARIABLE_NAME + "): " + propertiesFilenameEnvironment);
		logger.info("Tomcat configured location (" + TOMCAT_LOCATION_ENVIRONMENT_VARIABLE_NAME + "): " + propertiesFilenameTomcatEnvironment);
		logger.info("Tomcat default location: " + propertiesFilenameTomcatDefault);

		String propertiesFilename = null;

		if(fileExists(propertiesFilenameCli))
		{
			propertiesFilename = propertiesFilenameCli;
		}
		else if(fileExists(propertiesFilenameEnvironment))
		{
			propertiesFilename = propertiesFilenameEnvironment;
		}
		else if(fileExists(propertiesFilenameTomcatEnvironment))
		{
			propertiesFilename = propertiesFilenameTomcatEnvironment;
		}
		else if(fileExists(propertiesFilenameTomcatDefault))
		{
			propertiesFilename = propertiesFilenameTomcatDefault;
		}

		if(propertiesFilename != null)
		{
			logger.info("Loading properties from file " + propertiesFilename);

			try
			{
				oscarProperties.readFromFile(propertiesFilename);
			}
			catch(IOException e)
			{
				logger.error("Error loading properties file.  Using defaults.", e);
			}
		}
		else
		{
			logger.info("No valid properties file found.  Using defaults.");
		}
	}

	private boolean fileExists(String filename)
	{
		if(filename == null)
		{
			return false;
		}

		File file = new File(filename);

		return (file.exists() && file.isFile());
	}

	private String getContextPath(ConfigurableWebApplicationContext context)
	{
		if(context == null || context.getServletContext() == null)
		{
			return null;
		}

		ServletContext servlet = context.getServletContext();
		String rawContextPath = servlet.getContextPath();

		String contextPath = cleanContextPath(rawContextPath);

		if("".equals(contextPath))
		{
			return null;
		}

		return contextPath;
	}

	// Remove leading slash if there is one
	private String cleanContextPath(String rawContextPath)
	{
		if(rawContextPath == null)
		{
			return null;
		}

		if("/".equals(rawContextPath.charAt(0)))
		{
			return rawContextPath.substring(1);
		}

		return rawContextPath;
	}
}
