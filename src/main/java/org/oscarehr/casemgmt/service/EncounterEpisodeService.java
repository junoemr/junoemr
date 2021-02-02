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
import org.oscarehr.common.dao.EpisodeDao;
import org.oscarehr.common.model.Episode;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

public class EncounterEpisodeService extends EncounterSectionService
{
	public static final String SECTION_ID = "episode";
	private static final String SECTION_TITLE_KEY = "global.episode";
	private static final String SECTION_TITLE_COLOUR = "#045228";
	private static final String WIN_NAME = "AddEpisode";

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
		String winName = WIN_NAME + sectionParams.getDemographicNo();
		String url = sectionParams.getContextPath() + "/Episode.do" +
				"?method=edit" +
				"&demographicNo=" + encodeUrlParam(sectionParams.getDemographicNo());

		return "popupPage(500,600,'" + winName + "','" + url + "');";
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		String winName = "episode" + sectionParams.getDemographicNo();
		String url = sectionParams.getContextPath() + "/Episode.do" +
				"?method=list" +
				"&demographicNo=" + encodeUrlParam(sectionParams.getDemographicNo());

		return "popupPage(500,900,'" + winName + "','" + url + "');";
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		// XXX: as per EctDisplayEpisodeAction, this file appears to ignore permissions, so I have
		//      left the check out.

		List<EncounterSectionNote> out = new ArrayList<>();

		try
		{
			EpisodeDao episodeDao = SpringUtils.getBean(EpisodeDao.class);
			List<Episode> episodes = episodeDao.findAllCurrent(Integer.parseInt(sectionParams.getDemographicNo()));

			for(Episode episode:episodes)
			{
				String text = EncounterSectionService.getTrimmedText(episode.getDescription());

				EncounterSectionNote sectionNote = new EncounterSectionNote();
				sectionNote.setText(text);
				sectionNote.setTitle(episode.getDescription());
				sectionNote.setUpdateDate(ConversionUtils.toNullableLocalDateTime(episode.getStartDate()));

				String winName = WIN_NAME + sectionParams.getDemographicNo();
				int hash = Math.abs(winName.hashCode());
				String url = sectionParams.getContextPath() + "/Episode.do" +
						"?method=edit" +
						"&episode.id=" + encodeUrlParam(episode.getId().toString());
				String onClickString = "popupPage(500,900,'" + hash + "','" + url +"');";
				sectionNote.setOnClick(onClickString);

				out.add(sectionNote);
			}
		}
		catch( Exception e )
		{
			MiscUtils.getLogger().error("Error", e);
			return EncounterNotes.noNotes();
		}

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}
}
