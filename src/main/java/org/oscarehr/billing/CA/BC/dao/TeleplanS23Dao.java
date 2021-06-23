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


package org.oscarehr.billing.CA.BC.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.billing.CA.BC.model.TeleplanS23;
import org.oscarehr.common.dao.AbstractDao;
import org.springframework.stereotype.Repository;

@Repository
public class TeleplanS23Dao extends AbstractDao<TeleplanS23>{

	public TeleplanS23Dao() {
		super(TeleplanS23.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<TeleplanS23> search_taS23 (Integer s21Id, String type, String aji) {
		Query q = entityManager.createQuery("select t from TeleplanS23 t where t.s21Id=?1 and t.s23Type<>?2 and t.aji like ?3 order by t.id");
		q.setParameter(1, s21Id);
		q.setParameter(2, type);
		q.setParameter(3, aji);
		return q.getResultList();
	}


	public List<TeleplanS23> findDuplicates(TeleplanS23 lineEntry)
	{
		Query query = entityManager.createQuery(
				"SELECT t FROM TeleplanS23 t " +
						"WHERE t.s23Type = :s23Type " +
						"AND t.dataCentre = :dataCenter " +
						"AND t.dataSeq = :sequenceNumber " +
						"AND t.mspCtlNo = :mspInternal " +
						"AND t.payment = :paymentDate " +
						"AND t.ajm = :adjustmentName " +
						"ORDER BY t.id ASC");
		query.setParameter("s23Type", lineEntry.getS23Type());
		query.setParameter("dataCenter", lineEntry.getDataCentre());
		query.setParameter("sequenceNumber", lineEntry.getDataSeq());
		query.setParameter("mspInternal", lineEntry.getMspCtlNo());
		query.setParameter("paymentDate", lineEntry.getPayment());
		query.setParameter("adjustmentName", lineEntry.getAjm());

		return query.getResultList();
	}

	public boolean isDuplicate(TeleplanS23 lineEntry)
	{
		return !findDuplicates(lineEntry).isEmpty();
	}
}
