/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.ws.rest;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.dao.EFormReportToolDao;
import org.oscarehr.common.model.DemographicSets;
import org.oscarehr.common.model.EFormReportTool;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.managers.DemographicSetsManager;
import org.oscarehr.managers.EFormReportToolManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.conversion.EFormReportToolConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.AbstractSearchResponse;
import org.oscarehr.ws.rest.to.GenericRESTResponse;
import org.oscarehr.ws.rest.to.model.EFormReportToolTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/reporting/")
@Component
public class ReportingService extends AbstractServiceImpl {
	
	@Autowired
	DemographicSetsManager demographicSetsManager;
	
	@Autowired
	DemographicManager demographicManager;
	
	@Autowired
	EFormReportToolManager eformReportToolManager;
	
	@GET
	@Path("/demographicSets/list")
	@Produces("application/json")
	public RestResponse<AbstractSearchResponse<String>> listDemographicSets()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.REPORT_READ);

		AbstractSearchResponse<String> response = new AbstractSearchResponse<String>();
		
		response.setContent(demographicSetsManager.getNames(getLoggedInInfo()));
		response.setTotal(response.getContent().size());
		
		return (RestResponse.successResponse(response));
	}
	
	@GET
	@Path("/demographicSets/demographicSet/{name}")
	@Produces("application/json")
	public AbstractSearchResponse<DemographicSets> getDemographicSetByName(@PathParam("name") String name)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.REPORT_READ);

		AbstractSearchResponse<DemographicSets> response = new AbstractSearchResponse<DemographicSets>();
		
		response.setContent(demographicSetsManager.getByName(getLoggedInInfo(), name));
		response.setTotal(response.getContent().size());
		
		return (response);
	}
		
	/**
	 * EFromReportTool is a utility for taking a snapshot of key-value pair data from saved eforms
	 * to a new table for easier querying. need _admin.eformreporttool security object.
	 * @return AbstractSearchResponse
	 */
	@GET
	@Path("/eformReportTool/list")
	@Produces("application/json")
	public AbstractSearchResponse<EFormReportToolTo1> eformReportToolList()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.REPORT_READ);
		
		List<EFormReportTool> results = eformReportToolManager.findAll(getLoggedInInfo(), 0, EFormReportToolDao.MAX_LIST_RETURN_SIZE);
		
		EFormReportToolConverter converter = new EFormReportToolConverter(true, true);
		
		AbstractSearchResponse<EFormReportToolTo1> response = new AbstractSearchResponse<EFormReportToolTo1>();
		
		response.setContent(converter.getAllAsTransferObjects(getLoggedInInfo(), results));
		response.setTotal(response.getContent().size());
		
		return (response);
	}
	
	
	@POST
	@Path("/eformReportTool/add")
	@Produces("application/json")
	@Consumes("application/json")
	public GenericRESTResponse addEFormReportTool(EFormReportToolTo1 json)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.REPORT_CREATE);

		GenericRESTResponse response = new GenericRESTResponse();
		
		if(StringUtils.isEmpty(json.getName()) || json.getEformId() == 0) {
			response.setSuccess(false);
			response.setMessage("Need required fields");
			return response;
		}
		
		EFormReportToolConverter converter = new EFormReportToolConverter();
		
		eformReportToolManager.addNew(getLoggedInInfo(),converter.getAsDomainObject(getLoggedInInfo(),json));
		
		return (response);
	}
	
	@POST
	@Path("/eformReportTool/populate")
	@Produces("application/json")
	@Consumes("application/json")
	public GenericRESTResponse populateEFormReportTool(EFormReportToolTo1 json)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.REPORT_CREATE);
		
		GenericRESTResponse response = new GenericRESTResponse();
		
		eformReportToolManager.populateReportTable(getLoggedInInfo(), json.getId());
		
		return (response);
	}
	
	
	@POST
	@Path("/eformReportTool/remove")
	@Produces("application/json")
	@Consumes("application/json")
	public GenericRESTResponse removeEFormReportTool(EFormReportToolTo1 json)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.REPORT_DELETE);
		
		GenericRESTResponse response = new GenericRESTResponse();
		
		eformReportToolManager.remove(getLoggedInInfo(), json.getId());
		
		return (response);
	}
	
	@POST
	@Path("/eformReportTool/markLatest")
	@Produces("application/json")
	@Consumes("application/json")
	public GenericRESTResponse markLatestEFormReportTool(EFormReportToolTo1 json)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.REPORT_UPDATE);
		
		GenericRESTResponse response = new GenericRESTResponse();
		
		eformReportToolManager.markLatest(getLoggedInInfo(), json.getId());
		
		return (response);
	}
}
