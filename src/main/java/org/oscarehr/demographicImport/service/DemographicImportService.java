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
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.hl7.copd.mapper.AppointmentMapper;
import org.oscarehr.common.hl7.copd.mapper.DemographicMapper;
import org.oscarehr.common.hl7.copd.mapper.DocumentMapper;
import org.oscarehr.common.hl7.copd.mapper.MedicationMapper;
import org.oscarehr.common.hl7.copd.mapper.NoteMapper;
import org.oscarehr.common.hl7.copd.mapper.ProviderMapper;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.hl7.copd.parser.CoPDParser;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicCust;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.provider.service.ProviderService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;

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
	private static final OscarProperties properties = OscarProperties.getInstance();

	@Autowired
	DemographicService demographicService;

	@Autowired
	DemographicDao demographicDao;

	@Autowired
	ProviderService providerService;

	@Autowired
	ProviderDataDao providerDataDao;

	@Autowired
	DocumentService documentService;

	@Autowired
	private OscarAppointmentDao appointmentDao;

	public void importDemographicDataCOPD(GenericFile genericFile) throws IOException, HL7Exception
	{
		logger.info("Read import file");
		File file = genericFile.getFileObject();
		InputStream is = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		StringBuffer sb = new StringBuffer();
		String line;
		while((line = br.readLine()) != null)
		{
			sb.append(line);
		}

		logger.info("Split hl7 messages");
		List<String> messageList = separateMessages(sb.toString());

		logger.info("Initialize HL7 parser");
		HapiContext context = new DefaultHapiContext();
		context.getParserConfiguration().setDefaultObx2Type("ST");
//		context.setValidationContext(new NoValidation());

		// this package string needs to match the custom model location in the oscar source code.
		ModelClassFactory modelClassFactory = new CustomModelClassFactory(ZPD_ZTR.ROOT_PACKAGE);
		context.setModelClassFactory(modelClassFactory);

		Parser p = new CoPDParser(context);
//		Parser p = context.getXMLParser();
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

		logger.info("Create Appointments ...");
		importAppointmentData(zpdZtrMessage, demographic, provider);
	}

	/**
	 * TODO  -- make this more efficient for larger files
	 * @param messageStr the whole file as a string
	 * @return - list of message strings
	 */
	private List<String> separateMessages(String messageStr)
	{
		List<String> messageList = new LinkedList<>();

		Pattern messagePattern = Pattern.compile("<ZPD_ZTR\\.MESSAGE>(.*?)<\\/ZPD_ZTR\\.MESSAGE>", Pattern.DOTALL);
		Matcher messagePatternMatcher = messagePattern.matcher(messageStr);
		while(messagePatternMatcher.find())
		{
			// split messages by each MESSAGE group segment in the file
			String message = "<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\">" + messagePatternMatcher.group(1) + "</ZPD_ZTR>";

			Pattern versionPattern = Pattern.compile("<VID\\.1>(.*?)<\\/VID\\.1>");
			Matcher versionPatternMatcher = versionPattern.matcher(message);

			StringBuffer sb = new StringBuffer(message.length());
			while(versionPatternMatcher.find())
			{
				// the hl7 version must be 2.4
				String replacement = "<VID\\.1>2.4</VID\\.1>";
				versionPatternMatcher.appendReplacement(sb, replacement);
			}
			versionPatternMatcher.appendTail(sb);
			message = sb.toString();

			Pattern phonePattern = Pattern.compile("<XTN\\.7>(.*?)<\\/XTN\\.7>");
			Matcher phonePatternMatcher = phonePattern.matcher(message);

			sb = new StringBuffer(message.length());
			while(phonePatternMatcher.find())
			{
				// strip non numeric characters from phone numbers
				String replacement = "<XTN\\.7>" + phonePatternMatcher.group(1).replaceAll("[^\\d.]", "") + "</XTN\\.7>";
				phonePatternMatcher.appendReplacement(sb, replacement);
			}
			phonePatternMatcher.appendTail(sb);
			message = sb.toString();

			messageList.add(message);
		}
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

			if(providerFirstName == null || providerLastName == null)
			{
				throw new RuntimeException("Not enough provider info found to link or create provider record (first and last name are required).");
			}

			//TODO how to determine MRP doctor when there are more than 1
			mrpProvider = findOrCreateProviderRecord(providerMapper.getProvider(i), providerFirstName, providerLastName);

			logger.info("Import Notes & History ...");
			importProviderNotes(zpdZtrMessage, i, mrpProvider, demographic);
			logger.info("Import Medications ...");
			importMedicationData(zpdZtrMessage, i, mrpProvider, demographic);
			logger.info("Import Pediatrics ...");
			importPediatricsData(zpdZtrMessage, i, mrpProvider, demographic);
			logger.info("Import Pregnancy ...");
			importPregnancyData(zpdZtrMessage, i, mrpProvider, demographic);
			logger.info("Import Allergies ...");
			importAllergyData(zpdZtrMessage, i, mrpProvider, demographic);
			logger.info("Import Immunizations ...");
			importImmunizationData(zpdZtrMessage, i, mrpProvider, demographic);
			logger.info("Import Labs ...");
			importLabData(zpdZtrMessage, i, mrpProvider, demographic);
			logger.info("Import Documents ...");
			importDocumentData(zpdZtrMessage, i, mrpProvider, demographic);
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

			String billCenterCode = properties.getProperty("default_bill_center","");
			provider = providerService.addNewProvider(IMPORT_PROVIDER, provider, billCenterCode);
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

	private void importAppointmentData(ZPD_ZTR zpdZtrMessage, Demographic demographic, ProviderData provider) throws HL7Exception
	{
		AppointmentMapper appointmentMapper = new AppointmentMapper(zpdZtrMessage);

		for(Appointment appointment : appointmentMapper.getAppointmentList())
		{
			appointment.setDemographicNo(demographic.getDemographicId());
			appointment.setName(demographic.getLastName() + "," + demographic.getFirstName());
			appointment.setCreator(IMPORT_PROVIDER);
			appointment.setProviderNo(String.valueOf(provider.getProviderNo()));

			if(properties.isPropertyActive("multisites"))
			{
				throw new RuntimeException("Multisite Imports not supported");
			}

			logger.info("Add appointment: " + appointment.getAppointmentDate());
			appointmentDao.persist(appointment);
		}

	}

	private void importMedicationData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic)
	{
		MedicationMapper medicationMapper = new MedicationMapper(zpdZtrMessage, providerRep);
	}

	private void importPediatricsData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic)
	{
	}
	private void importPregnancyData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic)
	{
	}

	private void importAllergyData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic)
	{
	}

	private void importImmunizationData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic)
	{
	}

	private void importLabData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic)
	{
		//TODO
	}

	private void importDocumentData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic)
	{
		DocumentMapper documentMapper = new DocumentMapper(zpdZtrMessage, providerRep);

		for(Document document : documentMapper.getDocumentList())
		{
			document.setDoccreator(String.valueOf(provider.getProviderNo()));
			document.setResponsible(String.valueOf(provider.getProviderNo()));
			documentService.addDocumentModel(document, demographic.getDemographicId());
			documentService.routeToProviderInbox(document.getDocumentNo(), provider.getProviderNo());
		}
	}

	private void importProviderNotes(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic)
	{
		NoteMapper noteMapper = new NoteMapper(zpdZtrMessage, providerRep);
	}
}
