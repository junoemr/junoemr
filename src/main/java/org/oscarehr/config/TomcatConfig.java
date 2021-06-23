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

package org.oscarehr.config;

import de.javakaffee.web.msm.MemcachedBackupSessionManager;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.webresources.ExtractingRoot;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationPropertiesScan("org.oscarehr.config")
public class TomcatConfig
{
	private final oscar.OscarProperties oscarProperties = oscar.OscarProperties.getInstance();

	private static final String QUERY_STRING_CHARACTERS_TO_ALLOW = "[]";

	private final JunoProperties junoProperties;

	public TomcatConfig(JunoProperties junoProperties)
	{
		this.junoProperties = junoProperties;
	}

	// TODO: SPRINGUPGRADE: Set the context path from the properties files.  This might not be a
	//                      thing we need to do.
	@Bean
	public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer()
	{
		String contextPath = oscarProperties.getProjectHome();
		return factory -> factory.setContextPath("/" + contextPath);
	}

	@Bean
	public TomcatServletWebServerFactory tomcatServletWebServerFactory()
	{
		return new TomcatServletWebServerFactory()
		{
			@Override
			protected void postProcessContext(Context context)
			{
				super.postProcessContext(context);

				// This turns off the manifest jar scanner to get rid of exceptions during boot, as per
				// https://stackoverflow.com/a/52229296
                // XXX: Potentially need to remove this line for integration tests
				((StandardJarScanner) context.getJarScanner()).setScanManifest(false);

				// Add filetypes to compile as jsp files.  Formerly configured in web.xml
				context.addServletMappingDecoded("*.jsp", "jsp");
				context.addServletMappingDecoded("*.jspf", "jsp");
				context.addServletMappingDecoded("*.json", "jsp");

				context.setResources(new ExtractingRoot());

				// Set up redis session management
				if(junoProperties.getRedisSessionStore().isEnabled())
				{
					String redisConnectionString = "redis://";
					if (junoProperties.getRedisSessionStore().getPassword() != null)
					{
						redisConnectionString +=
							"default:" + junoProperties.getRedisSessionStore().getPassword() + "@";
					}
					redisConnectionString += junoProperties.getRedisSessionStore().getEndpoint();

					MemcachedBackupSessionManager manager = new MemcachedBackupSessionManager();
					manager.setMemcachedNodes(redisConnectionString);
					manager.setSticky(true);
					manager.setSessionBackupAsync(true);
					manager.setLockingMode("none");
					manager.setStorageKeyPrefix("");

					context.setManager(manager);
				}
			}
		};
	}

	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainerCustomizer()
	{
		return new WebServerFactoryCustomizer<TomcatServletWebServerFactory>()
		{
			@Override
			public void customize(TomcatServletWebServerFactory container)
			{
				container.addContextCustomizers(
						new TomcatContextCustomizer() {
							@Override
							public void customize(Context context) {
								context.setReloadable(false);
							}
						});

				container.addConnectorCustomizers(new TomcatConnectorCustomizer()
				{
					@Override
					public void customize(Connector connector)
					{
						connector.setAttribute("relaxedQueryChars", QUERY_STRING_CHARACTERS_TO_ALLOW);
					}
				});
			}
		};
	}
}
