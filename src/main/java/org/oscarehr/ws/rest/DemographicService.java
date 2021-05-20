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
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.casemgmt.service.CaseManagementIssueService;
import org.oscarehr.casemgmt.service.CaseManagementManager;
import org.oscarehr.common.dao.ContactDao;
import org.oscarehr.common.dao.DemographicContactDao;
import org.oscarehr.common.dao.ProfessionalSpecialistDao;
import org.oscarehr.common.dao.WaitingListDao;
import org.oscarehr.common.dao.WaitingListNameDao;
import org.oscarehr.common.exception.PatientDirectiveException;
import org.oscarehr.common.model.Contact;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DemographicContact;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.WaitingList;
import org.oscarehr.common.model.WaitingListName;
import org.oscarehr.demographic.model.DemographicCust;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.demographic.service.HinValidationService;
import org.oscarehr.encounterNote.dao.CaseManagementIssueDao;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.provider.service.RecentDemographicAccessService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.conversion.CaseManagementIssueConverter;
import org.oscarehr.ws.rest.conversion.DemographicContactFewConverter;
import org.oscarehr.ws.rest.conversion.DemographicConverter;
import org.oscarehr.ws.rest.conversion.WaitingListNameConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.to.OscarSearchResponse;
import org.oscarehr.ws.rest.to.model.CaseManagementIssueTo1;
import org.oscarehr.ws.rest.to.model.DemographicContactFewTo1;
import org.oscarehr.ws.rest.to.model.DemographicTo1;
import org.oscarehr.ws.rest.to.model.WaitingListNameTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarEncounter.data.EctProgram;
import oscar.oscarWaitingList.util.WLWaitingListUtil;

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
public class DemographicService extends AbstractServiceImpl {

	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	CaseManagementIssueService caseManagementIssueService;

	@Autowired
	CaseManagementIssueDao caseManagementIssueDao;

	@Autowired
	private DemographicManager demographicManager;

	@Autowired
	private DemographicContactDao demographicContactDao;
	
	@Autowired
	private ContactDao contactDao;
	
	@Autowired
	private WaitingListDao waitingListDao;
	
	@Autowired
	private WaitingListNameDao waitingListNameDao;
	
	@Autowired
	private ProviderDao providerDao;
	
	@Autowired
	private ProfessionalSpecialistDao specialistDao;

	@Autowired
	private RecentDemographicAccessService recentDemographicAccessService;

	private CaseManagementManager caseManagementMgr;

	public void setCaseManagementManager(CaseManagementManager caseManagementMgr)
	{
		this.caseManagementMgr = caseManagementMgr;
	}

	@Autowired
	private HinValidationService hinValidationService;

	private DemographicConverter demoConverter = new DemographicConverter();
	private DemographicContactFewConverter demoContactFewConverter = new DemographicContactFewConverter();
	private WaitingListNameConverter waitingListNameConverter = new WaitingListNameConverter();
	
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
	public OscarSearchResponse<DemographicTo1> getAllDemographics(@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit) {
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
	public RestResponse<DemographicTo1> getDemographicData(@PathParam("dataId") Integer id) throws PatientDirectiveException {
		try
		{
			String providerNoStr = getLoggedInInfo().getLoggedInProviderNo();
			int providerNo = Integer.parseInt(providerNoStr);

			Demographic demo = demographicManager.getDemographic(getLoggedInInfo(), id);
			if (demo == null)
			{
				return RestResponse.errorResponse("No demographic found with id " + id);
			}

			List<DemographicExt> demoExts = demographicManager.getDemographicExts(getLoggedInInfo(), id);
			if (demoExts != null && !demoExts.isEmpty())
			{
				DemographicExt[] demoExtArray = demoExts.toArray(new DemographicExt[demoExts.size()]);
				demo.setExtras(demoExtArray);
			}

			DemographicTo1 result = demoConverter.getAsTransferObject(getLoggedInInfo(), demo);

			DemographicCust demoCust = demographicManager.getDemographicCust(getLoggedInInfo(), id);
			if (demoCust != null)
			{
				result.setNurse(demoCust.getNurse());
				result.setResident(demoCust.getResident());
				result.setAlert(demoCust.getAlert());
				result.setMidwife(demoCust.getMidwife());
				result.setNotes(demoCust.getNotes());
			}

			List<WaitingList> waitingList = waitingListDao.search_wlstatus(id);
			if (waitingList != null && !waitingList.isEmpty())
			{
				WaitingList wl = waitingList.get(0);
				result.setWaitingListID(wl.getListId());
				result.setWaitingListNote(wl.getNote());
				result.setOnWaitingListSinceDate(wl.getOnListSince());
			}

			List<WaitingListName> waitingListNames = waitingListNameDao.findAll(null, null);
			if (waitingListNames != null)
			{
				for (WaitingListName waitingListName : waitingListNames)
				{
					if (waitingListName.getIsHistory().equals("Y")) continue;

					WaitingListNameTo1 waitingListNameTo1 = waitingListNameConverter.getAsTransferObject(getLoggedInInfo(), waitingListName);
					result.getWaitingListNames().add(waitingListNameTo1);
				}
			}

			LogAction.addLogEntry(providerNoStr, demo.getDemographicNo(), LogConst.ACTION_READ, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, null, getLoggedInInfo().getIp());
			recentDemographicAccessService.updateAccessRecord(providerNo, demo.getDemographicNo());

			return RestResponse.successResponse(result);
		}
		catch (Exception e)
		{
			logger.error("Error",e);
		}
		return RestResponse.errorResponse("Error");
	}

	@GET
	@Path("/{dataId}/contacts")
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<DemographicContactFewTo1> getDemographicContacts(@PathParam("dataId") Integer demographicNo,
	                                                                           @QueryParam("category") String category)
	{
		try
		{
			// return error if invalid category
			if(!DemographicContact.ALL_CATEGORIES.contains(category))
			{
				return RestSearchResponse.errorResponse("Invalid Category");
			}

			List<DemographicContactFewTo1> results = new ArrayList<>();

			List<DemographicContact> demoContacts = demographicContactDao.findByDemographicNoAndCategory(demographicNo, category);
			for (DemographicContact demoContact : demoContacts)
			{
				Integer contactId = Integer.valueOf(demoContact.getContactId());
				DemographicContactFewTo1 demoContactTo1 = new DemographicContactFewTo1();

				if (demoContact.getCategory().equals(DemographicContact.CATEGORY_PERSONAL))
				{
					if (demoContact.getType() == DemographicContact.TYPE_DEMOGRAPHIC)
					{
						Demographic contactD = demographicManager.getDemographic(getLoggedInInfo(), contactId);
						demoContactTo1 = demoContactFewConverter.getAsTransferObject(demoContact, contactD);
						if (demoContactTo1.getPhone() == null || demoContactTo1.getPhone().equals(""))
						{
							DemographicExt ext = demographicManager.getDemographicExt(getLoggedInInfo(), demographicNo, DemographicExt.KEY_DEMO_CELL);
							if (ext != null) demoContactTo1.setPhone(ext.getValue());
						}
					}
					else if (demoContact.getType() == DemographicContact.TYPE_CONTACT)
					{
						Contact contactC = contactDao.find(contactId);
						demoContactTo1 = demoContactFewConverter.getAsTransferObject(demoContact, contactC);
					}
					results.add(demoContactTo1);
				}
				else if (demoContact.getCategory().equals(DemographicContact.CATEGORY_PROFESSIONAL))
				{
					if (demoContact.getType() == DemographicContact.TYPE_PROVIDER)
					{
						Provider contactP = providerDao.getProvider(contactId.toString());
						demoContactTo1 = demoContactFewConverter.getAsTransferObject(demoContact, contactP);
					}
					else if (demoContact.getType() == DemographicContact.TYPE_PROFESSIONALSPECIALIST)
					{
						ProfessionalSpecialist contactS = specialistDao.find(contactId);
						demoContactTo1 = demoContactFewConverter.getAsTransferObject(demoContact, contactS);
					}
					results.add(demoContactTo1);
				}
			}
			return RestSearchResponse.successResponse(results, 0, 0, 0);

		}
		catch (Exception e)
		{
			logger.error("Error",e);
		}
		return RestSearchResponse.errorResponse("Error");
	}

	/**
	 * Saves demographic information. 
	 *
	 * @param data
	 * 		Detailed demographic data to be saved
	 * @return
	 * 		Returns the saved demographic data
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<DemographicTo1> createDemographicData(DemographicTo1 data)
	{
		Demographic demographic = demoConverter.getAsDomainObject(getLoggedInInfo(), data);
		hinValidationService.validateNoDuplication(demographic.getHin(), demographic.getVer(), demographic.getHcType());
		demographicManager.createDemographic(getLoggedInInfo(), demographic, data.getAdmissionProgramId());

		String providerNoStr = getLoggedInInfo().getLoggedInProviderNo();
		int providerNo = Integer.parseInt(providerNoStr);

		LogAction.addLogEntry(providerNoStr, demographic.getDemographicNo(), LogConst.ACTION_ADD, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, null,getLoggedInInfo().getIp());
		recentDemographicAccessService.updateAccessRecord(providerNo, demographic.getDemographicNo());

		return RestResponse.successResponse(demoConverter.getAsTransferObject(getLoggedInInfo(), demographic));
	}

	/**
	 * Updates demographic information. 
	 * 
	 * @param data
	 * 		Detailed demographic data to be updated
	 * @return
	 * 		Returns the updated demographic data
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<DemographicTo1> updateDemographicData(DemographicTo1 data) {

		try
		{
			//update demographiccust
			if (data.getNurse() != null || data.getResident() != null || data.getAlert() != null || data.getMidwife() != null || data.getNotes() != null)
			{
				DemographicCust demoCust = demographicManager.getDemographicCust(getLoggedInInfo(), data.getDemographicNo());
				if (demoCust == null)
				{
					demoCust = new DemographicCust();
					demoCust.setId(data.getDemographicNo());
				}
				demoCust.setNurse(data.getNurse());
				demoCust.setResident(data.getResident());
				demoCust.setAlert(data.getAlert());
				demoCust.setMidwife(data.getMidwife());
				demoCust.setNotes(data.getNotes());
				demographicManager.createUpdateDemographicCust(getLoggedInInfo(), demoCust);
			}

			//update waitingList
			if (data.getWaitingListID() != null)
			{
				WLWaitingListUtil.updateWaitingListRecord(data.getWaitingListID().toString(), data.getWaitingListNote(), data.getDemographicNo().toString(), null);
			}

			Demographic demographic = demoConverter.getAsDomainObject(getLoggedInInfo(), data);
			demographicManager.updateDemographic(getLoggedInInfo(), demographic);

			String providerNoStr = getLoggedInInfo().getLoggedInProviderNo();
			int providerNo = Integer.parseInt(providerNoStr);

			LogAction.addLogEntry(providerNoStr, demographic.getDemographicNo(), LogConst.ACTION_UPDATE, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, null, getLoggedInInfo().getIp());
			recentDemographicAccessService.updateAccessRecord(providerNo, demographic.getDemographicNo());

			return RestResponse.successResponse(demoConverter.getAsTransferObject(getLoggedInInfo(), demographic));
		}
		catch (Exception e)
		{
			logger.error("Error",e);
		}
		return RestResponse.errorResponse("Error");
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
	public RestResponse<DemographicTo1> deleteDemographicData(@PathParam("dataId") Integer id) {
		try
		{
			Demographic demo = demographicManager.getDemographic(getLoggedInInfo(), id);
			DemographicTo1 result = getDemographicData(id).getBody();
			if (demo == null)
			{
				return RestResponse.errorResponse("Demographic with id " + id + " not found");
			}

			String providerNoStr = getLoggedInInfo().getLoggedInProviderNo();
			int providerNo = Integer.parseInt(providerNoStr);

			demographicManager.deleteDemographic(getLoggedInInfo(), demo);
			LogAction.addLogEntry(providerNoStr, demo.getDemographicNo(), LogConst.ACTION_DELETE, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, null, getLoggedInInfo().getIp());
			recentDemographicAccessService.updateAccessRecord(providerNo, demo.getDemographicNo());

			return RestResponse.successResponse(result);
		}
		catch (Exception e)
		{
			logger.error("Error",e);
		}
		return RestResponse.errorResponse("Error");
	}

	@GET
	@Path("/{demographicNo}/caseManagementIssue/{issueId}")
	@Produces("application/json")
	public RestResponse<CaseManagementIssueTo1> getCaseManagementIssue(
			@PathParam("demographicNo") Integer demographicNo,
			@PathParam("issueId") Long issueId
	)
	{
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
		List<CaseManagementIssueTo1> issues = getIssues(request, demographicNo,
				org.oscarehr.encounterNote.model.CaseManagementIssue.ISSUE_FILTER_UNRESOLVED);

		return RestResponse.successResponse(issues);
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
