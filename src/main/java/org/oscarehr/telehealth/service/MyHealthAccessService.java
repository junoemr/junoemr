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
import org.oscarehr.util.MiscUtils;
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
	IntegrationPushUpdateService integrationPushUpdateService;

	protected static OscarProperties oscarProps = OscarProperties.getInstance();
	protected static final String MHA_DOMAIN = oscarProps.getProperty("myhealthaccess_domain");
	protected static final String CLOUD_MD_DOMAIN = oscarProps.getProperty("cloudmd_domain");
	protected static final String MHA_HOME_URL = "/clinic/home";
	public static final String MHA_BASE_TELEHEALTH_URL = "/patient/#/clinic/%s/telehealth/appointment/%s";
	public static final String MHA_BASE_AQS_TELEHEALTH_URL = "/patient/#/clinic_user/aqs/queue/%s/queued_appointment/%s/session";

	public String getTelehealthUrl(IntegrationData integrationData, String appointmentNo)
	{
		return getTelehealthUrl(integrationData, appointmentNo, String.format(MHA_BASE_TELEHEALTH_URL, integrationData.getIntegration().getRemoteId(), appointmentNo));
	}

	// TODO: Refactor redirect from Juno to MyHealthAccess
	public String getTelehealthUrl(IntegrationData integrationData, String appointmentNo, String redirectUrl)
	{
		boolean isCloudMd = integrationData.isCloudMd();
		String hostDomain = isCloudMd ? CLOUD_MD_DOMAIN : MHA_DOMAIN;

		String clinicId = integrationData.getIntegration().getRemoteId();
		String clinicUserId = integrationData.getRemoteUserId();
		String loginToken = integrationData.getLoginToken();

		String clinicUserPath = "/clinic_users/push_token?";

		String endpoint = ClinicService.concatEndpointStrings(hostDomain, clinicUserPath);
		endpoint = clinicService.buildUrl(endpoint) + "/clinic_id=%s&user_id=%s&redirect_url=%s#token=%s";

		if (StringUtils.isNullOrEmpty(appointmentNo))
		{
			redirectUrl = MHA_HOME_URL;
		}

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
