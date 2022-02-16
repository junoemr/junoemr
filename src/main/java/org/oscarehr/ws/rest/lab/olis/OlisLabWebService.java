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

package org.oscarehr.ws.rest.lab.olis;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.olis.service.OLISConfigService;
import org.oscarehr.olis.transfer.OLISProviderSettingsTransfer;
import org.oscarehr.olis.transfer.OLISSystemSettingsTransfer;
import org.oscarehr.olis.transfer.OLISSystemSettingsUpdateInput;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/olis")
@Component("OlisLabWebService")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "lab")
public class OlisLabWebService extends AbstractServiceImpl
{
    @Autowired
    private OLISConfigService olisConfigService;

    @GET
    @Path("/config/system")
    public RestResponse<OLISSystemSettingsTransfer> getOlisSystemSettings()
    {
        securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.LAB_READ, Permission.ADMIN_READ);
        return RestResponse.successResponse(olisConfigService.getOlisSystemSettings());
    }

    @PUT
    @Path("/config/system")
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<OLISSystemSettingsTransfer> updateOlisSystemSettings(OLISSystemSettingsUpdateInput updateTransfer)
    {
        securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.LAB_UPDATE, Permission.ADMIN_UPDATE);
        return RestResponse.successResponse(olisConfigService.updateOlisSystemSettings(updateTransfer));
    }

    @GET
    @Path("/config/providers")
    public RestSearchResponse<OLISProviderSettingsTransfer> getOlisProviderSettings()
    {
        securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.LAB_READ);
        return RestSearchResponse.successResponseOnePage(olisConfigService.getAllProviderSettings());
    }
}
