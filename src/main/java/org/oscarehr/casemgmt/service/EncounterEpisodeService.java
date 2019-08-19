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

import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.common.dao.EpisodeDao;
import org.oscarehr.common.model.Episode;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.oscarEncounter.pageUtil.NavBarDisplayDAO;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EncounterEpisodeService extends EncounterSectionService
{
	public List<EncounterSectionNote> getNotes(
			LoggedInInfo loggedInInfo,
			String roleName,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String programId)
	{
		// XXX: as per EctDisplayEpisodeAction, this file appears to ignore permissions, so I have
		//      left the check out.

		List<EncounterSectionNote> out = new ArrayList<>();

		try
		{
			//Set lefthand module heading and link
			//String winName = "episode" + bean.demographicNo;
			//String pathview, pathedit;

			//pathview = request.getContextPath() + "/Episode.do?method=list&demographicNo=" + bean.demographicNo;
			//pathedit = request.getContextPath() + "/Episode.do?method=edit&demographicNo=" + bean.demographicNo;


			//String url = "popupPage(500,900,'" + winName + "','" + pathview + "')";
			//Dao.setLeftHeading(messages.getMessage(request.getLocale(), "global.episode"));
			//Dao.setLeftURL(url);

			//set right hand heading link
			//winName = "AddEpisode" + bean.demographicNo;
			//url = "popupPage(500,600,'" + winName + "','" + pathedit + "'); return false;";
			//Dao.setRightURL(url);
			//Dao.setRightHeadingID(cmd);


			EpisodeDao episodeDao = SpringUtils.getBean(EpisodeDao.class);
			List<Episode> episodes = episodeDao.findAllCurrent(Integer.parseInt(demographicNo));

			for(Episode episode:episodes)
			{
				NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
				String itemHeader = StringUtils.maxLenString(episode.getDescription(), MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);

				EncounterSectionNote sectionNote = new EncounterSectionNote();
				sectionNote.setText(itemHeader);
				sectionNote.setUpdateDate(ConversionUtils.toNullableLocalDateTime(episode.getStartDate()));

				//item.setLinkTitle(itemHeader);
				//item.setTitle(itemHeader);
				//item.setDate(episode.getStartDate());

				//int hash = Math.abs(winName.hashCode());
				//url = "popupPage(500,900,'" + hash + "','" + request.getContextPath() + "/Episode.do?method=edit&episode.id="+ episode.getId() +"'); return false;";
				//item.setURL(url);

				out.add(sectionNote);
			}

		}
		catch( Exception e )
		{
			MiscUtils.getLogger().error("Error", e);
			return new ArrayList<>();
		}

		return out;
	}
}
