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

package org.oscarehr.ws.rest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.casemgmt.dto.EncounterSection;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.casemgmt.exception.EncounterSectionException;
import org.oscarehr.casemgmt.service.EncounterDocumentService;
import org.oscarehr.casemgmt.service.EncounterEFormService;
import org.oscarehr.casemgmt.service.EncounterFormService;
import org.oscarehr.casemgmt.service.EncounterSectionService;
import org.oscarehr.casemgmt.service.EncounterService;
import org.oscarehr.casemgmt.service.MultiSearchResult;
import org.oscarehr.common.dao.EncounterTemplateDao;
import org.oscarehr.common.model.EncounterTemplate;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import oscar.oscarEncounter.data.EctProgram;
import oscar.oscarEncounter.pageUtil.EctSessionBean;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Path("/encounterSections")
@Component("EncounterSectionsService")
public class EncounterSectionsService extends AbstractServiceImpl
{
	Logger logger = Logger.getLogger(EFormsService.class);

	private static final long MULTISEARCH_RESULT_COUNT = 10;

	@Context
	ServletContext context;

	@Context
	HttpServletRequest request;

	@Autowired
	private EncounterService encounterService;

	@Autowired
	private EncounterFormService encounterFormService;

	@Autowired
	private EncounterEFormService encounterEFormService;

	@Autowired
	private EncounterDocumentService encounterDocumentService;

	@Autowired
	@Qualifier("service_EFormService")
	private org.oscarehr.eform.service.EFormService eFormService;

	@Autowired
	@Qualifier("service_FormService")
	private org.oscarehr.form.service.FormService formService;

	@Autowired
	private EncounterTemplateDao encounterTemplateDao;

	@GET
	@Path("/{demographicNo}/section/{sectionName}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<EncounterSection> getEncounterSection(
			@PathParam("demographicNo") Integer demographicNo,
			@PathParam("sectionName") String sectionName,
			@QueryParam("appointmentNo") String appointmentNo,
			@QueryParam("eChartUUID") String eChartUUID,
			@QueryParam("limit") Integer limit,
			@QueryParam("offset") Integer offset
	)
			throws EncounterSectionException
	{
		EncounterSectionService sectionService = encounterService.getEncounterSectionServiceByName(sectionName);

		EncounterSectionService.SectionParameters sectionParams = getSectionParams(appointmentNo, eChartUUID);

		return RestResponse.successResponse(sectionService.getSection(
				sectionParams,
				limit,
				offset
		));
	}

	@GET
	@Path("/{demographicNo}/autocomplete/{searchTerm}")
	@Produces({MediaType.APPLICATION_JSON})
	public RestResponse<List<MultiSearchResult>> getAutocompleteResults(
			@PathParam("demographicNo") Integer demographicNo,
			@PathParam("searchTerm") String searchTerm,
			@QueryParam("appointmentNo") String appointmentNo
	)
			throws EncounterSectionException
	{
		EncounterSectionService.SectionParameters sectionParams = getSectionParams(appointmentNo, null);

		// This might need to be faster.
		// It gets all of the values for the sections being searched (documents, eforms, forms)
		// then filters them.

		// Get patient's documents
		EncounterSection documentSection = encounterDocumentService.getSection(sectionParams, null, null);

		// Get patient's eforms
		EncounterSection eformSection = encounterEFormService.getSection(sectionParams, null, null);

		// Get patient's forms
		EncounterSection formSection = encounterFormService.getSection(sectionParams, null, null);

		// Get eforms
		List<MultiSearchResult> eforms = eFormService.getEFormsForSearch(
				sectionParams.getContextPath(), demographicNo, appointmentNo);

		// Get forms
		List<MultiSearchResult> forms = formService.getFormsForSearch(
				sectionParams.getContextPath(), demographicNo, appointmentNo,
				sectionParams.getProviderNo());

		List<MultiSearchResult> results = new ArrayList<>();

		for(EncounterTemplate template : encounterTemplateDao.findAll())
		{
			// XXX: Hack: put the templates in an EncounterSectionNote
			EncounterSectionNote note = new EncounterSectionNote();
			note.setText(template.getEncounterTemplateName());
			note.setOnClick("junoEncounter.ajaxInsertTemplate('" + template.getEncounterTemplateName() + "');");
			results.add(note);
		}

		results.addAll(documentSection.getNotes());
		results.addAll(eformSection.getNotes());
		results.addAll(formSection.getNotes());
		results.addAll(eforms);
		results.addAll(forms);

		results.sort(new EncounterSectionNote.SortAlphabetic());

		// Filter results based on the search term
		List<MultiSearchResult> filteredResults = new ArrayList<>();
		List<MultiSearchResult> restOfResults = new ArrayList<>();
		for(MultiSearchResult result: results)
		{
			if(StringUtils.startsWithIgnoreCase(result.getText(), searchTerm))
			{
				filteredResults.add(result);
			}
			else if(StringUtils.containsIgnoreCase(result.getText(), searchTerm))
			{
				restOfResults.add(result);
			}
		}

		Collections.sort(filteredResults);
		Collections.sort(restOfResults);

		filteredResults.addAll(restOfResults);

		return RestResponse.successResponse(
				filteredResults.stream().limit(MULTISEARCH_RESULT_COUNT).collect(Collectors.toList()));
	}

	private EncounterSectionService.SectionParameters getSectionParams(String appointmentNo, String eChartUUID)
	{
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
		HttpSession session = loggedInInfo.getSession();

		Locale locale = request.getLocale();

		String contextPath = context.getContextPath();

		EctProgram prgrmMgr = new EctProgram(session);
		String programId = prgrmMgr.getProgram(loggedInProviderNo);

		String roleName = session.getAttribute("userrole") + "," + session.getAttribute("user");

		// XXX: Ew, don't like doing this
		EctSessionBean encounterSessionBean =
				(EctSessionBean) session.getAttribute("EctSessionBean");

		EncounterSectionService.SectionParameters sectionParams =
				new EncounterSectionService.SectionParameters();

		sectionParams.setLoggedInInfo(loggedInInfo);
		sectionParams.setLocale(locale);
		sectionParams.setContextPath(contextPath);
		sectionParams.setRoleName(roleName);
		sectionParams.setProviderNo(loggedInProviderNo);
		sectionParams.setDemographicNo(encounterSessionBean.demographicNo);
		sectionParams.setPatientFirstName(encounterSessionBean.patientFirstName);
		sectionParams.setPatientLastName(encounterSessionBean.patientLastName);
		sectionParams.setFamilyDoctorNo(encounterSessionBean.familyDoctorNo);
		sectionParams.setAppointmentNo(appointmentNo);
		sectionParams.setChartNo(encounterSessionBean.chartNo);
		sectionParams.setProgramId(programId);
		sectionParams.setUserName(encounterSessionBean.userName);
		sectionParams.seteChartUUID(eChartUUID);

		return sectionParams;
	}
}
