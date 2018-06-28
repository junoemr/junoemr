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

import java.util.List;

public class CommandLineRunner
{
	private static final Logger logger = Logger.getLogger(CommandLineRunner.class);

	public static void main (String [] args)
	{
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

		logger.info("BEGIN DEMOGRAPHIC IMPORT PROCESS ...");

		CoPDImportService coPDImportService = ctx.getBean(CoPDImportService.class);
		CoPDPreProcessorService coPDPreProcessorService = ctx.getBean(CoPDPreProcessorService.class);

		GenericFile tempFile = null;

		if(args[0] == null)
		{
			logger.error("parameter 0 should provide import file");
			return;
		}

		try
		{
			tempFile = FileFactory.getExistingFile(args[0], args[1]);

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
		}
	}
}