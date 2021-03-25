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

package org.oscarehr;

import org.apache.log4j.Logger;
import org.oscarehr.init.OscarPropertiesInitializerHeadless;
import org.oscarehr.init.OscarPropertiesInitializerWeb;
import org.oscarehr.util.MiscUtils;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import java.io.IOException;


@SpringBootApplication
@ComponentScan(basePackages = {"oscar", "org.oscarehr", "com.quatro"})
@ServletComponentScan(basePackages = {"com.junoemr", "org.oscarehr"})
@ImportResource({"classpath*:applicationContext.xml"})
@EnableConfigurationProperties
public class JunoApplication extends SpringBootServletInitializer
{
	private static final Logger logger = MiscUtils.getLogger();

	// This is used to start the app from a web container (e.g. tomcat)
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
	{
		logger.info("Starting Juno (JunoApplication.configure())");

		return JunoApplication.initSpring(application);
	}

	// This is used to start the app from Intellij or when running the war file directly
	public static void main(String[] args) throws IOException
	{
		if(args.length != 0)
		{
			logger.info("Starting Juno (JunoApplication.main()) in headless mode");

			new OscarPropertiesInitializerHeadless().initialize();
			new SpringApplicationBuilder(JunoApplication.class)
					.web(WebApplicationType.NONE)
					.sources(JunoApplication.class)
					.run(args);
		}
		else
		{
			logger.info("Starting Juno (JunoApplication.main())");
			JunoApplication.initSpring(new SpringApplicationBuilder(JunoApplication.class).web(WebApplicationType.SERVLET)).run(args);
		}
	}

	private static SpringApplicationBuilder initSpring(SpringApplicationBuilder application)
	{
		return application
			.sources(JunoApplication.class)
			.initializers(new OscarPropertiesInitializerWeb());
	}
}
