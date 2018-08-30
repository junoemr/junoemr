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

import org.apache.log4j.Logger;
import org.oscarehr.fax.dao.FaxConfigDao;
import org.oscarehr.fax.model.FaxConfig;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.ws.rest.conversion.FaxSettingsConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.transfer.fax.FaxSettingsTransferInbound;
import org.oscarehr.ws.rest.transfer.fax.FaxSettingsTransferOutbound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@Path("/faxConfig")
@Component("FaxConfigWebService")
public class FaxConfigWebService extends AbstractServiceImpl
{
	private static Logger logger = Logger.getLogger(FaxConfigWebService.class);

	@Autowired
	SecurityInfoManager securityInfoManager;

	@Autowired
	FaxConfigDao faxConfigDao;

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<FaxSettingsTransferOutbound> listAccounts(@QueryParam("page")
	                                                                    @DefaultValue("1")
			                                                                    Integer page,
	                                                                    @QueryParam("perPage")
	                                                                    @DefaultValue("10")
			                                                                    Integer perPage)
	{
		String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, SecurityInfoManager.READ, null, "_admin");

		page = validPageNo(page);
		perPage = limitedResultCount(perPage);
		int offset = calculatedOffset(page, perPage);

		List<FaxConfig> configList = faxConfigDao.findAll(offset, perPage);

		return RestSearchResponse.successResponse(FaxSettingsConverter.getAllAsOutboundTransferObject(configList), page, perPage, -1);
	}

	@GET
	@Path("/{id}/enabled")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> isEnabled(@PathParam("id") Integer id)
	{
		String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, SecurityInfoManager.READ, null, "_admin");

		FaxConfig config = faxConfigDao.find(id);
		return RestResponse.successResponse(config.isActive());
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxSettingsTransferOutbound> getAccountSettings(@PathParam("id") Integer id)
	{
		String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, SecurityInfoManager.READ, null, "_admin");

		FaxSettingsTransferOutbound accountSettingsTo1 = FaxSettingsConverter.getAsOutboundTransferObject(faxConfigDao.find(id));
		return RestResponse.successResponse(accountSettingsTo1);
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxSettingsTransferOutbound> addAccountSettings(FaxSettingsTransferInbound accountSettingsTo1)
	{
		String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, SecurityInfoManager.WRITE, null, "_admin");

		FaxConfig config = FaxSettingsConverter.getAsDomainObject(accountSettingsTo1);
		faxConfigDao.persist(config);

		return RestResponse.successResponse(FaxSettingsConverter.getAsOutboundTransferObject(config));
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<FaxSettingsTransferOutbound> updateAccountSettings(@PathParam("id") Integer id,
	                                                                       FaxSettingsTransferInbound accountSettingsTo1)
	{
		String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, SecurityInfoManager.WRITE, null, "_admin");

		FaxConfig config = faxConfigDao.find(id);
		if(config == null)
		{
			throw new RuntimeException("Invalid Fax Config Id: " + id);
		}
		config = FaxSettingsConverter.getAsDomainObject(accountSettingsTo1);
		config.setId(id);
		faxConfigDao.merge(config);
		return RestResponse.successResponse(FaxSettingsConverter.getAsOutboundTransferObject(config));
	}

	@POST
	@Path("/testConnection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> testConnection(FaxSettingsTransferInbound accountSettingsTo1)
	{
		String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, SecurityInfoManager.READ, null, "_admin");

		return RestResponse.successResponse(false);
	}
}
