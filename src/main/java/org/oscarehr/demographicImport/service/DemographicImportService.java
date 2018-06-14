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
import ca.uhn.hl7v2.parser.CustomModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.ProviderDataDao;
import org.oscarehr.common.hl7.copd.mapper.DemographicMapper;
import org.oscarehr.common.hl7.copd.mapper.ProviderMapper;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.ProviderData;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicCust;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.provider.service.ProviderService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class DemographicImportService
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final String IMPORT_PROVIDER = "999900"; //TODO dont use this system forever

	@Autowired
	DemographicService demographicService;

	@Autowired
	DemographicDao demographicDao;

	@Autowired
	ProviderService providerService;

	@Autowired
	ProviderDataDao providerDataDao;

	public void importDemographicDataCOPD(GenericFile genericFile) throws IOException, HL7Exception
	{
		logger.info("Read import file");
		File file = genericFile.getFileObject();
		InputStream is = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String messageStrTmp = "";

		String line;
		while ((line = br.readLine()) != null) {
			messageStrTmp += line;
		}
		//TODO fix namespace, hl7 version

		logger.info("Split hl7 messages");
		List<String> messageList = seperateMessages(messageStrTmp);

		logger.info("Initialize HL7 parser");
		HapiContext context = new DefaultHapiContext();
//		context.setValidationContext(new NoValidation());

		// this package string needs to match the custom model location in the oscar source code.
		ModelClassFactory modelClassFactory = new CustomModelClassFactory("org.oscarehr.common.hl7.copd.model");
		context.setModelClassFactory(modelClassFactory);

		Parser p = context.getXMLParser();
//		p.getParserConfiguration().setAllowUnknownVersions(true);

		logger.info("Parse Messages");
		int counter = 1;
		for(String messageStr : messageList)
		{
			logger.info("Parse message " + counter + " of " + messageList.size());

			messageStr = preProcessMessage(messageStr);
			ZPD_ZTR zpdZtrMessage = (ZPD_ZTR) p.parse(messageStr);

			importRecordData(zpdZtrMessage);
			counter++;
		}
	}

	private void importRecordData(ZPD_ZTR zpdZtrMessage) throws HL7Exception
	{
		logger.info("Creating Demographic Record ...");
		Demographic demographic = importDemographicData(zpdZtrMessage);
		logger.info("Created record " + demographic.getDemographicId() + " for patient: " + demographic.getLastName() + ", " + demographic.getFirstName());

		logger.info("Find/Create Provider Record(s) ...");
		ProviderData provider = importProviderData(zpdZtrMessage, demographic);

		// set the mrp doctor after all the provider records are created
		demographic.setProviderNo(provider.getId());
		demographicDao.merge(demographic);
	}

	private List<String> seperateMessages(String messageStr)
	{
		List<String> messageList = new LinkedList<>();

		//TODO split xml into messages
		messageList.add(messageStr);

		return messageList;
	}

	/**
	 * Preprocess the message string. This is for fixing hl7 messages that do not conform to the specs
	 * @param message - the message string (xml)
	 * @return - the fixed message string
	 */
	private String preProcessMessage(String message)
	{
		Pattern patt = Pattern.compile("<PRD>(.*?)<\\/PRD>", Pattern.DOTALL);
		Matcher m = patt.matcher(message);
		StringBuffer sb = new StringBuffer(message.length());
		while(m.find())
		{
			// for each PRD segment in the message, fix the PRD numbers
			String replacement = "<PRD>" + fixPRDSegmentNumbers(m.group(1)) + "</PRD>";
			m.appendReplacement(sb, replacement);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * PRD.1 is required, but the spec has an off by one error, putting it at PRD.2, and all subsequent segments are off by 1.
	 * If this is the case, find them all and decrement them by 1 to match the regular hl7 standard
	 */
	private String fixPRDSegmentNumbers(String xmlPRD)
	{
		if(!xmlPRD.contains("<PRD.1>"))
		{
			Pattern patt = Pattern.compile("<(\\/?PRD)\\.([0-9]+)>");
			Matcher m = patt.matcher(xmlPRD);
			StringBuffer sb = new StringBuffer(xmlPRD.length());
			while(m.find())
			{
				String segmentNumStr = m.group(2);
				Integer segmentNumber = Integer.parseInt(segmentNumStr);
				String replacement = "<" + m.group(1) + "." + String.valueOf(segmentNumber - 1) + ">";

				m.appendReplacement(sb, replacement);
				logger.info("Replace:" + m.group(0) + " -> " + replacement);
			}
			m.appendTail(sb);
			xmlPRD = sb.toString();
		}
		return xmlPRD;
	}

	/**
	 * imports provider data for each provider group in the message
	 * @param zpdZtrMessage the hl7 message to parse
	 * @param demographic the new demographic record
	 * @return the MRP doctor record. This should never be null, as all messages are required to have at least one provider record
	 * @throws HL7Exception
	 */
	private ProviderData importProviderData(ZPD_ZTR zpdZtrMessage, Demographic demographic) throws HL7Exception
	{
		ProviderData mrpProvider = null;
		ProviderMapper providerMapper = new ProviderMapper(zpdZtrMessage);

		int numProviders = providerMapper.getNumProviders();
		logger.info("Found " + numProviders + " provider groups");
		if(numProviders < 1)
		{
			throw new RuntimeException("No provider information found");
		}

		for(int i=0; i< numProviders; i++)
		{
			String providerFirstName = providerMapper.getFirstName(i);
			String providerLastName = providerMapper.getLastName(i);

			logger.info("First Name:" + providerFirstName);
			logger.info("Last Name:" + providerLastName);
			if(providerFirstName == null || providerLastName == null)
			{
				throw new RuntimeException("Not enough provider info found to link or create provider record (first and last name are required).");
			}

			mrpProvider = findOrCreateProviderRecord(providerMapper.getProvider(i), providerFirstName, providerLastName);
		}


		return mrpProvider;
	}

	private ProviderData findOrCreateProviderRecord(ProviderData newProvider, String providerFirstName, String providerLastName)
	{
		ProviderData provider;
		List<ProviderData> matchedProviders = providerDataDao.findByName(providerFirstName, providerLastName, false);
		if(matchedProviders.isEmpty())
		{
			provider = newProvider;
			// providers don't have auto-generated id's, so we have to pick one
			Integer newProviderId = providerService.getNextProviderNumberInSequence(9999, 900000);
			newProviderId = (newProviderId == null) ? 10000 : newProviderId;
			provider.set(String.valueOf(newProviderId));

			provider = providerService.addNewProvider(IMPORT_PROVIDER, provider);
			logger.info("Created new Provider record " + provider.getId() + " (" + provider.getLastName() + "," + provider.getFirstName() + ")");
		}
		else if(matchedProviders.size() == 1)
		{
			provider = matchedProviders.get(0);
			logger.info("Use existing Provider record " + provider.getId() + " (" + provider.getLastName() + "," + provider.getFirstName() + ")");
		}
		else
		{
			throw new RuntimeException("Multiple providers exist in the system with the same name (" + providerLastName + "," + providerFirstName + ").");
		}
		return provider;
	}

	private Demographic importDemographicData(ZPD_ZTR zpdZtrMessage) throws HL7Exception
	{
		DemographicMapper demographicMapper = new DemographicMapper(zpdZtrMessage);
		Demographic demographic = demographicMapper.getDemographic();
		DemographicCust demographicCust = demographicMapper.getDemographicCust();
		List<DemographicExt> demographicExtList = demographicMapper.getDemographicExtensions();

		demographicService.addNewDemographicRecord(IMPORT_PROVIDER, demographic, demographicCust, demographicExtList);
		return demographic;
	}
}
