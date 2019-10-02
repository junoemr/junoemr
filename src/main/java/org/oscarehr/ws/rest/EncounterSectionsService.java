/*
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

package org.oscarehr.ws.rest;

import org.apache.log4j.Logger;
import org.drools.FactException;
import org.oscarehr.casemgmt.dto.EncounterSection;
import org.oscarehr.casemgmt.service.EncounterSectionService;
import org.oscarehr.casemgmt.service.EncounterService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.oscarEncounter.data.EctProgram;

import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/encounterSections")
@Component("EncounterSectionsService")
public class EncounterSectionsService extends AbstractServiceImpl
{
	Logger logger = Logger.getLogger(EFormsService.class);

	@Autowired
	private EncounterService encounterService;

	@GET
	@Path("/{demographicNo}/{sectionName}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<EncounterSection> getEncounterSection(
			@PathParam("demographicNo") Integer demographicNo,
			@PathParam("sectionName") String sectionName,
			@QueryParam("appointmentNo") String appointmentNo,
			@QueryParam("limit") Integer limit,
			@QueryParam("offset") Integer offset
	)
			throws FactException
	{
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
		HttpSession session = loggedInInfo.getSession();

		EctProgram prgrmMgr = new EctProgram(session);
		String programId = prgrmMgr.getProgram(loggedInProviderNo);

		String roleName = session.getAttribute("userrole") + "," + session.getAttribute("user");

		EncounterSectionService sectionService = encounterService.getEncounterSectionServiceByName(sectionName);

		return RestResponse.successResponse(sectionService.getSection(
				loggedInInfo,
				roleName,
				loggedInProviderNo,
				demographicNo.toString(),
				appointmentNo,
				programId,
				"Tickler",
				"#FF6600",
				limit,
				offset
		));
	}
}
