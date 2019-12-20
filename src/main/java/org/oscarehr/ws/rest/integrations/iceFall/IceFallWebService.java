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

package org.oscarehr.ws.rest.integrations.iceFall;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.integration.iceFall.model.IceFallCredentials;
import org.oscarehr.integration.iceFall.service.IceFallRESTService;
import org.oscarehr.integration.iceFall.service.IceFallService;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallException;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallRESTException;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.integrations.iceFall.transfer.IceFallSendFormTo1;
import org.oscarehr.ws.rest.integrations.iceFall.transfer.IceFallSettingsTo1;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/integrations/iceFall")
@Component("IceFallWebService")
@Tag(name = "iceFall")
public class IceFallWebService extends AbstractServiceImpl
{
	@Autowired
	SystemPreferenceService systemPreferenceService;

	@Autowired
	IceFallService iceFallService;

	@Autowired
	IceFallRESTService iceFallRESTService;

	@Autowired
	DemographicDao demographicDao;

	@GET
	@Path("/settings")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<IceFallSettingsTo1> getIceFallSettings()
	{
		IceFallCredentials creds = iceFallService.getCredentials();

		return RestResponse.successResponse(new IceFallSettingsTo1(
						systemPreferenceService.isPreferenceEnabled(UserProperty.ICE_FALL_VISIBLE, false),
						systemPreferenceService.isPreferenceEnabled(UserProperty.ICE_FALL_INTEGRATION_ENABLED, false),
						creds.getUsername(),
						creds.getEmail()
		));
	}

	@PUT
	@Path("/settings")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<IceFallSettingsTo1> setIceFallSettings(IceFallSettingsTo1 iceFallSettingsTo1)
	{
		IceFallCredentials creds = iceFallService.getCredentials();

		iceFallSettingsTo1.updateCredentials(creds);
		iceFallService.updateCredentials(creds);

		systemPreferenceService.setPreferenceValue(UserProperty.ICE_FALL_INTEGRATION_ENABLED, iceFallSettingsTo1.getEnabled().toString());
		systemPreferenceService.setPreferenceValue(UserProperty.ICE_FALL_VISIBLE, iceFallSettingsTo1.getVisible().toString());

		return RestResponse.successResponse(iceFallSettingsTo1);
	}

	@POST
	@Path("/authenticate")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> authenticateWithIceFallApi()
	{
		return RestResponse.successResponse(true);
	}

	@POST
	@Path("/sendForm")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> sendFormToIceFall(IceFallSendFormTo1 iceFallSendFormTo1)
	{
		Provider provider = getCurrentProvider();
		Demographic demo = demographicDao.find(iceFallSendFormTo1.getDemographicNo());

		HashMap<String, String> eformValues = new HashMap<>();
		for(Map.Entry<String, String> entry : iceFallSendFormTo1.getEformValues().entrySet())
		{
			if (entry.getValue() != null && !entry.getValue().isEmpty())
			{
				eformValues.put(entry.getKey(), entry.getValue());
			}
		}

		Integer eformId = iceFallSendFormTo1.getFid();
		boolean isInstance = false;
		if (iceFallSendFormTo1.getFdid() != null)
		{
			isInstance = true;
			eformId = iceFallSendFormTo1.getFdid();
		}

		try
		{
			iceFallService.sendIceFallForm(provider, demo, eformId, isInstance, eformValues, getHttpServletRequest());
		}
		catch (IceFallException e)
		{
			MiscUtils.getLogger().error("Failed to send IceFall Prescription due to exception", e);
			return RestResponse.errorResponse(e.getUserErrorMessage(provider));
		}
		catch (IceFallRESTException e)
		{
			MiscUtils.getLogger().error("Failed to send IceFall Prescription due to REST exception", e);
			return RestResponse.errorResponse(IceFallException.getUserErrorMessage(IceFallException.USER_ERROR_MESSAGE.UNKNOWN_ERROR, provider));
		}

		//TODO change me.
		return RestResponse.successResponse(true);
	}

}
