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

package org.oscarehr.integration.myhealthaccess.service;

import org.oscarehr.common.model.Security;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.model.IntegrationData;
import org.oscarehr.integration.model.UserIntegrationAccess;
import org.oscarehr.integration.myhealthaccess.ErrorHandler;
import org.oscarehr.integration.myhealthaccess.dto.ClinicStatusResponseTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserCreateResponseTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserCreateTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserLoginTokenTo1;
import org.oscarehr.integration.myhealthaccess.exception.BaseException;
import org.oscarehr.integration.myhealthaccess.exception.InvalidAccessException;
import org.oscarehr.integration.myhealthaccess.exception.InvalidIntegrationException;
import org.oscarehr.integration.myhealthaccess.exception.InvalidUserIntegrationException;
import org.oscarehr.telehealth.service.MyHealthAccessService;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.oscarehr.common.model.Provider;

@Service
public class ClinicService extends BaseService
{
	@Autowired
	MyHealthAccessService myHealthAccessService;

	// Clinic API calls
	public ClinicUserCreateResponseTo1 createClinicUser(IntegrationData integrationData, ClinicUserCreateTo1 newUser)
	{
		String endpoint = "/clinic/%s/clinic_user/create";

		ClinicUserCreateResponseTo1 response = null;
		String apiKey = integrationData.getClinicApiKey();
		String clinicId = integrationData.getIntegration().getRemoteId();

		try
		{
			endpoint = formatEndpoint(endpoint, clinicId);
			response = post(endpoint, apiKey, newUser, ClinicUserCreateResponseTo1.class);
		}
		catch (BaseException e)
		{
			ErrorHandler.handleError(e);
		}

		return response;
	}

	/**
	 * login to or create a new clinic user
	 * @param loggedInInfo - oscar logged in info
	 * @param siteName - the site to which registration should happen.
	 * @return - the new / existing clinic user's login token.
	 * @throws InvalidIntegrationException - when no MHA integration exists for the system.
	 */
	public ClinicUserLoginTokenTo1 loginOrCreateClinicUser(LoggedInInfo loggedInInfo, String siteName) throws InvalidIntegrationException
	{
		try
		{
			return clinicUserLogin(loggedInInfo, siteName);
		}
		catch(InvalidUserIntegrationException e)
		{
			IntegrationData integrationData = getIntegrationData(siteName);
			Provider provider = loggedInInfo.getLoggedInProvider();
			integrationData = myHealthAccessService.createClinicUser(integrationData,loggedInInfo.getLoggedInSecurity(),
							new ClinicUserCreateTo1(Integer.toString(loggedInInfo.getLoggedInSecurity().getSecurityNo()), provider.getFirstName(), provider.getLastName()));
			return new ClinicUserLoginTokenTo1(integrationData.getLoginToken());
		}
	}

	public ClinicUserLoginTokenTo1 clinicUserLogin(LoggedInInfo loggedInInfo, String siteName) throws InvalidIntegrationException, InvalidUserIntegrationException
	{
		Security security = loggedInInfo.getLoggedInSecurity();

		IntegrationData integrationData = getIntegrationData(siteName);

		UserIntegrationAccess userIntegrationAccess = integrationService.findMhaUserAccessBySecurityAndSiteName(security, siteName);
		if (userIntegrationAccess == null)
		{
			throw new InvalidUserIntegrationException("no user integration record for security record: [" + security + "] ");
		}
		integrationData.setUserIntegrationAccess(userIntegrationAccess);

		return clinicUserLogin(integrationData);
	}

	public ClinicUserLoginTokenTo1 clinicUserLogin(IntegrationData integrationData) throws InvalidAccessException
	{
		String endpoint = "/clinic_user/%s/api_key_login";

		String apiKey = integrationData.getUserApiKey();
		String remoteUserId = integrationData.getRemoteUserId();

		ClinicUserLoginTokenTo1 loginToken = null;

		try
		{
			endpoint = formatEndpoint(endpoint, remoteUserId);
			loginToken = post(endpoint, apiKey, null, ClinicUserLoginTokenTo1.class);
		}
		catch (BaseException e)
		{
			ErrorHandler.handleError(e);
		}

		return loginToken;
	}

	public ClinicStatusResponseTo1 testConnection(Integration integration)
	{
		String endpoint = "/clinic/%s/test_connection";

		String apiKey = integration.getApiKey();
		String clinicId = integration.getRemoteId();

		ClinicStatusResponseTo1 response = null;

		try
		{
			endpoint = formatEndpoint(endpoint, clinicId);
			response = get(endpoint, apiKey, ClinicStatusResponseTo1.class);
		}
		catch (BaseException e)
		{
			ErrorHandler.handleError(e);
		}

		return response;
	}

	protected IntegrationData getIntegrationData(String siteName) throws InvalidIntegrationException
	{
		Integration integration = integrationService.findMhaIntegration(siteName);
		if (integration == null)
		{
			throw new InvalidIntegrationException("no integration record for site: [" + siteName + "]");
		}
		return new IntegrationData(integration);
	}
}
