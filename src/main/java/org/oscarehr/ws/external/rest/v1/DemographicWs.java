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

import io.swagger.v3.oas.annotations.Operation;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DemographicCust;
import org.oscarehr.common.model.DemographicExt;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.eform.service.EFormDataService;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.provider.service.RecentDemographicAccessService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.rest.AbstractExternalRestWs;
import org.oscarehr.ws.external.rest.v1.conversion.DemographicConverter;
import org.oscarehr.ws.external.rest.v1.transfer.DemographicTransfer;
import org.oscarehr.ws.external.rest.v1.transfer.eform.EFormTransferInbound;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component("DemographicWs")
@Path("/demographic")
@Produces(MediaType.APPLICATION_JSON)
public class DemographicWs extends AbstractExternalRestWs
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	DemographicManager demographicManager;

	@Autowired
	RecentDemographicAccessService recentDemographicAccessService;

	@Autowired
	ProgramManager programManager;

	@Autowired
	DocumentService documentService;

	@Autowired
	EFormDataService eFormService;

	@GET
	@Path("/{demographicId}")
	@Operation(summary = "Retrieve an existing patient demographic record by demographic id.")
	public RestResponse<DemographicTransfer> getDemographic(@PathParam("demographicId") Integer demographicNo)
	{
		DemographicTransfer demographicTransfer;
		try
		{
			String providerNoStr = getOAuthProviderNo();
			int providerNo = Integer.parseInt(providerNoStr);
			Demographic demographic = demographicManager.getDemographic(providerNoStr, demographicNo);
			List<DemographicExt> demoExtras = demographicManager.getDemographicExts(providerNoStr, demographicNo);
			DemographicCust demoCustom = demographicManager.getDemographicCust(providerNoStr, demographicNo);

			demographicTransfer = DemographicConverter.getAsTransferObject(demographic, demoExtras, demoCustom);

			LogAction.addLogEntry(providerNoStr, demographic.getDemographicNo(), LogConst.ACTION_READ, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, null, getLoggedInInfo().getIp());
			recentDemographicAccessService.updateAccessRecord(providerNo, demographic.getDemographicNo());
		}
		catch(SecurityException e)
		{
			logger.error("Security Error", e);
			return RestResponse.errorResponse("User Permissions Error");
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			return RestResponse.errorResponse("System Error");
		}
		return RestResponse.successResponse(demographicTransfer);
	}

	@PUT
	@Path("/{demographicId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Update an existing patient demographic record by demographic id.")
	public RestResponse<DemographicTransfer> putDemographic(@PathParam("demographicId") Integer demographicNo,
	                                                        @Valid DemographicTransfer demographicTo)
	{
		return RestResponse.errorResponse("Not Implemented");
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Add a new patient demographic record to the system.")
	public RestResponse<Integer> postDemographic(@Valid DemographicTransfer demographicTo)
	{
		Integer demographicNo;
		try
		{
			Demographic demographic = DemographicConverter.getAsDomainObject(demographicTo);

			if(demographic.getDemographicNo() != null)
			{
				return RestResponse.errorResponse("Demographic number for a new record must be null");
			}
			String providerNoStr = getOAuthProviderNo();
			int providerNo = Integer.parseInt(providerNoStr);
			String ip = getHttpServletRequest().getRemoteAddr();

			/* set some default values */
			demographic.setLastUpdateDate(new Date());
			demographic.setLastUpdateUser(providerNoStr);

			// save the base demographic object
			demographicManager.createDemographic(providerNoStr, demographic, programManager.getDefaultProgramId());
			demographicNo = demographic.getDemographicNo();

			DemographicCust demoCustom = DemographicConverter.getCustom(demographicTo);
			if(demoCustom != null)
			{
				// save the custom fields
				demoCustom.setId(demographicNo);
				demographicManager.createUpdateDemographicCust(providerNoStr, demoCustom);
			}
			List<DemographicExt> demographicExtensions = DemographicConverter.getExtensionList(demographicTo);
			for(DemographicExt extension : demographicExtensions)
			{
				//save the extension fields
				extension.setDemographicNo(demographicNo);
				extension.setProviderNo(providerNoStr);
				demographicManager.createExtension(providerNoStr, extension);
			}

			// log the action and update the access record
			LogAction.addLogEntry(providerNoStr, demographicNo, LogConst.ACTION_ADD, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, null, ip);
			recentDemographicAccessService.updateAccessRecord(providerNo, demographic.getDemographicNo());
		}
		catch(SecurityException e)
		{
			logger.error("Security Error", e);
			return RestResponse.errorResponse("User Permissions Error");
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			return RestResponse.errorResponse("System Error");
		}
		return RestResponse.successResponse(demographicNo);
	}

	@POST
	@Path("/{demographicId}/chart/document/{documentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Assign a document in the system to the demographic chart")
	public RestResponse<Integer> assignDocument(@PathParam("demographicId") Integer demographicId,
	                                            @PathParam("documentId") Integer documentId)
	{
		try
		{
			String providerNoStr = getOAuthProviderNo();
			String ip = getHttpServletRequest().getRemoteAddr();

			documentService.assignDocumentToDemographic(documentId, demographicId);
			LogAction.addLogEntry(providerNoStr, demographicId, LogConst.ACTION_UPDATE, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS,
					String.valueOf(documentId), ip);
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			return RestResponse.errorResponse("System Error");
		}
		return RestResponse.successResponse(documentId);
	}

	@POST
	@Path("/{demographicId}/chart/eform")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Create an eForm on the patient chart with the passed in values")
	public RestResponse<Integer> postEForm(@PathParam("demographicId") Integer demographicId,
	                                       @Valid EFormTransferInbound transfer)
	{
		EFormData eForm;
		try
		{
			String providerNoStr = getOAuthProviderNo();
			int providerNo = Integer.parseInt(providerNoStr);
			String ip = getHttpServletRequest().getRemoteAddr();

			eForm = eFormService.saveNewEForm(transfer.getTemplateId(), demographicId, providerNo,
					transfer.getSubject(), new HashMap<>(), transfer.getFormValues(), null);

			LogAction.addLogEntry(providerNoStr, demographicId, LogConst.ACTION_ADD, LogConst.CON_EFORM_DATA, LogConst.STATUS_SUCCESS,
					String.valueOf(eForm.getId()), ip, eForm.getFormName());
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			return RestResponse.errorResponse("System Error");
		}

		return RestResponse.successResponse(eForm.getId());
	}
}
