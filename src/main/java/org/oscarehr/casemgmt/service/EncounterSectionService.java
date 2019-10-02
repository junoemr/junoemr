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

import org.drools.FactException;
import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSection;
import org.oscarehr.util.LoggedInInfo;


public abstract class EncounterSectionService
{
	protected static final String ELLIPSES = "...";
	protected static final int MAX_LEN_TITLE = 48;
	protected static final int CROP_LEN_TITLE = 45;
	protected static final int MAX_LEN_KEY = 12;
	protected static final int CROP_LEN_KEY = 9;

	protected static final String COLOUR_HIGHLITE = "#FF0000";
	protected static final String COLOUR_INELLIGIBLE = "#FF6600";
	protected static final String COLOUR_PENDING = "#FF00FF";
	protected static final String COLOUR_WARNING = "#FFA500";

	public static final int INITIAL_ENTRIES_TO_SHOW = 6;
	public static final int INITIAL_OFFSET = 0;

	public abstract EncounterNotes getNotes(
			LoggedInInfo loggedInInfo,
			String roleName,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String programId,
			Integer limit,
			Integer offset
	) throws FactException;

	/*
	public int getNoteCount(
			LoggedInInfo loggedInInfo,
			String roleName,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String programId
	)
	{
		return 0;
	}
	 */

	public EncounterSection getInitialSection(
			LoggedInInfo loggedInInfo,
			String roleName,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String programId,
			String title,
			String colour
	) throws FactException
	{
		return getSection(
				loggedInInfo,
				roleName,
				providerNo,
				demographicNo,
				appointmentNo,
				programId,
				title,
				colour,
				INITIAL_ENTRIES_TO_SHOW,
				INITIAL_OFFSET
		);
	}

	public EncounterSection getSection(
			LoggedInInfo loggedInInfo,
			String roleName,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String programId,
			String title,
			String colour,
			Integer limit,
			Integer offset
	) throws FactException
	{
		EncounterSection section = new EncounterSection();

		section.setTitle(title);
		section.setColour(colour);
		section.setCppIssues("");
		section.setAddUrl("");
		section.setIdentUrl("");

		EncounterNotes notes = getNotes(
				loggedInInfo,
				roleName,
				providerNo,
				demographicNo,
				appointmentNo,
				programId,
				limit,
				offset
		);

		section.setNotes(notes.getEncounterSectionNotes());

		section.setRemainingNotes(notes.getNoteCount() - notes.getEncounterSectionNotes().size());

		/*
		// Ask for one more note than is required.  If the full amount is returned, show the
		// controls to show all notes, and remove it from the results.
		if(notes.size() > INITIAL_ENTRIES_TO_SHOW)
		{
			notes.remove(notes.size() - 1);
			section.setShowingPartialNoteList(true);
		}
		 */


		return section;
	}
}
