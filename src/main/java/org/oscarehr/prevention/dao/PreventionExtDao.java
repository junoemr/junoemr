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

package org.oscarehr.prevention.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.prevention.model.PreventionExt;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class PreventionExtDao extends AbstractDao<PreventionExt>
{

	public PreventionExtDao()
	{
		super(PreventionExt.class);
	}

	public List<PreventionExt> findByPreventionId(Integer preventionId)
	{
		Query query = entityManager.createQuery("select x from PreventionExt x where x.prevention.id=:id");
		query.setParameter("id", preventionId);

		List<PreventionExt> results = query.getResultList();

		return (results);
	}

	public List<PreventionExt> findByKeyAndValue(String key, String value)
	{
		Query query = entityManager.createQuery("select x from PreventionExt x where x.keyval=?1 and x.val=?2");
		query.setParameter(1, key);
		query.setParameter(2, value);

		List<PreventionExt> results = query.getResultList();

		return (results);
	}

	public List<PreventionExt> findByPreventionIdAndKey(Integer preventionId, String key)
	{
		Query query = entityManager.createQuery("select x from PreventionExt x where x.prevention.id=:id and x.keyval=:keyval");
		query.setParameter("id", preventionId);
		query.setParameter("keyval", key);

		List<PreventionExt> results = query.getResultList();

		return (results);
	}

	public HashMap<String, String> getPreventionExt(Integer preventionId)
	{
		HashMap<String, String> results = new HashMap<String, String>();

		List<PreventionExt> preventionExts = findByPreventionId(preventionId);
		for(PreventionExt preventionExt : preventionExts)
		{
			results.put(preventionExt.getkeyval(), preventionExt.getVal());
		}

		return results;
	}
}
