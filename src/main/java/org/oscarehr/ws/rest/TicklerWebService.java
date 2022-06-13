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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.CustomFilter;
import org.oscarehr.ticklers.entity.Tickler;
import org.oscarehr.ticklers.entity.TicklerTextSuggest;
import org.oscarehr.encounterNote.service.TicklerNoteService;
import org.oscarehr.managers.ProgramManager2;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.managers.TicklerManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ticklers.search.TicklerCriteriaSearch;
import org.oscarehr.ticklers.service.TicklerService;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.rest.conversion.TicklerConverter;
import org.oscarehr.ws.rest.conversion.TicklerTextSuggestConverter;
import org.oscarehr.ws.rest.conversion.tickler.TicklerDtoToTicklerConverter;
import org.oscarehr.ws.rest.to.AbstractSearchResponse;
import org.oscarehr.ws.rest.to.GenericRESTResponse;
import org.oscarehr.ws.rest.to.TicklerResponse;
import org.oscarehr.ws.rest.to.model.TicklerTextSuggestTo1;
import org.oscarehr.ws.rest.transfer.tickler.TicklerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Date;
import java.util.List;

@Path("/tickler")
@Component("ticklerWebService")
public class TicklerWebService extends AbstractServiceImpl
{
	@Autowired
	private TicklerManager ticklerManager; 

	private TicklerConverter ticklerConverter = new TicklerConverter();

	@Autowired
	private TicklerService ticklerService;

	@Autowired
	private SecurityInfoManager securityInfoManager;
	
	@Autowired
	private ProgramManager2 programManager;

	@Autowired
	private TicklerNoteService ticklerNoteService;

	@Autowired
	private TicklerDtoToTicklerConverter ticklerDtoToTicklerConverter;

	@POST
	@Path("/search")
	@Produces("application/json")
	@Consumes("application/json")
	public TicklerResponse search(JSONObject json, @QueryParam("startIndex") int startIndex, @QueryParam("limit") int limit)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.TICKLER_READ);
		
		CustomFilter cf = new CustomFilter(true);
		
		if(json.containsKey("status")) {
			cf.setStatus(json.getString("status"));
		}
		if(json.containsKey("priority")) {
			cf.setPriority(json.getString("priority"));
		}
		if(json.containsKey("assignee")) {
			cf.setAssignee(json.getString("assignee"));
		}
		if(json.containsKey("demographicNo")){
			cf.setDemographicNo(json.getString("demographicNo"));
		}
		
		//this will need refactor...needs a manager layer and some useful methods.
		//basically if overdueOnly='property', I check Persona for what to return, this
		//avoids cliend needing to know their preferences and passing them back.
		if(json.containsKey("overdueOnly") && "property".equals(json.getString("overdueOnly"))) {
			UserPropertyDAO propDao =(UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");
			String strVal = propDao.getStringValue(getCurrentProvider().getProviderNo(), "dashboard.expiredTicklersOnly");
			if(strVal != null && "true".equalsIgnoreCase(strVal) ) {
				cf.setEndDate(new Date());
			}
			if(strVal != null && "false".equalsIgnoreCase(strVal) ) {
				cf.setEndDate(null);
			}
			
			if(strVal == null ) {
				cf.setEndDate(new Date());
			}
			
		}

		List<Tickler> ticklers = ticklerManager.getTicklers(getLoggedInInfo(),cf,startIndex,limit);

		TicklerResponse result = new TicklerResponse();
		
		if(ticklers.size()==limit) {
			result.setTotal(ticklerManager.getNumTicklers(getLoggedInInfo(), cf));
		} else {
			result.setTotal(ticklers.size());
		}
		
		result.getContent().addAll(ticklerConverter.getAllAsTransferObjects(getLoggedInInfo(),ticklers)); 
		
		
		return result;
	}
	
	@GET
	@Path("/mine")
	@Produces("application/json")
	public TicklerResponse getMyTicklers(@QueryParam("limit") int limit)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.TICKLER_READ);
		
		CustomFilter cf = new CustomFilter(true);
		cf.setAssignee(getLoggedInInfo().getLoggedInProviderNo());
		cf.setStatus("A");
		
		List<Tickler> ticklers = ticklerManager.getTicklers(getLoggedInInfo(),cf,0,limit);

		TicklerResponse result = new TicklerResponse();
		result.setTotal(ticklers.size());
		result.getContent().addAll(ticklerConverter.getAllAsTransferObjects(getLoggedInInfo(),ticklers)); 

		return result;
	}
	
	@GET
	@Path("/ticklers")
	@Produces("application/json")
	public TicklerResponse getTicklerList(@QueryParam("count") Integer count,
										  @QueryParam("page") Integer page,
										  @QueryParam("serviceStartDate") String serviceStartDate,
										  @QueryParam("serviceEndDate") String serviceEndDate,
										  @DefaultValue("A") @QueryParam("status") String status,
										  @QueryParam("taskAssignedTo") String taskAssignedTo,
										  @QueryParam("mrp") String mrp,
										  @QueryParam("creator") String creator,
										  @QueryParam("priority") String priority,
										  @QueryParam("sortColumn") String sortColumn,
										  @QueryParam("sortDirection") String sortDirection,
										  @QueryParam("demographicNo") Integer demographicNo,
										  @QueryParam("includeComments") boolean includeComments,
										  @QueryParam("includeLinks") boolean includeLinks,
										  @QueryParam("includeProgram") boolean includeProgram,
										  @QueryParam("includeUpdates") boolean includeUpdates)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.TICKLER_READ);

		TicklerCriteriaSearch.SORTDIR sortDir = TicklerCriteriaSearch.SORTDIR.valueOf(sortDirection);
		TicklerCriteriaSearch.SORT_MODE sortMode = TicklerCriteriaSearch.SORT_MODE.valueOf(sortColumn);

		TicklerCriteriaSearch ticklerCriteriaSearch = new TicklerCriteriaSearch();
		ticklerCriteriaSearch.setSortDir(sortDir);
		ticklerCriteriaSearch.setSortMode(sortMode);
		ticklerCriteriaSearch.setStartDate(ConversionUtils.fromDateString(serviceStartDate));
		ticklerCriteriaSearch.setEndDate(ConversionUtils.fromDateString(serviceEndDate));
		ticklerCriteriaSearch.setTaskAssignedTo(StringUtils.trimToNull(taskAssignedTo));
		ticklerCriteriaSearch.setCreator(StringUtils.trimToNull(creator));
		ticklerCriteriaSearch.setMrp(StringUtils.trimToNull(mrp));
		ticklerCriteriaSearch.setDemographicNo(demographicNo);

		if(StringUtils.isNotBlank(status))
		{
			Tickler.STATUS ticklerStatus = Tickler.STATUS.valueOf(status);
			ticklerCriteriaSearch.setStatus(ticklerStatus);
		}

		if (StringUtils.isNotBlank(priority))
		{
			Tickler.PRIORITY ticklerPriority = Tickler.PRIORITY.valueOf(priority);
			ticklerCriteriaSearch.setPriority(ticklerPriority);
		}

		TicklerResponse result = new TicklerResponse();

		ticklerConverter.setIncludeLinks(includeLinks);
		ticklerConverter.setIncludeComments(includeComments);
		ticklerConverter.setIncludeUpdates(includeUpdates);
		ticklerConverter.setIncludeProgram(includeProgram);

		int ticklerCount = ticklerService.getTicklerCount(ticklerCriteriaSearch);
		result.setTotal(ticklerCount);

		List<Tickler> comparisonSearch = ticklerService.getSearchResponse(ticklerCriteriaSearch, page, count);
		result.getContent().addAll(ticklerConverter.getAllAsTransferObjects(getLoggedInInfo(), comparisonSearch));
		
		return result;
	}

	@POST
	@Path("/complete")
	@Produces("application/json")
	@Consumes("application/json")
	public GenericRESTResponse completeTicklers(JSONObject json)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.TICKLER_UPDATE);
		GenericRESTResponse response = new GenericRESTResponse();

		JSONArray ticklerIds = json.getJSONArray("ticklers");
		
		for(Object id : ticklerIds) {
			int ticklerNo = (Integer)id;
			ticklerManager.completeTickler(getLoggedInInfo(), ticklerNo, getLoggedInInfo().getLoggedInProviderNo());
		}
		
		return response;
	}
	
	@POST
	@Path("/delete")
	@Produces("application/json")
	@Consumes("application/json")
	public GenericRESTResponse deleteTicklers(JSONObject json)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.TICKLER_DELETE);

		GenericRESTResponse response = new GenericRESTResponse();
		JSONArray ticklerIds = json.getJSONArray("ticklers");
		
		for(Object id : ticklerIds) {
			int ticklerNo = (Integer)id;
			ticklerManager.deleteTickler(getLoggedInInfo(), ticklerNo, getLoggedInInfo().getLoggedInProviderNo());
		}
		
		return response;
	}
	
	@POST
	@Path("/update")
	@Produces("application/json")
	@Consumes("application/json")
	public GenericRESTResponse updateTickler(@QueryParam("writeEncounterNote") Boolean writeEncounterNote,
	                                         JSONObject json)
	{
		String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), json.getInt("id"), Permission.TICKLER_UPDATE);

		Tickler tickler = ticklerManager.getTickler(getLoggedInInfo(), json.getInt("id"));
		
		if(tickler == null) {
			throw new RuntimeException("Tickler not found");
		}
		
		//TODO-legacy: verify it's good data associations
		tickler.setTaskAssignedTo(json.getString("taskAssignedTo"));
	
		tickler.setStatusAsChar(json.getString("status").charAt(0));
		
		tickler.setPriorityAsString(json.getString("priority"));
		
		tickler.setMessage(json.getString("message"));
		
		//tickler.setUpdateDate(new Date());
		
		String dt = json.getString("serviceDate");
		// tickler.setServiceDate(javax.xml.bind.DatatypeConverter.parseDateTime(dt).getTime());
		tickler.setServiceDate(new Date(Long.parseLong(dt)));

		GenericRESTResponse response = new GenericRESTResponse();
		response.setSuccess(ticklerManager.updateTickler(getLoggedInInfo(), tickler));
		
		if(response.isSuccess()) {
		
			if(json.has("ticklerComments")) {
				JSONArray arr = json.getJSONArray("ticklerComments");
				for(int x=0;x<arr.size();x++) {
					JSONObject c = (JSONObject)arr.get(x);
					
					if(c.has("newComment")) {
						ticklerManager.addComment(getLoggedInInfo(), tickler.getId(), c.getString("providerNo"), c.getString("message"));
					}
				}
			}
		}

		if(writeEncounterNote)
		{
			ticklerNoteService.saveTicklerNoteFromPrevious(tickler.getMessage(), tickler, loggedInProviderNo, tickler.getDemographicNo());
		}
		

		return response;
	}
	
	@GET
	@Path("/textSuggestions")
	@Produces("application/json")
	public AbstractSearchResponse<TicklerTextSuggestTo1> getTextSuggestions()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.TICKLER_READ);
		
		AbstractSearchResponse<TicklerTextSuggestTo1> response = new AbstractSearchResponse<TicklerTextSuggestTo1>();
		List<TicklerTextSuggest> suggestions = ticklerManager.getActiveTextSuggestions(getLoggedInInfo());
		
		response.setContent(new TicklerTextSuggestConverter().getAllAsTransferObjects(getLoggedInInfo(),suggestions));
		response.setTotal(response.getContent().size());
		
		return response;
	}
	
	@POST
	@Path("/add")
	@Produces("application/json")
	@Consumes("application/json")
	public GenericRESTResponse addTickler(@QueryParam("writeEncounterNote") Boolean writeEncounterNote,
	                                      TicklerDto ticklerDto)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.TICKLER_CREATE);

		Tickler tickler = ticklerDtoToTicklerConverter.convert(ticklerDto);
		String loggedInProviderNo = getLoggedInProviderId();

		tickler.setUpdateDate(new Date());
		tickler.setCreator(loggedInProviderNo);

		ProgramProvider programProvider = programManager.getCurrentProgramInDomain(getLoggedInInfo(), loggedInProviderNo);
		
		if(programProvider != null)
		{
			tickler.setProgramId(programProvider.getProgramId().intValue());
		}

		boolean success = ticklerManager.addTickler(getLoggedInInfo(), tickler);
		if(writeEncounterNote)
		{
			ticklerNoteService.saveTicklerNote(tickler.getMessage(), tickler, loggedInProviderNo, tickler.getDemographicNo());
		}
		GenericRESTResponse response = new GenericRESTResponse();
		response.setSuccess(success);

		return response;
	}
}
