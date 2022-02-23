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
package org.oscarehr.ws.rest.fax;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.fax.schedulingTasks.OutboundFaxSchedulingTask;
import org.oscarehr.fax.service.OutgoingFaxService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.common.annotation.SkipContentLoggingOutbound;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.fax.transfer.FaxOutboxTransferOutbound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Path("/faxOutbound")
@Component("FaxOutboundWebService")
@Tag(name = "faxOutbound")
public class FaxOutboundWebService extends AbstractServiceImpl
{
	private static final Logger logger = Logger.getLogger(FaxOutboundWebService.class);

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private OutgoingFaxService outgoingFaxService;

	@Autowired
	private OutboundFaxSchedulingTask outboundFaxSchedulingTask;

	@PUT
	@Path("/{id}/resend")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxOutboxTransferOutbound> resend(@PathParam("id") Long id) throws IOException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FAX_UPDATE);

		return RestResponse.successResponse(outgoingFaxService.resendFax(id));
	}

	@PUT
	@Path("/{id}/notificationStatus")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxOutboxTransferOutbound> setNotificationStatus(@PathParam("id") Long id, String status)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FAX_UPDATE);

		return RestResponse.successResponse(outgoingFaxService.setNotificationStatus(id, status));
	}

	@PUT
	@Path("/{id}/archive")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxOutboxTransferOutbound> archive(@PathParam("id") Long id)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FAX_UPDATE);

		return RestResponse.successResponse(outgoingFaxService.setArchived(id, true));
	}

	@GET
	@Path("/getNextPushTime")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<LocalDateTime> getNextPushTime()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FAX_READ);

		return RestResponse.successResponse(outboundFaxSchedulingTask.getNextRunTime());
	}

	/** retrieve the associated pdf based on outgoing fax record id */
	@GET
	@Path("/{id}/download")
	@Produces("application/pdf")
	@SkipContentLoggingOutbound
	public Response download(@PathParam("id") Long id)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FAX_READ);

		FileInputStream stream = null;
		String filename = "faxed-document-" + id + ".pdf";
		try
		{
			GenericFile file = outgoingFaxService.getFile(id);
			stream = new FileInputStream(file.getFileObject());
		}
		catch(Exception e)
		{
			/* handle all exception because the return type of this is not json, and will break the normal exception mappers */
			logger.error("Error retrieving fax file", e);
		}
		Response.ResponseBuilder response = Response.ok(stream);

		response.header("Content-Disposition", "filename="+filename);
		response.type("application/pdf");
		return response.build();
	}

}
