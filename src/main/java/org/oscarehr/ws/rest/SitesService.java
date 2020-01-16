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

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.model.Site;
import org.oscarehr.ws.rest.conversion.SiteConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.transfer.SiteTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.time.LocalDate;
import java.util.List;

@Path("/sites")
@Component("SitesService")
@Produces("application/json")
@Tag(name = "sites")
public class SitesService extends AbstractServiceImpl
{
	@Autowired
	SiteDao siteDao;

	@GET
	public RestSearchResponse<SiteTransfer> getSiteList()
	{
		List<Site> sites = siteDao.getAllSites();

		SiteConverter converter = new SiteConverter();
		List<SiteTransfer> transferList = converter.getAllAsTransferObjects(null, sites);

		return RestSearchResponse.successResponseOnePage(transferList);
	}

	@GET
	@Path("/enabled")
	public RestResponse<Boolean> getSitesEnabled()
	{
		return RestResponse.successResponse(org.oscarehr.common.IsPropertiesOn.isMultisitesEnable());
	}

	@GET
	@Path("/provider/{providerNo}")
	public RestSearchResponse<SiteTransfer> getSitesByProvider(@PathParam("providerNo") String providerNo)
	{
		List<Site> sites = siteDao.getActiveSitesByProviderNo(providerNo);

		SiteConverter converter = new SiteConverter();
		List<SiteTransfer> transferList = converter.getAllAsTransferObjects(null, sites);

		return RestSearchResponse.successResponseOnePage(transferList);
	}

	@GET
	@Path("/provider/{providerNo}/{sdate}")
	public RestResponse<SiteTransfer> getProviderSiteBySchedule(@PathParam("providerNo") String providerNo, @PathParam("sdate") String sdate)
	{
		LocalDate sdateLocalDate = ConversionUtils.toLocalDate(sdate);
		Site site = siteDao.getProviderSiteByScheduleDate(providerNo, sdateLocalDate);

		if (site != null)
		{
			SiteConverter converter = new SiteConverter();
			return RestResponse.successResponse(converter.getAsTransferObject(null, site));
		}
		else
		{
			return RestResponse.errorResponse("No Site Assigned to provider: " + providerNo + " for day: " + sdate);
		}
	}

}
