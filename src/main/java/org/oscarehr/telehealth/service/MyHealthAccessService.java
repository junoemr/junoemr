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

package org.oscarehr.telehealth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Security;
import org.oscarehr.config.JunoProperties;
import org.oscarehr.integration.dao.UserIntegrationAccessDao;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.model.IntegrationData;
import org.oscarehr.integration.model.UserIntegrationAccess;
import org.oscarehr.integration.myhealthaccess.dto.AppointmentCacheTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicStatusResponseTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserCreateResponseTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserCreateTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserLoginTokenTo1;
import org.oscarehr.integration.myhealthaccess.exception.InvalidIntegrationException;
import org.oscarehr.integration.myhealthaccess.service.AppointmentService;
import org.oscarehr.integration.myhealthaccess.service.ClinicService;
import org.oscarehr.integration.service.IntegrationPushUpdateService;
import org.oscarehr.integration.service.IntegrationService;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


@Service("telehealth.service.MyHealthAccessService")
@Transactional
public class MyHealthAccessService
{
	@Autowired
	ProviderDataDao providerDataDao;

	@Autowired
	IntegrationService integrationService;

	@Autowired
	ClinicService clinicService;

	@Autowired
	@Qualifier("myHealthAppointmentService")
	AppointmentService appointmentService;

	@Autowired
	UserIntegrationAccessDao userIntegrationAccessDao;

	@Autowired
	IntegrationPushUpdateService integrationPushUpdateService;

	private static final JunoProperties junoProps = SpringUtils.getBean(JunoProperties.class);

	protected static OscarProperties oscarProps = OscarProperties.getInstance();
	protected static final String MHA_DOMAIN = junoProps.getMyhealthaccess().getMyhealthaccessDomain();
	protected static final String CLOUD_MD_DOMAIN = junoProps.getMyhealthaccess().getCloudmdDomain();
	public static final String MHA_HOME_URL = "/clinic/home";
	public static final String MHA_BASE_TELEHEALTH_URL = "/patient/#/clinic/%s/telehealth/appointment/%s";
	public static final String MHA_BASE_AQS_TELEHEALTH_URL = "/patient/#/clinic_user/aqs/queue/%s/queued_appointment/%s/session";
	public static final String MHA_OD_AUDIO_CALL_URL = "/patient/#/clinic_user/appointments/%s/audio/session";

	public String getTelehealthUrlForAppointment(IntegrationData integrationData, String appointmentNo)
	{
		return getSSORedirectUrl(integrationData, String.format(MHA_BASE_TELEHEALTH_URL, integrationData.getIntegration().getRemoteId(), appointmentNo));
	}

	/**
	 * get SSO redirect link in to MHA. Link will automatically sign the clinic_user in and route to the specified url
	 * @param integrationData - integration data including clinic_user login token.
	 * @param redirectUrl - the url to redirect to in MHA.
	 * @return the appropriate url for the SSO redirect.
	 */
	public String getSSORedirectUrl(IntegrationData integrationData, String redirectUrl)
	{
		boolean isCloudMd = integrationData.isCloudMd();
		String hostDomain = isCloudMd ? CLOUD_MD_DOMAIN : MHA_DOMAIN;

		String clinicId = integrationData.getIntegration().getRemoteId();
		String clinicUserId = integrationData.getRemoteUserId();
		String loginToken = integrationData.getLoginToken();

		String clinicUserPath = "/clinic_users/push_token?";

		String endpoint = ClinicService.concatEndpointStrings(hostDomain, clinicUserPath);
		// Note the "#" before the token query param! This prevents the token from being submitted to the server. client side code in MHA will handle the token.
		// The token should NEVER be sent to the server.
		endpoint = clinicService.buildUrl(endpoint) + "/clinic_id=%s&user_id=%s&redirect_url=%s#token=%s";

		try
		{
			endpoint = String.format(
					endpoint,
					clinicId,
					clinicUserId,
					URLEncoder.encode(redirectUrl, "UTF-8"),
					loginToken
			);

			return endpoint;
		}
		catch (UnsupportedEncodingException e)
		{
			MiscUtils.getLogger().error("Error encoding MyHealthAccess redirect URL " + e.getMessage());
			return String.format(endpoint, clinicId, clinicUserId, redirectUrl, loginToken);
		}
	}

	public IntegrationData createClinicUser(IntegrationData integrationData, Security loggedInUser, ClinicUserCreateTo1 clinicUserTo1)
	{
		Integration integration = integrationData.getIntegration();
		ClinicUserCreateResponseTo1 response = clinicService.createClinicUser(integrationData, clinicUserTo1);

		UserIntegrationAccess integrationAccess = new UserIntegrationAccess(
				integration,
				loggedInUser,
				response.getClinicUser().getMyhealthaccessId(),
				response.getApiKey()
		);

		integrationService.updateUserIntegrationAccess(integrationAccess);

		integrationData.setUserIntegrationAccess(integrationAccess);
		integrationData.setLoginToken(response.getToken());

		return integrationData;
	}

	/**
	 * create or get user remote integration data (user integration access) &amp; loginToken.
	 * @param integrationData - integration to create the record in
	 * @param loggedInUser - the logged in user who the account is for
	 * @return existing or new integrationData depending on if the record already exists
	 */
	public IntegrationData createOrGetUserIntegrationData(IntegrationData integrationData, Security loggedInUser)
	{
		UserIntegrationAccess userAccess = userIntegrationAccessDao.findByIntegrationAndSecurity(integrationData.getIntegration(), loggedInUser);
		IntegrationData newIntegrationData = new IntegrationData(integrationData.getIntegration());

		if (userAccess != null)
		{
			newIntegrationData.setUserIntegrationAccess(userAccess);
			newIntegrationData.setLoginToken(clinicService.clinicUserLogin(newIntegrationData).getToken());
			return newIntegrationData;
		}
		else
		{
			ProviderData provider = providerDataDao.find(loggedInUser.getProviderNo());

			ClinicUserCreateTo1 clinicUser = new ClinicUserCreateTo1(
					Integer.toString(loggedInUser.getSecurityNo()),
					provider.getFirstName(),
					provider.getLastName()
			);

			return createClinicUser(newIntegrationData, loggedInUser, clinicUser);
		}
	}

	public ClinicStatusResponseTo1 testConnection(Integration integration)
	{
		return clinicService.testConnection(integration);
	}

	public IntegrationData clinicUserLogin(IntegrationData integrationData)
	{
		ClinicUserLoginTokenTo1 loginTokenTo1 = clinicService.clinicUserLogin(integrationData);
		integrationData.setLoginToken(loginTokenTo1.getToken());

		return integrationData;
	}

	public void queueAppointmentCacheUpdate(Appointment appointment)
	{
		try
		{
			String siteName = (oscarProps.isMultisiteEnabled()) ? appointment.getLocation() : null;
			addCacheEntry(getCacheTransfer(appointment), siteName);
		}
		catch(Exception e)
		{
			MiscUtils.getLogger().error("MHA Update Error", e);
		}
	}
	public void queueAppointmentCacheDelete(Appointment appointment)
	{
		try
		{
			// deleted appointments are treated as canceled when sent to MHA
			String siteName = (oscarProps.isMultisiteEnabled()) ? appointment.getLocation() : null;
			AppointmentCacheTo1 transfer = getCacheTransfer(appointment);
			transfer.setCancelled(true);
			addCacheEntry(transfer, siteName);
		}
		catch(Exception e)
		{
			MiscUtils.getLogger().error("MHA Update Error", e);
		}
	}

	private void addCacheEntry(AppointmentCacheTo1 transfer, String siteName) throws InvalidIntegrationException, JsonProcessingException
	{
		Integration integration = integrationService.findMhaIntegration(siteName);

		if (integration == null)
		{
			String noIntegrationError = InvalidIntegrationException.NO_INTEGRATION_MHA;

			if (!StringUtils.isNullOrEmpty(siteName))
			{
				noIntegrationError = String.format("%s for %s", noIntegrationError, siteName);
			}

			throw new InvalidIntegrationException(noIntegrationError);
		}

		integrationPushUpdateService.queueAppointmentCacheUpdate(integration, transfer);
	}

	private AppointmentCacheTo1 getCacheTransfer(Appointment appointment)
	{
		AppointmentCacheTo1 transfer = new AppointmentCacheTo1();
		transfer.setId(String.valueOf(appointment.getId()));
		transfer.setCancelled(appointment.getAppointmentStatus().equals(Appointment.CANCELLED));
		transfer.setVirtual(appointment.getIsVirtual());
		transfer.setStartDateTime(ConversionUtils.toZonedDateTime(appointment.getStartTimeAsFullDate()));
		transfer.setEndDateTime(ConversionUtils.toZonedDateTime(appointment.getEndTimeAsFullDate()));
		transfer.setProviderNo(appointment.getProviderNo());
		transfer.setDemographicNo(String.valueOf(appointment.getDemographicNo()));

		return transfer;
	}
}
