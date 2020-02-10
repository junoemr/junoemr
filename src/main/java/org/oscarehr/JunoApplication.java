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

import org.oscarehr.util.ContextStartupListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import oscar.login.Startup;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@SpringBootApplication
@ComponentScan(basePackages = {"oscar", "org.oscarehr", "com.quatro"})
@ServletComponentScan(basePackages = {"com.junoemr", "org.oscarehr"})
@ImportResource({"classpath*:applicationContext.xml"})
public class JunoApplication extends SpringBootServletInitializer
{
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
	{
		return application.sources(JunoApplication.class);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException
	{
		super.onStartup(servletContext);
		servletContext.addListener(new Startup());
		servletContext.addListener(new ContextStartupListener());
	}

	public static void main(String[] args)
	{
		SpringApplication.run(JunoApplication.class, args);
	}

}
