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

import org.oscarehr.billing.CA.AB.dao.AlbertaFacilityDao;
import org.oscarehr.billing.CA.AB.dao.AlbertaFunctionalCenterDao;
import org.oscarehr.billing.CA.AB.dao.AlbertaSkillCodeDao;
import org.oscarehr.billing.CA.ON.dao.OntarioMasterNumberDao;
import org.oscarehr.common.dao.BillingBCDao;
import org.oscarehr.common.dao.BillingServiceDao;
import org.oscarehr.managers.BillingManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.conversion.ServiceTypeConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.AbstractSearchResponse;
import org.oscarehr.ws.rest.to.GenericRESTResponse;
import org.oscarehr.ws.rest.to.model.ServiceTypeTo;
import org.oscarehr.ws.rest.transfer.billing.AlbertaFacilityTo1;
import org.oscarehr.ws.rest.transfer.billing.AlbertaFunctionalCenterTo1;
import org.oscarehr.ws.rest.transfer.billing.AlbertaSkillCodeTo1;
import org.oscarehr.ws.rest.transfer.billing.BCBillingLocationTo1;
import org.oscarehr.ws.rest.transfer.billing.BCBillingVisitCodeTo1;
import org.oscarehr.ws.rest.transfer.billing.OntarioMasterNumberTo1;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.OscarProperties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/billing")
public class BillingService extends AbstractServiceImpl
{
	@Autowired
	BillingManager billingManager;

	@Autowired
	AlbertaSkillCodeDao albertaSkillCodeDao;

	@Autowired
	AlbertaFacilityDao albertaFacilityDao;

	@Autowired
	AlbertaFunctionalCenterDao albertaFunctionalCenterDao;

	@Autowired
	BillingBCDao billingBCDao;

	@Autowired
	OntarioMasterNumberDao ontarioMasterNumberDao;

	private OscarProperties oscarProperties = OscarProperties.getInstance();
	
	@GET
	@Path("/uniqueServiceTypes")
	@Produces("application/json")
	public AbstractSearchResponse<ServiceTypeTo> getUniqueServiceTypes(@QueryParam("type") String type)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.BILLING_READ);

		AbstractSearchResponse<ServiceTypeTo> response = new AbstractSearchResponse<ServiceTypeTo>();
		ServiceTypeConverter converter = new ServiceTypeConverter();
		if(type == null)
		{
			response.setContent(converter.getAllAsTransferObjects(getLoggedInInfo(), billingManager.getUniqueServiceTypes(getLoggedInInfo())));
		}
		else
		{
			response.setContent(converter.getAllAsTransferObjects(getLoggedInInfo(), billingManager.getUniqueServiceTypes(getLoggedInInfo(), type)));
		}
		response.setTotal(response.getContent().size());
		return response;
	}

	@GET
	@Path("/billingRegion")
	@Produces("application/json")
	public GenericRESTResponse billingRegion()
	{
		boolean billRegionSet = true;
		String billRegion = oscarProperties.getBillingType().trim().toUpperCase();
		if(billRegion.isEmpty())
		{
			billRegionSet = false;
		}
		return new GenericRESTResponse(billRegionSet, billRegion);
	}

	@GET
	@Path("/defaultView")
	@Produces("application/json")
	public GenericRESTResponse defaultView()
	{
		boolean defaultViewSet = true;
		String defaultView = oscarProperties.getProperty("default_view", "").trim();
		if(defaultView.isEmpty())
		{
			defaultViewSet = false;
		}
		return new GenericRESTResponse(defaultViewSet, defaultView);
	}

	@GET
	@Path("/alberta/skillCodes")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<AlbertaSkillCodeTo1>> getAlbertaSkillCodes()
	{
		return RestResponse.successResponse(AlbertaSkillCodeTo1.fromList(albertaSkillCodeDao.getAllSkillCodes()));
	}

	@GET
	@Path("/alberta/facilities")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<AlbertaFacilityTo1>> getAlbertaActiveFacilities()
	{
		return RestResponse.successResponse(AlbertaFacilityTo1.fromList(albertaFacilityDao.getAllActiveFacilities()));
	}

	@GET
	@Path("/alberta/functional_centers")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<AlbertaFunctionalCenterTo1>> getAlbertaFunctionalCenters()
	{
		return RestResponse.successResponse(AlbertaFunctionalCenterTo1.fromList(albertaFunctionalCenterDao.getAllFunctionalCenters()));
	}

	@GET
	@Path("/bc/billing_visit_codes")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<BCBillingVisitCodeTo1>> getBCBillingVisitCodes()
	{
		return RestResponse.successResponse(BCBillingVisitCodeTo1.fromList(billingBCDao.findBillingVisits(BillingServiceDao.BC)));
	}

	@GET
	@Path("/bc/billing_locations")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<BCBillingLocationTo1>> getBCBillingLocations()
	{
		return RestResponse.successResponse(BCBillingLocationTo1.fromList(billingBCDao.findBillingLocations(BillingServiceDao.BC)));
	}

	@GET
	@Path("/on/master_numbers")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<OntarioMasterNumberTo1>> getOntarioMasterNumbers()
	{
		return RestResponse.successResponse(OntarioMasterNumberTo1.fromList(ontarioMasterNumberDao.findAll()));
	}

}
