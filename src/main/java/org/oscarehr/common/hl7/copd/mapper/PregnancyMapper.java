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
package org.oscarehr.common.hl7.copd.mapper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.model.Episode;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PregnancyMapper extends AbstractMapper
{
	private static final Logger logger = MiscUtils.getLogger();

	public PregnancyMapper(ZPD_ZTR message, int providerRep)
	{
		super(message, providerRep);
	}

	public int getNumPregnancies()
	{
		return provider.getPREGNANCYReps();
	}

	public List<Episode> getPregnancyEpisodes()
	{
		int numPregnancies = getNumPregnancies();
		List<Episode> episodeList = new ArrayList<>(numPregnancies);
		for(int i = 0; i < numPregnancies; i++)
		{
			Episode pregnancyEpisode = getPregnancyEpisode(i);
			if(pregnancyEpisode != null)
			{
				episodeList.add(pregnancyEpisode);
			}
		}
		return episodeList;
	}

	public Episode getPregnancyEpisode(int rep)
	{
		Episode pregnancy = new Episode();
		pregnancy.setCode("72892002"); //TODO how to pick these?
		pregnancy.setCodingSystem("SnomedCore");
		pregnancy.setDescription("Normal pregnancy");
		pregnancy.setNotes(getNote(rep));
		pregnancy.setStatus(getStatus(rep));
		// spec only allows for single due date, so use it for both start and end date
		pregnancy.setStartDate(getDueDate(rep));
		if(isComplete(rep))
		{
			pregnancy.setEndDate(getDueDate(rep));
		}

		return pregnancy;
	}

	public String getNote(int rep)
	{
		return StringUtils.trimToNull(provider.getPREGNANCY(rep).getZPG().getZpg5_DueDateConfirmed().getValue());
	}

	public Date getDueDate(int rep)
	{
		Date dueDate = getNullableDate(provider.getPREGNANCY(rep).getZPG().getZpg4_DueDate().getTs1_TimeOfAnEvent().getValue());
		if(dueDate == null)
		{
			// if there is no date provided, default to obviously wrong (but valid) date
			dueDate = ConversionUtils.toLegacyDate(LocalDate.of(1900, 1, 1));
		}
		return dueDate;
	}

	public String getStatus(int rep)
	{
		String value = StringUtils.trimToEmpty(provider.getPREGNANCY(rep).getZPG().getZpg5_DueDateConfirmed().getValue());
		switch(value)
		{
			case "No Date": return Episode.STATUS_CURRENT;
			case "Delivery":
			default: return Episode.STATUS_COMPLETE;
		}
	}

	public boolean isComplete(int rep)
	{
		return Episode.STATUS_COMPLETE.equals(getStatus(rep));
	}
}
