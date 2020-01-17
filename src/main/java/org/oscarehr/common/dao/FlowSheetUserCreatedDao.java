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


package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;

import org.oscarehr.common.model.FlowSheetUserCreated;
import org.springframework.stereotype.Repository;
import javax.persistence.Query;

@Repository
public class FlowSheetUserCreatedDao extends AbstractDao<FlowSheetUserCreated> {

	public FlowSheetUserCreatedDao() {
		super(FlowSheetUserCreated.class);
	}
	
	public List<FlowSheetUserCreated> getAllUserCreatedFlowSheets()
	{
		Query query = entityManager.createQuery("SELECT f FROM FlowSheetUserCreated f ");
		return query.getResultList();
	}

	public FlowSheetUserCreated findByName(String name) {
		Query query = entityManager.createQuery("select f from FlowSheetUserCreated f where f.name=?");
		query.setParameter(1, name);

		return getSingleResultOrNull(query);
	}

	/**
	 * Create a custom flowsheet given all of the required parameters.
	 * @param name Internal name for the flowsheet
	 * @param dxCodeTriggers set of ICD9 codes of which any can trigger visibility for a demographic
	 * @param displayName user-friendly name for the flowsheet
	 * @param warningColour custom colour to set for warning
	 * @param recommendationColour custom colour to set for recommendation
	 * @return FlowSheetUserCreated object after a successful persist
	 */
	public FlowSheetUserCreated create(String name, String dxCodeTriggers, String displayName, String warningColour, String recommendationColour)
	{
		FlowSheetUserCreated flowSheetUserCreated = new FlowSheetUserCreated();
		flowSheetUserCreated.setName(name);
		flowSheetUserCreated.setDxcodeTriggers(dxCodeTriggers);
		flowSheetUserCreated.setDisplayName(displayName);
		flowSheetUserCreated.setWarningColour(warningColour);
		flowSheetUserCreated.setRecommendationColour(recommendationColour);
		flowSheetUserCreated.setCreatedDate(new Date());
		flowSheetUserCreated.setArchived(false);

		persist(flowSheetUserCreated);

		return flowSheetUserCreated;
	}

	/**
	 * Given a user created flowsheet that's currently active, archive it.
	 * @param flowSheetUserCreated user-created flowsheet to archive
	 */
	public void archive(FlowSheetUserCreated flowSheetUserCreated)
	{
		flowSheetUserCreated.setArchived(true);
		merge(flowSheetUserCreated);
	}

	/**
	 * Given a user created flowsheet that's been archived, unarchive it.
	 * @param flowSheetUserCreated user-created flowsheet to unarchive
	 */
	public void unarchive(FlowSheetUserCreated flowSheetUserCreated)
	{
		flowSheetUserCreated.setArchived(false);
		merge(flowSheetUserCreated);
	}
	
}
