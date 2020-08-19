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
package org.oscarehr.integration.aqs.service;

import ca.cloudpractice.aqs.client.ApiClient;
import ca.cloudpractice.aqs.client.Configuration;
import ca.cloudpractice.aqs.client.api.AdminApi;
import ca.cloudpractice.aqs.client.api.OrganizationApi;
import ca.cloudpractice.aqs.client.auth.ApiKeyAuth;
import ca.cloudpractice.aqs.client.model.RemoteUserType;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import oscar.OscarProperties;

@Service
public abstract class BaseService extends org.oscarehr.integration.BaseService
{
	protected static OscarProperties oscarProps = OscarProperties.getInstance();
	protected final String AQS_PROTOCOL = oscarProps.getProperty("aqs_protocol");
	protected final String AQS_DOMAIN = oscarProps.getProperty("aqs_domain");
	protected final String BASE_API_URI = oscarProps.getProperty("aqs_api_uri");
	protected final String BASE_END_POINT = AQS_DOMAIN + BASE_API_URI;

	protected final String AQS_AUTH_REMOTE_INTEGRATION_ID = "RemoteIntegrationId";
	protected final String AQS_AUTH_REMOTE_USER_ID = "RemoteUserId";
	protected final String AQS_AUTH_REMOTE_USER_TYPE = "RemoteUserType";
	protected final String AQS_AUTH_REMOTE_ORG_SECRET = "OrgSecret";

	protected final String AQS_PROPERTY_API_SECRET_KEY = "aqs_api_secret_key";

	protected ApiClient apiClient;

	public BaseService()
	{
		//setup api client
		this.apiClient = Configuration.getDefaultApiClient();
		apiClient.setBasePath(AQS_PROTOCOL + "://" + BASE_END_POINT);
		((ApiKeyAuth) apiClient.getAuthentication(AQS_AUTH_REMOTE_INTEGRATION_ID)).setApiKey(OscarProperties.getInstance().getProperty("project_home"));
		((ApiKeyAuth) apiClient.getAuthentication(AQS_AUTH_REMOTE_USER_TYPE)).setApiKey(RemoteUserType.JUNO_SECURITY.name());

		// autowire not available in constructor of abstract class
		UserPropertyDAO userPropertyDao = SpringUtils.getBean(UserPropertyDAO.class);
		if (userPropertyDao.getProp(AQS_PROPERTY_API_SECRET_KEY) != null)
		{
			((ApiKeyAuth) apiClient.getAuthentication(AQS_AUTH_REMOTE_ORG_SECRET)).setApiKey(userPropertyDao.getProp(AQS_PROPERTY_API_SECRET_KEY).getValue());
		}
		else
		{
			throw new RuntimeException("The property [" + AQS_PROPERTY_API_SECRET_KEY + "] is not set in the [property] table! " +
			                           "AQS integration will not function until corrected!");
		}
	}

	/**
	 * get the organization api.
	 * @param securityNo - security no of the user performing the action
	 * @return - the organization api
	 */
	public OrganizationApi getOrganizationApi(Integer securityNo)
	{
		((ApiKeyAuth) apiClient.getAuthentication(AQS_AUTH_REMOTE_USER_ID)).setApiKey(securityNo.toString());
		return new OrganizationApi(apiClient);
	}

	/**
	 * get the admin api
	 * @param securityNo - security no of the user performing the action
	 * @return - the admin api
	 */
	public AdminApi getAdminApi(Integer securityNo)
	{
		((ApiKeyAuth) apiClient.getAuthentication(AQS_AUTH_REMOTE_USER_ID)).setApiKey(securityNo.toString());
		return new AdminApi(apiClient);
	}
}
