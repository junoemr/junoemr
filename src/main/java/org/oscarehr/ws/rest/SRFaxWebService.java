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
package org.oscarehr.ws.rest;

import org.apache.log4j.Logger;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.model.SRFaxAccountSettingsTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/srfax")
@Component("SRFaxWebService")
public class SRFaxWebService extends AbstractServiceImpl
{
	private static Logger logger = Logger.getLogger(SRFaxWebService.class);

	@Autowired
	SecurityInfoManager securityInfoManager;

	@GET
	@Path("/enabled")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> isEnabled()
	{
		String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, SecurityInfoManager.READ, null, "_admin");

		return RestResponse.successResponse(false);
	}

	@GET
	@Path("/account")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<SRFaxAccountSettingsTo1> getAccountSettings()
	{
		String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, SecurityInfoManager.READ, null, "_admin");

		SRFaxAccountSettingsTo1 settings = new SRFaxAccountSettingsTo1();
		settings.setEnabled(false);
		settings.setAccountLogin("test value");

		return RestResponse.successResponse(settings);
	}

	@POST
	@Path("/account")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<SRFaxAccountSettingsTo1> setAccountSettings(SRFaxAccountSettingsTo1 accountSettingsTo1)
	{
		String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, SecurityInfoManager.WRITE, null, "_admin");

		accountSettingsTo1.setPassword(null);
		return RestResponse.successResponse(accountSettingsTo1);
	}

	@POST
	@Path("/testConnection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> testConnection(SRFaxAccountSettingsTo1 accountSettingsTo1)
	{
		String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, SecurityInfoManager.READ, null, "_admin");

		return RestResponse.successResponse(false);
	}
}
