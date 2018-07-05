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

import ca.uhn.hl7v2.HL7Exception;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.XMLFile;
import org.oscarehr.demographicImport.service.CoPDImportService;
import org.oscarehr.demographicImport.service.CoPDPreProcessorService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import oscar.OscarProperties;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommandLineRunner
{
	private static final Logger logger = Logger.getLogger(CommandLineRunner.class);

	private static CoPDImportService coPDImportService;
	private static CoPDPreProcessorService coPDPreProcessorService;

	/**
	 * Run this in the WEB-INF folder in one of 2 ways:
	 *
	 * deployed: in ~tomcat/webapps/context_path/WEB-INF
	 * un-deployed: in [code_source]/target/oscar-14.0.0-SNAPSHOT/WEB-INF
	 * java -cp "classes/:lib/*:/usr/java/apache-tomcat/lib/*" org.oscarehr.util.CommandLineRunner [props_file] [topd_files_directory] [topd_documents_directory]
	 *
	 * @param args
	 */
	public static void main (String [] args)
	{
		logger.setLevel(Level.INFO);
		// Set up a simple configuration that logs on the console.
		BasicConfigurator.configure();

		if(args == null || args.length != 3)
		{
			logger.error("Invalid Argument Count");
			return;
		}

		ClassPathXmlApplicationContext ctx = null;
		try
		{
			String propertiesFileName = args[0];
			String copdFileLocation = args[1];
			String copdDocumentLocation = args[2];

			// load properties from file
			OscarProperties properties = OscarProperties.getInstance();
			// This has been used to look in the users home directory that started tomcat
			properties.readFromFile(propertiesFileName);
			logger.info("loading properties from " + propertiesFileName);

			ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			// initialize spring bean factory for old style access
			SpringUtils.beanFactory = ctx.getBeanFactory();

			coPDImportService = ctx.getBean(CoPDImportService.class);
			coPDPreProcessorService = ctx.getBean(CoPDPreProcessorService.class);

			// -------------------------------------------------------------------
			logger.info("BEGIN DEMOGRAPHIC IMPORT PROCESS ...");

			File copdDirectory = new File(copdFileLocation);
			File[] fileList = copdDirectory.listFiles();

			for(File file : fileList)
			{
				GenericFile copdFile = FileFactory.getExistingFile(file);
				if(copdFile instanceof XMLFile)
				{
					String fileString = coPDPreProcessorService.getFileString(copdFile);
					if(coPDPreProcessorService.looksLikeCoPDFormat(fileString))
					{
						logger.info("Import from file: " + copdFile.getName());

						try
						{
							importFileString(fileString, copdDocumentLocation);
						}
						catch(Exception e)
						{
							logger.error("Failed to import " + copdFile.getName(), e);
							e.printStackTrace();
						}
					}
					else
					{
						logger.warn(copdFile.getName() + " does not look like a valid import xml file.");
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error("CommandLineRunner Error", e);
		}
		finally
		{
			if(ctx != null)
			{
				ctx.close();
			}
		}
		logger.info("IMPORT PROCESS COMPLETE");
	}

	private static void importFileString(String fileString, String documentDirectory) throws HL7Exception, IOException, InterruptedException
	{
		List<String> messageList = coPDPreProcessorService.separateMessages(fileString);
		for(String message : messageList)
		{
			message = coPDPreProcessorService.preProcessMessage(message);
			coPDImportService.importFromHl7Message(message, documentDirectory);
		}
	}
}