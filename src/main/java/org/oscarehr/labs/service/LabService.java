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
package org.oscarehr.labs.service;

import org.apache.commons.codec.binary.Base64;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.dao.Hl7TextMessageDao;
import org.oscarehr.common.dao.PatientLabRoutingDao;
import org.oscarehr.common.dao.ProviderLabRoutingDao;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.common.model.Hl7TextMessage;
import org.oscarehr.common.model.PatientLabRouting;
import org.oscarehr.common.model.ProviderLabRoutingModel;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.oscarLab.ForwardingRules;
import oscar.oscarLab.ca.all.Hl7textResultsData;
import oscar.oscarLab.ca.all.parsers.MessageHandler;
import oscar.util.ConversionUtils;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * TODO This service should replace the MessageUploader routing eventually
 */
@Service
@Transactional
public class LabService
{
	@Autowired
	Hl7TextInfoDao hl7TextInfoDao;

	@Autowired
	Hl7TextMessageDao hl7TextMessageDao;

	@Autowired
	ProviderLabRoutingDao providerLabRoutingDao;

	@Autowired
	PatientLabRoutingDao patientLabRoutingDao;

	public void persistNewHL7Lab(MessageHandler messageHandler, String hl7Message, String serviceName, int fileId) throws UnsupportedEncodingException
	{
		//TODO-legacy find demographic for routing

		//TODO-legacy find providers for routing

		persistNewHL7Lab(messageHandler, hl7Message, serviceName, fileId, null, null, null);
	}

	public Hl7TextMessage persistNewHL7Lab(MessageHandler messageHandler, String hl7Message, String serviceName, int fileId,
	                             Demographic demographic,Map<ProviderData, LocalDateTime> providerList) throws UnsupportedEncodingException
	{
		return persistNewHL7Lab(messageHandler, hl7Message, serviceName, fileId, demographic, providerList, null);
	}

	public Hl7TextMessage persistNewHL7Lab(MessageHandler messageHandler, String hl7Message, String serviceName, int fileId,
	                                       Demographic demographic, Map<ProviderData, LocalDateTime> providerList, String inboxRouteStatus) throws UnsupportedEncodingException
	{
		String labType = messageHandler.getMsgType();
		String firstName = messageHandler.getFirstName();
		String lastName = messageHandler.getLastName();
		String sex = messageHandler.getSex();
		String hin = messageHandler.getHealthNum();
		String resultStatus = (messageHandler.isAbnormal() ? "A" : null);
		String priority = messageHandler.getMsgPriority();
		String requestingClient = messageHandler.getDocName();
		String reportStatus = messageHandler.getOrderStatus();
		String accessionNum = messageHandler.getAccessionNum();
		String fillerOrderNum = messageHandler.getFillerOrderNumber();
		int finalResultCount = messageHandler.getOBXFinalResultCount();
		String obrDate = ConversionUtils.toTimestampString(ConversionUtils.getLegacyDateFromDateString(messageHandler.getMsgDate()));
		String discipline = findDiscipline(messageHandler);

		Hl7TextMessage hl7TextMessage = new Hl7TextMessage();
		Hl7TextInfo hl7TextInfo = new Hl7TextInfo();

		hl7TextMessage.setFileUploadCheckId(fileId);
		hl7TextMessage.setType(labType);
		hl7TextMessage.setBase64EncodedeMessage(new String(
				Base64.encodeBase64(hl7Message.getBytes(MiscUtils.DEFAULT_UTF8_ENCODING)), MiscUtils.DEFAULT_UTF8_ENCODING));
		hl7TextMessage.setServiceName(serviceName);

		hl7TextInfo.setLastName(lastName);
		hl7TextInfo.setFirstName(firstName);
		hl7TextInfo.setSex(sex);
		hl7TextInfo.setHealthNumber(hin);
		hl7TextInfo.setResultStatus(resultStatus);
		hl7TextInfo.setFinalResultCount(finalResultCount);
		hl7TextInfo.setObrDate(obrDate);
		hl7TextInfo.setPriority(priority);
		hl7TextInfo.setRequestingProvider(requestingClient);
		hl7TextInfo.setDiscipline(discipline);
		hl7TextInfo.setReportStatus(reportStatus);
		hl7TextInfo.setAccessionNumber(accessionNum);
		hl7TextInfo.setFillerOrderNum(fillerOrderNum);

		//TODO additional logic for lab uploads. Most of the lab specific stuff that should get moved to the handler

		return persistNewHL7Lab(hl7TextMessage, hl7TextInfo, demographic, providerList, inboxRouteStatus);
	}

	/**
	 * logic copied from MessageUploader
	 * TODO this but better
	 */
	private static String findDiscipline(MessageHandler messageHandler)
	{
		ArrayList<String> disciplineArray = messageHandler.getHeaders();
		String next = "";
		if(disciplineArray != null && disciplineArray.size() > 0)
		{
			next = disciplineArray.get(0);
		}

		int sepMark;
		if((sepMark = next.indexOf("<br />")) < 0)
		{
			if((sepMark = next.indexOf(" ")) < 0) sepMark = next.length();
		}
		String discipline = next.substring(0, sepMark).trim();

		for(int i = 1; i < disciplineArray.size(); i++)
		{

			next = disciplineArray.get(i);
			if((sepMark = next.indexOf("<br />")) < 0)
			{
				if((sepMark = next.indexOf(" ")) < 0) sepMark = next.length();
			}

			if(!next.trim().equals(""))
			{
				discipline = discipline + "/" + next.substring(0, sepMark);
			}
		}
		return discipline;
	}

	private Hl7TextMessage persistNewHL7Lab(Hl7TextMessage hl7TextMessage, Hl7TextInfo hl7TextInfo, Demographic demographic, Map<ProviderData, LocalDateTime> providerList, String inboxRouteStatus)
	{
		hl7TextMessageDao.persist(hl7TextMessage);

		hl7TextInfo.setLabNumber(hl7TextMessage.getId());
		hl7TextInfoDao.persist(hl7TextInfo);

		// route to the given demographic
		Integer demographicNo = (demographic != null)? demographic.getDemographicId() : PatientLabRoutingDao.UNMATCHED;

		routeToDemographic(hl7TextMessage.getId(), demographicNo);
		addMeasurements(hl7TextMessage.getId(), demographicNo);

		// route to the providers inbox
		if(providerList != null && !providerList.isEmpty())
		{
			for(ProviderData provider : providerList.keySet())
			{
				routeToProvider(hl7TextMessage.getId(), provider.getProviderNo(), inboxRouteStatus, providerList.get(provider));
			}
		}
		else
		{
			routeToProvider(hl7TextMessage.getId(), ProviderLabRoutingDao.PROVIDER_UNMATCHED, inboxRouteStatus, null);
		}
		return hl7TextMessage;
	}

	private void routeToDemographic(int labId, Integer demographicNo)
	{
		PatientLabRouting patientLabRouting = new PatientLabRouting();

		patientLabRouting.setDemographicNo(demographicNo);
		patientLabRouting.setLabNo(labId);
		patientLabRouting.setLabType(PatientLabRoutingDao.HL7);
		patientLabRouting.setCreated(new Date());
		patientLabRouting.setDateModified(new Date());
		patientLabRoutingDao.persist(patientLabRouting);
	}

	private void addMeasurements(int labId, Integer demographicNo)
	{
		Hl7textResultsData.populateMeasurementsTable(String.valueOf(labId), String.valueOf(demographicNo));
	}

	private void routeToProvider(int labId, Integer providerNo, String inboxRouteStatus, LocalDateTime timestamp)
	{
		String providerNoStr = String.valueOf(providerNo);
		if(inboxRouteStatus == null)
		{
			ForwardingRules fr = new ForwardingRules();
			inboxRouteStatus = fr.getStatus(providerNoStr);
		}

		ProviderLabRoutingModel newRoute = new ProviderLabRoutingModel();
		newRoute.setProviderNo(providerNoStr);
		newRoute.setLabNo(labId);
		newRoute.setLabType(ProviderLabRoutingDao.LAB_TYPE_HL7);
		newRoute.setStatus(inboxRouteStatus);

		if (timestamp != null)
		{
			newRoute.setTimestamp(ConversionUtils.toLegacyDateTime(timestamp));
		}

		providerLabRoutingDao.persist(newRoute);
	}
}
