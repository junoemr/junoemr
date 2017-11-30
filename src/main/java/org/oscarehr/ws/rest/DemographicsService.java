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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.caisi_integrator.ws.DemographicTransfer;
import org.oscarehr.caisi_integrator.ws.MatchingDemographicParameters;
import org.oscarehr.caisi_integrator.ws.MatchingDemographicTransferScore;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.web.DemographicSearchHelper;
import org.oscarehr.ws.rest.to.AbstractSearchResponse;
import org.oscarehr.ws.rest.to.model.DemographicSearchRequest;
import org.oscarehr.ws.rest.to.model.DemographicSearchRequest.SEARCHMODE;
import org.oscarehr.ws.rest.to.model.DemographicSearchRequest.SORTDIR;
import org.oscarehr.ws.rest.to.model.DemographicSearchRequest.SORTMODE;
import org.oscarehr.ws.rest.to.model.DemographicSearchRequest.STATUSMODE;
import org.oscarehr.ws.rest.to.model.DemographicSearchResult;
import org.oscarehr.ws.rest.to.model.StatusValueTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import oscar.OscarProperties;


/**
 * Defines a service contract for main operations on demographic.
 */
@Path("/demographics")
@Component("demographicsService")
public class DemographicsService extends AbstractServiceImpl {

	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	private DemographicManager demographicManager;

	@Autowired
	private SecurityInfoManager securityInfoManager;

	/**
	 * Search demographics - used by navigation of OSCAR webapp
	 *
	 * Currently supports LastName[,FirstName] and address searches.
	 *
	 * @param query
	 * @return
	 * 		Returns data for the demographic provided
	 */
	@GET
	@Path("/quickSearch")
	@Produces("application/json")
	public RestResponse<AbstractSearchResponse<DemographicSearchResult>,String> search(@QueryParam("query") String query) {

		try
		{
			if (!securityInfoManager.hasPrivilege(getLoggedInInfo(), "_demographic", "r", null))
			{
				throw new RuntimeException("Access Denied");
			}

			AbstractSearchResponse<DemographicSearchResult> response = new AbstractSearchResponse<DemographicSearchResult>();

			List<DemographicSearchResult> results = new ArrayList<DemographicSearchResult>();

			if (query == null)
			{
				return RestResponse.errorResponse("No Query Parameter Sent");
			}

			DemographicSearchRequest req = new DemographicSearchRequest();
			req.setStatusMode(STATUSMODE.active);
			req.setIntegrator(false); //this should be configurable by persona

			//caisi
			boolean outOfDomain = true;
			if (OscarProperties.getInstance().getProperty("ModuleNames", "").indexOf("Caisi") != -1)
			{
				outOfDomain = false;
			}
			req.setOutOfDomain(outOfDomain);


			if (query.startsWith("addr:"))
			{
				req.setMode(SEARCHMODE.Address);
				req.setKeyword(query.substring("addr:".length()));
			}
			else if (query.startsWith("chartNo:"))
			{
				req.setMode(SEARCHMODE.ChartNo);
				req.setKeyword(query.substring("chartNo:".length()));
			}
			else
			{
				req.setMode(SEARCHMODE.Name);
				req.setKeyword(query);
			}

			int count = demographicManager.searchPatientsCount(getLoggedInInfo(), req);

			if (count > 0)
			{
				results = demographicManager.searchPatients(getLoggedInInfo(), req, 0, 10);
				response.setContent(results);
				response.setTotal(count);
				response.setQuery(query);

			}
			return RestResponse.successResponse(response);
		}
		catch (Exception e)
		{
			logger.error("Error", e);
		}
		return RestResponse.errorResponse("Error");
	}

	@GET
	@Path("/search")
	@Produces("application/json")
	public RestResponse<AbstractSearchResponse<DemographicSearchResult>,String> search(@QueryParam("jsonData") String jsonStr,
	                                                                                   @QueryParam("startIndex") Integer startIndex,
	                                                                                   @QueryParam("itemsToReturn") Integer itemsToReturn )
	{
		try
		{
			AbstractSearchResponse<DemographicSearchResult> response = new AbstractSearchResponse<DemographicSearchResult>();
			JSONObject json = JSONObject.fromObject(jsonStr);

			if (!securityInfoManager.hasPrivilege(getLoggedInInfo(), "_demographic", "r", null))
			{
				throw new RuntimeException("Access Denied");
			}

			DemographicSearchRequest req = convertFromJSON(json);
			//caisi
			boolean outOfDomain = true;
			if (OscarProperties.getInstance().getProperty("ModuleNames", "").indexOf("Caisi") != -1)
			{
				outOfDomain = false;
			}
			req.setOutOfDomain(outOfDomain);


			List<DemographicSearchResult> results = new ArrayList<DemographicSearchResult>();

			if (json.getString("term").length() >= 1)
			{
				int count = demographicManager.searchPatientsCount(getLoggedInInfo(), req);

				if (count > 0)
				{
					results = demographicManager.searchPatients(getLoggedInInfo(), req, startIndex, itemsToReturn);
					response.setContent(results);
					response.setTotal(count);
				}
			}

			return RestResponse.successResponse(response);
		}
		catch (Exception e)
		{
			logger.error("Error", e);
		}
		return RestResponse.errorResponse("Error");
	}

	@GET
	@Path("/searchIntegrator")
	@Produces("application/json")
	public RestResponse<AbstractSearchResponse<DemographicSearchResult>,String> searchIntegrator(@QueryParam("jsonData") String jsonStr,
	                                                                                             @QueryParam("itemsToReturn") Integer itemsToReturn ) {
		AbstractSearchResponse<DemographicSearchResult> response = new AbstractSearchResponse<DemographicSearchResult>();
		try
		{
			JSONObject json = JSONObject.fromObject(jsonStr);

			if (!securityInfoManager.hasPrivilege(getLoggedInInfo(), "_demographic", "r", null))
			{
				throw new RuntimeException("Access Denied");
			}

			List<DemographicSearchResult> results = new ArrayList<DemographicSearchResult>();

			if (json.getString("term").length() >= 1)
			{

				MatchingDemographicParameters matches = CaisiIntegratorManager.getMatchingDemographicParameters(getLoggedInInfo(), convertFromJSON(json));
				List<MatchingDemographicTransferScore> integratorSearchResults = null;
				try
				{
					matches.setMaxEntriesToReturn(itemsToReturn);
					matches.setMinScore(7);
					integratorSearchResults = DemographicSearchHelper.getIntegratedSearchResults(getLoggedInInfo(), matches);
					MiscUtils.getLogger().info("Integrator search results : " + (integratorSearchResults == null ? "null" : String.valueOf(integratorSearchResults.size())));
				}
				catch (Exception e)
				{
					MiscUtils.getLogger().error("error searching integrator", e);
				}

				if (integratorSearchResults != null)
				{
					for (MatchingDemographicTransferScore matchingDemographicTransferScore : integratorSearchResults)
					{
						if (isLocal(matchingDemographicTransferScore))
						{
							MiscUtils.getLogger().warn("ignoring remote demographic since we already have them locally");
							continue;
						}
						if (matchingDemographicTransferScore.getDemographicTransfer() != null)
						{
							DemographicTransfer obj = matchingDemographicTransferScore.getDemographicTransfer();
							DemographicSearchResult item = new DemographicSearchResult();
							item.setLastName(obj.getLastName());
							item.setFirstName(obj.getFirstName());
							item.setSex(obj.getGender().toString());
							item.setDob(obj.getBirthDate().getTime());
							item.setRemoteFacilityId(obj.getIntegratorFacilityId());
							item.setDemographicNo(obj.getCaisiDemographicId());
							results.add(item);
						}
					}
				}
			}

			response.setContent(results);
			response.setTotal((response.getContent() != null) ? response.getContent().size() : 0);

			return RestResponse.successResponse(response);
		}
		catch (Exception e)
		{
			logger.error("Error", e);
		}
		return RestResponse.errorResponse("Error");
	}

	@GET
	@Path("/statusList")
	@Produces("application/json")
	public RestResponse<List<StatusValueTo1>, String> getStatusList(@QueryParam("type") String listType)
	{
		try
		{
			// get the list
			List<String> statusList;
			if("ROSTER".equalsIgnoreCase(listType))
				statusList = demographicManager.getRosterStatusList();
			else
				statusList = demographicManager.getPatientStatusList();

			// create transfer objects list
			List<StatusValueTo1> resultList = new ArrayList<>(statusList.size());
			for(String status : statusList)
			{
				resultList.add(new StatusValueTo1(status));
			}

			return RestResponse.successResponse(resultList);
		}
		catch (Exception e)
		{
			logger.error("Error", e);
		}
		return RestResponse.errorResponse("Error");
	}

	private DemographicSearchRequest convertFromJSON(JSONObject json) {
		if(json ==null)return null;

		String searchType = json.getString("type");

		DemographicSearchRequest req = new DemographicSearchRequest();

		req.setMode(SEARCHMODE.valueOf(searchType));
		if(req.getMode() == null) {
			req.setMode(SEARCHMODE.Name);
		}

		req.setKeyword(json.getString("term"));
		req.setIntegrator(Boolean.valueOf(json.getString("integrator")));
		req.setOutOfDomain(Boolean.valueOf(json.getString("outofdomain")));
		req.setStatusMode(STATUSMODE.valueOf(json.getString("status")));

		Pattern namePtrn = Pattern.compile("sorting\\[(\\w+)\\]");

		JSONObject params = json.getJSONObject("params");
		if(params != null) {
			for(Object key:params.keySet()) {
				Matcher nameMtchr = namePtrn.matcher((String)key);
				if (nameMtchr.find()) {
					String var = nameMtchr.group(1);
					req.setSortMode(SORTMODE.valueOf(var));
					req.setSortDir(SORTDIR.valueOf(params.getString((String)key)));
				}
			}
		}
		return req;
	}

	private boolean isLocal(MatchingDemographicTransferScore matchingDemographicTransferScore) {
		String hin = matchingDemographicTransferScore.getDemographicTransfer().getHin();

		if(hin != null && !hin.isEmpty()) {
			DemographicSearchRequest dsr = new DemographicSearchRequest();
			dsr.setStatusMode(STATUSMODE.active);
			dsr.setKeyword(hin);
			dsr.setMode(SEARCHMODE.HIN);
			dsr.setOutOfDomain(true);
			dsr.setSortMode(SORTMODE.Name);
			dsr.setSortDir(SORTDIR.asc);

			if(demographicManager.searchPatientsCount(getLoggedInInfo(), dsr) > 0) {
				return true;
			}
		}
		return false;
	}
}
