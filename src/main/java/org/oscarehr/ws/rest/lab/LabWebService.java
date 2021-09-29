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

package org.oscarehr.ws.rest.lab;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.ProviderLabRoutingDao;
import org.oscarehr.common.model.ProviderLabRoutingModel;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.conversion.ProviderLabRoutingConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.transfer.ProviderLabRoutingTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/lab")
@Component("LabWebService")
@Produces("application/json")
@Tag(name = "lab")
public class LabWebService extends AbstractServiceImpl
{
    private static final Logger logger = MiscUtils.getLogger();

    @Autowired
    private ProviderLabRoutingDao providerLabRoutingDao;

    @Autowired
    private ProviderLabRoutingConverter converter;

    @GET
    @Path("/{labID}/provider/{providerID}/labRouting")
    public RestResponse<ProviderLabRoutingTransfer> getProviderLabRouting(@PathParam("labID") Integer labID, @PathParam("providerID") String providerID)
    {
        List<ProviderLabRoutingModel> providerLabRoutingModel = providerLabRoutingDao.findByLabNoAndLabTypeAndProviderNo(labID, ProviderLabRoutingModel.LAB_TYPE_LABS, providerID);

        ProviderLabRoutingTransfer transfer = null;

        if(!providerLabRoutingModel.isEmpty())
        {
            transfer = converter.convert(providerLabRoutingModel.get(providerLabRoutingModel.size() - 1));
        }

        return RestResponse.successResponse(transfer);
    }
}
