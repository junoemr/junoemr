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
import org.oscarehr.config.JunoProperties;
import org.oscarehr.olis.exception.OLISUnknownFacilityException;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.OscarAuditLogger;
import org.oscarehr.util.SpringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import oscar.oscarLab.ca.all.parsers.AlphaHandler;
import oscar.oscarLab.ca.all.parsers.CMLHandler;
import oscar.oscarLab.ca.all.parsers.GDMLHandler;
import oscar.oscarLab.ca.all.parsers.OLIS.OLISHL7Handler;
import oscar.oscarLab.ca.all.parsers.PATHL7Handler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;


public class OLISUtils
{
	public static final String PROVINCIAL_LAB_ON = "2.16.840.1.113883.3.59.1";

	private static final Logger logger = MiscUtils.getLogger();
	private static final Hl7TextInfoDao hl7TextInfoDao = SpringUtils.getBean(Hl7TextInfoDao.class);
	private static final JunoProperties junoProperties = SpringUtils.getBean(JunoProperties.class);

	private static final String CMLIndentifier = "5047";// Canadian Medical Laboratories
	private static final String GammaDyancareIndentifier = "5552";// Gamma Dynacare
	private static final String LifeLabsIndentifier = "5687";// LifeLabs
	private static final String AlphaLabsIndetifier = "5254";// Alpha Laboratories"

	public static Response getOLISResponse(String response) throws ParserConfigurationException, JAXBException, SAXException
	{
		response = response.replaceAll("<Content", "<Content xmlns=\"\" ");
		response = response.replaceAll("<Errors", "<Errors xmlns=\"\" ");

		DocumentBuilderFactory.newInstance().newDocumentBuilder();
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

		Source schemaFile = new StreamSource(new File(junoProperties.getOlis().getResponseSchema()));
		factory.newSchema(schemaFile);

		JAXBContext jc = JAXBContext.newInstance("ca.ssha._2005.hial");
		Unmarshaller u = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		Response root = ((JAXBElement<Response>) u.unmarshal(new InputSource(new StringReader(response)))).getValue();
		return root;
	}

	public static String getOLISResponseContent(String response) throws ParserConfigurationException, JAXBException, SAXException
	{
		return getOLISResponse(response).getContent();
	}

	public static boolean isDuplicate(LoggedInInfo loggedInInfo, OLISHL7Handler h, String msg)
	{
		String sendingFacility = h.getPlacerGroupNumber();
		logger.debug("SENDING FACILITY: " + sendingFacility);
		String uniqueIdentifier = h.getUniqueIdentifier();
		String uniqueVersionIdentifier = h.getUniqueVersionIdentifier();
		String hin = h.getHealthNum();
		String collectionDate = h.getCollectionDateTime(0);
		collectionDate = (StringUtils.isNotBlank(collectionDate)) ? collectionDate.substring(0, 10).replaceAll("-", "") : null;

		return isDuplicate(loggedInInfo, sendingFacility, uniqueIdentifier, uniqueVersionIdentifier, msg, hin, collectionDate);
	}

	public static boolean isDuplicate(LoggedInInfo loggedInInfo, String sendingFacility, String accessionNumber, String fillerOrderNo, String msg, String hin, String olisCollectionDate)
	{
		logger.debug("Facility " + sendingFacility + " Accession # " + accessionNumber);

		if(StringUtils.isNotBlank(sendingFacility))
		{
			String provincialLab = sendingFacility.split(":")[0];
			String labIdentifier = sendingFacility.split(":")[1];

			if(CMLIndentifier.equals(labIdentifier))
			{
				String accessionNoPt1 = accessionNumber.split("-")[0];

				List<Hl7TextInfo> dupResults = hl7TextInfoDao.searchByAccessionNumber(accessionNumber, OLISHL7Handler.OLIS_MESSAGE_TYPE);
				dupResults.addAll(hl7TextInfoDao.searchByAccessionNumber(accessionNoPt1, CMLHandler.CML_MESSAGE_TYPE));

				for(Hl7TextInfo dupResult : dupResults)
				{
					if(hin.equals(dupResult.getHealthNumber()))
					{
						String collectionDate = dupResult.getObrDate().substring(0,10).replaceAll("-", "");
						if(!StringUtils.isEmpty(collectionDate) && olisCollectionDate.equals(collectionDate))
						{
							OscarAuditLogger.getInstance().log(loggedInInfo, "Lab", "Skip", "Duplicate CML lab skipped - accession " + accessionNumber + "\n" + msg);
							return true;
						}
					}
				}
			}
			else if(LifeLabsIndentifier.equals(labIdentifier))
			{
				List<Hl7TextInfo> dupResults = hl7TextInfoDao.searchByAccessionNumber(accessionNumber, OLISHL7Handler.OLIS_MESSAGE_TYPE);
				dupResults.addAll(hl7TextInfoDao.searchByAccessionNumber(accessionNumber.substring(5), PATHL7Handler.LIFELABS_MESSAGE_TYPE));

				for(Hl7TextInfo dupResult : dupResults)
				{
					logger.debug("LIFELABS " + dupResult.getUniqueIdentifier() + " " + accessionNumber + " == " + dupResult.getUniqueIdentifier().equals(accessionNumber.substring(5)));

					if(hin.equals(dupResult.getHealthNumber()))
					{
						String collectionDate = dupResult.getObrDate().substring(0,10).replaceAll("-", "");
						if(!StringUtils.isEmpty(collectionDate) && olisCollectionDate.equals(collectionDate))
						{
							OscarAuditLogger.getInstance().log(loggedInInfo, "Lab", "Skip", "Duplicate LifeLabs lab skipped - accession " + accessionNumber + "\n" + msg);
							return true;
						}
					}
				}
			}
			else if(GammaDyancareIndentifier.equals(labIdentifier))
			{
				/*
				 * Expect accession variants (examples):                    (local name reference)
				 * Fixed:
				 *    Dynacare -direct          34567890                    variant d8
				 *    Dynacare -OLIS            20071234567890
				 * Hl7:
				 *    Dynacare -direct          12-34567890                 variant d10
				 *    Dynacare -OLIS            20071234567890
				 *
				 *    Dynacare -direct          AA-456789                   variant d6
				 *    Dynacare -OLIS            2016AA00456789
				 *
				 * so below we will check for an OLIS match, as well as the 3 variants within the GDML labs
				 */
				List<Hl7TextInfo> dupResults = hl7TextInfoDao.searchByAccessionNumber(accessionNumber, OLISHL7Handler.OLIS_MESSAGE_TYPE);
				if(accessionNumber.length() == 14) // hl7 or fixed: Dynacare-OLIS format
				{
					String d8 = accessionNumber.substring(6);
					String d10 = accessionNumber.substring(4, 6) + "-" + accessionNumber.substring(6);
					String d6 = accessionNumber.substring(4, 6) + "-" + accessionNumber.substring(8);

					dupResults.addAll(hl7TextInfoDao.searchByAccessionNumber(d6, GDMLHandler.GDML_MESSAGE_TYPE));
					dupResults.addAll(hl7TextInfoDao.searchByAccessionNumber(d8, GDMLHandler.GDML_MESSAGE_TYPE));
					dupResults.addAll(hl7TextInfoDao.searchByAccessionNumber(d10, GDMLHandler.GDML_MESSAGE_TYPE));

				}
				else // unknown format, do direct accession comparison only
				{
					logger.warn("Unknown GDML accession format from OLIS: " + accessionNumber);
					dupResults.addAll(hl7TextInfoDao.searchByAccessionNumber(accessionNumber, GDMLHandler.GDML_MESSAGE_TYPE));
				}

				for(Hl7TextInfo dupResult : dupResults)
				{
					if(hin.equals(dupResult.getHealthNumber()))
					{
						String collectionDate = dupResult.getObrDate().substring(0,10).replaceAll("-", "");
						if(!StringUtils.isEmpty(collectionDate) && olisCollectionDate.equals(collectionDate))
						{
							OscarAuditLogger.getInstance().log(loggedInInfo, "Lab", "Skip", "Duplicate GAMMA lab skipped - accession " + accessionNumber + "\n" + msg);
							return true;
						}
					}
				}
			}
			else if(AlphaLabsIndetifier.equals(labIdentifier))
			{
				List<Hl7TextInfo> dupResults = hl7TextInfoDao.searchByAccessionNumber(accessionNumber, OLISHL7Handler.OLIS_MESSAGE_TYPE);
				dupResults.addAll(hl7TextInfoDao.searchByAccessionNumber(accessionNumber.substring(5), AlphaHandler.ALPHA_MESSAGE_TYPE));

				for(Hl7TextInfo dupResult : dupResults)
				{
					logger.debug("AlphaLabs " + dupResult.getUniqueIdentifier() + " " + accessionNumber + " == " + dupResult.getUniqueIdentifier().equals(accessionNumber.substring(5)));
					if(hin.equals(dupResult.getHealthNumber()))
					{
						OscarAuditLogger.getInstance().log(loggedInInfo, "Lab", "Skip", "Duplicate AlphaLabs lab skipped - accession " + accessionNumber + "\n" + msg);
						return true;
					}
				}
			}
			else
			{
				// need to check duplicates because sometimes the first lab of a provider query will be the last lab from the previous query
				List<Hl7TextInfo> dupResults = hl7TextInfoDao.searchByAccessionNumber(accessionNumber, OLISHL7Handler.OLIS_MESSAGE_TYPE);

				Optional<Hl7TextInfo> duplicate = dupResults
						.stream()
						.filter((result -> fillerOrderNo.equals(result.getUniqueVersionIdentifier())))
						.findFirst();
				return duplicate.isPresent();
			}
		}
		else
		{
			throw new OLISUnknownFacilityException("missing facility data");
		}
		return false;
	}
}
