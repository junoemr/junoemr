/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.common.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.EncounterTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EncounterTemplateDao extends AbstractDao<EncounterTemplate> {

	public EncounterTemplateDao() {
		super(EncounterTemplate.class);
	}

	/**
	 * @return all encounterTemplates ordered by id/name
	 */
    public List<EncounterTemplate> findAll()
	{
		Query query = entityManager.createQuery("select x from "+modelClass.getSimpleName()+" x order by x.id");
		
		@SuppressWarnings("unchecked")
		List<EncounterTemplate> results=query.getResultList();
		
		return(results);
	}

	public List<EncounterTemplate> findLike(String templateName)
	{
		Query query = entityManager.createQuery("select x from "+modelClass.getSimpleName()+" x where x.encounterTemplateName like ?1 order by x.id");
		query.setParameter(1,"%"+templateName+"%");
		
		@SuppressWarnings("unchecked")
		List<EncounterTemplate> results=query.getResultList();
		
		return(results);
	}
}
