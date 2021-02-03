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

import org.oscarehr.common.dao.SecurityDao;
import org.oscarehr.common.model.Security;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.model.IntegrationData;
import org.oscarehr.integration.model.UserIntegrationAccess;
import org.oscarehr.integration.myhealthaccess.ErrorHandler;
import org.oscarehr.integration.myhealthaccess.client.RestClientBase;
import org.oscarehr.integration.myhealthaccess.client.RestClientFactory;
import org.oscarehr.integration.myhealthaccess.dto.ClinicStatusResponseTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserCreateResponseTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserCreateTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserLoginTokenTo1;
import org.oscarehr.integration.myhealthaccess.exception.BaseException;
import org.oscarehr.integration.myhealthaccess.exception.InvalidAccessException;
import org.oscarehr.integration.myhealthaccess.exception.InvalidIntegrationException;
import org.oscarehr.integration.myhealthaccess.exception.InvalidUserIntegrationException;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.telehealth.service.MyHealthAccessService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.oscarehr.common.model.Provider;

@Service("myHealthClinicService")
public class ClinicService extends BaseService
{
	@Autowired
	MyHealthAccessService myHealthAccessService;

	@Autowired
	SecurityDao securityDao;

	@Autowired
	ProviderDataDao providerDataDao;

	// Clinic API calls
	public ClinicUserCreateResponseTo1 createClinicUser(IntegrationData integrationData, ClinicUserCreateTo1 newUser)
	{
		String endpoint = "/clinic/%s/clinic_user/create";
		RestClientBase restClient = RestClientFactory.getRestClient(integrationData.getIntegration());

		ClinicUserCreateResponseTo1 response = null;
		String clinicId = integrationData.getIntegration().getRemoteId();

		endpoint = restClient.formatEndpoint(endpoint, clinicId);
		response = restClient.doPost(endpoint, newUser, ClinicUserCreateResponseTo1.class);

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
		Provider provider = loggedInInfo.getLoggedInProvider();
		return loginOrCreateClinicUser(loggedInInfo.getLoggedInSecurity(), provider.getFirstName(), provider.getLastName(), siteName);
	}

	/**
	 * login to or create a new clinic user
	 * @param integration - integration on which to perform this action
	 * @param securityNo - security_no of the user to login or create
	 * @return - clinic user login token
	 * @throws InvalidIntegrationException
	 */
	public ClinicUserLoginTokenTo1 loginOrCreateClinicUser(Integration integration, Integer securityNo) throws InvalidIntegrationException
	{
		Security security = securityDao.find(securityNo);
		ProviderData provider = providerDataDao.find(security.getProviderNo());
		if (security != null && provider != null)
		{
			return loginOrCreateClinicUser(security, provider.getFirstName(), provider.getLastName(),
					integration.getSite() != null ? integration.getSite().getName() : null);
		}
		else
		{
			MiscUtils.getLogger().warn("Failed to create or login to MHA clinic_user. Security lookup failed. Security No:" + securityNo);
			return null;
		}
	}

	public ClinicUserLoginTokenTo1 loginOrCreateClinicUser(Security security, String firstName,
														   String lastName, String siteName) throws InvalidIntegrationException
	{
		try
		{
			return clinicUserLogin(security, siteName);
		}
		catch(InvalidUserIntegrationException e)
		{
			IntegrationData integrationData = getIntegrationData(siteName);
			integrationData = myHealthAccessService.createClinicUser(integrationData,security,
							new ClinicUserCreateTo1(Integer.toString(security.getSecurityNo()), firstName, lastName));
			return new ClinicUserLoginTokenTo1(integrationData.getLoginToken());
		}
	}

	public ClinicUserLoginTokenTo1 clinicUserLogin(LoggedInInfo loggedInInfo, String siteName) throws InvalidIntegrationException, InvalidUserIntegrationException
	{
		return clinicUserLogin(loggedInInfo.getLoggedInSecurity(), siteName);
	}

	public ClinicUserLoginTokenTo1 clinicUserLogin(Security security, String siteName) throws InvalidIntegrationException, InvalidUserIntegrationException
	{
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
		RestClientBase restClient = RestClientFactory.getRestClient(integrationData.getIntegration());
		String endpoint = "/clinic_user/%s/api_key_login";
		String remoteUserId = integrationData.getRemoteUserId();

		ClinicUserLoginTokenTo1 loginToken = null;

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-API-Key", integrationData.getUserApiKey());
		endpoint = restClient.formatEndpoint(endpoint, remoteUserId);
		loginToken = restClient.doPost(endpoint, headers, null, ClinicUserLoginTokenTo1.class);

		return loginToken;
	}

	public ClinicStatusResponseTo1 testConnection(Integration integration)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);
		String endpoint = "/clinic/%s/test_connection";

		String clinicId = integration.getRemoteId();

		ClinicStatusResponseTo1 response = null;

		endpoint = restClient.formatEndpoint(endpoint, clinicId);
		response = restClient.doGet(endpoint, ClinicStatusResponseTo1.class);

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
