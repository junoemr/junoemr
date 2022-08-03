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
package org.oscarehr.ws.rest.provider;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.exception.NoSuchRecordException;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Provider;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.managers.PreferenceManager;
import org.oscarehr.managers.ProviderManager2;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.provider.model.RecentDemographicAccess;
import org.oscarehr.provider.service.RecentDemographicAccessService;
import org.oscarehr.providerBilling.model.ProviderBilling;
import org.oscarehr.providerBilling.transfer.ProviderBillingTransfer;
import org.oscarehr.security.model.Permission;
import org.oscarehr.security.service.SecurityRolesService;
import org.oscarehr.security.service.SecuritySetsService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.soap.v1.transfer.ProviderTransfer;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.conversion.ProviderConverter;
import org.oscarehr.ws.rest.exception.SecurityRecordAlreadyExistsException;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.to.AbstractSearchResponse;
import org.oscarehr.ws.rest.to.model.ProviderTo1;
import org.oscarehr.ws.rest.transfer.PatientListItemTransfer;
import org.oscarehr.ws.rest.transfer.providerManagement.ProviderEditFormTo1;
import org.oscarehr.ws.rest.transfer.providerManagement.ProviderEditResponseTo1;
import org.oscarehr.ws.rest.transfer.security.UserSecurityRolesTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component("ProviderService")
@Path("/providerService/")
@Tag(name = "provider")
@Transactional
public class ProviderWebService extends AbstractServiceImpl
{

	private static final Logger logger = MiscUtils.getLogger();

	@Autowired
	private ProviderDao providerDao;

	@Autowired
	private org.oscarehr.provider.service.ProviderService providerService;

	@Autowired
	private ProviderManager2 providerManager;
	
	@Autowired
	private DemographicManager demographicManager;

	@Autowired
	private RecentDemographicAccessService recentDemographicAccessService;

	@Autowired
	private PreferenceManager preferenceManager;

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private SecurityRolesService securityRolesService;

	@Autowired
	private SecuritySetsService securitySetsService;

    @GET
    @Path("/providers")
	@Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public org.oscarehr.ws.rest.to.OscarSearchResponse<ProviderTransfer> getProviders() {
    	org.oscarehr.ws.rest.to.OscarSearchResponse<ProviderTransfer> lst = new 
    			org.oscarehr.ws.rest.to.OscarSearchResponse<ProviderTransfer>();
    	   	
    	for(Provider p: providerDao.getActiveProviders()) {
    		lst.getContent().add(ProviderTransfer.toTransfer(p));
    	}

        return lst;
    }
 
    @GET
    @Path("/providers_json")
	@Produces(MediaType.APPLICATION_JSON)
    public AbstractSearchResponse<ProviderTo1> getProvidersAsJSON()
    {
    	List<ProviderTo1> providers = new ProviderConverter().getAllAsTransferObjects(getLoggedInInfo(), providerDao.getActiveProviders());
    	
    	AbstractSearchResponse<ProviderTo1> response = new AbstractSearchResponse<ProviderTo1>();
    	response.setContent(providers);
  
    	return response;
    }

    @GET
    @Path("/provider/me")
    @Produces(MediaType.APPLICATION_JSON)
    public ProviderTo1 getLoggedInProvider()
    {
    	Provider provider = getLoggedInInfo().getLoggedInProvider();

    	if(provider != null)
    	{
		    ProviderTo1 transfer = new ProviderConverter().getAsTransferObject(getLoggedInInfo(), provider);
		    return transfer;
    	}

    	return null;
    }

	@GET
	@Path("/provider/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ProviderTransfer getProvider(@PathParam("id") String id) {
		return ProviderTransfer.toTransfer(providerDao.getProvider(id));
	}


	/**
	 * enable or disable the provider
	 * @param id - the providerNo of the provider to enable or disable
	 * @param enable - true to enable false to disable
	 * @return - true on success. errorResponse on bad provider.
	 */
	@POST
	@Path("/provider/{id}/update_status")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> enableProvider(@PathParam("id") Integer id, Boolean enable)
	{
		String currentProvider = getLoggedInInfo().getLoggedInProviderNo();
		try
		{
			securityInfoManager.requireUserCanModify(currentProvider, id.toString());
			providerService.enableProvider(id, enable);
			return RestResponse.successResponse(true);
		}
		catch (NoSuchRecordException nsre)
		{
			return RestResponse.errorResponse("Cannot find provider, with id: " + id);
		}
		catch (SecurityException se)
		{
			return RestResponse.errorResponse(ProviderEditResponseTo1.STATUS_INSUFFICIENT_PRIVILEGE);
		}
	}

	/**
	 * create a new provider.
	 * @param providerEditFormTo1 - form data to create the provider from
	 * @return - the new provider.
	 */
	@POST
	@Path("/provider/new")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public synchronized RestResponse<ProviderEditResponseTo1> createProvider(ProviderEditFormTo1 providerEditFormTo1)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ADMIN_CREATE);
		try
		{
			ProviderData providerData = providerService.createProvider(providerEditFormTo1, getLoggedInInfo());
			return RestResponse.successResponse(new ProviderEditResponseTo1(providerData.getProviderNo().toString(), ProviderEditResponseTo1.STATUS_SUCCESS));
		}
		catch(SecurityRecordAlreadyExistsException secRecordExists)
		{
			return RestResponse.errorResponse(ProviderEditResponseTo1.STATUS_SEC_RECORD_EXISTS);
		}
	}

	/**
	 * edit provider.
	 * @param providerEditFormTo1 - form data to update the provider with
	 * @return - the new provider.
	 */
	@POST
	@Path("/provider/{id}/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public synchronized RestResponse<ProviderEditResponseTo1> editProvider(@PathParam("id") Integer providerNo, ProviderEditFormTo1 providerEditFormTo1)
	{
		String currentProviderId = getLoggedInProviderId();
		securityInfoManager.requireAllPrivilege(currentProviderId, Permission.ADMIN_UPDATE);
		securityInfoManager.requireUserCanModify(currentProviderId, providerNo.toString());
		try
		{
			ProviderData providerData = providerService.editProvider(providerEditFormTo1, providerNo, currentProviderId);
			return RestResponse.successResponse(new ProviderEditResponseTo1(providerData.getProviderNo().toString(), ProviderEditResponseTo1.STATUS_SUCCESS));
		}
		catch (SecurityRecordAlreadyExistsException secRecordExists)
		{
			return RestResponse.errorResponse(ProviderEditResponseTo1.STATUS_SEC_RECORD_EXISTS);
		}
		catch (SecurityException securityException)
		{
			return RestResponse.errorResponse(ProviderEditResponseTo1.STATUS_INSUFFICIENT_PRIVILEGE);
		}
	}

	@GET
	@Path("/provider/{id}/edit_form")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<ProviderEditFormTo1> getProviderEditForm(@PathParam("id") String id)
	{
		return RestResponse.successResponse(providerService.getEditFormForProvider(id, getLoggedInInfo().getLoggedInSecurity()));
	}

    @GET
    @Path("/provider/{id}/billing")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<ProviderBillingTransfer> getProviderBilling(@PathParam("id") String providerNo)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.BILLING_READ);

		ProviderBilling billing = providerService.getProviderBilling(providerNo);
		ProviderBillingTransfer transfer = ProviderBillingTransfer.toTransferObj(billing);
		return RestResponse.successResponse(transfer);
	}
    @GET
    @Path("/providerjson/{id}")
	@Produces(MediaType.APPLICATION_JSON)
    public ProviderTo1 getProviderAsJSON(@PathParam("id") String id)
    {
	    Provider provider = providerDao.getProvider(id);
	    ProviderTo1 transfer = new ProviderConverter().getAsTransferObject(getLoggedInInfo(), provider);

    	return transfer;
    }

    @GET
    @Path("/providers/bad")
	@Produces(MediaType.APPLICATION_JSON)
    public Response getBadRequest() {
        return Response.status(Status.BAD_REQUEST).build();
    }
	
	@POST
	@Path("/providers/search")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Hidden
	public AbstractSearchResponse<ProviderTo1> search(JSONObject json,@QueryParam("startIndex") Integer startIndex,@QueryParam("itemsToReturn") Integer itemsToReturn )
	{
		AbstractSearchResponse<ProviderTo1> response = new AbstractSearchResponse<ProviderTo1>();
		
		int startIndexVal = startIndex==null?0:startIndex.intValue();
		int itemsToReturnVal = itemsToReturn==null?5000:startIndex.intValue();

		String status = "%";// all provider statuses
		if (json.containsKey("active"))
		{
			if(Boolean.valueOf(json.getString("active")))
			{
				status = ProviderData.PROVIDER_STATUS_ACTIVE;
			}
			else
			{
				status = ProviderData.PROVIDER_STATUS_INACTIVE;
			}
		}
		
		String term = null;
		if(json.containsKey("searchTerm")) {
			term = json.getString("searchTerm");
		}
		
		List<Provider> results = providerManager.search(getLoggedInInfo(),term, status,startIndexVal, itemsToReturnVal);
		
		
		ProviderConverter converter = new ProviderConverter();
		response.setContent(converter.getAllAsTransferObjects(getLoggedInInfo(),results));
		response.setTotal(response.getContent().size());
		
		return response;
	}
	
	@GET
	@Path("/getRecentDemographicsViewed")
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<PatientListItemTransfer> getRecentDemographicsViewed()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.DEMOGRAPHIC_READ);

		int providerNo = Integer.parseInt(getLoggedInInfo().getLoggedInProviderNo());
		int offset = 0;
		int limit = 8;

		String recentPatients = preferenceManager.getProviderPreference(getLoggedInInfo(), "recentPatients");
		if(recentPatients != null)
		{
			limit = Integer.parseInt(recentPatients);
		}

		List<RecentDemographicAccess> results = recentDemographicAccessService.getRecentAccessList(providerNo, offset, limit);
		List<PatientListItemTransfer> resultList = new ArrayList<>(results.size());

		//TODO avoid the loop over demographics to get the display name
		for(RecentDemographicAccess result : results)
		{
			Integer demographicNo = result.getDemographicNo();
			Date accessDateTime = result.getAccessDateTime();
			Demographic demographic = demographicManager.getDemographic(getLoggedInInfo(), demographicNo);

			PatientListItemTransfer item = new PatientListItemTransfer();
			item.setDemographicNo(demographicNo);
			item.setDate(accessDateTime);
			item.setName(demographic.getDisplayName());
			resultList.add(item);
		}

		return RestSearchResponse.successResponseOnePage(resultList);
	}
	
	@GET
	@Path("/getActiveTeams")
	@Produces(MediaType.APPLICATION_JSON)
	public AbstractSearchResponse<String> getActiveTeams()
	{
		List<String> teams = providerManager.getActiveTeams(getLoggedInInfo());
		
		AbstractSearchResponse<String> response = new AbstractSearchResponse<String>();
		
		response.setContent(teams);
		response.setTotal(response.getContent().size());
		return response;
	}

	@GET
	@Path("/self/security/roles")
	public RestResponse<UserSecurityRolesTransfer> getCurrentUserSecurityRoles()
	{
		return RestResponse.successResponse(securityRolesService.getUserSecurityRolesTransfer(getLoggedInProviderId()));
	}

	@GET
	@Path("/self/security/access/demographic/{demographicId}")
	public RestResponse<Boolean> canCurrentUserAccessDemographic(@PathParam("demographicId") Integer demographicId)
	{
		return RestResponse.successResponse(securityInfoManager.isAllowedAccessToPatientRecord(getLoggedInProviderId(), demographicId));
	}

	@GET
	@Path("/provider/{providerId}/security/sets/blacklist")
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<String> getProviderSecurityDemographicSetsBlacklist(@PathParam("providerId") String providerId)
	{
		return RestSearchResponse.successResponseOnePage(securitySetsService.getSecurityDemographicSetNamesBlacklist(providerId));
	}

	@PUT
	@Path("/provider/{providerId}/security/sets/blacklist")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> setProviderSecurityDemographicSetsBlacklist(@PathParam("providerId") String providerId,
	                                                                         List<String> assignedSetNames)
	{
		String loggedInProviderId = getLoggedInProviderId();
		securityInfoManager.requireAllPrivilege(loggedInProviderId, Permission.CONFIGURE_SECURITY_ROLES_UPDATE);
		securitySetsService.setSecurityDemographicSetsBlacklist(loggedInProviderId, providerId, assignedSetNames);
		return RestResponse.successResponse(true);
	}

//	@GET
//	@Path("/settings/get")
//	@Produces("application/json")
//	public AbstractSearchResponse<ProviderSettings> getProviderSettings() {
//		AbstractSearchResponse<ProviderSettings> response = new AbstractSearchResponse<ProviderSettings>();
//
//		ProviderSettings settings = providerManager.getProviderSettings(getLoggedInInfo().getLoggedInProviderNo());
//		List<ProviderSettings> content = new ArrayList<ProviderSettings>();
//		content.add(settings);
//		response.setContent(content);
//		response.setTotal(1);
//		return response;
//	}
//
//	@POST
//	@Path("/settings/{providerNo}/save")
//	@Produces("application/json")
//	@Consumes("application/json")
//	public GenericRESTResponse saveProviderSettings(ProviderSettings json,@PathParam("providerNo")String providerNo){
//		GenericRESTResponse response = new GenericRESTResponse();
//
//		MiscUtils.getLogger().warn(json.toString());
//
//		providerManager.updateProviderSettings(getLoggedInInfo(),providerNo,json);
//		return response;
//	}
}
