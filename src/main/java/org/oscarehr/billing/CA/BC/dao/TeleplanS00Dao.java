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

import org.oscarehr.billing.CA.BC.model.TeleplanS00;
import org.oscarehr.common.dao.AbstractDao;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TeleplanS00Dao extends AbstractDao<TeleplanS00> {

	public TeleplanS00Dao() {
		super(TeleplanS00.class);
	}

	@SuppressWarnings("unchecked")
	public List<TeleplanS00> findAll() {
		Query query = createQuery("x", null);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<TeleplanS00> findByBillingNo(String mspCtlNo) {
		Query q = createQuery("t", "t.mspCtlNo = :no");
		q.setParameter("no", mspCtlNo);
		return q.getResultList();
	}

	public List<TeleplanS00> findByOfficeNumber(String officeNumber) {
		List<String> numbers = new ArrayList<String>();
		numbers.add(officeNumber);
		return findByOfficeNumbers(numbers);
	}

	@SuppressWarnings("unchecked")
	public List<TeleplanS00> findByOfficeNumbers(List<String> officeNumbers) {
		if (officeNumbers.isEmpty()) {
			return new ArrayList<TeleplanS00>();
		}

		Query q = createQuery("t", "t.officeNo IN (:no)");
		q.setParameter("no", officeNumbers);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<TeleplanS00> findBgs() {
		Query q = createQuery("t", "t.exp1 = :s OR t.exp2 = :s OR t.exp3 = :s OR t.exp4 = :s OR t.exp5 = :s OR t.exp6 = :s OR t.exp7 = :s");
		q.setParameter("s", "BG");
		return q.getResultList();
    }
	
	@SuppressWarnings("unchecked")
	public List<Object[]> search_taprovider(Integer s21Id) {
		Query q = entityManager.createQuery("select r.practitionerNo, p.LastName,p.FirstName from TeleplanS00 r, Provider p where p.OhipNo=r.practitionerNo and r.s21Id=?1 group by r.practitionerNo");
		q.setParameter(1, s21Id);
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<TeleplanS00> search_taS00 (Integer s21Id, String type, String practitionerNo) {
		Query q = entityManager.createQuery("select t from TeleplanS00 t where t.s21Id=?1 and t.s00Type<>?2 and t.practitionerNo like ?3 order by t.id");
		q.setParameter(1, s21Id);
		q.setParameter(2, type);
		q.setParameter(3, practitionerNo);
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<TeleplanS00> search_taS01 (Integer s21Id, String type, String practitionerNo) {
		Query q = entityManager.createQuery("select t from TeleplanS00 t where t.s21Id=?1 and t.s00Type<>?2 and t.practitionerNo like ?3 order by t.id");
		q.setParameter(1, s21Id);
		q.setParameter(2, type);
		q.setParameter(3, practitionerNo);
		return q.getResultList();
	}

	public List<TeleplanS00> findDuplicates(TeleplanS00 lineEntry)
	{
		Query q = entityManager.createQuery(
				"SELECT t FROM TeleplanS00 t " +
						"WHERE t.s00Type = :s00Type " +
						"AND t.dataCentre = :dataCenter " +
						"AND t.dataSeq = :sequenceNumber " +
						"AND t.mspCtlNo = :mspInternal " +
						"AND t.officeNo = :officeNo " +
						"AND t.practitionerNo = :practitionerNo " +
						"AND t.payment = :paymentDate " +
						"AND t.paidAmount = :paidAmount " +
						"AND t.phn = :mspHin " +
						"AND t.billNoServices = :billedNoServices " +
						"AND t.billFeeSchedule = :billedServiceCode " +
						"AND t.billAmount = :billedAmount " +
						"AND t.exp1 = :explanatory1 " +
						"AND t.exp2 = :explanatory2 " +
						"AND t.exp3 = :explanatory3 " +
						"AND t.ajc1 = :adjustmentCode1 " +
						"AND t.ajc2 = :adjustmentCode2 " +
						"AND t.ajc3 = :adjustmentCode3 " +
						"ORDER BY t.id ASC");
		q.setParameter("s00Type", lineEntry.getS00Type());
		q.setParameter("dataCenter", lineEntry.getDataCentre());
		q.setParameter("sequenceNumber", lineEntry.getDataSeq());
		q.setParameter("mspInternal", lineEntry.getMspCtlNo());
		q.setParameter("officeNo", lineEntry.getOfficeNo());
		q.setParameter("practitionerNo", lineEntry.getPractitionerNo());
		q.setParameter("paymentDate", lineEntry.getPayment());
		q.setParameter("paidAmount", lineEntry.getPaidAmount());
		q.setParameter("mspHin", lineEntry.getPhn());
		q.setParameter("billedNoServices", lineEntry.getBillNoServices());
		q.setParameter("billedServiceCode", lineEntry.getBillFeeSchedule());
		q.setParameter("billedAmount", lineEntry.getBillAmount());
		q.setParameter("explanatory1", lineEntry.getExp1());
		q.setParameter("explanatory2", lineEntry.getExp2());
		q.setParameter("explanatory3", lineEntry.getExp3());
		q.setParameter("adjustmentCode1", lineEntry.getAjc1());
		q.setParameter("adjustmentCode2", lineEntry.getAjc2());
		q.setParameter("adjustmentCode3", lineEntry.getAjc3());

		return q.getResultList();
	}

	public boolean isDuplicate(TeleplanS00 lineEntry)
	{
		return !findDuplicates(lineEntry).isEmpty();
	}
}
