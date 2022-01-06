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

package org.oscarehr.ws.rest.demographic;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.contact.transfer.DemographicContactFewTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("demographic/{demographicId}/contact/")
@Component("DemographicContactWebService")
@Tag(name = "demographic")
public class DemographicContactWebService extends AbstractServiceImpl
{
    protected SecurityInfoManager securityInfoManager;
    protected DemographicManager demographicManager;

    // ==========================================================================
	// Public Methods
	// ==========================================================================

    @Autowired
	public DemographicContactWebService(
			SecurityInfoManager securityInfoManager,
            DemographicManager demographicManager)
	{
		this.securityInfoManager = securityInfoManager;
		this.demographicManager = demographicManager;
	}

    // ==========================================================================
	// Endpoints
	// ==========================================================================

    @PUT
	@Path("/{contactId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<DemographicContactFewTo1> updateExternalContact(@PathParam("demographicId") Integer demographicId,
																		@PathParam("contactId") String contactId,
																		DemographicContactFewTo1 demographicContactFewTo1)
	{
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), demographicId, Permission.DEMOGRAPHIC_READ);

		DemographicContactFewTo1 updatedContact = demographicManager.updateExternalContact(demographicContactFewTo1, contactId, demographicId);

		LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(), demographicId, LogConst.ACTION_UPDATE, LogConst.CON_DEMOGRAPHIC_CONTACT, LogConst.STATUS_SUCCESS,
					demographicContactFewTo1.getContactId(), getHttpServletRequest().getRemoteAddr());
		return RestResponse.successResponse(updatedContact);
	}
}
