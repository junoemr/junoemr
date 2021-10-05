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
import org.apache.jcs.access.exception.InvalidArgumentException;
import org.oscarehr.common.model.DemographicContact;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.to.model.DemographicContactFewTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("demographic/{demographicId}/contacts/")
@Component("DemographicContactsWebService")
@Tag(name = "demographic")
public class DemographicContactsWebService extends AbstractServiceImpl
{
    protected SecurityInfoManager securityInfoManager;
    protected DemographicManager demographicManager;

    // ==========================================================================
    // Public Methods
    // ==========================================================================

    @Autowired
    public DemographicContactsWebService(
            SecurityInfoManager securityInfoManager,
            DemographicManager demographicManager)
    {
        this.securityInfoManager = securityInfoManager;
        this.demographicManager = demographicManager;
    }

    // ==========================================================================
    // Endpoints
    // ==========================================================================

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public RestSearchResponse<DemographicContactFewTo1> getDemographicContacts(@PathParam("demographicId") Integer demographicId,
                                                                               @QueryParam("categoryType") String categoryType) throws InvalidArgumentException
    {
        LoggedInInfo loggedInInfo = getLoggedInInfo();
        securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), demographicId, Permission.DEMOGRAPHIC_READ);

        if (!DemographicContact.ALL_CATEGORIES.contains(categoryType))
        {
            throw new InvalidArgumentException("Invalid category: " + categoryType);
        }

        List<DemographicContactFewTo1> results = demographicManager.getDemographicContactsByCategory(loggedInInfo, demographicId, categoryType);
        return RestSearchResponse.successResponseOnePage(results);
    }
}