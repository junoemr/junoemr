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

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.log4j.Logger;
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.casemgmt.service.CaseManagementIssueService;
import org.oscarehr.common.dao.WaitingListDao;
import org.oscarehr.common.dao.WaitingListNameDao;
import org.oscarehr.common.exception.PatientDirectiveException;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.demographic.model.DemographicModel;
import org.oscarehr.demographic.service.HinValidationService;
import org.oscarehr.demographic.transfer.DemographicCreateInput;
import org.oscarehr.demographic.transfer.DemographicUpdateInput;
import org.oscarehr.demographicRoster.service.DemographicRosterService;
import org.oscarehr.demographicRoster.transfer.DemographicRosterTransfer;
import org.oscarehr.encounterNote.dao.CaseManagementIssueDao;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.provider.service.RecentDemographicAccessService;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.conversion.CaseManagementIssueConverter;
import org.oscarehr.ws.rest.conversion.DemographicConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.to.OscarSearchResponse;
import org.oscarehr.ws.rest.to.model.CaseManagementIssueTo1;
import org.oscarehr.ws.rest.to.model.DemographicTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarEncounter.data.EctProgram;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;


/**
 * Defines a service contract for main operations on demographic. 
 */
@Path("/demographic")
@Component("demographicService")
@Tag(name = "demographic")
public class DemographicService extends AbstractServiceImpl {

	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	CaseManagementIssueService caseManagementIssueService;

	@Autowired
	CaseManagementIssueDao caseManagementIssueDao;

	@Autowired
	private DemographicManager demographicManager;

	@Autowired
	private WaitingListDao waitingListDao;
	
	@Autowired
	private WaitingListNameDao waitingListNameDao;

	@Autowired
	private RecentDemographicAccessService recentDemographicAccessService;

	@Autowired
	private DemographicRosterService demographicRosterService;

	@Autowired
	private org.oscarehr.demographic.service.DemographicService demographicService;

	@Autowired
	private HinValidationService hinValidationService;

	@Deprecated // use ToTransfer/ToDomain + JPA demographic model
	private DemographicConverter demoConverter = new DemographicConverter();
	
	/**
	 * Finds all demographics.
	 * <p/>
	 * In case limit or offset parameters are set to null or zero, the entire result set is returned.
	 * 
	 * @param offset
	 * 		First record in the entire result set to be returned
	 * @param limit
	 * 		Maximum total number of records that should be returned
	 * @return
	 * 		Returns all demographics.
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public OscarSearchResponse<DemographicTo1> getAllDemographics(@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.DEMOGRAPHIC_READ);

		OscarSearchResponse<DemographicTo1> result = new OscarSearchResponse<DemographicTo1>();
		
		if (offset == null) {
			offset = 0;
		}
		if (limit == null) {
			limit = 0;
		}
		
		result.setLimit(limit);
		result.setOffset(offset);
		result.setTotal(demographicManager.getActiveDemographicCount(getLoggedInInfo()).intValue());
		
		for(Demographic demo : demographicManager.getActiveDemographics(getLoggedInInfo(), offset, limit)) {
			result.getContent().add(demoConverter.getAsTransferObject(getLoggedInInfo(),demo));
		}
		
		return result;
	}

	/**
	 * Gets detailed demographic data.
	 * 
	 * @param id
	 * 		Id of the demographic to get data for 
	 * @return
	 * 		Returns data for the demographic provided 
	 */
	@GET
	@Path("/{dataId}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<DemographicModel> getDemographicData(@PathParam("dataId") Integer id) throws PatientDirectiveException
	{
		String loggedInUserId = getLoggedInProviderId();
		securityInfoManager.requireAllPrivilege(loggedInUserId, id, Permission.DEMOGRAPHIC_READ);

		DemographicModel demo = demographicService.getDemographic(id);
		LogAction.addLogEntry(loggedInUserId, demo.getId(), LogConst.ACTION_READ,
				LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, null, getLoggedInInfo().getIp());
		recentDemographicAccessService.updateAccessRecord(loggedInUserId, demo.getId());

		return RestResponse.successResponse(demo);

//		try
//		{
//			String providerNoStr = getLoggedInInfo().getLoggedInProviderNo();
//			int providerNo = Integer.parseInt(providerNoStr);
//
//			Demographic demo = demographicManager.getDemographic(getLoggedInInfo(), id);
//			if (demo == null)
//			{
//				return RestResponse.errorResponse("No demographic found with id " + id);
//			}
//
//			List<DemographicExt> demoExts = demographicManager.getDemographicExts(getLoggedInInfo(), id);
//			if (demoExts != null && !demoExts.isEmpty())
//			{
//				DemographicExt[] demoExtArray = demoExts.toArray(new DemographicExt[demoExts.size()]);
//				demo.setExtras(demoExtArray);
//			}
//
//			DemographicTo1 result = demoConverter.getAsTransferObject(getLoggedInInfo(), demo);
//			AddressTo1 extraAddress = demographicManager.getExtraAddress(result);
//			result.setAddress2(extraAddress);
//
//			DemographicCust demoCust = demographicManager.getDemographicCust(getLoggedInInfo(), id);
//			if (demoCust != null)
//			{
//				result.setNurse(demoCust.getNurse());
//				result.setResident(demoCust.getResident());
//				result.setAlert(demoCust.getAlert());
//				result.setMidwife(demoCust.getMidwife());
//				result.setNotes(demoCust.getNotes());
//			}
//
//			List<WaitingList> waitingList = waitingListDao.search_wlstatus(id);
//			if (waitingList != null && !waitingList.isEmpty())
//			{
//				WaitingList wl = waitingList.get(0);
//				result.setWaitingListID(wl.getListId());
//				result.setWaitingListNote(wl.getNote());
//				result.setOnWaitingListSinceDate(wl.getOnListSince());
//			}
//
//			List<WaitingListName> waitingListNames = waitingListNameDao.findAll(null, null);
//			if (waitingListNames != null)
//			{
//				for (WaitingListName waitingListName : waitingListNames)
//				{
//					if (waitingListName.getIsHistory().equals("Y")) continue;
//
//					WaitingListNameTo1 waitingListNameTo1 = waitingListNameConverter.getAsTransferObject(getLoggedInInfo(), waitingListName);
//					result.getWaitingListNames().add(waitingListNameTo1);
//				}
//			}
//
//			LogAction.addLogEntry(providerNoStr, demo.getDemographicNo(), LogConst.ACTION_READ, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, null, getLoggedInInfo().getIp());
//			recentDemographicAccessService.updateAccessRecord(providerNo, demo.getDemographicNo());
//
//			return RestResponse.successResponse(result);
//		}
//		catch (Exception e)
//		{
//			logger.error("Error",e);
//		}
//		return RestResponse.errorResponse("Error");
	}

	/**
	 * Saves demographic information. 
	 *
	 * @param createInput
	 * 		Detailed demographic data to be saved
	 * @return
	 * 		Returns the saved demographic data
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<DemographicModel> createDemographicData(DemographicCreateInput createInput)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.DEMOGRAPHIC_CREATE);

		hinValidationService.validateNoDuplication(
				createInput.getHealthNumber(),
				createInput.getHealthNumberVersion(),
				createInput.getHealthNumberProvinceCode());
		DemographicModel demographicModel = demographicService.addNewDemographicRecord(getLoggedInProviderId(), createInput);

		LogAction.addLogEntry(getLoggedInProviderId(), demographicModel.getId(),
				LogConst.ACTION_ADD,
				LogConst.CON_DEMOGRAPHIC,
				LogConst.STATUS_SUCCESS,
				null,
				getLoggedInInfo().getIp());
		recentDemographicAccessService.updateAccessRecord(getLoggedInProviderId(), demographicModel.getId());

		return RestResponse.successResponse(demographicModel);
	}

	/**
	 * Updates demographic information. 
	 * 
	 * @param updateInput
	 * 		Detailed demographic data to be updated
	 * @return
	 * 		Returns the updated demographic data
	 */
	@PUT
	@Path("/{demographicId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<DemographicModel> updateDemographicData(@PathParam("demographicId") Integer demographicId,
	                                                            DemographicUpdateInput updateInput)
	{
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), updateInput.getId(), Permission.DEMOGRAPHIC_UPDATE);

		hinValidationService.validateNoDuplication(
				updateInput.getHealthNumber(),
				updateInput.getHealthNumberVersion(),
				updateInput.getHealthNumberProvinceCode());

		DemographicModel updatedModel = demographicService.updateDemographicRecord(updateInput, loggedInInfo);

		LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(), updatedModel.getId(),
				LogConst.ACTION_UPDATE,
				LogConst.CON_DEMOGRAPHIC,
				LogConst.STATUS_SUCCESS,
				null,
				loggedInInfo.getIp());

		recentDemographicAccessService.updateAccessRecord(getLoggedInInfo().getLoggedInProviderNo(), updatedModel.getId());

		return RestResponse.successResponse(updatedModel);

//		try
//		{
//			if (data.getAddress2().getAddress() != null || data.getAddress2().getCity() != null ||
//					 data.getAddress2().getPostal() != null || data.getAddress2().getProvince() != null)
//			{
//				List<DemographicExtTo1> extraAddress = demographicManager.setExtraAddress(data);
//				data.setExtras(extraAddress);
//			}
//			//update demographiccust
//			if (data.getNurse() != null || data.getResident() != null || data.getAlert() != null || data.getMidwife() != null || data.getNotes() != null)
//			{
//				DemographicCust demoCust = demographicManager.getDemographicCust(getLoggedInInfo(), data.getDemographicNo());
//				if (demoCust == null)
//				{
//					demoCust = new DemographicCust();
//					demoCust.setId(data.getDemographicNo());
//				}
//				demoCust.setNurse(data.getNurse());
//				demoCust.setResident(data.getResident());
//				demoCust.setAlert(data.getAlert());
//				demoCust.setMidwife(data.getMidwife());
//				demoCust.setNotes(data.getNotes());
//				demographicManager.createUpdateDemographicCust(getLoggedInInfo(), demoCust);
//			}
//
//			//update waitingList
//			if (data.getWaitingListID() != null)
//			{
//				WLWaitingListUtil.updateWaitingListRecord(data.getWaitingListID().toString(), data.getWaitingListNote(), data.getDemographicNo().toString(), null);
//			}
//
//			org.oscarehr.demographic.entity.Demographic demographic = demographicToDomainConverter.convert(data);
//			demographicManager.updateDemographic(loggedInInfo, demographic);
//
//			LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(), demographic.getDemographicId(),
//					LogConst.ACTION_UPDATE,
//					LogConst.CON_DEMOGRAPHIC,
//					LogConst.STATUS_SUCCESS,
//					null,
//					loggedInInfo.getIp());
//
//			Integer providerNo = Integer.parseInt(loggedInInfo.getLoggedInProviderNo());
//			recentDemographicAccessService.updateAccessRecord(providerNo, demographic.getId());
//
//			return RestResponse.successResponse(demographicService.getDemographic(demographic.getId()));
//		}
//		catch (Exception e)
//		{
//			logger.error("Error",e);
//		}
//		return RestResponse.errorResponse("Error");
	}

	/**
	 * Deletes demographic information. 
	 * 
	 * @param id
	 * 		Id of the demographic data to be deleted
	 * @return
	 * 		Returns the deleted demographic data
	 */
	@DELETE
	@Path("/{dataId}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<DemographicModel> deleteDemographicData(@PathParam("dataId") Integer id)
	{
		//TODO This seems incorrect, as demographics should not be deleteable. remove after checking this
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), id, Permission.DEMOGRAPHIC_DELETE);

		Demographic demo = demographicManager.getDemographic(getLoggedInInfo(), id);

		String providerNoStr = getLoggedInInfo().getLoggedInProviderNo();
		int providerNo = Integer.parseInt(providerNoStr);

		demographicManager.deleteDemographic(getLoggedInInfo(), demo);
		LogAction.addLogEntry(providerNoStr, demo.getDemographicNo(), LogConst.ACTION_DELETE, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, null, getLoggedInInfo().getIp());
		recentDemographicAccessService.updateAccessRecord(providerNo, demo.getDemographicNo());

		return RestResponse.successResponse(demographicService.getDemographic(id));
	}

	@GET
	@Path("/{demographicNo}/caseManagementIssue/{issueId}")
	@Produces("application/json")
	public RestResponse<CaseManagementIssueTo1> getCaseManagementIssue(
			@PathParam("demographicNo") Integer demographicNo,
			@PathParam("issueId") Long issueId
	)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), demographicNo,
				Permission.DEMOGRAPHIC_READ, Permission.ENCOUNTER_ISSUE_READ);

		CaseManagementIssueTo1 issue = caseManagementIssueService.getIssueById(demographicNo, issueId);

		return RestResponse.successResponse(issue);
	}

	@POST
	@Path("/{demographicNo}/caseManagementIssue/{issueId}/updateProperty")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public RestResponse<CaseManagementIssueTo1> setCaseManagementIssueProperty(
			@PathParam("demographicNo") int demographicNo,
			@PathParam("issueId") int issueId,
			PropertyData propertyData
	)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), demographicNo, Permission.ENCOUNTER_ISSUE_UPDATE);

		CaseManagementIssueTo1 issue = caseManagementIssueService.updateProperty(
				demographicNo,
				issueId,
				propertyData.getPropertyName(),
				propertyData.isPropertyValue()
		);

		return RestResponse.successResponse(issue);
	}

	@POST
	@Path("/{demographicNo}/caseManagementIssue/{issueId}/updateIssue")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public RestResponse<CaseManagementIssueTo1> setCaseManagementIssue(
			@PathParam("demographicNo") int demographicNo,
			@PathParam("issueId") int issueId,
			IssueData issueData
	)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), demographicNo, Permission.ENCOUNTER_ISSUE_CREATE);

		CaseManagementIssueTo1 issue = caseManagementIssueService.updateIssue(
				demographicNo,
				issueId,
				issueData.getNewIssueId()
		);

		return RestResponse.successResponse(issue);
	}

	@GET
	@Path("/{demographicNo}/issues")
	@Produces("application/json")
	public RestResponse<List<CaseManagementIssueTo1>> getAllIssues(
			@Context HttpServletRequest request,
			@PathParam("demographicNo") int demographicNo
	)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), demographicNo, Permission.ENCOUNTER_ISSUE_READ);

		List<CaseManagementIssueTo1> issues = getIssues(request, demographicNo,
				org.oscarehr.encounterNote.model.CaseManagementIssue.ISSUE_FILTER_ALL);
		return RestResponse.successResponse(issues);
	}

	@GET
	@Path("/{demographicNo}/resolvedIssues")
	@Produces("application/json")
	public RestResponse<List<CaseManagementIssueTo1>> getResolvedIssues(
			@Context HttpServletRequest request,
			@PathParam("demographicNo") int demographicNo
	)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), demographicNo, Permission.ENCOUNTER_ISSUE_READ);

		List<CaseManagementIssueTo1> issues = getIssues(request, demographicNo,
				org.oscarehr.encounterNote.model.CaseManagementIssue.ISSUE_FILTER_RESOLVED);
		return RestResponse.successResponse(issues);
	}

	@GET
	@Path("/{demographicNo}/unresolvedIssues")
	@Produces("application/json")
	public RestResponse<List<CaseManagementIssueTo1>> getUnresolvedIssues(
			@Context HttpServletRequest request,
			@PathParam("demographicNo") int demographicNo
	)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), demographicNo, Permission.ENCOUNTER_ISSUE_READ);

		List<CaseManagementIssueTo1> issues = getIssues(request, demographicNo,
				org.oscarehr.encounterNote.model.CaseManagementIssue.ISSUE_FILTER_UNRESOLVED);

		return RestResponse.successResponse(issues);
	}

	@GET
	@Path("/{demographicNo}/rosterHistory")
	public RestSearchResponse<DemographicRosterTransfer> getRosteredHistory(
			@PathParam("demographicNo") Integer demographicNo)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), demographicNo, Permission.DEMOGRAPHIC_READ);
		List<DemographicRosterTransfer> rosteredHistory = demographicRosterService.getRosteredHistory(demographicNo);
		return RestSearchResponse.successResponseOnePage(rosteredHistory);
	}

	private List<CaseManagementIssueTo1> getIssues(HttpServletRequest request, int demographicNo, String filter)
	{
		HttpSession session = request.getSession();

		LoggedInInfo loggedInInfo = getLoggedInInfo();
		String providerNo = (String) session.getAttribute("user");

		EctProgram prgrmMgr = new EctProgram(session);
		String programId = prgrmMgr.getProgram(providerNo);

		List<CaseManagementIssue> issues;
		issues = caseManagementIssueService.getIssues(
				loggedInInfo, Integer.toString(demographicNo), providerNo, programId, filter);

		List<CaseManagementIssueTo1> issuesOutput = new ArrayList<>();
		CaseManagementIssueConverter convertor = new CaseManagementIssueConverter();

		for(CaseManagementIssue issue: issues)
		{
			issuesOutput.add(convertor.getAsTransferObject(loggedInInfo, issue));
		}

		return issuesOutput;
	}

	private static class PropertyData
	{
		private String propertyName;
		private boolean propertyValue;

		public String getPropertyName()
		{
			return propertyName;
		}

		public void setPropertyName(String propertyName)
		{
			this.propertyName = propertyName;
		}

		public boolean isPropertyValue()
		{
			return propertyValue;
		}

		public void setPropertyValue(boolean propertyValue)
		{
			this.propertyValue = propertyValue;
		}
	}

	private static class IssueData
	{
		private int newIssueId;

		public int getNewIssueId()
		{
			return newIssueId;
		}

		public void setNewIssueId(int newIssueId)
		{
			this.newIssueId = newIssueId;
		}
	}
}
