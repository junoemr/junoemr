/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.ws.external.rest.v1;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsDateJsonBeanProcessor;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.RestResponse;
import org.oscarehr.ws.rest.conversion.ProviderConverter;
import org.oscarehr.ws.rest.to.AbstractSearchResponse;
import org.oscarehr.ws.rest.to.model.DemographicTo1;
import org.oscarehr.ws.rest.to.model.ProviderTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

@Component("ProviderWs")
@Path("/v1/providers/")
@Produces("application/json")
public class ProvidersWs extends AbstractServiceImpl
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	ProviderDao providerDao;

	@GET
	@Path("/search")
	public RestResponse<DemographicTo1, String> search(
			@QueryParam("page") @DefaultValue("1") Integer page,
			@QueryParam("perPage") @DefaultValue("10") Integer perPage,
			@QueryParam("hin") String hin)
	{
		perPage = limitedResultCount(perPage);
		page = validPageNo(page);
		int offset = calculatedOffset(page, perPage);


		return null;
	}

	@GET
	@Path("/providers_json")
	public AbstractSearchResponse<ProviderTo1> getProvidersAsJSON() {
		JsonConfig config = new JsonConfig();
		config.registerJsonBeanProcessor(java.sql.Date.class, new JsDateJsonBeanProcessor());

		List<ProviderTo1> providers = new ProviderConverter().getAllAsTransferObjects(getLoggedInInfo(), providerDao.getActiveProviders());

		AbstractSearchResponse<ProviderTo1> response = new AbstractSearchResponse<ProviderTo1>();
		response.setContent(providers);

		return response;
	}
}
