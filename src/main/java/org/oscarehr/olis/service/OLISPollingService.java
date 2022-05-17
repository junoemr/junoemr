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
package org.oscarehr.olis.service;

import com.indivica.olis.Driver;
import com.indivica.olis.DriverResponse;
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
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.dao.ProviderLabRoutingDao;
import org.oscarehr.common.dao.SecurityDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.config.JunoProperties;
import org.oscarehr.olis.dao.OLISProviderPreferencesDao;
import org.oscarehr.olis.dao.OLISSystemPreferencesDao;
import org.oscarehr.olis.exception.OLISAckFailedException;
import org.oscarehr.olis.model.OLISProviderPreferences;
import org.oscarehr.olis.model.OLISSystemPreferences;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.oscarLab.ca.all.upload.handlers.LabHandlerService;
import oscar.util.ConversionUtils;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static oscar.oscarLab.ca.all.parsers.OLIS.OLISHL7Handler.OLIS_MESSAGE_TYPE;
import static oscar.oscarLab.ca.all.upload.handlers.OLISHL7Handler.ALL_DUPLICATES_MARKER;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class OLISPollingService implements Runnable
{
	public static final String OLIS_DATE_FORMAT = "yyyyMMddHHmmssZ";

	private static final Logger logger = MiscUtils.getLogger();
	private static final int DEFAULT_FETCH_PERIOD_MONTHS = 1;

	@Autowired
	private ProviderDataDao providerDao;

	@Autowired
	private OLISSystemPreferencesDao olisSystemPreferencesDao;

	@Autowired
	private OLISProviderPreferencesDao olisProviderPreferencesDao;

	@Autowired
	private UserPropertyDAO userPropertyDAO;

	@Autowired
	private LabHandlerService labHandlerService;

	@Autowired
	private SystemPreferenceService systemPreferenceService;

	@Autowired
	private ProviderDao legacyProviderDao;

	@Autowired
	private SecurityDao securityDao;

	@Autowired
	private JunoProperties junoProperties;

	public OLISPollingService()
	{
		super();
	}

	@Override
	public void run()
	{
		if(systemPreferenceService.isPreferenceEnabled(UserProperty.OLIS_INTEGRATION_ENABLED, false)
		&& systemPreferenceService.isPreferenceEnabled(UserProperty.OLIS_POLLING_ENABLED, false))
		{
			OLISSystemPreferences olisPrefs = olisSystemPreferencesDao.getPreferences();

			logger.info("OLIS POLLING TASK RUNNING....");
			olisPrefs.setLastRun(new Date());
			olisSystemPreferencesDao.merge(olisPrefs);

			LoggedInInfo loggedInInfo = new LoggedInInfo();
			loggedInInfo.setLoggedInProvider(legacyProviderDao.getProvider(ProviderData.SYSTEM_PROVIDER_NO));
			loggedInInfo.setLoggedInSecurity(securityDao.getByProviderNo(ProviderData.SYSTEM_PROVIDER_NO));

			// set practitioner number, as it cannot be blank in ZSH segment
			loggedInInfo.getLoggedInProvider().setPractitionerNo(ProviderData.SYSTEM_PROVIDER_NO);

			requestResults(loggedInInfo);
			logger.info("OLIS POLLING TASK COMPLETE....");
		}
	}
	
	public void requestResults(LoggedInInfo loggedInInfo)
	{
	    pollZ04Query(loggedInInfo);
	    
	    String facilityId = junoProperties.getOlis().getPollingFacilityId(); //Most of the time this will default to null.
		if(StringUtils.isNotBlank(facilityId))
		{
			pollZ06Query(loggedInInfo, facilityId);
		}
	}

	/**
	 * Query OLIS by provider
	 * @param loggedInInfo - current user info
	 */
	private void pollZ04Query(LoggedInInfo loggedInInfo)
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
				String olisId =  provider.getOlisPractitionerNo();
				
				//There is no need to query for users without this configured, it will just end in an error.
	    		if(StringUtils.isBlank(officialLastName) || StringUtils.isBlank(olisIdType) || StringUtils.isBlank(olisId))
	    		{
	    			continue;
	    		}
	    		
		    	OLISProviderPreferences olisProviderPreferences = findOrCreateOLISProviderPrefs(providerId);

				Z04Query providerQuery = new Z04Query();
				// Setting HIC for Z04 Request
			    ZRP1 zrp1 = new ZRP1(
					    olisId,
					    olisIdType,
					    ZRP1.ASSIGNING_JURISDICTION,
					    ZRP1.ASSIGNING_JURISDICTION_CODING_SYSTEM,
					    officialLastName,
					    officialFirstName,
					    officialSecondName);
				providerQuery.setRequestingHic(zrp1);

				logger.info("Query OLIS for provider " + providerId);
				Pair<ZonedDateTime, ZonedDateTime> startEnd = findStartEndTimestamps(olisProviderPreferences);
				String timeStampForNextStartDate = queryAndImportDateRange(loggedInInfo, providerQuery, startEnd.getLeft(), startEnd.getRight());
				updateProviderStartTime(olisProviderPreferences, timeStampForNextStartDate);
			}
			catch(OLISAckFailedException e)
			{
				logger.warn("Error polling OLIS for provider " + provider.getProviderNo() + ": " + e.getMessage());
			}
			catch(Exception e)
			{
				logger.error("Error polling OLIS for provider " + provider.getProviderNo(), e);
			}
		}
	}

	private void pollZ06Query(LoggedInInfo loggedInInfo, String facilityId)
	{
		try
		{
			OLISProviderPreferences olisProviderPreferences = findOrCreateOLISProviderPrefs(ProviderData.SYSTEM_PROVIDER_NO);

			Z06Query facilityQuery = new Z06Query();
			facilityQuery.setOrderingFacilityId(new ORC21(facilityId, ORC21.UNIVERSAL_ID_ISO));

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

	private OLISProviderPreferences findOrCreateOLISProviderPrefs(@NotNull String providerId)
	{
		Optional<OLISProviderPreferences> olisProviderPreferencesOptional = olisProviderPreferencesDao.findById(providerId);
		return olisProviderPreferencesOptional.orElseGet(() -> new OLISProviderPreferences(providerId));
	}

	private void updateProviderStartTime(@NotNull OLISProviderPreferences olisProviderPreferences, String timeStampForNextStartDate)
	{
		if(StringUtils.isNotBlank(timeStampForNextStartDate) && !ALL_DUPLICATES_MARKER.equals(timeStampForNextStartDate))
		{
			logger.info("set provider time slot: "+timeStampForNextStartDate);
			olisProviderPreferences.setStartTime(timeStampForNextStartDate);
		}
		olisProviderPreferencesDao.saveEntity(olisProviderPreferences);
	}

	private String queryAndImportDateRange(LoggedInInfo loggedInInfo, DateRangeQuery query, ZonedDateTime startDateTime, ZonedDateTime endDateTime) throws Exception
	{
		List<GenericFile> labTempFiles = new LinkedList<>();
		OBR22 obr22 = buildRequestStartEndTimestamp(startDateTime, endDateTime);
		query.setStartEndTimestamp(obr22);

		logger.info("Submit OLIS query for date range: " + ConversionUtils.toDateTimeString(startDateTime) +
				" to " + ((endDateTime != null) ? ConversionUtils.toDateTimeString(endDateTime) : "present"));

		String timeStampForNextStartDate = null;
		Optional<String> continuationPointerOption = Optional.empty();
		do
		{
			DriverResponse driverResponse = Driver.submitOLISQuery(loggedInInfo.getLoggedInProvider(), query, continuationPointerOption.orElse(null));
			continuationPointerOption = driverResponse.getContinuationPointer();

			if(driverResponse.hasErrors() || !driverResponse.hasHl7())
			{
				logger.error("response contains errors, aborting:\n" + String.join("\n", driverResponse.getErrors()));
				return null;
			}
			labTempFiles.add(writeLabFileTempFile(driverResponse.getHl7Response()));

		} while(continuationPointerOption.isPresent());

		Map<GenericFile, String> resultMap = new HashMap<>();
		try
		{
			for(GenericFile labTempFile : labTempFiles)
			{
				// according to OLIS the last timestamp should always be in the last result,
				// so using the result from the last file processed should be ok.
				timeStampForNextStartDate = parseAndImportResponse(loggedInInfo, labTempFile);
				resultMap.put(labTempFile, timeStampForNextStartDate);
			}
			// force recursive call attempt if response has labs but all are duplicates. Otherwise polling will get stuck.
			if(ALL_DUPLICATES_MARKER.equals(timeStampForNextStartDate))
			{
				timeStampForNextStartDate = queryAndImportNextDateRange(loggedInInfo, query, startDateTime, endDateTime);
			}
		}
		catch(OLISAckFailedException olisAckFailedException)
		{
			// if no results found, and we are not polling up to the current datetime, advance the query dates and try again
			if(olisAckFailedException.isStatusNotFound())
			{
				timeStampForNextStartDate = queryAndImportNextDateRange(loggedInInfo, query, startDateTime, endDateTime);
			}
			else
			{
				// error statuses
				throw olisAckFailedException;
			}
		}
		finally
		{
			// don't keep unused files - it will fill up the server fast
			for(GenericFile labTempFile : labTempFiles)
			{
				String timeStampStr = resultMap.get(labTempFile);
				if(timeStampStr == null || ALL_DUPLICATES_MARKER.equals(timeStampStr))
				{
					labTempFile.deleteFile();
				}
				else
				{
					labTempFile.moveToLabs();
				}
			}
		}

		return timeStampForNextStartDate;
	}

	private String queryAndImportNextDateRange(LoggedInInfo loggedInInfo, DateRangeQuery query, ZonedDateTime startDateTime, ZonedDateTime endDateTime) throws Exception
	{
		String timeStampForNextStartDate = null;
		ZonedDateTime nextStartDateTime = Optional.ofNullable(endDateTime).orElse(startDateTime.plusMonths(junoProperties.getOlis().getMaxFetchMonths()));
		if(nextStartDateTime.isBefore(ZonedDateTime.now()))
		{
			logger.info("OLIS response returned no new data, checking next date range...");
			timeStampForNextStartDate = queryAndImportDateRange(loggedInInfo, query, nextStartDateTime, calcEndDate(nextStartDateTime));
		}
		else
		{
			logger.info("OLIS response returned no new data, up to date");
		}
		return timeStampForNextStartDate;
	}

	private GenericFile writeLabFileTempFile(String hl7Response) throws IOException, InterruptedException
	{
		return FileFactory.createTempFile(new ByteArrayInputStream(hl7Response.getBytes(StandardCharsets.UTF_8)), "-olis-response.hl7");
	}

	private String parseAndImportResponse(LoggedInInfo loggedInInfo, GenericFile labTempFile) throws Exception
	{
		String labType = OLIS_MESSAGE_TYPE;
		String serviceName = "OLIS_HL7";
		String providerNumber = String.valueOf(ProviderLabRoutingDao.PROVIDER_UNMATCHED);
		logger.debug("Lab Type: " + labType);
		logger.debug("Lab file path: " + labTempFile.getPath());

		String timeStringForNextStartDate = labHandlerService.importLab(
				labType,
				loggedInInfo,
				serviceName,
				labTempFile.getPath(),
				providerNumber,
				null
		);

		String message = (ALL_DUPLICATES_MARKER.equals(timeStringForNextStartDate)) ? "Lab processed, only found duplicates." : "Lab successfully added.";
		logger.info(message);
		return timeStringForNextStartDate;
	}

	private Pair<ZonedDateTime, ZonedDateTime> findStartEndTimestamps(@NotNull OLISProviderPreferences olisProviderPreferences)
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

	private ZonedDateTime calcEndDate(@NotNull ZonedDateTime zonedStartTime)
	{
		ZonedDateTime maxFetchPeriod = ZonedDateTime.now().minusMonths(junoProperties.getOlis().getMaxFetchMonths());
		if(zonedStartTime.isBefore(maxFetchPeriod))
		{
			// subtract a day to avoid 'max 12 months' error response.
			// not 100% sure why OLIS sends this if the period is exactly 1 year, but probably timezones somehow
			return zonedStartTime.plusMonths(junoProperties.getOlis().getMaxFetchMonths()).minusDays(1);
		}
		return null;
	}
}
