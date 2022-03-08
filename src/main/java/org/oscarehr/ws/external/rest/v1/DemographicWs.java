/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.ws.external.rest.v1;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.log4j.Logger;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.demographic.service.HinValidationService;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.eform.service.EFormDataService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.provider.service.RecentDemographicAccessService;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.rest.AbstractExternalRestWs;
import org.oscarehr.ws.external.rest.v1.transfer.demographic.DemographicTransferInbound;
import org.oscarehr.ws.external.rest.v1.transfer.demographic.DemographicTransferOutbound;
import org.oscarehr.ws.external.rest.v1.transfer.eform.EFormTransferInbound;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.validator.DemographicNoConstraint;
import org.oscarehr.ws.validator.DocumentNoConstraint;
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
import java.util.HashMap;

@Component("DemographicWs")
@Path("/demographic")
@Produces(MediaType.APPLICATION_JSON)
public class DemographicWs extends AbstractExternalRestWs
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	private RecentDemographicAccessService recentDemographicAccessService;

	@Autowired
	private DemographicService demographicService;

	@Autowired
	private HinValidationService hinValidationService;

	@Autowired
	private DocumentService documentService;

	@Autowired
	private EFormDataService eFormService;

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@GET
	@Path("/{demographicId}")
	@Operation(summary = "Retrieve an existing patient demographic record by demographic id.")
	public RestResponse<DemographicTransferOutbound> getDemographic(@DemographicNoConstraint @PathParam("demographicId") Integer demographicNo)
	{
		String providerNoStr = getOAuthProviderNo();
		int providerNo = Integer.parseInt(providerNoStr);

		securityInfoManager.requireAllPrivilege(providerNoStr, demographicNo, Permission.DEMOGRAPHIC_READ);
		DemographicTransferOutbound demographicTransfer = demographicService.getDemographicTransferOutbound(demographicNo);

		LogAction.addLogEntry(providerNoStr, demographicTransfer.getDemographicNo(), LogConst.ACTION_READ, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, null, getLoggedInInfo().getIp());
		recentDemographicAccessService.updateAccessRecord(providerNo, demographicTransfer.getDemographicNo());

		return RestResponse.successResponse(demographicTransfer);
	}

	@PUT
	@Path("/{demographicId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Update an existing patient demographic record by demographic id.")
	public RestResponse<DemographicTransferOutbound> putDemographic(@DemographicNoConstraint @PathParam("demographicId") Integer demographicNo,
	                                                                @Valid DemographicTransferInbound demographicTo)
	{
		securityInfoManager.requireAllPrivilege(getOAuthProviderNo(), demographicNo, Permission.DEMOGRAPHIC_UPDATE);

		return RestResponse.errorResponse("Not Implemented");
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Add a new patient demographic record to the system.")
	public RestResponse<Integer> postDemographic(@Valid DemographicTransferInbound demographicTo)
	{
		String providerNoStr = getOAuthProviderNo();
		int providerNo = Integer.parseInt(providerNoStr);
		String ip = getHttpServletRequest().getRemoteAddr();

		securityInfoManager.requireAllPrivilege(providerNoStr, Permission.DEMOGRAPHIC_CREATE);
		hinValidationService.validateNoDuplication(demographicTo.getHin(), demographicTo.getHcVersion(), demographicTo.getHcType());
		Demographic demographic = demographicService.addNewDemographicRecord(providerNoStr, demographicTo);

		// log the action and update the access record
		LogAction.addLogEntry(providerNoStr, demographic.getId(), LogConst.ACTION_ADD, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, null, ip);
		recentDemographicAccessService.updateAccessRecord(providerNo, demographic.getDemographicId());
		return RestResponse.successResponse(demographic.getId());
	}

	@POST
	@Path("/{demographicId}/chart/document/{documentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Assign a document in the system to the demographic chart")
	public RestResponse<Integer> assignDocument(@DemographicNoConstraint @PathParam("demographicId") Integer demographicId,
	                                            @DocumentNoConstraint @PathParam("documentId") Integer documentId)
	{
		String providerNoStr = getOAuthProviderNo();
		String ip = getHttpServletRequest().getRemoteAddr();
		securityInfoManager.requireAllPrivilege(providerNoStr, demographicId, Permission.DOCUMENT_UPDATE);

		documentService.assignDocumentToDemographic(documentId, demographicId);
		LogAction.addLogEntry(providerNoStr, demographicId, LogConst.ACTION_UPDATE, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS,
				String.valueOf(documentId), ip);

		return RestResponse.successResponse(documentId);
	}

	@POST
	@Path("/{demographicId}/chart/eform")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Create an eForm on the patient chart with the passed in values")
	public RestResponse<Integer> postEForm(@DemographicNoConstraint @PathParam("demographicId") Integer demographicId,
	                                       @Valid EFormTransferInbound transfer)
	{
		String providerNoStr = getOAuthProviderNo();
		int providerNo = Integer.parseInt(providerNoStr);
		String ip = getHttpServletRequest().getRemoteAddr();
		securityInfoManager.requireAllPrivilege(providerNoStr, demographicId, Permission.EFORM_CREATE);

		EFormData eForm = eFormService.saveNewEFormWithDatabaseTags(transfer.getTemplateId(), demographicId, providerNo,
				transfer.getSubject(), new HashMap<>(), transfer.getFormValues(), null);

		LogAction.addLogEntry(providerNoStr, demographicId, LogConst.ACTION_ADD, LogConst.CON_EFORM_DATA, LogConst.STATUS_SUCCESS,
				String.valueOf(eForm.getId()), ip, eForm.getFormName());

		return RestResponse.successResponse(eForm.getId());
	}
}
