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


/*
 * MessageUploader.java
 *
 * Created on June 18, 2007, 1:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oscar.oscarLab.ca.all.upload;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.OtherIdManager;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.dao.Hl7TextMessageDao;
import org.oscarehr.common.dao.PatientLabRoutingDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.common.model.Hl7TextMessage;
import org.oscarehr.common.model.OtherId;
import org.oscarehr.common.model.PatientLabRouting;
import org.oscarehr.common.model.Provider;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.olis.dao.OLISSystemPreferencesDao;
import org.oscarehr.olis.model.OLISSystemPreferences;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.OscarProperties;
import oscar.oscarDemographic.data.DemographicMerged;
import oscar.oscarLab.ca.all.Hl7textResultsData;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.parsers.HHSEmrDownloadHandler;
import oscar.oscarLab.ca.all.parsers.MessageHandler;
import oscar.oscarLab.ca.all.parsers.SpireHandler;
import oscar.util.UtilDateUtilities;

public final class MessageUploader {

	private static final Logger logger = MiscUtils.getLogger();
	private static PatientLabRoutingDao patientLabRoutingDao = SpringUtils.getBean(PatientLabRoutingDao.class);
	private static Hl7TextInfoDao hl7TextInfoDao = (Hl7TextInfoDao) SpringUtils.getBean("hl7TextInfoDao");
	private static Hl7TextMessageDao hl7TextMessageDao = (Hl7TextMessageDao) SpringUtils.getBean("hl7TextMessageDao");
	private static DemographicManager demographicManager = SpringUtils.getBean(DemographicManager.class);
	private static ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
	private static DemographicDao demographicDao = SpringUtils.getBean(DemographicDao.class);

	private MessageUploader() {
		// there's no reason to instantiate a class with no fields.
	}

	@Deprecated
	public static String routeReport(LoggedInInfo loggedInInfo, String serviceName, String type, String hl7Body, int fileId) throws Exception {
		return routeReport(loggedInInfo, serviceName, type, hl7Body, fileId, null);
	}
	public static String routeReport(String loggedInProviderNo, String serviceName, String type, String hl7Body, int fileId) throws Exception {
		return routeReport(loggedInProviderNo, serviceName, type, hl7Body, fileId, null);
	}

	/**
	 * Insert the lab into the proper tables of the database
	 */
	@Deprecated
	public static String routeReport(LoggedInInfo loggedInInfo, String serviceName, String type, String hl7Body, int fileId, RouteReportResults results) throws Exception
	{
		return routeReport(loggedInInfo.getLoggedInProviderNo(), serviceName, type, hl7Body, fileId, results);
	}
	/**
	 * Insert the lab into the proper tables of the database
	 */
	public static String routeReport(String loggedInProviderNo, String serviceName, String type, String hl7Body, int fileId, RouteReportResults results) throws Exception
	{

		String retVal = "";
		try {
			MessageHandler messageHandler = Factory.getHandler(type, hl7Body);

			String firstName = messageHandler.getFirstName();
			String lastName = messageHandler.getLastName();
			String dob = messageHandler.getDOB();
			String sex = messageHandler.getSex();
			String hin = messageHandler.getHealthNum();
			String resultStatus = "";
			String priority = messageHandler.getMsgPriority();
			String requestingClient = messageHandler.getDocName();
			String reportStatus = messageHandler.getOrderStatus();
			String accessionNum = messageHandler.getAccessionNum();
			String fillerOrderNum = messageHandler.getFillerOrderNumber();
			String sendingFacility = messageHandler.getPatientLocation();
			ArrayList docNums = messageHandler.getDocNums();
			int finalResultCount = messageHandler.getOBXFinalResultCount();
			String obrDate = messageHandler.getMsgDate();

			if(messageHandler instanceof HHSEmrDownloadHandler) {
				try{
	            	String chartNo = ((HHSEmrDownloadHandler)messageHandler).getPatientIdByType("MR");
	            	if(chartNo != null) {
	            		//let's get the hin
	            		List<Demographic> clients = demographicManager.getDemosByChartNo(loggedInProviderNo, chartNo);
	            		if(clients!=null && clients.size()>0) {
	            			hin = clients.get(0).getHin();
	            		}
	            	}
				}catch(Exception e){
					logger.error("HHS ERROR",e);
				}
            }
            
            // get actual ohip numbers based on doctor first and last name for spire lab
            if(messageHandler instanceof SpireHandler) {
				List<String> docNames = ((SpireHandler)messageHandler).getDocNames();
				//logger.debug("docNames:");
	            for (int i=0; i < docNames.size(); i++) {
					logger.info(i + " " + docNames.get(i));
				}
            	if (docNames != null) {
					docNums = findProvidersForSpireLab(docNames);
				}
            }

			try {
				// reformat date
				String format = "yyyy-MM-dd HH:mm:ss".substring(0, obrDate.length() - 1);
				obrDate = UtilDateUtilities.DateToString(UtilDateUtilities.StringToDate(obrDate, format), "yyyy-MM-dd HH:mm:ss");
			} catch (Exception e) {				
				logger.error("Error parsing obr date : ", e);
				throw e;
			}

			int i = 0;
			int j = 0;
			while (resultStatus.equals("") && i < messageHandler.getOBRCount()) {
				j = 0;
				while (resultStatus.equals("") && j < messageHandler.getOBXCount(i)) {
					if (messageHandler.isOBXAbnormal(i, j)) resultStatus = "A";
					j++;
				}
				i++;
			}

			ArrayList<String> disciplineArray = messageHandler.getHeaders();
			String next = "";
			if (disciplineArray != null && disciplineArray.size() > 0) next = disciplineArray.get(0);

			int sepMark;
			if ((sepMark = next.indexOf("<br />")) < 0) {
				if ((sepMark = next.indexOf(" ")) < 0) sepMark = next.length();
			}
			String discipline = next.substring(0, sepMark).trim();

			for (i = 1; i < disciplineArray.size(); i++) {

				next = disciplineArray.get(i);
				if ((sepMark = next.indexOf("<br />")) < 0) {
					if ((sepMark = next.indexOf(" ")) < 0) sepMark = next.length();
				}

				if (!next.trim().equals("")) discipline = discipline + "/" + next.substring(0, sepMark);
			}

			boolean isTDIS = type.equals("TDIS");
			boolean hasBeenUpdated = false;
			Hl7TextMessage hl7TextMessage = new Hl7TextMessage();
			Hl7TextInfo hl7TextInfo = new Hl7TextInfo();


			if (isTDIS) {
				List<Hl7TextInfo> matchingTdisLab =  hl7TextInfoDao.searchByFillerOrderNumber(fillerOrderNum, sendingFacility);
				if (matchingTdisLab.size()>0) {

					hl7TextMessageDao.updateIfFillerOrderNumberMatches(new String(Base64.encodeBase64(hl7Body.getBytes(MiscUtils.DEFAULT_UTF8_ENCODING)), MiscUtils.DEFAULT_UTF8_ENCODING),fileId,matchingTdisLab.get(0).getLabNumber());

					hl7TextInfoDao.updateReportStatusByLabId(reportStatus,matchingTdisLab.get(0).getLabNumber());
					hasBeenUpdated = true;
				}
			}
			int insertID = 0;
			if (!isTDIS || !hasBeenUpdated) {
				hl7TextMessage.setFileUploadCheckId(fileId);
				hl7TextMessage.setType(type);
				hl7TextMessage.setBase64EncodedeMessage(new String(Base64.encodeBase64(hl7Body.getBytes(MiscUtils.DEFAULT_UTF8_ENCODING)), MiscUtils.DEFAULT_UTF8_ENCODING));
				hl7TextMessage.setServiceName(serviceName);
				hl7TextMessageDao.persist(hl7TextMessage);

				insertID = hl7TextMessage.getId();
				hl7TextInfo.setLabNumber(insertID);
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
				hl7TextInfoDao.persist(hl7TextInfo);
			}

			String demProviderNo = "0";
			try
			{
				demProviderNo = patientRouteReport(loggedInProviderNo, insertID, lastName,
						firstName, sex, dob, hin);
			} catch (Exception ignored)
			{

			}

			if(type.equals("OLIS_HL7") && demProviderNo.equals("0"))
			{
				OLISSystemPreferencesDao olisPrefDao = SpringUtils.getBean(OLISSystemPreferencesDao.class);
				OLISSystemPreferences olisPreferences = olisPrefDao.getPreferences();
				if(olisPreferences.isFilterPatients())
				{
					//set as unclaimed
					providerRouteReport(String.valueOf(insertID), null, DbConnectionFilter.getThreadLocalDbConnection(), String.valueOf(0), type);
				}
				else
				{
					providerRouteReport(String.valueOf(insertID), docNums, DbConnectionFilter.getThreadLocalDbConnection(), demProviderNo, type);
				}
			}
			else
			{
				Integer limit = null;
				boolean orderByLength = false;
				String search = null;
				if(type.equals("Spire"))
				{
					limit = new Integer(1);
					orderByLength = true;
					search = "provider_no";
				}
				else if(type.equals("CLS") || type.equals("CLSDI"))
				{
					search = "hso_no";
				}
				else if(type.equals("IHA"))
				{
					search = "alberta_e_delivery_ids";
				}

				/* allow property override setting to route all labs to a specific inbox or list of inboxes. */
				ArrayList<String> providers = OscarProperties.getInstance().getRouteLabsToProviders(docNums);
				providerRouteReport(String.valueOf(insertID), providers, DbConnectionFilter.getThreadLocalDbConnection(), demProviderNo, type, search, limit, orderByLength);
			}
			retVal = messageHandler.audit();
			if(results != null)
			{
				results.segmentId = insertID;
			}
		}
		catch(Exception e)
		{
			logger.error("Error uploading lab to database");
			throw e;
		}

		return (retVal);

	}
	
	/**
	 * Method findProvidersForSpireLab
	 * Finds the providers that are associated with a spire lab.  (need to do this using doctor names, as
	 * spire labs don't have a valid ohip number associated with them).
	 */ 
	private static ArrayList<String> findProvidersForSpireLab(List<String> docNames) {
		List<String> docNums = new ArrayList<String>();
		ProviderDao providerDao = (ProviderDao)SpringUtils.getBean("providerDao");
		
		for (int i=0; i < docNames.size(); i++) {
			String[] firstLastName = docNames.get(i).split("\\s");
			if (firstLastName != null && firstLastName.length >= 2) {
				//logger.debug("Searching for provider with first and last name: " + firstLastName[0] + " " + firstLastName[firstLastName.length-1]);
				List<Provider> provList = providerDao.getProviderLikeFirstLastName("%"+firstLastName[0]+"%", firstLastName[firstLastName.length-1]);
				if (provList != null) {
					int provIndex = findProviderWithShortestFirstName(provList);
					if (provIndex != -1 && provList.size() >= 1 && !provList.get(provIndex).getProviderNo().equals("0")) {
						docNums.add( provList.get(provIndex).getProviderNo() );
						//logger.debug("ADDED1: " + provList.get(provIndex).getProviderNo());
					} else {
						// prepend 'dr ' to first name and try again
						provList = providerDao.getProviderLikeFirstLastName("dr " + firstLastName[0], firstLastName[1]);
						if (provList != null) {
							provIndex = findProviderWithShortestFirstName(provList);
							if (provIndex != -1 && provList.size() == 1 && !provList.get(provIndex).getProviderNo().equals("0")) {
								//logger.debug("ADDED2: " + provList.get(provIndex).getProviderNo());
								docNums.add( provList.get(provIndex).getProviderNo() );
							}
						}
					}
				}
			}
		}
		
		return (ArrayList<String>)docNums;
	}
	
	/**
	 * Method findProviderWithShortestFirstName
	 * Finds the provider with the shortest first name in a list of providers.
	 */ 
	private static int findProviderWithShortestFirstName(List<Provider> provList) {
		if (provList == null || provList.isEmpty())
			return -1;
			
		int index = 0;
		int shortestLength = provList.get(0).getFirstName().length();
		for (int i=1; i < provList.size(); i++) {
			int curLength = provList.get(i).getFirstName().length();
			if (curLength < shortestLength) {
				index = i;
				shortestLength = curLength;
			}
		}
		
		return index;
	}
	
	/**
	 * Attempt to match the doctors from the lab to a provider
	 */ 
	private static void providerRouteReport(String labId, ArrayList<String> docNums, Connection conn, String altProviderNo, String labType, String search_on, Integer limit, boolean orderByLength) throws Exception {
		ArrayList<String> providerNums = new ArrayList<String>();
		String sqlSearchOn = "ohip_no";
		
		if (search_on != null && search_on.length() > 0) {
			sqlSearchOn = search_on;
		}
		
		if (docNums != null) {
			for (int i = 0; i < docNums.size(); i++) {
				if (docNums.get(i) != null && !(docNums.get(i)).trim().equals("")) {

					List<Provider> results = providerDao.getProvidersByFieldId(docNums.get(i), labType, sqlSearchOn, limit, orderByLength);
					for(Provider p: results) {
						providerNums.add(p.getProviderNo());
					}

					String otherIdMatchKey = OscarProperties.getInstance().getProperty("lab.other_id_matching", "");
					if(otherIdMatchKey.length()>0) {
						OtherId otherId = OtherIdManager.searchTable(OtherIdManager.PROVIDER, otherIdMatchKey, docNums.get(i));
						if(otherId != null) {
							providerNums.add(otherId.getTableId());
						}
					}
				}
			}
		}

		ProviderLabRouting routing = new ProviderLabRouting();
		if (providerNums.size() > 0) {
			for (int i = 0; i < providerNums.size(); i++) {
				String provider_no = providerNums.get(i);
				routing.route(labId, provider_no, conn, "HL7");
			}
		}
		else {
			routing.route(labId, "0", conn, "HL7");
			routing.route(labId, altProviderNo, conn, "HL7");
		}
	}

	/**
	 * Attempt to match the doctors from the lab to a provider
	 */
	private static void providerRouteReport(String labId, ArrayList<String> docNums, Connection conn, String altProviderNo, String labType) throws Exception {
		providerRouteReport(labId, docNums, conn, altProviderNo, labType, null, null, false);
	}

	/**
	 * Attempt to match the patient from the lab to a demographic, return the patients provider which is to be used then no other provider can be found to match the patient to.
	 */
	private static String patientRouteReport(String loggedInProviderNo, int labId,
											 String lastName, String firstName,
											 String sex, String dob, String hin) throws Exception {

		PatientLabRoutingResult patientLabRoutingResult = null;
		Integer demographicNumber = 0;
		String providerNumber = "0";
		GregorianCalendar dateOfBirth = null;

		if (hin != null) {
			// This is for one of the Ontario labs I think??
			if (hin.length() == 12) {
				hin = hin.substring(0, 10);
			}
		}

		if (dob != null && !dob.trim().equals("")) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = dateFormat.parse(dob.trim());
			dateOfBirth = new GregorianCalendar();
			dateOfBirth.setTime(date);
		}

		// Only match against first chars of patient name
		if (!firstName.equals("")) firstName = firstName.substring(0, 1);
		if (!lastName.equals("")) lastName = lastName.substring(0, 1);

		Demographic demographic = demographicDao.findMatchingLab(
				hin, firstName, lastName, sex, dateOfBirth);

		patientLabRoutingResult = new PatientLabRoutingResult();
		demographicNumber = demographic.getDemographicNo();
		providerNumber = demographic.getProviderNo();
		patientLabRoutingResult.setDemographicNo(demographicNumber);
		patientLabRoutingResult.setProviderNo(providerNumber);
		
		//did this link a merged patient? if so, we need to make sure we are the head record,
		// or update result to be the head record.
		DemographicMerged demographicMerged = new DemographicMerged();
		Integer headDemo = demographicMerged.getHead(demographicNumber);
		if(headDemo != null && headDemo.intValue() != demographicNumber.intValue()) {
			Demographic demoTmp = demographicManager.getDemographic(loggedInProviderNo, headDemo);
			if(demoTmp != null) {
				patientLabRoutingResult.setDemographicNo(demoTmp.getDemographicNo());
				patientLabRoutingResult.setProviderNo(demoTmp.getProviderNo());
			} else {
				String message = "Unable to load the head record of this patient record. (" +
						patientLabRoutingResult.getDemographicNo()  + ")";
				logger.info(message);
				throw new Exception(message);
			}
		}

		Hl7textResultsData.populateMeasurementsTable(
				Integer.toString(labId), patientLabRoutingResult.getDemographicNo().toString());

		PatientLabRouting patientLabRouting = new PatientLabRouting();
		Integer demographicNo = patientLabRoutingResult.getDemographicNo();
		if(demographicNo == null)
		{
			demographicNo = PatientLabRoutingDao.UNMATCHED;
		}

		patientLabRouting.setDemographicNo(demographicNo);
		patientLabRouting.setLabNo(labId);
		patientLabRouting.setLabType(PatientLabRoutingDao.HL7);
		patientLabRouting.setCreated(new Date());
		patientLabRouting.setDateModified(new Date());
		patientLabRoutingDao.persist(patientLabRouting);

		return patientLabRoutingResult.getProviderNo();
	}
}
