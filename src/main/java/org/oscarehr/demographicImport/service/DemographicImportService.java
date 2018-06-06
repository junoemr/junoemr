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
package org.oscarehr.demographicImport.service;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.CustomModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.demographicImport.copd.COPDHandler;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
@Transactional
public class DemographicImportService
{
	private static final Logger logger = MiscUtils.getLogger();

	public void importDemographicData(GenericFile genericFile) throws HL7Exception, IOException
	{
		File file = genericFile.getFileObject();
		InputStream is = new FileInputStream(file);
//			is = new BufferedInputStream(is);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String messageStr = "";

		String line;
		while ((line = br.readLine()) != null) {
			messageStr += line;
		}

//			Hl7InputStreamMessageIterator iter = new Hl7InputStreamMessageIterator(is);
//
//			while (iter.hasNext())
//			{
//				Message next = iter.next();
//				logger.info(next.encode());
//			}

//			Hl7InputStreamMessageStringIterator iter2 = new Hl7InputStreamMessageStringIterator(is);
//			while (iter2.hasNext())
//			{
//				String next = iter2.next();
//				logger.info(next);
//			}

		//TODO
		// parse xml
		// break into strings based on MSH segments
		// each segment is uploaded through service as demographic
		// service will parse each string as hl7 v2.4
		// read values and either save or error

		HapiContext context = new DefaultHapiContext();
//		context.setValidationContext(new NoValidation());
		ModelClassFactory modelClassFactory = new CustomModelClassFactory("org.oscarehr.demographicImport.copd.hl7");
		context.setModelClassFactory(modelClassFactory);

		Parser p = context.getXMLParser();
//		p.getParserConfiguration().setAllowUnknownVersions(true);

		Message msg = p.parse(messageStr);

		if(COPDHandler.isCOPDFormat(msg))
		{
			COPDHandler parser = new COPDHandler(msg);
		}
	}
}
