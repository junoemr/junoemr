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

package org.oscarehr.forms.service;

import org.oscarehr.common.dao.forms.FormsDao;
import org.oscarehr.forms.transfer.FormBCAR2012Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FormService
{
	@Autowired
	private FormsDao formsDao;

	public List<FormBCAR2012Transfer> getBCAR2012(String beginEdd, String endEdd, int limit, int offset)
	{
		List<Object[]> rawResults = formsDao.selectBCAR2012(beginEdd, endEdd, limit, offset);
		List<FormBCAR2012Transfer> transfers = new ArrayList<>();

		for (Object[] result : rawResults)
		{
			FormBCAR2012Transfer bcarTransfer = new FormBCAR2012Transfer();

			bcarTransfer.setDemographicNo(Integer.parseInt(result[0].toString()));
			bcarTransfer.setEdd((Date)result[1]);
			bcarTransfer.setLastName(getNullableStringFromObject(result[2]));
			bcarTransfer.setFirstName(getNullableStringFromObject(result[3]));
			bcarTransfer.setDateOfBirth((Date)result[4]);
			bcarTransfer.setGravida(getNullableStringFromObject(result[5]));
			bcarTransfer.setTerm(getNullableStringFromObject(result[6]));
			bcarTransfer.setPhone(getNullableStringFromObject(result[7]));
			bcarTransfer.setLangPreferred(getNullableStringFromObject(result[8]));
			bcarTransfer.setPhn(getNullableStringFromObject(result[9]));
			bcarTransfer.setDoula(getNullableStringFromObject(result[10]));
			bcarTransfer.setDoulaNo(getNullableStringFromObject(result[11]));

			transfers.add(bcarTransfer);
		}

		return transfers;
	}


	/**
	 * Helper method to safely cast from a possibly null Object to a String.
	 * Needed because forms are dumb and we are currently querying them via native queries, and
	 * not all of the fields may be filled out properly.
	 * @param object possibly null object we're getting
	 * @return String corresponding to object's .getString() if not null, "null" otherwise
	 */
	private String getNullableStringFromObject(Object object)
	{
		if (object == null)
		{
			return "null";
		}

		return object.toString();
	}

}
