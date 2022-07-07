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
import org.oscarehr.fax.dao.FaxInboundDao;
import org.oscarehr.fax.schedulingTasks.InboundFaxSchedulingTask;
import org.oscarehr.fax.search.FaxInboundCriteriaSearch;
import org.oscarehr.fax.service.FaxAccountService;
import org.oscarehr.fax.transfer.FaxInboxTransferOutbound;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Path("/faxInbound")
@Component("FaxInboundWebService")
@Tag(name = "faxInbound")
public class FaxInboundWebService extends AbstractServiceImpl
{
	@Autowired
	private InboundFaxSchedulingTask inboundFaxSchedulingTask;

	@Autowired
	private FaxInboundDao faxInboundDao;

	@Autowired
	private FaxAccountService faxAccountService;

	@GET
	@Path("/getNextPullTime")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<ZonedDateTime> getNextPullTime()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FAX_READ);

		return RestResponse.successResponse(inboundFaxSchedulingTask.getNextRunTime());
	}

	@GET
	@Path("/inbox")
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<FaxInboxTransferOutbound> getInbox(@QueryParam("page") @DefaultValue("1") Integer page,
	                                                             @QueryParam("perPage") @DefaultValue("10") Integer perPage,
	                                                             @QueryParam("endDate") LocalDate endDate,
	                                                             @QueryParam("startDate") LocalDate startDate,
	                                                             @QueryParam("accountId") Long accountId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FAX_READ);

		page = validPageNo(page);
		perPage = limitedResultCount(perPage);
		int offset = calculatedOffset(page, perPage);

		FaxInboundCriteriaSearch criteriaSearch = new FaxInboundCriteriaSearch();
		criteriaSearch.setOffset(offset);
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
		if(accountId != null)
		{
			criteriaSearch.setFaxAccountId(accountId);
		}

		int total = faxInboundDao.criteriaSearchCount(criteriaSearch);
		List<FaxInboxTransferOutbound> inboundList = faxAccountService.getInboxResults(criteriaSearch);

		return RestSearchResponse.successResponse(inboundList, page, perPage, total);
	}

}
