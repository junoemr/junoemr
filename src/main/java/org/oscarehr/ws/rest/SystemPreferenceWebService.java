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
import org.oscarehr.common.model.Property;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/systemPreference")
@Component("SystemPreferenceWebService")
@Tag(name = "systemPreference")
public class SystemPreferenceWebService extends AbstractServiceImpl
{
	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private SystemPreferenceService systemPreferenceService;

	/**
	 * get the given property value
	 * @param key - the name of the property to get
	 * @param defaultValue - the default value to return if the property is not set
	 * @return - the value of the given property, or the default value if not found
	 */
	@GET
	@Path("/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<String> getPreferenceValue(@PathParam("key") String key,
	                                               @QueryParam("default") String defaultValue)
	{
		return RestResponse.successResponse(systemPreferenceService.getPreferenceValue(key, defaultValue));
	}

	/**
	 * Set the value of a property by name.
	 * This acts as a insert update because the system setting should always exist, so no POST option should offered.
	 * Since this is not always the case, the property will be persisted if there is no previous record of it.
	 * @param key - the property name to update
	 * @param value - the property value to set
	 * @return id of the property record
	 */
	@PUT
	@Path("/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<Integer> putPreferenceValue(@PathParam("key") String key,
	                                                String value)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ADMIN_UPDATE);

		Property property = systemPreferenceService.setPreferenceValue(key, value);
		return RestResponse.successResponse(property.getId());
	}

	/**
	 * check if the given property is enabled/active
	 * @param key - the name of the property to get
	 * @param defaultValue - the default state to return if the property is not set
	 * @return - true if the property is enabled, false otherwise
	 */
	@GET
	@Path("/{key}/enabled")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> getPreferenceEnabled(@PathParam("key") String key,
	                                                  @QueryParam("default") Boolean defaultValue)
	{
		return RestResponse.successResponse(systemPreferenceService.isPreferenceEnabled(key, defaultValue));
	}

	/**
	 * get the given Oscar property value
	 * @param key - the name of the Oscar property to get
	 * @param defaultValue - the default value to return if the property is not set
	 * @return - the value of the given Oscar property, or the default value if not defined in the properties file
	 */
	@GET
	@Path("/property/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<String> getPropertyValue(@PathParam("key") String key,
												 @QueryParam("default") String defaultValue)
	{
		return RestResponse.successResponse(systemPreferenceService.getPropertyValue(key, defaultValue));
	}

	/**
	 * check if the given Oscar property value is enabled/active
	 * @param key - the name of the Oscar property to get
	 * @return - true if it's enabled in the properties file, false otherwise
	 */
	@GET
	@Path("/property/{key}/enabled")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> getPropertyEnabled(@PathParam("key") String key)
	{
		return RestResponse.successResponse(systemPreferenceService.isPropertyEnabled(key));
	}
}
