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
import org.oscarehr.fax.dao.FaxAccountDao;
import org.oscarehr.fax.model.FaxAccountConnectionStatus;
import org.oscarehr.fax.search.FaxAccountCriteriaSearch;
import org.oscarehr.fax.service.FaxAccountService;
import org.oscarehr.fax.transfer.FaxAccountCreateInput;
import org.oscarehr.fax.transfer.FaxAccountTransferOutbound;
import org.oscarehr.fax.transfer.FaxAccountUpdateInput;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.common.annotation.MaskParameter;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/faxAccount")
@Component("FaxAccountWebService")
@Tag(name = "faxAccount")
public class FaxAccountWebService extends AbstractServiceImpl
{
	@Autowired
	private FaxAccountDao faxAccountDao;

	@Autowired
	private FaxAccountService faxAccountService;

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<FaxAccountTransferOutbound> listAccounts(@QueryParam("page")
																	   @DefaultValue("1")
																			   Integer page,
																	   @QueryParam("perPage")
																	   @DefaultValue("10")
																			   Integer perPage)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_READ);

		page = validPageNo(page);
		perPage = limitedResultCount(perPage);
		int offset = calculatedOffset(page, perPage);

		FaxAccountCriteriaSearch criteriaSearch = new FaxAccountCriteriaSearch();
		criteriaSearch.setOffset(offset);
		criteriaSearch.setLimit(perPage);
		criteriaSearch.setSortDirAscending();

		int total = faxAccountDao.criteriaSearchCount(criteriaSearch);
		List<FaxAccountTransferOutbound> accountList = faxAccountService.listAccounts(criteriaSearch);
		return RestSearchResponse.successResponse(accountList, page, perPage, total);
	}

	@GET
	@Path("/{id}/enabled")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> isEnabled(@PathParam("id") Long id)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_READ);
		return RestResponse.successResponse(faxAccountService.isFaxAccountEnabled(id));
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxAccountTransferOutbound> getAccountSettings(@PathParam("id") Long id)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_READ);
		return RestResponse.successResponse(faxAccountService.getFaxAccount(id));
	}

	@POST
	@Path("/")
	@MaskParameter
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxAccountTransferOutbound> createAccountSettings(FaxAccountCreateInput createInput)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_CREATE);
		return RestResponse.successResponse(faxAccountService.createFaxAccount(createInput));
	}

	@PUT
	@Path("/{id}")
	@MaskParameter
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxAccountTransferOutbound> updateAccountSettings(@PathParam("id") Long id,
	                                                                      FaxAccountUpdateInput updateInput)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_UPDATE);
		return RestResponse.successResponse(faxAccountService.updateFaxAccount(updateInput));
	}

	@DELETE
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> deleteAccountSettings(@PathParam("id") Long id)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_DELETE);
		return RestResponse.successResponse(faxAccountService.deleteFaxAccount(id));
	}

	@PATCH
	@Path("/{id}/disconnect")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> disconnectAccountSettings(@PathParam("id") Long id)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_UPDATE);
		return RestResponse.successResponse(faxAccountService.disconnectFaxAccount(id));
	}

	@POST
	@Path("/testConnection")
	@MaskParameter
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxAccountConnectionStatus> testFaxConnection(FaxAccountCreateInput createInput)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_READ);
		return RestResponse.successResponse(faxAccountService.testConnectionStatus(createInput));
	}

	@POST
	@Path("/{id}/testConnection")
	@MaskParameter
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxAccountConnectionStatus> testExistingFaxConnection(@PathParam("id") Long id,
	                                                       FaxAccountUpdateInput updateInput)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_READ);
		return RestResponse.successResponse(faxAccountService.testConnectionStatus(updateInput));
	}

	@GET
	@Path("/{id}/coverLetterOptions")
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<String> getCoverLetterOptions(@PathParam("id") Long id)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_READ);
		return RestSearchResponse.successResponseOnePage(faxAccountService.getAccountCoverLetterOptions(id));
	}

	@GET
	@Path("/active")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxAccountTransferOutbound> getActiveFaxAccount()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_READ);
		return RestResponse.successResponse(faxAccountService.getActiveFaxAccount(getLoggedInProviderId()));
	}
}