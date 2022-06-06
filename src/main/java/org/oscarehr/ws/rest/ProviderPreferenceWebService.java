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

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.managers.ProviderManager2;
import org.oscarehr.managers.model.ProviderSettings;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.AbstractSearchResponse;
import org.oscarehr.ws.rest.to.GenericRESTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;

@Component("ProviderPreferenceWebService")
@Path("/providerSettings")
@Produces("application/json")
@Tag(name = "providerPreference")
public class ProviderPreferenceWebService extends AbstractServiceImpl
{
	@Autowired
	private ProviderManager2 providerManager;

	@GET
	@Path("/all")
	public AbstractSearchResponse<ProviderSettings> getAllProviderSettings()
	{
		AbstractSearchResponse<ProviderSettings> response = new AbstractSearchResponse<>();

		ProviderSettings settings = providerManager.getProviderSettings(getLoggedInInfo().getLoggedInProviderNo());
		List<ProviderSettings> content = new ArrayList<>();
		content.add(settings);
		response.setContent(content);
		response.setTotal(1);
		return response;
	}

	@POST
	@Path("/{providerNo}/save")
	@Consumes("application/json")
	public GenericRESTResponse saveProviderSettings(@PathParam("providerNo") String providerNo,
	                                                ProviderSettings providerSettings)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.PREFERENCE_UPDATE);
		GenericRESTResponse response = new GenericRESTResponse();

		providerManager.updateProviderSettings(getLoggedInInfo(), providerNo, providerSettings);
		return response;
	}

	@GET
	@Path("/{key}")
	public RestResponse<String> getProviderSetting(@PathParam("key") String key)
	{
		String loggedInProviderId = getLoggedInProviderId();
		securityInfoManager.requireAllPrivilege(loggedInProviderId, Permission.PREFERENCE_READ);
		return RestResponse.successResponse(
				providerManager.getSingleSetting(loggedInProviderId, key));
	}

	@PUT
	@Path("/{providerNo}/{key}")
	@Consumes("application/json")
	public RestResponse<String> updateProviderSetting(@PathParam("providerNo") String providerNo,
	                                                  @PathParam("key") String key,
	                                                  String value)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.PREFERENCE_UPDATE);
		providerManager.updateSingleSetting(providerNo, key, value);
		return RestResponse.successResponse("Success");
	}
}
