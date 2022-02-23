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
package org.oscarehr.ws.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.oscarehr.fax.dao.FaxAccountDao;
import org.oscarehr.fax.dao.FaxInboundDao;
import org.oscarehr.fax.dao.FaxOutboundDao;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.provider.FaxProvider;
import org.oscarehr.fax.search.FaxAccountCriteriaSearch;
import org.oscarehr.fax.search.FaxInboundCriteriaSearch;
import org.oscarehr.fax.search.FaxOutboundCriteriaSearch;
import org.oscarehr.fax.service.FaxAccountService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.common.annotation.MaskParameter;
import org.oscarehr.ws.rest.conversion.FaxTransferConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.transfer.fax.FaxAccountTransferInbound;
import org.oscarehr.ws.rest.transfer.fax.FaxAccountTransferOutbound;
import org.oscarehr.ws.rest.transfer.fax.FaxInboxTransferOutbound;
import org.oscarehr.ws.rest.transfer.fax.FaxOutboxTransferOutbound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
public class FaxAccountWebService extends AbstractServiceImpl
{
	private static Logger logger = Logger.getLogger(FaxAccountWebService.class);

	@Autowired
	SecurityInfoManager securityInfoManager;

	@Autowired
	FaxAccountDao faxAccountDao;

	@Autowired
	FaxOutboundDao faxOutboundDao;

	@Autowired
	FaxInboundDao faxInboundDao;

	@Autowired
	FaxAccountService faxAccountService;

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

		FaxAccount faxSettings = faxAccountDao.find(id);
		return RestResponse.successResponse(faxSettings.isIntegrationEnabled());
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxAccountTransferOutbound> getAccountSettings(@PathParam("id") Long id)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_READ);

		FaxAccountTransferOutbound accountSettingsTo1 = FaxTransferConverter.getAsOutboundTransferObject(faxAccountDao.find(id));
		return RestResponse.successResponse(accountSettingsTo1);
	}

	@POST
	@Path("/")
	@MaskParameter
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxAccountTransferOutbound> addAccountSettings(FaxAccountTransferInbound accountSettingsTo1)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_CREATE);

		FaxAccount faxAccount = FaxTransferConverter.getAsDomainObject(accountSettingsTo1);
		faxAccount.setIntegrationType(FaxProvider.SRFAX);
		faxAccountDao.persist(faxAccount);

		return RestResponse.successResponse(FaxTransferConverter.getAsOutboundTransferObject(faxAccount));
	}

	@PUT
	@Path("/{id}")
	@MaskParameter
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxAccountTransferOutbound> updateAccountSettings(@PathParam("id") Long id,
																		  FaxAccountTransferInbound accountSettingsTo1)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_UPDATE);

		FaxAccount faxAccount = faxAccountDao.find(id);
		if (faxAccount == null)
		{
			throw new ResourceNotFoundException("Invalid Fax Config Id: " + id);
		}

		// keep current password if a new one is not set
		if (accountSettingsTo1.getPassword() == null || accountSettingsTo1.getPassword().trim().isEmpty())
		{
			accountSettingsTo1.setPassword(faxAccount.getLoginPassword());
		}
		faxAccount = FaxTransferConverter.getAsDomainObject(accountSettingsTo1);
		faxAccount.setIntegrationType(FaxProvider.SRFAX);// hardcoded until more than one type exists
		faxAccount.setId(id);
		faxAccountDao.merge(faxAccount);

		return RestResponse.successResponse(FaxTransferConverter.getAsOutboundTransferObject(faxAccount));
	}

	@POST
	@Path("/testConnection")
	@MaskParameter
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> testConnection(FaxAccountTransferInbound accountSettingsTo1)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_READ);
		return RestResponse.successResponse(faxAccountService.testConnectionStatus(accountSettingsTo1));
	}

	@POST
	@Path("/{id}/testConnection")
	@MaskParameter
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> testConnection(@PathParam("id") Long id,
												FaxAccountTransferInbound accountSettingsTo1)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_FAX_READ);

		// if the password is not changed, use the saved one
		String password = accountSettingsTo1.getPassword();
		String username = accountSettingsTo1.getAccountLogin();
		if (password == null || password.isEmpty())
		{
			FaxAccount faxAccount = faxAccountDao.find(id);
			password = faxAccount.getLoginPassword();
		}
		boolean success = faxAccountService.testConnectionStatus(username, password);
		return RestResponse.successResponse(success);
	}

	@GET
	@Path("/{id}/inbox")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<FaxInboxTransferOutbound> getInbox(@PathParam("id") Long id,
																 @QueryParam("page") @DefaultValue("1") Integer page,
																 @QueryParam("perPage") @DefaultValue("10") Integer perPage,
																 @QueryParam("endDate") String endDateStr,
																 @QueryParam("startDate") String startDateStr)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FAX_READ);

		page = validPageNo(page);
		perPage = limitedResultCount(perPage);
		int offset = calculatedOffset(page, perPage);

		FaxInboundCriteriaSearch criteriaSearch = new FaxInboundCriteriaSearch();
		criteriaSearch.setOffset(offset);
		criteriaSearch.setLimit(perPage);
		criteriaSearch.setFaxAccountId(id);
		criteriaSearch.setSortDirDescending();

		if (endDateStr != null)
		{
			criteriaSearch.setEndDate(ConversionUtils.toLocalDate(endDateStr));
		}
		if (startDateStr != null)
		{
			criteriaSearch.setStartDate(ConversionUtils.toLocalDate(startDateStr));
		}

		int total = faxInboundDao.criteriaSearchCount(criteriaSearch);
		List<FaxInboxTransferOutbound> inboundList = faxAccountService.getInboxResults(criteriaSearch);

		return RestSearchResponse.successResponse(inboundList, page, perPage, total);
	}

	@GET
	@Path("/{id}/outbox")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<FaxOutboxTransferOutbound> getOutbox(@PathParam("id") Long id,
																   @QueryParam("page") @DefaultValue("1") Integer page,
																   @QueryParam("perPage") @DefaultValue("10") Integer perPage,
																   @QueryParam("endDate") String endDateStr,
																   @QueryParam("startDate") String startDateStr,
																   @QueryParam("combinedStatus") String combinedStatus,
																   @QueryParam("archived") String archived)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FAX_READ);

		page = validPageNo(page);
		perPage = limitedResultCount(perPage);
		int offset = calculatedOffset(page, perPage);

		FaxOutboundCriteriaSearch criteriaSearch = new FaxOutboundCriteriaSearch();
		criteriaSearch.setLimit(perPage);
		criteriaSearch.setFaxAccountId(id);
		criteriaSearch.setSortDirDescending();

		if (endDateStr != null)
		{
			criteriaSearch.setEndDate(ConversionUtils.toLocalDate(endDateStr));
		}
		if (startDateStr != null)
		{
			criteriaSearch.setStartDate(ConversionUtils.toLocalDate(startDateStr));
		}
		if (StringUtils.trimToNull(combinedStatus) != null)
		{
			criteriaSearch.setCombinedStatus(FaxOutboxTransferOutbound.CombinedStatus.valueOf(combinedStatus));
		}
		if (StringUtils.trimToNull(archived) != null)
		{
			criteriaSearch.setArchived(Boolean.parseBoolean(archived));
		}
		criteriaSearch.setOffset(offset);
		int total = faxOutboundDao.criteriaSearchCount(criteriaSearch);

		FaxAccount faxAccount = faxAccountDao.find(id);
		List<FaxOutboxTransferOutbound> transferList = faxAccountService.getOutboxResults(faxAccount, criteriaSearch);

		return RestSearchResponse.successResponse(transferList, page, perPage, total);
	}
}