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
package org.oscarehr.maven;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OpenApiSpecBuilder
{
	private static final String FILENAME = "openapi.json";

	public static void main(String[] args)
	{
		// Disable debug logging for this because it's very verbose and I couldn't figure out how
		// to set log levels in the maven-exec-plugin settings.
		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.INFO);

		String resourceClassCsv = System.getProperty("openApiSpecBuilder.resourceClassCsv");
		String outputDirectory = System.getProperty("openApiSpecBuilder.outputDirectory");
		String filename = outputDirectory + "/" + FILENAME;

		SwaggerConfiguration oasConfig = new SwaggerConfiguration().resourceClasses(
				(Stream.of(resourceClassCsv.split("\n")))
				.map(str -> str.replaceAll("\\s+", ""))
				.collect(Collectors.toSet()));
		try
		{
			OpenAPI openAPI = new JaxrsOpenApiContextBuilder()
				.openApiConfiguration(oasConfig)
				.buildContext(true)
				.read();

			String json = Json.mapper().writeValueAsString(openAPI);


			File directory = new File(outputDirectory);

			if(!directory.exists())
			{
				directory.mkdir();
			}

			try(PrintWriter out = new PrintWriter(filename))
			{
				out.println(json);
			}

		}
		catch(OpenApiConfigurationException | IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
