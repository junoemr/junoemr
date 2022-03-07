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
package org.oscarehr.fax.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.model.FaxStatusInternal;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class FaxOutboundDao extends AbstractDao<FaxOutbound>
{
	public FaxOutboundDao()
	{
		super(FaxOutbound.class);
	}

	public List<FaxOutbound> findActiveQueued()
	{
		Query query = entityManager.createQuery(
				"SELECT record " +
						"FROM FaxOutbound record " +
						"INNER JOIN record.faxAccount acct " +
						"WHERE record.status = :status " +
						"AND acct.outboundEnabled = :enabled " +
						"ORDER BY record.createdAt ASC");
		query.setParameter("status", FaxStatusInternal.QUEUED);
		query.setParameter("enabled", true);
		return query.getResultList();
	}
}
