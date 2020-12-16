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

import org.oscarehr.billing.CA.BC.model.TeleplanS22;
import org.oscarehr.common.dao.AbstractDao;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class TeleplanS22Dao extends AbstractDao<TeleplanS22>{

	public TeleplanS22Dao() {
		super(TeleplanS22.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<TeleplanS22> search_taS22 (Integer s21Id, String type, String practitionerNo) {
		Query q = entityManager.createQuery("select t from TeleplanS22 t where t.s21Id=?1 and t.s22Type<>?2 and t.practitionerNo like ?3 order by t.id");
		q.setParameter(1, s21Id);
		q.setParameter(2, type);
		q.setParameter(3, practitionerNo);
		return q.getResultList();
	}

	public List<TeleplanS22> findDuplicates(TeleplanS22 lineEntry)
	{
		Query q = entityManager.createQuery(
				"SELECT t FROM TeleplanS22 t " +
						"WHERE t.s22Type = :s22Type " +
						"AND t.dataCentre = :dataCenter " +
						"AND t.dataSeq = :sequenceNumber " +
						"AND t.mspCtlNo = :mspInternal " +
						"AND t.practitionerNo = :practitionerNo " +
						"AND t.payment = :paymentDate " +
						"AND t.amountPaid = :paidAmount " +
						"AND t.amountBilled = :billedAmount " +
						"ORDER BY t.id ASC");
		q.setParameter("s22Type", lineEntry.getS22Type());
		q.setParameter("dataCenter", lineEntry.getDataCentre());
		q.setParameter("sequenceNumber", lineEntry.getDataSeq());
		q.setParameter("mspInternal", lineEntry.getMspCtlNo());
		q.setParameter("practitionerNo", lineEntry.getPractitionerNo());
		q.setParameter("paymentDate", lineEntry.getPayment());
		q.setParameter("paidAmount", lineEntry.getAmountPaid());
		q.setParameter("billedAmount", lineEntry.getAmountBilled());
		return q.getResultList();
	}

	public boolean isDuplicate(TeleplanS22 lineEntry)
	{
		return !findDuplicates(lineEntry).isEmpty();
	}
}
