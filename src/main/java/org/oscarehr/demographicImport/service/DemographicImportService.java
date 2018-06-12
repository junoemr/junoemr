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

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class DemographicImportService
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final String IMPORT_PROVIDER = "999900"; //TODO dont use this system forever

	@Autowired
	DemographicService demographicService;

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
			ZPD_ZTR zpdZtrMessage = (ZPD_ZTR) p.parse(messageStr);

			logger.info("Find/Create Provider Record ...");
			ProviderData provider = importProviderData(zpdZtrMessage);

			logger.info("Creating Demographic Record ...");
			Demographic demographic = importDemographicData(zpdZtrMessage, provider);

			counter++;
		}
	}
	private List<String> seperateMessages(String messageStr)
	{
		List<String> messageList = new LinkedList<>();

		//TODO split xml into messages
		messageList.add(messageStr);

		return messageList;
	}

	private ProviderData importProviderData(ZPD_ZTR zpdZtrMessage) throws HL7Exception
	{
		ProviderData provider = null;
		ProviderMapper providerMapper = new ProviderMapper(zpdZtrMessage);
		if(providerMapper.hasProviderInfo())
		{
			String providerFirstName = providerMapper.getFirstName(0);
			String providerLastName = providerMapper.getLastName(0);

			if(providerFirstName == null || providerLastName == null)
			{
				throw new RuntimeException("Not enough provider info found to link or create provider record (first and last name are required).");
			}

			List<ProviderData> matchedProviders = providerDataDao.findByName(providerFirstName, providerLastName, false);
			if(matchedProviders.isEmpty())
			{
				provider = providerMapper.getProvider();
				// providers don't have auto-generated id's, so we have to pick one
				String newProviderId = providerService.getNextProviderNumberInSequence(10000, 900000);
				newProviderId = (newProviderId == null) ? "10000" : newProviderId;
				provider.set(newProviderId);

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
		}
		else
		{
			logger.info("No Provider info found");
		}
		return provider;
	}

	private Demographic importDemographicData(ZPD_ZTR zpdZtrMessage, ProviderData provider) throws HL7Exception
	{
		DemographicMapper demographicMapper = new DemographicMapper(zpdZtrMessage);
		Demographic demographic = demographicMapper.getDemographic();
		DemographicCust demographicCust = demographicMapper.getDemographicCust();
		List<DemographicExt> demographicExtList = demographicMapper.getDemographicExtensions();

		// assign the demographic to a provider if possible
		if(provider != null)
		{
			demographic.setProviderNo(provider.getId());
		}

		demographicService.addNewDemographicRecord(IMPORT_PROVIDER, demographic, demographicCust, demographicExtList);
		return demographic;
	}
}
