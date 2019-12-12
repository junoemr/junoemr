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

package org.oscarehr.casemgmt.service;

import org.apache.log4j.Logger;
import org.oscarehr.casemgmt.dto.EncounterHeader;
import org.oscarehr.casemgmt.dto.EncounterSection;
import org.oscarehr.casemgmt.model.ClientImage;
import org.oscarehr.common.model.EncounterTemplate;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
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
	WebApplicationContext appContext;

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
			case EncounterSection.TYPE_HEALTH_CARE_TEAM: return encounterTeamService;
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

	public EncounterHeader getEncounterHeader(
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
			List<EncounterTemplate> encounterTemplates
	)
			throws UnsupportedEncodingException
	{
		EncounterHeader encounterHeader = new EncounterHeader();


		// User Colour

		ProviderColourUpdater colourUpdater = new ProviderColourUpdater(providerNo);
		String userColour = colourUpdater.getColour();

		if( userColour == null || userColour.length() == 0 )
		{
			userColour = "#CCCCFF";   //default blue if no preference set
		}

		encounterHeader.setUserColour(userColour);


		// Inverse user colour

		int base = 16;
		//strip leading # sign and convert
		int num = Integer.parseInt(userColour.substring(1), base);
		//get inverse
		int inv = ~num;
		//strip 2 leading digits as html colour codes are 24bits
		String inverseUserColour = "#" + Integer.toHexString(inv).substring(2);

		encounterHeader.setInverseUserColour(inverseUserColour);

		encounterHeader.setRoleName(roleName);


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
			if (familyDoctorColour.length() == 0)
			{
				familyDoctorColour= "#CCCCFF";
			}
		}

		encounterHeader.setFamilyDoctorFirstName(familyDoctorFirstName);
		encounterHeader.setFamilyDoctorLastName(familyDoctorLastName);
		encounterHeader.setFamilyDoctorColour(familyDoctorColour);

		// Demographic link dat
		String windowName = "Master" + demographicNo;

		encounterHeader.setWindowName(windowName);

		String demographicUrl = contextPath + "/demographic/demographiccontrol.jsp" +
				"?demographic_no=" + URLEncoder.encode(demographicNo, StandardCharsets.UTF_8.name()) +
				"&displaymode=edit" +
				"&dboperation=search_detail" +
				"&appointment=" + URLEncoder.encode(appointmentNo, StandardCharsets.UTF_8.name());

		encounterHeader.setDemographicUrl(demographicUrl);

		encounterHeader.setDemographicNo(demographicNo);

		encounterHeader.setProviderNo(providerNo);

		encounterHeader.setPatientFirstName(patientFirstName);
		encounterHeader.setPatientLastName(patientLastName);
		encounterHeader.setPatientSex(patientSex);
		encounterHeader.setPatientAge(patientAge);
		encounterHeader.setPatientAgeInYears(patientAgeInYears);
		encounterHeader.setPatientBirthdate(patientBirthdate);
		encounterHeader.setPatientPhone(phone);

		encounterHeader.setEchartAdditionalPatientInfoEnabled(OscarProperties.getInstance().isEChartAdditionalPatientInfoEnabled());

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

		encounterHeader.setDemographicAdditionalInfoUrl(demographicAdditionalInfoUrl);

		encounterHeader.setEchartUuid(echartUuid);

		encounterHeader.setReferringDoctorName(referringDoctorName);
		encounterHeader.setReferringDoctorNumber(referringDoctorNumber);
		encounterHeader.setRosterDateEnabled(rosterDateEnabled);
		if(rosterDate != null)
		{
			encounterHeader.setRosterDateString(rosterDate.toString());
		}

		encounterHeader.setIncomingRequestorSet(
				oscar.OscarProperties.getInstance().hasProperty("ONTARIO_MD_INCOMINGREQUESTOR"));

		String diseaseListUrl = contextPath + "/common/omdDiseaseList.jsp" +
				"?sex=" + URLEncoder.encode(patientSex, StandardCharsets.UTF_8.name()) +
				"&age=" + URLEncoder.encode(patientAge, StandardCharsets.UTF_8.name());

		encounterHeader.setDiseaseListUrl(diseaseListUrl);

		encounterHeader.setEchartLinks(getEChartLinks());

		encounterHeader.setImagePresentPlaceholderUrl(contextPath + ClientImage.imagePresentPlaceholderUrl);
		encounterHeader.setImageMissingPlaceholderUrl(contextPath + ClientImage.imageMissingPlaceholderUrl);

		encounterHeader.setAppointmentNo(appointmentNo);
		encounterHeader.setSource(source);
		encounterHeader.setEncounterNoteHideBeforeDate(encounterNoteHideBeforeDate);

		encounterHeader.setBillingUrl(billingUrl);

		encounterHeader.setCmeJs(cmeJs);

		encounterHeader.setEncounterTemplates(encounterTemplates);

		return encounterHeader;
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
