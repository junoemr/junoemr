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

import org.oscarehr.billing.CA.BC.model.TeleplanS21;
import org.oscarehr.common.dao.AbstractDao;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class TeleplanS21Dao extends AbstractDao<TeleplanS21>{

	public TeleplanS21Dao() {
		super(TeleplanS21.class);
	}
	
	public List<TeleplanS21> findByFilenamePaymentPayeeNo(String filename, String payment, String payeeNo) {
		Query q = entityManager.createQuery("SELECT t from TeleplanS21 t WHERE t.fileName=?1 AND t.payment=?2 AND t.payeeNo=?3 ORDER BY t.payment");
		q.setParameter(1, filename);
		q.setParameter(2, payment);
		q.setParameter(3, payeeNo);
		
		@SuppressWarnings("unchecked")
		List<TeleplanS21> results = q.getResultList();
		
		return results;
	}

	public List<TeleplanS21> findByFilename(String filename)
	{
		Query q = entityManager.createQuery("SELECT t from TeleplanS21 t WHERE t.fileName=:filename");
		q.setParameter("filename", filename);

		@SuppressWarnings("unchecked")
		List<TeleplanS21> results = q.getResultList();

		return results;
	}
	
	public List<TeleplanS21> search_all_tahd(Character excludeStatus)
	{
		Query q = entityManager.createQuery("SELECT t from TeleplanS21 t WHERE t.status <> ?1 ORDER BY t.payment desc");
		q.setParameter(1, excludeStatus);
		
		
		@SuppressWarnings("unchecked")
		List<TeleplanS21> results = q.getResultList();
		
		return results;
	}

	public List<TeleplanS21> findDuplicates(TeleplanS21 lineEntry)
	{
		Query q = entityManager.createQuery(
				"SELECT t FROM TeleplanS21 t " +
						"WHERE t.dataCentre = :dataCenter " +
						"AND t.dataSeq = :sequenceNumber " +
						"AND t.mspCtlNo = :mspInternal " +
						"AND t.payment = :paymentDate " +
						"AND t.amountPaid = :paidAmount " +
						"AND t.amountBilled = :billedAmount " +
						"ORDER BY t.id ASC");
		q.setParameter("dataCenter", lineEntry.getDataCentre());
		q.setParameter("sequenceNumber", lineEntry.getDataSeq());
		q.setParameter("mspInternal", lineEntry.getMspCtlNo());
		q.setParameter("paymentDate", lineEntry.getPayment());
		q.setParameter("paidAmount", lineEntry.getAmountPaid());
		q.setParameter("billedAmount", lineEntry.getAmountBilled());
		return q.getResultList();
	}
}
