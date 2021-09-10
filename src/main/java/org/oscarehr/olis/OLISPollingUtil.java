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
package org.oscarehr.olis;

import com.indivica.olis.Driver;
import com.indivica.olis.parameters.OBR22;
import com.indivica.olis.parameters.ORC21;
import com.indivica.olis.parameters.ZRP1;
import com.indivica.olis.queries.DateRangeQuery;
import com.indivica.olis.queries.Z04Query;
import com.indivica.olis.queries.Z06Query;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.ProviderLabRoutingDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.olis.dao.OLISProviderPreferencesDao;
import org.oscarehr.olis.dao.OLISSystemPreferencesDao;
import org.oscarehr.olis.exception.OLISAckFailedException;
import org.oscarehr.olis.model.OLISProviderPreferences;
import org.oscarehr.olis.model.OLISSystemPreferences;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.oscarLab.ca.all.upload.handlers.LabHandlerService;
import oscar.oscarLab.ca.all.util.Utilities;
import oscar.util.ConversionUtils;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static oscar.oscarLab.ca.all.parsers.OLISHL7Handler.OLIS_MESSAGE_TYPE;
import static oscar.oscarLab.ca.all.upload.handlers.OLISHL7Handler.ALL_DUPLICATES_MARKER;

public class OLISPollingUtil
{
	public static final String OLIS_DATE_FORMAT = "yyyyMMddHHmmssZ";
	private static final OscarProperties props = OscarProperties.getInstance();
	private static final int DEFAULT_FETCH_PERIOD_MONTHS = 1;
	private static final int MAX_FETCH_PERIOD_MONTHS = Integer.parseInt(props.getProperty("olis_max_fetch_months", "12")); //max 12
	private static final Logger logger = MiscUtils.getLogger();

	private static final ProviderDataDao providerDao = SpringUtils.getBean(ProviderDataDao.class);
	private static final OLISSystemPreferencesDao olisSystemPreferencesDao =  SpringUtils.getBean(OLISSystemPreferencesDao.class);
	private static final OLISProviderPreferencesDao olisProviderPreferencesDao =  SpringUtils.getBean(OLISProviderPreferencesDao.class);
	private static final UserPropertyDAO userPropertyDAO = SpringUtils.getBean(UserPropertyDAO.class);

	public OLISPollingUtil()
	{
		super();
	}
	
	public static void requestResults(LoggedInInfo loggedInInfo)
	{
	    pollZ04Query(loggedInInfo);
	    
	    String facilityId = props.getProperty("olis_polling_facility"); //Most of the time this will default to null.
		if(facilityId != null)
		{
			pollZ06Query(loggedInInfo, facilityId);
		}
	}

	/**
	 * Query OLIS by provider
	 * @param loggedInInfo - current user info
	 */
	private static void pollZ04Query(LoggedInInfo loggedInInfo)
	{
		//Z04Query providerQuery;
		List<ProviderData> allProvidersList = providerDao.findByActiveStatus(true);
		for(ProviderData provider : allProvidersList)
		{
			String providerId = provider.getId();
			try
			{
	    		String officialLastName  = userPropertyDAO.getStringValue(providerId,UserProperty.OFFICIAL_LAST_NAME);
	    		String officialFirstName = userPropertyDAO.getStringValue(providerId,UserProperty.OFFICIAL_FIRST_NAME);
				String officialSecondName = userPropertyDAO.getStringValue(providerId,UserProperty.OFFICIAL_SECOND_NAME);
				String olisIdType = userPropertyDAO.getStringValue(providerId, UserProperty.OFFICIAL_OLIS_IDTYPE);
				
				//There is no need to query for users without this configured, it will just end in an error.
	    		if(StringUtils.isBlank(officialLastName) || StringUtils.isBlank(olisIdType))
	    		{
	    			continue;
	    		}
	    		
		    	OLISProviderPreferences olisProviderPreferences = findOrCreateOLISProviderPrefs(providerId);

				Z04Query providerQuery = new Z04Query();
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

				logger.info("Query OLIS for provider " + providerId);
				Pair<ZonedDateTime, ZonedDateTime> startEnd = findStartEndTimestamps(olisProviderPreferences);
				String timeStampForNextStartDate = queryAndImportDateRange(loggedInInfo, providerQuery, startEnd.getLeft(), startEnd.getRight());
				updateProviderStartTime(olisProviderPreferences, timeStampForNextStartDate);
			}
			catch(Exception e)
			{
				logger.error("Error polling OLIS for provider " + provider.getProviderNo(), e);
			}
		}
	}

	private static void pollZ06Query(LoggedInInfo loggedInInfo, String facilityId)
	{
		try
		{
			OLISProviderPreferences olisProviderPreferences = findOrCreateOLISProviderPrefs(ProviderData.SYSTEM_PROVIDER_NO);

			Z06Query facilityQuery = new Z06Query();
			ORC21 orc21 = new ORC21();
			orc21.setValue(6, 2, "^"+facilityId);
			orc21.setValue(6, 3, "^ISO");
			facilityQuery.setOrderingFacilityId(orc21);

			logger.info("Query OLIS for facility " + facilityId);
			Pair<ZonedDateTime, ZonedDateTime> startEnd = findStartEndTimestamps(olisProviderPreferences);
			String timeStampForNextStartDate = queryAndImportDateRange(loggedInInfo, facilityQuery, startEnd.getLeft(), startEnd.getRight());
			updateProviderStartTime(olisProviderPreferences, timeStampForNextStartDate);
		}
		catch(Exception e)
		{
			logger.error("Error polling OLIS for facility", e);
		}
	}

	private static OLISProviderPreferences findOrCreateOLISProviderPrefs(@NotNull String providerId)
	{
		OLISProviderPreferences olisProviderPreferences = olisProviderPreferencesDao.findById(providerId);
		if(olisProviderPreferences == null)
		{
			olisProviderPreferences = new OLISProviderPreferences();
			olisProviderPreferences.setProviderId(providerId);
		}
		return olisProviderPreferences;
	}

	private static void updateProviderStartTime(@NotNull OLISProviderPreferences olisProviderPreferences, String timeStampForNextStartDate)
	{
		logger.info("timeSlot "+timeStampForNextStartDate);
		if(StringUtils.isNotBlank(timeStampForNextStartDate) && !ALL_DUPLICATES_MARKER.equals(timeStampForNextStartDate))
		{
			olisProviderPreferences.setStartTime(timeStampForNextStartDate);
		}
		olisProviderPreferencesDao.saveEntity(olisProviderPreferences);
	}

	private static String queryAndImportDateRange(LoggedInInfo loggedInInfo, DateRangeQuery query, ZonedDateTime startDateTime, ZonedDateTime endDateTime) throws Exception
	{
		OBR22 obr22 = buildRequestStartEndTimestamp(startDateTime, endDateTime);
		query.setStartEndTimestamp(obr22);

		logger.info("Submit OLIS query for date range: " + ConversionUtils.toDateTimeString(startDateTime) + " to " + ConversionUtils.toDateTimeString(endDateTime));
		String response = Driver.submitOLISQuery(loggedInInfo.getLoggedInProvider(), null, query);

		if(!response.startsWith("<Response"))
		{
			logger.error("response does not match, aborting " + response);
			return null;
		}

		String timeStampForNextStartDate = null;
		try
		{
			timeStampForNextStartDate = OLISPollingUtil.parseAndImportResponse(loggedInInfo, response);
		}
		catch(OLISAckFailedException olisAckFailedException)
		{
			// if no results found, and we are not polling up to the current datetime, advance the query dates and try again
			if(olisAckFailedException.isStatusNotFound())
			{
				ZonedDateTime nextStartDateTime = Optional.ofNullable(endDateTime).orElse(startDateTime.plusMonths(MAX_FETCH_PERIOD_MONTHS));
				if(nextStartDateTime.isBefore(ZonedDateTime.now()))
				{
					logger.info("OLIS response returned no data, checking next date range");
					timeStampForNextStartDate = queryAndImportDateRange(loggedInInfo, query, nextStartDateTime, calcEndDate(nextStartDateTime));
				}
				else
				{
					logger.info("OLIS response returned no data, up to date");

				}
			}
			else
			{
				// error statuses
				throw olisAckFailedException;
			}
		}
		return timeStampForNextStartDate;
	}
	
	private static String parseAndImportResponse(LoggedInInfo loggedInInfo, String response) throws Exception
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
		String providerNumber = String.valueOf(ProviderLabRoutingDao.PROVIDER_UNMATCHED);
		logger.debug("Lab Type: " + labType);
		logger.debug("Lab file path: " + fileLocation);

		LabHandlerService labHandlerService = SpringUtils.getBean(LabHandlerService.class);
		String timeStringForNextStartDate = labHandlerService.importLab(
				labType,
				loggedInInfo,
				serviceName,
				fileLocation,
				providerNumber,
				null
		);
		logger.info("Lab successfully added.");
		return timeStringForNextStartDate;
	}

	private static Pair<ZonedDateTime, ZonedDateTime> findStartEndTimestamps(@NotNull OLISProviderPreferences olisProviderPreferences)
	{
		OLISSystemPreferences olisSystemPreferences = olisSystemPreferencesDao.getPreferences();
		Optional<String> optionalDefaultStartTime = Optional.ofNullable(StringUtils.trimToNull(olisSystemPreferences.getStartTime()));
		Optional<String> optionalDefaultEndTime = Optional.ofNullable(StringUtils.trimToNull(olisSystemPreferences.getEndTime()));

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(OLIS_DATE_FORMAT);

		Optional<String> prefStartTime = olisProviderPreferences.getOptionalStartDateTime();

		// provider has a start time, so use this and a max end time if needed.
		ZonedDateTime startTime;
		ZonedDateTime endTime;
		if(prefStartTime.isPresent())
		{
			startTime = ConversionUtils.toZonedDateTime(prefStartTime.get(), dateTimeFormatter);
			endTime = calcEndDate(startTime);
		}
		else
		{
			// use the default start and end time for the initial provider query.
			startTime = optionalDefaultStartTime.map((dateStr) -> ConversionUtils.toZonedDateTime(dateStr, dateTimeFormatter))
					.orElseGet(() -> ZonedDateTime.now().minusMonths(DEFAULT_FETCH_PERIOD_MONTHS));
			endTime = optionalDefaultEndTime.map((dateStr) -> ConversionUtils.toZonedDateTime(dateStr, dateTimeFormatter))
					.orElseGet(() -> calcEndDate(startTime));
		}
		return new ImmutablePair<>(startTime, endTime);
	}

	private static OBR22 buildRequestStartEndTimestamp(@NotNull ZonedDateTime startTime, ZonedDateTime endTime)
	{
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(OLIS_DATE_FORMAT);

		String endDateStr = (endTime != null) ? ConversionUtils.toDateTimeString(endTime, dateTimeFormatter) : null;
		return buildRequestStartEndTimestamp(ConversionUtils.toDateTimeString(startTime, dateTimeFormatter), endDateStr);
	}
	private static OBR22 buildRequestStartEndTimestamp(@NotNull String startTimeStr, String endTimeStr)
	{
		OBR22 obr22 = new OBR22();

		// only build start date
		if(StringUtils.isBlank(endTimeStr))
		{
			obr22.setValue(startTimeStr);
		}
		else // build start and end date
		{
			List<String> dateList = new LinkedList<>();
			dateList.add(startTimeStr);
			dateList.add(endTimeStr);
			obr22.setStringValue(dateList);
		}
		return obr22;
	}

	private static ZonedDateTime calcEndDate(@NotNull ZonedDateTime zonedStartTime)
	{
		ZonedDateTime maxFetchPeriod = ZonedDateTime.now().minusMonths(MAX_FETCH_PERIOD_MONTHS);
		if(zonedStartTime.isBefore(maxFetchPeriod))
		{
			return zonedStartTime.plusMonths(MAX_FETCH_PERIOD_MONTHS);
		}
		return null;
	}
}
