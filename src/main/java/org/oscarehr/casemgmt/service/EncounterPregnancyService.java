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

import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSectionMenuItem;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.common.dao.EpisodeDao;
import org.oscarehr.common.model.Episode;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.OscarProperties;
import oscar.oscarEncounter.data.EctFormData;
import oscar.util.ConversionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class EncounterPregnancyService extends EncounterSectionService
{
	public static final String SECTION_ID = "pregnancy";
	private static final String SECTION_TITLE_KEY = "global.pregnancy";
	private static final String SECTION_TITLE_COLOUR = "#045228";
	private static final String SECTION_MENU_HEADER_KEY = "oscarEncounter.NavBar.PregnancyType";

	@Autowired
	private EpisodeDao episodeDao;

	@Override
	public String getSectionId()
	{
		return SECTION_ID;
	}

	@Override
	protected String getSectionTitleKey()
	{
		return SECTION_TITLE_KEY;
	}

	@Override
	protected String getSectionTitleColour()
	{
		return SECTION_TITLE_COLOUR;
	}

	@Override
	protected String getOnClickPlus(SectionParameters sectionParams)
	{
		return "";
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		String winName = "pregnancy" + sectionParams.getDemographicNo();
		String pathview = sectionParams.getContextPath() + "/Pregnancy.do" +
				"?method=list" +
				"&demographicNo=" + encodeUrlParam(sectionParams.getDemographicNo());

		return "popupPage(500,900,'" + winName + "','" + pathview + "')";
	}

	@Override
	protected String getMenuId()
	{
		return SECTION_ID;
	}

	@Override
	protected String getMenuHeaderKey()
	{
		return SECTION_MENU_HEADER_KEY;
	}

	@Override
	protected List<EncounterSectionMenuItem> getMenuItems(SectionParameters sectionParams)
	{
		String winName = "AddPregnancy" + sectionParams.getDemographicNo();

		List<EncounterSectionMenuItem> menuItems = new ArrayList<>();

		addMenuItem(
				menuItems,
				null,
				"oscarEncounter.NavBar.PregnancyType.normal",
				getUrl(sectionParams, "72892002", winName)
		);

		addMenuItem(
				menuItems,
				null,
				"oscarEncounter.NavBar.PregnancyType.highRisk",
				getUrl(sectionParams, "47200007", winName)
		);

		addMenuItem(
				menuItems,
				null,
				"oscarEncounter.NavBar.PregnancyType.multiple",
				getUrl(sectionParams, "16356006", winName)
		);

		addMenuItem(
				menuItems,
				null,
				"oscarEncounter.NavBar.PregnancyType.ectopic",
				getUrl(sectionParams, "34801009", winName)
		);

		//check to see if they have an onar2005 form
		if(OscarProperties.getInstance().isOntarioInstanceType())
		{
			EctFormData.PatientForm[] pforms = EctFormData.getPatientForms(sectionParams.getDemographicNo(), "formONAR");
			EctFormData.PatientForm[] eforms = EctFormData.getPatientForms(sectionParams.getDemographicNo(), "formONAREnhancedRecord");

			if(pforms.length>0 && eforms.length == 0)
			{
				addMenuItem(
						menuItems,
						"Migration tool",
						null,
						"popupPage(700,1000,'"+ winName + "', '" + sectionParams.getContextPath() + "/Pregnancy.do" +
								"?method=migrate" +
								"&demographicNo=" + sectionParams.getDemographicNo() +
								"')"
				);
			}
		}

		return menuItems;
	}

	private String getUrl(SectionParameters sectionParams, String code, String winName)
	{
		return "popupPage(700,1000,'"+ winName +"', '"+ sectionParams.getContextPath() +"/Pregnancy.do" +
				"?method=create" +
				"&code=" + encodeUrlParam(code) +
				"&codetype=SnomedCore" +
				"&demographicNo="+ encodeUrlParam(sectionParams.getDemographicNo()) +
				"&appointment="+ encodeUrlParam(sectionParams.getAppointmentNo()) +
				"')";
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		String winName = "pregnancy" + sectionParams.getDemographicNo();

		//check for an existing pregnancy
		List<String> codes = new ArrayList<String>();
		codes.add("72892002");
		codes.add("47200007");
		codes.add("16356006");
		codes.add("34801009");

		List<Episode> existingCurEpisodes = episodeDao.findCurrentByCodeTypeAndCodes(
				Integer.parseInt(sectionParams.getDemographicNo()),
				"SnomedCore",
				codes
		);

		if(existingCurEpisodes.size() > 0)
		{
			Episode episode = existingCurEpisodes.get(0);

			int hash = Math.abs(winName.hashCode());
			String url = sectionParams.getContextPath() + "/Pregnancy.do" +
					"?method=complete" +
					"&episodeId="+ encodeUrlParam(episode.getId().toString());

			String onClickString = "popupPage(500,900,'" + hash + "','" + url +"'); return false;";

			EncounterSectionNote sectionNote = new EncounterSectionNote();
			sectionNote.setText(episode.getDescription());
			sectionNote.setTitle("");
			sectionNote.setUpdateDate(LocalDateTime.ofInstant(episode.getStartDate().toInstant(), ZoneId.systemDefault()));
			sectionNote.setOnClick(onClickString);

			out.add(sectionNote);
		}

		List<Episode> existingPastEpisodes = episodeDao.findCompletedByCodeTypeAndCodes(
				Integer.parseInt(sectionParams.getDemographicNo()),
				"SnomedCore",
				codes
		);

		String red = "red";
		for(Episode episode:existingPastEpisodes)
		{
			String onClickString = "return false;";

			String linkTitle = "";
			if(episode.getNotes() != null)
			{
				linkTitle = episode.getNotes();
			}

			LocalDateTime date = ConversionUtils.toLocalDateTime(episode.getStartDate());

			EncounterSectionNote sectionNote = new EncounterSectionNote();
			sectionNote.setText(EncounterSectionService.getTrimmedText(episode.getDescription()));
			sectionNote.setTitle(linkTitle);
			sectionNote.setColour(COLOUR_RED);
			sectionNote.setUpdateDate(date);
			sectionNote.setOnClick(onClickString);

			out.add(sectionNote);
		}

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}
}
