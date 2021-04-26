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

import org.apache.tomcat.util.descriptor.web.ServletDef;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.descriptor.web.WebXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xml.sax.InputSource;

import javax.servlet.ServletRegistration;
import java.io.InputStream;
import java.util.Map;

@Configuration
public class PreCompileJspRegistry
{
    private static final int DEFAULT_JSP_SERVLET_PRIORITY = 99;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public ServletContextInitializer registerPreCompiledJsps()
    {
        return servletContext -> {
            InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/precompiled-jsp-web.xml");
            if (inputStream == null)
            {
                logger.info("Could not read web.xml");
                return;
            }
            try
            {
                WebXmlParser parser = new WebXmlParser(false, false, true);
                WebXml webXml = new WebXml();
                boolean success = parser.parseWebXml(new InputSource(inputStream), webXml, false);
                if (!success)
                {
                    logger.error("Error registering precompiled JSPs");
                }

                logger.info("Registering {} precompiled JSPs", webXml.getServlets().size());
                for (ServletDef def : webXml.getServlets().values())
                {
                    ServletRegistration.Dynamic reg = servletContext.addServlet(def.getServletName(), def.getServletClass());
                    reg.setLoadOnStartup(DEFAULT_JSP_SERVLET_PRIORITY);
                }

                logger.info("Mapping {} JSPs as servlets", webXml.getServletMappings().size());
                for (Map.Entry<String, String> mapping : webXml.getServletMappings().entrySet())
                {
                    servletContext.getServletRegistration(mapping.getValue()).addMapping(mapping.getKey());
                }
            }
            catch (Exception e)
            {
                logger.error("Error registering precompiled JSPs", e);
            }
        };
    }
}