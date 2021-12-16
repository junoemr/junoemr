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

package org.oscarehr.hospitalReportManager.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.Query;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.hospitalReportManager.model.HRMCategory;
import org.springframework.stereotype.Repository;

@Repository
public class HRMCategoryDao extends AbstractDao<HRMCategory> {
	
	public HRMCategoryDao()
	{
	    super(HRMCategory.class);
    }

    public List<HRMCategory> getActiveCategories()
	{
		String sql = "SELECT c from HRMCategory c where c.disabledAt IS NULL order by c.categoryName";
		Query query = entityManager.createQuery(sql);

		return query.getResultList();
	}

	public Optional<HRMCategory> findActiveByName(String categoryName)
	{
		String sql = "SELECT c FROM HRMCategory c where c.disabledAt IS NULL and c.categoryName = :name";
		Query query = entityManager.createQuery(sql);
		query.setParameter("name", categoryName);

		return Optional.ofNullable(getSingleResultOrNull(query));
	}
}