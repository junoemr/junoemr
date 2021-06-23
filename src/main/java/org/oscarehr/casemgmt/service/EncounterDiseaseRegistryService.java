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
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.managers.SecurityInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.oscarResearch.oscarDxResearch.bean.dxResearchBean;
import oscar.oscarResearch.oscarDxResearch.bean.dxResearchBeanHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EncounterDiseaseRegistryService extends EncounterSectionService
{
	public static final String SECTION_ID = "Dx";
	protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.n";
	protected static final String SECTION_TITLE_KEY = "oscarEncounter.LeftNavBar.DxRegistry";
	protected static final String SECTION_TITLE_COLOUR = "#5A5A5A";

	@Autowired
	protected SecurityInfoManager securityInfoManager;

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
		return getOnClick(sectionParams);
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		return getOnClick(sectionParams);
	}

	private String getOnClick(SectionParameters sectionParams)
	{
		String winName = "Disease" + sectionParams.getDemographicNo();
		String url = sectionParams.getContextPath() + "/oscarResearch/oscarDxResearch/setupDxResearch.do" +
				"?demographicNo=" + encodeUrlParam(sectionParams.getDemographicNo()) +
				"&providerNo=" + encodeUrlParam(sectionParams.getProviderNo()) +
				"&quickList=";

		return "popupPage(580,900,'" + winName + "','" + url + "');";
	}

	public EncounterNotes getNotes(SectionParameters sectionParams, Integer limit, Integer offset)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		if (!securityInfoManager.hasPrivilege(sectionParams.getLoggedInInfo(),
				"_dxresearch", "r", null))
		{
			return EncounterNotes.noNotes();
		}

		//grab all of the diseases associated with patient and add a list item for each
		dxResearchBeanHandler hd = new dxResearchBeanHandler(sectionParams.getDemographicNo());
		List<dxResearchBean> diseases = hd.getDxResearchBeans();

		for(dxResearchBean dxBean: diseases)
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			if (!dxBean.getStatus().equals("A"))
			{
				continue;
			}

			// Colour
			if (dxBean.getStatus() != null && dxBean.getStatus().equalsIgnoreCase("C"))
			{
				sectionNote.setColour("000000");
			}

			// Date
			String dateStr = dxBean.getEnd_date();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
			LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
			sectionNote.setUpdateDate(date);

			// Title
			sectionNote.setText(EncounterSectionService.getTrimmedText(dxBean.getDescription()));

			// Link title
			sectionNote.setTitle(EncounterSectionService.formatTitleWithLocalDateTime(dxBean.getDescription(), date));

			out.add(sectionNote);
		}

		Collections.sort(out, new EncounterSectionNote.SortChronologic());

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}
}
