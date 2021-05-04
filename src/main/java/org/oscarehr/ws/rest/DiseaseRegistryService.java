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

import org.apache.log4j.Logger;
import org.oscarehr.casemgmt.dao.IssueDAO;
import org.oscarehr.casemgmt.model.Issue;
import org.oscarehr.common.dao.DxresearchDAO;
import org.oscarehr.common.dao.QuickListDao;
import org.oscarehr.common.model.Dxresearch;
import org.oscarehr.common.model.QuickListView;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.model.DiagnosisTo1;
import org.oscarehr.ws.rest.to.model.DxQuickList;
import org.oscarehr.ws.rest.to.model.IssueTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/dxRegisty")
public class DiseaseRegistryService extends AbstractServiceImpl {

	private static Logger logger = Logger.getLogger(DiseaseRegistryService.class);
	
	@Autowired
	QuickListDao quickListDao;
	
	@Autowired
    @Qualifier("DxresearchDAO")
    protected DxresearchDAO dxresearchDao;
	
	@Autowired
	@Qualifier("IssueDAO")
	private IssueDAO issueDao;

	/**
	 * gets quick lists
	 * @return List of quickList transfer objects
	 */
	@GET
	@Path("/quickLists")
	@Produces("application/json")
	public RestResponse<List<DxQuickList>> getQuickLists()
	{
		List<DxQuickList> resultList;
		try {
			resultList = createQuickList(quickListDao.getQuickLists());
		}
		catch(Exception e) {
			logger.error("Error", e);
			return RestResponse.errorResponse("Server Query Error");
		}
		return RestResponse.successResponse(resultList);
	}

	/**
	 * gets quick lists but ensures that all items returned also have a corresponding entry in the issue table
	 * This is because the database is supposed to have a copy of the dx in the issue table.
	 * @return List of quickList transfer objects
	 */
	@GET
	@Path("/issueQuickLists")
	@Produces("application/json")
	public RestResponse<List<DxQuickList>> getIssueQuickLists()
	{
		List<DxQuickList> resultList;
		try {
			resultList = createQuickList(quickListDao.getIssueQuickLists());
		}
		catch(Exception e) {
			logger.error("Error", e);
			return RestResponse.errorResponse("Server Query Error");
		}
		return RestResponse.successResponse(resultList);
	}

	@GET
	@Path("/findDxIssue")
	@Produces("application/json")
	public RestResponse<IssueTo1> findDxIssue(@QueryParam("codingSystem") String codingSystem, @QueryParam("code") String code)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ENCOUNTER_ISSUE_READ);

		Issue issue = issueDao.findIssueByTypeAndCode(codingSystem, code);
		if(issue != null) {
			IssueTo1 returnIssue = new IssueTo1();
			returnIssue.setCode(issue.getCode());
			returnIssue.setDescription(issue.getDescription());
			returnIssue.setId(issue.getId());
			returnIssue.setType(issue.getType());
			returnIssue.setPriority(issue.getPriority());
			returnIssue.setRole(issue.getRole());
			returnIssue.setUpdate_date(issue.getUpdate_date());
			returnIssue.setSortOrderId(issue.getSortOrderId());
			return RestResponse.successResponse(returnIssue);
		}
		return RestResponse.errorResponse("No Issue Found");
	}
	
	@POST
	@Path("/{demographicNo}/add")
	@Produces("application/json")
	@Consumes("application/json")
	public Response addToDiseaseRegistry(@PathParam("demographicNo") Integer demographicNo, IssueTo1 issue)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), demographicNo, Permission.DX_CREATE);

		boolean activeEntryExists = dxresearchDao.activeEntryExists(demographicNo, issue.getType(), issue.getCode());
		
		if(!activeEntryExists){
			Dxresearch dx = new Dxresearch();
			dx.setStartDate(new Date());
			dx.setCodingSystem(issue.getType());
			dx.setDemographicNo(demographicNo);
			dx.setDxresearchCode(issue.getCode());
			dx.setStatus('A');
			dx.setProviderNo(getCurrentProvider().getProviderNo());
			dxresearchDao.persist(dx);
			LogAction.addLogEntry(getLoggedInInfo().getLoggedInProviderNo(), demographicNo,
					LogConst.ACTION_ADD, LogConst.CON_DISEASE_REG, LogConst.STATUS_SUCCESS, ""+dx.getId(), getLoggedInInfo().getIp(), dx.toString());
		}
		
		return Response.ok().build();
	}

	private List<DxQuickList> createQuickList(List<QuickListView> resultList) {

		Map<String,DxQuickList> quickListMap = new HashMap<String,DxQuickList>();

		for(QuickListView entry : resultList) {
			DxQuickList dxList = quickListMap.get(entry.getQuickListName());
			// initialize quick-list list objects
			if (dxList == null) {
				dxList = new DxQuickList();
				dxList.setLabel(entry.getQuickListName());
				quickListMap.put(entry.getQuickListName(), dxList);
			}
			// populate each list with the diagnoses
			DiagnosisTo1 dx = new DiagnosisTo1();
			dx.setCode(entry.getCode());
			dx.setCodingSystem(entry.getCodingSystem());
			dx.setDescription(entry.getDescription());

			dxList.getDxList().add(dx);
		}
		return new ArrayList<DxQuickList>(quickListMap.values());
	}

}
