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
import org.oscarehr.fax.dao.FaxOutboundDao;
import org.oscarehr.fax.model.FaxNotificationStatus;
import org.oscarehr.fax.model.FaxStatusCombined;
import org.oscarehr.fax.schedulingTasks.OutboundFaxSchedulingTask;
import org.oscarehr.fax.search.FaxOutboundCriteriaSearch;
import org.oscarehr.fax.service.FaxAccountService;
import org.oscarehr.fax.service.FaxUploadService;
import org.oscarehr.fax.transfer.FaxOutboxTransferOutbound;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.common.annotation.SkipContentLoggingOutbound;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Path("/faxOutbound")
@Component("FaxOutboundWebService")
@Tag(name = "faxOutbound")
public class FaxOutboundWebService extends AbstractServiceImpl
{
	private static final Logger logger = Logger.getLogger(FaxOutboundWebService.class);

	@Autowired
	private FaxUploadService faxUploadService;

	@Autowired
	private OutboundFaxSchedulingTask outboundFaxSchedulingTask;

	@Autowired
	private FaxOutboundDao faxOutboundDao;

	@Autowired
	private FaxAccountService faxAccountService;

	@PUT
	@Path("/{id}/resend")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxOutboxTransferOutbound> resend(@PathParam("id") Long id) throws IOException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FAX_UPDATE);

		return RestResponse.successResponse(faxUploadService.resendFax(id));
	}

	@PUT
	@Path("/{id}/notificationStatus")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxOutboxTransferOutbound> setNotificationStatus(@PathParam("id") Long id, FaxNotificationStatus status)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FAX_UPDATE);

		return RestResponse.successResponse(faxUploadService.setNotificationStatus(id, status));
	}

	@PUT
	@Path("/{id}/archive")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxOutboxTransferOutbound> archive(@PathParam("id") Long id)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FAX_UPDATE);

		return RestResponse.successResponse(faxUploadService.setArchived(id, true));
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
			GenericFile file = faxUploadService.getFile(id);
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

	@GET
	@Path("/getNextPushTime")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<ZonedDateTime> getNextPushTime()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FAX_READ);

		return RestResponse.successResponse(outboundFaxSchedulingTask.getNextRunTime());
	}

	@GET
	@Path("/outbox")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<FaxOutboxTransferOutbound> getOutbox(@QueryParam("page") @DefaultValue("1") Integer page,
	                                                               @QueryParam("perPage") @DefaultValue("10") Integer perPage,
	                                                               @QueryParam("endDate") LocalDate endDate,
	                                                               @QueryParam("startDate") LocalDate startDate,
	                                                               @QueryParam("accountId") Long accountId,
	                                                               @QueryParam("combinedStatus") FaxStatusCombined combinedStatus,
	                                                               @QueryParam("archived") Boolean archived)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FAX_READ);

		page = validPageNo(page);
		perPage = limitedResultCount(perPage);
		int offset = calculatedOffset(page, perPage);

		FaxOutboundCriteriaSearch criteriaSearch = new FaxOutboundCriteriaSearch();
		criteriaSearch.setLimit(perPage);
		criteriaSearch.setSortDirDescending();

		if (endDate != null)
		{
			criteriaSearch.setEndDate(endDate);
		}
		if (startDate != null)
		{
			criteriaSearch.setStartDate(startDate);
		}
		if (combinedStatus != null)
		{
			criteriaSearch.setCombinedStatus(combinedStatus);
		}
		if (archived != null)
		{
			criteriaSearch.setArchived(archived);
		}
		if (accountId != null)
		{
			criteriaSearch.setFaxAccountId(accountId);
		}
		criteriaSearch.setOffset(offset);

		List<FaxOutboxTransferOutbound> transferList = faxAccountService.getOutboxResults(criteriaSearch);
		int total = faxOutboundDao.criteriaSearchCount(criteriaSearch);

		return RestSearchResponse.successResponse(transferList, page, perPage, total);
	}

}