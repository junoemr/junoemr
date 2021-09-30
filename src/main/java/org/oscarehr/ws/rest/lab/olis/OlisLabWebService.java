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
import org.apache.log4j.Logger;
import org.oscarehr.olis.service.OLISConfigService;
import org.oscarehr.olis.transfer.OLISProviderSettingsTransfer;
import org.oscarehr.olis.transfer.OLISSystemSettingsTransfer;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/olis")
@Component("OlisLabWebService")
@Produces("application/json")
@Tag(name = "lab")
public class OlisLabWebService extends AbstractServiceImpl
{
    private static final Logger logger = MiscUtils.getLogger();

    @Autowired
    private OLISConfigService olisConfigService;

    @GET
    @Path("/config/system")
    public RestResponse<OLISSystemSettingsTransfer> getOlisSystemSettings()
    {
        securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.LAB_READ);
        return RestResponse.successResponse(olisConfigService.getOlisSystemSettings());
    }

    @GET
    @Path("/config/providers")
    public RestSearchResponse<OLISProviderSettingsTransfer> getOlisProviderSettings()
    {
        securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.LAB_READ);
        return RestSearchResponse.successResponseOnePage(olisConfigService.getAllProviderSettings());
    }
}
