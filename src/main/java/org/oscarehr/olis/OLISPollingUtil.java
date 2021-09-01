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

import com.indivica.olis.Driver;
import com.indivica.olis.parameters.OBR22;
import com.indivica.olis.parameters.ORC21;
import com.indivica.olis.parameters.ZRP1;
import com.indivica.olis.queries.Z04Query;
import com.indivica.olis.queries.Z06Query;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.olis.dao.OLISProviderPreferencesDao;
import org.oscarehr.olis.dao.OLISSystemPreferencesDao;
import org.oscarehr.olis.model.OLISProviderPreferences;
import org.oscarehr.olis.model.OLISSystemPreferences;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.oscarLab.ca.all.upload.handlers.LabHandlerService;
import oscar.oscarLab.ca.all.util.Utilities;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static oscar.oscarLab.ca.all.parsers.OLISHL7Handler.OLIS_MESSAGE_TYPE;

public class OLISPollingUtil
{
	public static final String OLIS_DATE_FORMAT = "yyyyMMddHHmmssZ";
	private static final Logger logger = MiscUtils.getLogger();
	
	static ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
    static OLISSystemPreferencesDao olisSystemPreferencesDao =  SpringUtils.getBean(OLISSystemPreferencesDao.class);  
    static OLISProviderPreferencesDao olisProviderPreferencesDao =  SpringUtils.getBean(OLISProviderPreferencesDao.class);

	public OLISPollingUtil()
	{
		super();
	}
	
	public static void requestResults(LoggedInInfo loggedInInfo)
	{
		OLISSystemPreferences olisSystemPreferences = olisSystemPreferencesDao.getPreferences();
		String defaultStartTime = oscar.Misc.getStr(olisSystemPreferences.getStartTime(), "").trim();
	    String defaultEndTime = oscar.Misc.getStr(olisSystemPreferences.getEndTime(), "").trim();
	    
	    pollZ04Query(loggedInInfo, defaultStartTime,defaultEndTime);
	    
	    String facilityId = OscarProperties.getInstance().getProperty("olis_polling_facility"); //Most of the time this will default to null.
		if(facilityId != null)
		{
			pollZ06Query(loggedInInfo, defaultStartTime, defaultEndTime, facilityId);
		}
	}
	
	private static void pollZ04Query(LoggedInInfo loggedInInfo, String defaultStartTime,String defaultEndTime){
		//Z04Query providerQuery;
		List<Provider> allProvidersList = providerDao.getActiveProviders();
	    UserPropertyDAO userPropertyDAO = (UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");
	    for (Provider provider : allProvidersList) {
	    	try {
	    		String officialLastName  = userPropertyDAO.getStringValue(provider.getProviderNo(),UserProperty.OFFICIAL_LAST_NAME);
	    		String officialFirstName = userPropertyDAO.getStringValue(provider.getProviderNo(),UserProperty.OFFICIAL_FIRST_NAME);
				String officialSecondName = userPropertyDAO.getStringValue(provider.getProviderNo(),UserProperty.OFFICIAL_SECOND_NAME);
				String olisIdType = userPropertyDAO.getStringValue(provider.getProviderNo(), UserProperty.OFFICIAL_OLIS_IDTYPE);
				
				//There is no need to query for users without this configured, it will just end in an error.
	    		if(StringUtils.isBlank(officialLastName) || StringUtils.isBlank(olisIdType))
	    		{
	    			continue;
	    		}
	    		
	    		Z04Query providerQuery = new Z04Query();
		    	OLISProviderPreferences olisProviderPreferences = olisProviderPreferencesDao.findById(provider.getProviderNo());

		    	// Creating OBR22 for this request.
			    OBR22 obr22 = buildRequestStartEndTimestamp(olisProviderPreferences, defaultStartTime, defaultEndTime);
			    if(olisProviderPreferences == null)
			    {
				    olisProviderPreferences = new OLISProviderPreferences();
				    olisProviderPreferences.setProviderId(provider.getProviderNo());
			    }
				providerQuery.setStartEndTimestamp(obr22);
	
				// Setting HIC for Z04 Request
			    ZRP1 zrp1 = new ZRP1(
					    provider.getPractitionerNo(),
					    olisIdType,
					    "ON",
					    "HL70347",
					    officialLastName,
					    officialFirstName,
					    officialSecondName);
				providerQuery.setRequestingHic(zrp1);
				String response = Driver.submitOLISQuery(loggedInInfo.getLoggedInProvider(), null, providerQuery);
				
				if(!response.startsWith("<Response")){
					logger.error("response does not match, aborting "+response);
					continue;
				}
				String timeStampForNextStartDate= OLISPollingUtil.parseAndImportResponse(loggedInInfo, response);
				logger.info("timeSlot "+timeStampForNextStartDate);
				
				if(timeStampForNextStartDate != null){
					olisProviderPreferences.setStartTime(timeStampForNextStartDate);
				}
				
				if(olisProviderPreferences.getId()!=null) {
					olisProviderPreferencesDao.merge(olisProviderPreferences);
				} else {
					olisProviderPreferencesDao.persist(olisProviderPreferences);
				}
		    }
		    catch(Exception e)
		    {
			    logger.error("Error polling OLIS for provider " + provider.getProviderNo(), e);
		    }
	    }
	}
	
	private static void pollZ06Query(LoggedInInfo loggedInInfo, String defaultStartTime,String defaultEndTime,String facilityId)
	{
		try
		{
			Z06Query facilityQuery = new Z06Query();
			OLISProviderPreferences olisProviderPreferences = olisProviderPreferencesDao.findById(Provider.SYSTEM_PROVIDER_NO);
			// Creating OBR22 for this request.
			OBR22 obr22 = buildRequestStartEndTimestamp(olisProviderPreferences, defaultStartTime, defaultEndTime);
			if(olisProviderPreferences == null)
			{
				olisProviderPreferences = new OLISProviderPreferences();
				olisProviderPreferences.setProviderId(Provider.SYSTEM_PROVIDER_NO);
			}
	    	facilityQuery.setStartEndTimestamp(obr22);
	    	ORC21 orc21 = new ORC21();
	    	orc21.setValue(6, 2, "^"+facilityId);
	    	orc21.setValue(6, 3, "^ISO");    	
	    	facilityQuery.setOrderingFacilityId(orc21);
	    	
	    	String response = Driver.submitOLISQuery(loggedInInfo.getLoggedInProvider(), null, facilityQuery);
	    	
	    	if(!response.startsWith("<Response")){
	    		logger.debug("Didn't equal response.  Returning "+response);
				return;
			}
	    	
	    	String timeStampForNextStartDate= OLISPollingUtil.parseAndImportResponse(loggedInInfo, response);
			
			if(timeStampForNextStartDate != null){
				olisProviderPreferences.setStartTime(timeStampForNextStartDate);
			}
			
			if(olisProviderPreferences.getId()!=null) {
				olisProviderPreferencesDao.merge(olisProviderPreferences);
			} else {
				olisProviderPreferencesDao.persist(olisProviderPreferences);
			}
		}
		catch(Exception e)
		{
			logger.error("Error polling OLIS for facility", e);
		}
    }
	
	public static String parseAndImportResponse(LoggedInInfo loggedInInfo, String response) throws Exception
	{
		UUID uuid = UUID.randomUUID();
		String originalFile = "olis_"+uuid.toString()+".response";
		String hl7Filename = "olis_"+uuid.toString()+".hl7";
		//write full response to disk, this will make diagnosing issues easier
		Utilities.saveFile(new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)), originalFile);

		//Get HL7 Content from xml
		String responseContent =  OLISUtils.getOLISResponseContent(response);
		//Write HL7 file to disk.
		String fileLocation = Utilities.saveFile(
				new ByteArrayInputStream(responseContent.getBytes(StandardCharsets.UTF_8)), hl7Filename);

		String labType = OLIS_MESSAGE_TYPE;
		String serviceName = "OLIS_HL7";
		String providerNumber = Provider.UNCLAIMED_PROVIDER_NO;
		String timeStringForNextStartDate = null;
		try
		{
			logger.debug("Lab Type: " + labType);
			logger.debug("Lab file path: " + fileLocation);

			LabHandlerService labHandlerService = SpringUtils.getBean(LabHandlerService.class);
			timeStringForNextStartDate = labHandlerService.importLab(
					labType,
					loggedInInfo,
					serviceName,
					fileLocation,
					providerNumber,
					null
			);
			logger.info("Lab successfully added.");

		} catch(FileAlreadyExistsException e) {
			logger.warn("Lab already in system.");
		} catch(Exception e) {
			logger.error( "Failed insert lab into DB: " + fileLocation + " of type: " + labType, e);
		}
		return timeStringForNextStartDate;
	}

	private static OBR22 buildRequestStartEndTimestamp(OLISProviderPreferences olisProviderPreferences, String defaultStartTime, String defaultEndTime)
	{
		OBR22 obr22 = new OBR22();
		if(olisProviderPreferences != null)
		{
			String dateTimeStr = olisProviderPreferences.getOptionalStartDateTime().orElse(defaultStartTime);
			validateDateTimeString(dateTimeStr);
			obr22.setValue(dateTimeStr);
		}
		else
		{
			validateDateTimeString(defaultStartTime);
			if(StringUtils.isBlank(defaultEndTime))
			{
				obr22.setValue(defaultStartTime);
			}
			else
			{
				validateDateTimeString(defaultEndTime);
				List<String> dateList = new LinkedList<>();
				dateList.add(defaultStartTime);
				dateList.add(defaultEndTime);
				obr22.setValue(dateList);
			}
		}
		return obr22;
	}

	/**
	 * basic check to ensure the datetime string is in the correct format
 	 */
	private static void validateDateTimeString(String toValidate)
	{
		boolean isValid = toValidate.matches("\\d{14}([+-]\\d+)?");
		if(!isValid)
		{
			throw new IllegalArgumentException("Malformed OLIS date: " + toValidate);
		}
	}
}
