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
package org.oscarehr.allergy.service;

import org.oscarehr.allergy.dao.AllergyDao;
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.common.dao.PartialDateDao;
import org.oscarehr.common.model.PartialDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service("allergy.service.AllergyService")
@Transactional
public class AllergyService
{
	@Autowired
	AllergyDao allergyDao;

	@Autowired
	PartialDateDao partialDateDao;

	public Allergy addNewAllergy(Allergy allergy)
	{
		//set any missing default values
		if(allergy.getEntryDate() == null)
		{
			allergy.setEntryDate(new Date());
		}

		allergyDao.persist(allergy);
		partialDateDao.setPartialDate(PartialDate.ALLERGIES, allergy.getId(), PartialDate.ALLERGIES_STARTDATE, allergy.getStartDateFormat());

		return allergy;
	}

	/**
	 * Wrapper for updating an old allergy entry.
	 * Handles updating the allergy as well as any associated partial_date entry.
	 * @param allergy allergy model to update
	 */
	public void update(Allergy allergy)
	{
		allergyDao.merge(allergy);
		partialDateDao.setPartialDate(PartialDate.ALLERGIES, allergy.getId(), PartialDate.ALLERGIES_STARTDATE, allergy.getStartDateFormat());
	}
}
