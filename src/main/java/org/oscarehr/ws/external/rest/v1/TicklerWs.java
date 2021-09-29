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

package org.oscarehr.ws.external.rest.v1;

import io.swagger.v3.oas.annotations.Operation;
import org.oscarehr.common.dao.TicklerDao;
import org.oscarehr.common.model.Tickler;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ticklers.service.TicklerService;
import org.oscarehr.ws.external.rest.AbstractExternalRestWs;
import org.oscarehr.ws.external.rest.v1.transfer.tickler.TicklerTransferInbound;
import org.oscarehr.ws.external.rest.v1.transfer.tickler.TicklerTransferOutbound;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component("TicklerWs")
@Path("/tickler")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicklerWs extends AbstractExternalRestWs
{
	@Autowired
	TicklerDao ticklerDao;

	@Autowired
	TicklerService ticklerService;

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@GET
	@Path("/{id}")
	@Operation(summary = "Retrieve a tickler by id")
	public RestResponse<TicklerTransferOutbound> getTickler(@PathParam("id") Integer id)
	{
		securityInfoManager.requireAllPrivilege(getOAuthProviderNo(), Permission.TICKLER_READ);

		if (id == null)
		{
			throw new ValidationException("Tickler id cannot be null");
		}

		Tickler tickler = ticklerDao.find(id);
		if (tickler == null)
		{
			throw new ValidationException("Tickler for id [" + id + "] not found");
		}

		return RestResponse.successResponse(new TicklerTransferOutbound(tickler));
	}


	@PUT
	@Path("/{id}")
	@Operation(summary = "Update the specified tickler")
	public RestResponse<TicklerTransferOutbound> updateTickler(@PathParam("id") Integer id, @Valid TicklerTransferInbound ticklerIn)
	{
		securityInfoManager.requireAllPrivilege(getOAuthProviderNo(), Permission.TICKLER_UPDATE);

		if (ticklerIn == null)
		{
			throw new ValidationException("Tickler definition required");
		}
		if (id == null)
		{
			throw new ValidationException("Tickler id cannot be null");
		}

		Tickler tickler = ticklerDao.find(id);
		if(tickler == null)
		{
			throw new ValidationException("Tickler for id [" + id + "] not found");
		}

		tickler = ticklerIn.copyToTickler(tickler);
		ticklerService.updateTickler(tickler);
		return RestResponse.successResponse(new TicklerTransferOutbound(tickler));

	}

	@POST
	@Path("/")
	@Operation(summary="Create a new tickler")
	public RestResponse<TicklerTransferOutbound> createTickler( @Valid TicklerTransferInbound ticklerIn)
	{
		securityInfoManager.requireAllPrivilege(getOAuthProviderNo(), Permission.TICKLER_CREATE);

		if (ticklerIn == null)
		{
			throw new ValidationException("Tickler definition required");
		}

		Tickler tickler = ticklerIn.toTickler();

		if (ticklerIn.getCreator() == null)
		{
			tickler.setCreator(getOAuthProviderNo());
		}

		ticklerService.createTickler(tickler);
		return RestResponse.successResponse(new TicklerTransferOutbound(tickler));
	}
}
