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
package org.oscarehr.util;

import org.apache.log4j.Logger;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.demographicImport.service.CoPDImportService;
import org.oscarehr.demographicImport.service.CoPDPreProcessorService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import oscar.OscarProperties;

import java.util.List;

public class CommandLineRunner
{
	private static final Logger logger = Logger.getLogger(CommandLineRunner.class);

	public static void main (String [] args)
	{
		if(args == null || args.length < 2)
		{
			logger.error("arguments required");
			System.out.println("arguments required");
			return;
		}
		try
		{
			String propertiesFileName = args[0];
			String copdFileName = args[1];

			// load properties from file
			OscarProperties properties = OscarProperties.getInstance();
			// This has been used to look in the users home directory that started tomcat
			properties.readFromFile(propertiesFileName);
			logger.info("loading properties from " + propertiesFileName);
			System.out.println("loading properties from " + propertiesFileName);

			ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			// initialize spring bean factory for old style access
			SpringUtils.beanFactory = ((ClassPathXmlApplicationContext) ctx).getBeanFactory();

			CoPDImportService coPDImportService = ctx.getBean(CoPDImportService.class);
			CoPDPreProcessorService coPDPreProcessorService = ctx.getBean(CoPDPreProcessorService.class);

			// -------------------------------------------------------------------
			logger.info("BEGIN DEMOGRAPHIC IMPORT PROCESS ...");
			System.out.println("BEGIN DEMOGRAPHIC IMPORT PROCESS ...");

			GenericFile tempFile = FileFactory.getExistingFile(copdFileName);

			List<String> messageList = coPDPreProcessorService.readMessagesFromFile(tempFile);
			for(String message : messageList)
			{
				message = coPDPreProcessorService.preProcessMessage(message);
				coPDImportService.importFromHl7Message(message);
			}
		}
		catch(Exception e)
		{
			logger.error("Import Error", e);
			System.out.println("Import Error");
			e.printStackTrace();
		}
		System.out.println("Import Process Complete");
	}
}