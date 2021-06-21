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

package org.oscarehr.casemgmt.service;

import com.quatro.model.security.Secrole;
import org.apache.log4j.Logger;
import org.oscarehr.casemgmt.dto.EncounterPageData;
import org.oscarehr.casemgmt.dto.EncounterSection;
import org.oscarehr.casemgmt.model.ClientImage;
import org.oscarehr.common.model.EncounterTemplate;
import org.oscarehr.common.model.Provider;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.to.model.CaseManagementIssueTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.OscarProperties;
import oscar.oscarEncounter.data.EctProviderData;
import oscar.oscarProvider.data.ProviderColourUpdater;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Collects data required for the header of the encounter page.
 */
@Service
public class EncounterService
{
	Logger logger = MiscUtils.getLogger();

	@Autowired
	private EncounterPreventionNoteService encounterPreventionNoteService;

	@Autowired
	private EncounterTicklerService encounterTicklerService;

	@Autowired
	private EncounterDiseaseRegistryService encounterDiseaseRegistryService;

	@Autowired
	private EncounterFormService encounterFormService;

	@Autowired
	private EncounterEFormService encounterEFormService;

	@Autowired
	private EncounterDocumentService encounterDocumentService;

	@Autowired
	private EncounterLabResultService encounterLabResultService;

	@Autowired
	private EncounterMessengerService encounterMessengerService;

	@Autowired
	private EncounterMeasurementsService encounterMeasurementsService;

	@Autowired
	private EncounterConsultationService encounterConsultationService;

	@Autowired
	private EncounterAllergyService encounterAllergyService;

	@Autowired
	private EncounterMedicationService encounterMedicationService;

	@Autowired
	private EncounterUnresolvedIssueService encounterUnresolvedIssueService;

	@Autowired
	private EncounterResolvedIssueService encounterResolvedIssueService;

	@Autowired
	private EncounterEpisodeService encounterEpisodeService;

	@Autowired
	private EncounterPregnancyService encounterPregnancyService;

	@Autowired
	private EncounterTeamService encounterTeamService;

	@Autowired
	private EncounterSocialHistoryService encounterSocialHistoryService;

	@Autowired
	private EncounterMedicalHistoryService encounterMedicalHistoryService;

	@Autowired
	private EncounterOngoingConcernsService encounterOngoingConcernsService;

	@Autowired
	private EncounterRemindersService encounterRemindersService;

	@Autowired
	private EncounterOtherMedsService encounterOtherMedsService;

	@Autowired
	private EncounterRiskFactorsService encounterRiskFactorsService;

	@Autowired
	private EncounterFamilyHistoryService encounterFamilyHistoryService;

	@Autowired
	private EncounterHRMService encounterHRMService;

	public EncounterSectionService getEncounterSectionServiceByName(String serviceName)
	{
		switch(serviceName)
		{
			case EncounterSection.TYPE_PREVENTIONS: return encounterPreventionNoteService;
			case EncounterSection.TYPE_TICKLER: return encounterTicklerService;
			case EncounterSection.TYPE_DISEASE_REGISTRY: return encounterDiseaseRegistryService;
			case EncounterSection.TYPE_FORMS: return encounterFormService;
			case EncounterSection.TYPE_EFORMS: return encounterEFormService;
			case EncounterSection.TYPE_DOCUMENTS: return encounterDocumentService;
			case EncounterSection.TYPE_LAB_RESULTS: return encounterLabResultService;
			case EncounterSection.TYPE_MESSENGER: return encounterMessengerService;
			case EncounterSection.TYPE_MEASUREMENTS: return encounterMeasurementsService;
			case EncounterSection.TYPE_CONSULTATIONS: return encounterConsultationService;
			case EncounterSection.TYPE_ALLERGIES: return encounterAllergyService;
			case EncounterSection.TYPE_MEDICATIONS: return encounterMedicationService;
			case EncounterSection.TYPE_UNRESOLVED_ISSUES: return encounterUnresolvedIssueService;
			case EncounterSection.TYPE_RESOLVED_ISSUES: return encounterResolvedIssueService;
			case EncounterSection.TYPE_EPISODES: return encounterEpisodeService;
			case EncounterSection.TYPE_PREGNANCIES: return encounterPregnancyService;
			case EncounterSection.TYPE_HEALTH_CARE_TEAM: return encounterTeamService;
			case EncounterSection.TYPE_HRM: return encounterHRMService;
			/*
			case encounterSocialHistoryService.SECTION_ID: return encounterSocialHistoryService;
			case encounterMedicalHistoryService.SECTION_ID: return encounterMedicalHistoryService;
			case encounterOngoingConcernsService.SECTION_ID: return encounterOngoingConcernsService;
			case encounterRemindersService.SECTION_ID: return encounterRemindersService;
			 */
		}

		// XXX: Is this good?
		List<EncounterSectionService> serviceList = new ArrayList<>();
		serviceList.add(encounterSocialHistoryService);
		serviceList.add(encounterMedicalHistoryService);
		serviceList.add(encounterOngoingConcernsService);
		serviceList.add(encounterRemindersService);
		serviceList.add(encounterOtherMedsService);
		serviceList.add(encounterRiskFactorsService);
		serviceList.add(encounterFamilyHistoryService);

		for(EncounterSectionService sectionService: serviceList)
		{
			if(serviceName.equals(sectionService.getSectionId()))
			{
				return sectionService;
			}
		}


		throw new IllegalArgumentException(String.format(
				"EncounterSectionService identified by %s doesn't exist.", serviceName));
	}

	public EncounterPageData getEncounterHeader(
			String providerNo,
			String loggedInProviderNo,
			String roleName,
			String demographicNo,
			String familyDoctorNo,
			String patientFirstName,
			String patientLastName,
			String patientSex,
			String patientAge,
			String patientAgeInYears,
			String patientBirthdate,
			String phone,
			String referringDoctorName,
			String referringDoctorNumber,
			String echartUuid,
			boolean rosterDateEnabled,
			Date rosterDate,
			String appointmentNo,
			String contextPath,
			String source,
			Date encounterNoteHideBeforeDate,
			String billingUrl,
			String cmeJs,
			List<EncounterTemplate> encounterTemplates,
			List<CaseManagementIssueTo1> issues,
			List<Provider> providers,
			List<Secrole> roles,
			boolean encounterWindowCustomSize,
			String encounterWindowHeight,
			String encounterWindowWidth,
			boolean encounterWindowMaximize,
			boolean clientImagePresent
	)
			throws UnsupportedEncodingException
	{
		EncounterPageData encounterPageData = new EncounterPageData();


		// User Colour

		ProviderColourUpdater colourUpdater = new ProviderColourUpdater(providerNo);
		String userColour = colourUpdater.getColour();

		if( userColour == null || userColour.length() == 0 )
		{
			userColour = "#CCCCFF";   //default blue if no preference set
		}

		encounterPageData.setUserColour(userColour);


		// Inverse user colour

		int base = 16;
		//strip leading # sign and convert
		int num = Integer.parseInt(userColour.substring(1), base);
		//get inverse
		int inv = ~num;
		//strip 2 leading digits as html colour codes are 24bits
		String inverseUserColour = "#" + Integer.toHexString(inv).substring(2);

		encounterPageData.setInverseUserColour(inverseUserColour);

		encounterPageData.setRoleName(roleName);


		// Family Doctor information
		String familyDoctorFirstName = "";
		String familyDoctorLastName= "";
		String familyDoctorColour = "";
		if (familyDoctorNo != null && !familyDoctorNo.equals(""))
		{

			EctProviderData.Provider prov =
					new EctProviderData().getProvider(familyDoctorNo);

			if(prov != null || prov.getFirstName() != null)
			{
				familyDoctorFirstName = prov.getFirstName();
			}

			if(prov != null || prov.getSurname() != null)
			{
				familyDoctorLastName = prov.getSurname();
			}

			colourUpdater = new ProviderColourUpdater(familyDoctorNo);
			familyDoctorColour = colourUpdater.getColour();
			if (familyDoctorColour == null || familyDoctorColour.length() == 0)
			{
				familyDoctorColour= "#CCCCFF";
			}
		}

		encounterPageData.setFamilyDoctorFirstName(familyDoctorFirstName);
		encounterPageData.setFamilyDoctorLastName(familyDoctorLastName);
		encounterPageData.setFamilyDoctorColour(familyDoctorColour);

		// Demographic link dat
		String windowName = "Master" + demographicNo;

		encounterPageData.setWindowName(windowName);

		String demographicUrl = contextPath + "/demographic/demographiccontrol.jsp" +
				"?demographic_no=" + URLEncoder.encode(demographicNo, StandardCharsets.UTF_8.name()) +
				"&displaymode=edit" +
				"&dboperation=search_detail" +
				"&appointment=" + URLEncoder.encode(appointmentNo, StandardCharsets.UTF_8.name());

		encounterPageData.setDemographicUrl(demographicUrl);

		encounterPageData.setDemographicNo(demographicNo);

		encounterPageData.setProviderNo(providerNo);

		encounterPageData.setPatientFirstName(patientFirstName);
		encounterPageData.setPatientLastName(patientLastName);
		encounterPageData.setPatientSex(patientSex);
		encounterPageData.setPatientAge(patientAge);
		encounterPageData.setPatientAgeInYears(patientAgeInYears);
		encounterPageData.setPatientBirthdate(patientBirthdate);
		encounterPageData.setPatientPhone(phone);

		encounterPageData.setEchartAdditionalPatientInfoEnabled(OscarProperties.getInstance().isEChartAdditionalPatientInfoEnabled());

		String demographicAdditionalInfoUrl = contextPath + "/demographic/demographiccontrol.jsp" +
				"?demographic_no=" + URLEncoder.encode(demographicNo, StandardCharsets.UTF_8.name()) +
				"&last_name=" + URLEncoder.encode(
						patientLastName.replaceAll("'", "\\\\'"),
						StandardCharsets.UTF_8.name()) +
				"&first_name=" + URLEncoder.encode(
						patientFirstName.replaceAll("'", "\\\\'"),
						StandardCharsets.UTF_8.name()) +
				"&orderby=appointment_date" +
				"&displaymode=appt_history" +
				"&dboperation=appt_history" +
				"&limit1=0" +
				"&limit2=25";

		encounterPageData.setDemographicAdditionalInfoUrl(demographicAdditionalInfoUrl);

		encounterPageData.setEchartUuid(echartUuid);

		encounterPageData.setReferringDoctorName(referringDoctorName);
		encounterPageData.setReferringDoctorNumber(referringDoctorNumber);
		encounterPageData.setRosterDateEnabled(rosterDateEnabled);
		if(rosterDate != null)
		{
			encounterPageData.setRosterDateString(rosterDate.toString());
		}

		encounterPageData.setIncomingRequestorSet(
				oscar.OscarProperties.getInstance().hasProperty("ONTARIO_MD_INCOMINGREQUESTOR"));

		String diseaseListUrl = contextPath + "/common/omdDiseaseList.jsp" +
				"?sex=" + URLEncoder.encode(patientSex, StandardCharsets.UTF_8.name()) +
				"&age=" + URLEncoder.encode(patientAge, StandardCharsets.UTF_8.name());

		encounterPageData.setDiseaseListUrl(diseaseListUrl);

		encounterPageData.setEchartLinks(getEChartLinks());

		encounterPageData.setClientImagePresent(clientImagePresent);
		encounterPageData.setImagePresentPlaceholderUrl(contextPath + ClientImage.imagePresentPlaceholderUrl);
		encounterPageData.setImageMissingPlaceholderUrl(contextPath + ClientImage.imageMissingPlaceholderUrl);

		encounterPageData.setAppointmentNo(appointmentNo);
		encounterPageData.setSource(source);
		encounterPageData.setEncounterNoteHideBeforeDate(encounterNoteHideBeforeDate);

		encounterPageData.setBillingUrl(billingUrl);

		encounterPageData.setCmeJs(cmeJs);

		encounterPageData.setEncounterTemplates(encounterTemplates);

		encounterPageData.setCaseManagementIssues(issues);

		encounterPageData.setProviders(providers);

		encounterPageData.setRoles(roles);

		encounterPageData.setEncounterWindowCustomSize(encounterWindowCustomSize);
		encounterPageData.setEncounterWindowHeight(encounterWindowHeight);
		encounterPageData.setEncounterWindowWidth(encounterWindowWidth);
		encounterPageData.setEncounterWindowMaximize(encounterWindowMaximize);

		encounterPageData.setLinkToOldEncounterPageEnabled(
				OscarProperties.getInstance().isJunoEncounterLinkToOldEncounterPageEnabled());

		return encounterPageData;
	}

	private String getEChartLinks()
	{
		String str = oscar.OscarProperties.getInstance().getProperty("ECHART_LINK");
		if (str == null){
			return "";
		}

		try
		{
			String[] httpLink = str.split("\\|");
			return "<a target=\"_blank\" href=\""+httpLink[1]+"\">"+httpLink[0]+"</a>";
		}
		catch(Exception e)
		{
			MiscUtils.getLogger().error("ECHART_LINK is not in the correct format. title|url :"+str, e);
		}

		return "";
	}
}
