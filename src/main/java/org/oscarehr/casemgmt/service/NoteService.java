/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.casemgmt.service;

import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.to.model.NoteSelectionTo1;

/**
 * Defines contract for retrieving notes for a demographic record.
 */
public interface NoteService {

	/**
	 * Searches for demographic notes. 
	 * 
	 * @param criteria
	 * 		Criteria to be used during the search.
	 * @return
	 * 		Returns all notes satisfying the search criteria.
	 */
	NoteSelectionResult findNotes(LoggedInInfo loggedInInfo, NoteSelectionCriteria criteria);


	NoteSelectionTo1 searchEncounterNotes(LoggedInInfo loggedInInfo, NoteSelectionCriteria criteria);
}
