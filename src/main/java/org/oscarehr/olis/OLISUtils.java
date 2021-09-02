package org.oscarehr.olis;
/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

import ca.ssha._2005.hial.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.olis.exception.OLISUnknownFacilityException;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.OscarAuditLogger;
import org.oscarehr.util.SpringUtils;
import org.xml.sax.InputSource;
import oscar.OscarProperties;
import oscar.oscarLab.ca.all.parsers.OLISHL7Handler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;


public class OLISUtils
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final Hl7TextInfoDao hl7TextInfoDao = SpringUtils.getBean(Hl7TextInfoDao.class);

	private static final String PROVINCIAL_LAB_ON = "2.16.840.1.113883.3.59.1";

	private static final String CMLIndentifier = "5047";// Canadian Medical Laboratories
	private static final String GammaDyancareIndentifier = "5552";// Gamma Dynacare
	private static final String LifeLabsIndentifier = "5687";// LifeLabs
	private static final String AlphaLabsIndetifier = "5254";// Alpha Laboratories"

	private static final String OlisTestingLab5 = "4005";// OLIS conformance testing lab
	private static final String OlisTestingLab6 = "4006";// OLIS conformance testing lab
	private static final String OlisTestingLab7 = "4007";// OLIS conformance testing lab
	private static final String OlisTestingLab8 = "4008";// OLIS conformance testing lab

	public static String getOLISResponseContent(String response) throws Exception{
		response = response.replaceAll("<Content", "<Content xmlns=\"\" ");
		response = response.replaceAll("<Errors", "<Errors xmlns=\"\" ");
		
		DocumentBuilderFactory.newInstance().newDocumentBuilder();
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		
		InputStream is = OLISPoller.class.getResourceAsStream("/org/oscarehr/olis/response.xsd");
		
		Source schemaFile = new StreamSource(is);
	
		if(OscarProperties.getInstance().getProperty("olis_response_schema") != null){
			schemaFile = new StreamSource(new File(OscarProperties.getInstance().getProperty("olis_response_schema")));
		}
		
		factory.newSchema(schemaFile);

		JAXBContext jc = JAXBContext.newInstance("ca.ssha._2005.hial");
		Unmarshaller u = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		Response root = ((JAXBElement<Response>) u.unmarshal(new InputSource(new StringReader(response)))).getValue();
		
		return root.getContent();
	}

	public static boolean isDuplicate(LoggedInInfo loggedInInfo, OLISHL7Handler h, String msg)
	{
		String sendingFacility = h.getPlacerGroupNumber();
		logger.debug("SENDING FACILITY: " + sendingFacility);
		String accessionNumber = h.getAccessionNum();
		String hin = h.getHealthNum();

		return isDuplicate(loggedInInfo, sendingFacility, accessionNumber, msg, hin);
	}

	public static boolean isDuplicate(LoggedInInfo loggedInInfo, String sendingFacility, String accessionNumber, String msg, String hin)
	{
		logger.debug("Facility " + sendingFacility + " Accession # " + accessionNumber);

		if(StringUtils.isNotBlank(sendingFacility))
		{
			String provincialLab = sendingFacility.split(":")[0];
			String labIdentifier = sendingFacility.split(":")[1];

			if(!PROVINCIAL_LAB_ON.equals(provincialLab))
			{
				throw new OLISUnknownFacilityException(provincialLab + " is not the Ontario laboratory");
			}

			if(CMLIndentifier.equals(labIdentifier))
			{
				String accessionNoPt1 = accessionNumber.split("-")[0];
				List<Hl7TextInfo> dupResults = hl7TextInfoDao.searchByAccessionNumber(accessionNoPt1);
				for(Hl7TextInfo dupResult : dupResults)
				{
					String dupResultAccessionNum = dupResult.getAccessionNumber();
					if(dupResultAccessionNum.contains("-"))
					{
						dupResultAccessionNum = dupResultAccessionNum.split("-")[0];
					}

					//direct
					if(dupResultAccessionNum.equals(accessionNoPt1))
					{
						if(hin.equals(dupResult.getHealthNumber()))
						{
							OscarAuditLogger.getInstance().log(loggedInInfo, "Lab", "Skip", "Duplicate CML lab skipped - accession " + accessionNumber + "\n" + msg);
							return true;
						}
					}
				}
			}
			else if(LifeLabsIndentifier.equals(labIdentifier))
			{
				List<Hl7TextInfo> dupResults = hl7TextInfoDao.searchByAccessionNumber(accessionNumber.substring(5));
				for(Hl7TextInfo dupResult : dupResults)
				{
					logger.debug("LIFELABS " + dupResult.getAccessionNumber() + " " + accessionNumber + " == " + dupResult.getAccessionNumber().equals(accessionNumber.substring(5)));

					if(dupResult.getAccessionNumber().equals(accessionNumber.substring(5)))
					{
						if(hin.equals(dupResult.getHealthNumber()))
						{
							OscarAuditLogger.getInstance().log(loggedInInfo, "Lab", "Skip", "Duplicate LifeLabs lab skipped - accession " + accessionNumber + "\n" + msg);
							return true;
						}
					}
				}


			}
			else if(GammaDyancareIndentifier.equals(labIdentifier))
			{
				String directAcc = accessionNumber.substring(4);
				directAcc = directAcc.substring(0, 2) + "-" + Integer.parseInt(directAcc.substring(2));
				List<Hl7TextInfo> dupResults = hl7TextInfoDao.searchByAccessionNumber(directAcc);

				for(Hl7TextInfo dupResult : dupResults)
				{
					logger.debug(dupResult.getAccessionNumber() + " == " + directAcc + " " + dupResult.getAccessionNumber().equals(directAcc));

					if(dupResult.getAccessionNumber().equals(directAcc))
					{
						if(hin.equals(dupResult.getHealthNumber()))
						{
							OscarAuditLogger.getInstance().log(loggedInInfo, "Lab", "Skip", "Duplicate GAMMA lab skipped - accession " + accessionNumber + "\n" + msg);
							return true;
						}
					}
				}
			}
			else if(AlphaLabsIndetifier.equals(labIdentifier))
			{
				List<Hl7TextInfo> dupResults = hl7TextInfoDao.searchByAccessionNumber(accessionNumber.substring(5));
				for(Hl7TextInfo dupResult : dupResults)
				{
					logger.debug("AlphaLabs " + dupResult.getAccessionNumber() + " " + accessionNumber + " == " + dupResult.getAccessionNumber().equals(accessionNumber.substring(5)));

					if(dupResult.getAccessionNumber().equals(accessionNumber.substring(5)))
					{
						if(hin.equals(dupResult.getHealthNumber()))
						{
							OscarAuditLogger.getInstance().log(loggedInInfo, "Lab", "Skip", "Duplicate AlphaLabs lab skipped - accession " + accessionNumber + "\n" + msg);
							return true;
						}
					}
				}
			}
			else if(OlisTestingLab5.equals(labIdentifier)
					|| OlisTestingLab6.equals(labIdentifier)
					|| OlisTestingLab7.equals(labIdentifier)
					|| OlisTestingLab8.equals(labIdentifier))
			{
				List<Hl7TextInfo> dupResults = hl7TextInfoDao.searchByAccessionNumber(accessionNumber, OLISHL7Handler.OLIS_MESSAGE_TYPE);
				return !dupResults.isEmpty();
			}
			else
			{
				throw new OLISUnknownFacilityException("Unknown OLIS Lab facility identifier", labIdentifier);
			}
		}
		else
		{
			throw new OLISUnknownFacilityException("missing facility data");
		}
		return false;
	}
}
