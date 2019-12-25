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

package org.oscarehr.integration.iceFall.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.integration.iceFall.model.IceFallLog;
import org.springframework.stereotype.Repository;
import oscar.util.ConversionUtils;

import javax.persistence.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class IceFallLogDao extends AbstractDao<IceFallLog>
{
	public IceFallLogDao()
	{
		super(IceFallLog.class);
	}

	public List<IceFallLog> getLogsPaginated (LocalDateTime start, LocalDateTime end, Integer page, Integer pageSize, String status)
	{
		Query query = entityManager.createQuery("FROM IceFallLog l WHERE l.createdAt >= :startDateTime AND l.createdAt <= :endDateTime AND l.status LIKE :status");
		query.setParameter("startDateTime", ConversionUtils.toLegacyDateTime(start));
		query.setParameter("endDateTime", ConversionUtils.toLegacyDateTime(end));
		query.setParameter("status", status);

		if (page != null && pageSize != null)
		{
			query.setMaxResults(pageSize);
			query.setFirstResult((page - 1) * pageSize);
		}

		return query.getResultList();
	}

	public Long getLogsPaginatedCount(LocalDateTime start, LocalDateTime end, Integer page, Integer pageSize, String status)
	{
		Query query = entityManager.createQuery("SELECT count(l.id) FROM IceFallLog l WHERE l.createdAt >= :startDateTime AND l.createdAt <= :endDateTime AND l.status LIKE :status");
		query.setParameter("startDateTime", ConversionUtils.toLegacyDateTime(start));
		query.setParameter("endDateTime", ConversionUtils.toLegacyDateTime(end));
		query.setParameter("status", status);

		return (Long)query.getSingleResult();
	}
}
