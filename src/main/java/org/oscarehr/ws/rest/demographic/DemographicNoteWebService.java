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
package org.oscarehr.ws.rest.demographic;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.encounterNote.model.TempNoteModel;
import org.oscarehr.encounterNote.service.EncounterNoteService;
import org.oscarehr.encounterNote.service.TempNoteService;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("demographic/{demographicNo}/note")
@Component("demographicNoteWebService")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "demographicNote")
public class DemographicNoteWebService extends AbstractServiceImpl
{
	@Autowired
	protected EncounterNoteService encounterNoteService;

	@Autowired
	protected TempNoteService tempNoteService;

	@GET
	@Path("/temp")
	public RestResponse<TempNoteModel> getCurrentTempNote(@PathParam("demographicNo") Integer demographicId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), demographicId, Permission.ENCOUNTER_NOTE_READ);

		Optional<TempNoteModel> modelOption = tempNoteService.getTempNote(getLoggedInProviderId(), demographicId);
		return RestResponse.successResponse(modelOption.orElse(null));
	}

	@POST
	@Path("/temp")
	public RestResponse<TempNoteModel> saveTempNote(@PathParam("demographicNo") Integer demographicId,
	                                                @QueryParam("noteId") Integer noteId,
	                                                String noteInput)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), demographicId,
				Permission.ENCOUNTER_NOTE_CREATE, Permission.ENCOUNTER_NOTE_UPDATE);

		TempNoteModel model = tempNoteService.setTempNote(getLoggedInProviderId(), demographicId, noteInput, noteId);
		return RestResponse.successResponse(model);
	}

	@DELETE
	@Path("/temp")
	public RestResponse<Boolean> deleteTempNote(@PathParam("demographicNo") Integer demographicId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), demographicId, Permission.ENCOUNTER_NOTE_DELETE);
		return RestResponse.successResponse(tempNoteService.deleteTempNote(getLoggedInProviderId(), demographicId));
	}
}
