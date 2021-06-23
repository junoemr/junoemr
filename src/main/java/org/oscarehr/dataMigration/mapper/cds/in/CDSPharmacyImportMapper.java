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
package org.oscarehr.dataMigration.mapper.cds.in;

import org.oscarehr.dataMigration.model.pharmacy.Pharmacy;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.Demographics;

@Component
public class CDSPharmacyImportMapper extends AbstractCDSImportMapper<Demographics.PreferredPharmacy, Pharmacy>
{
	public CDSPharmacyImportMapper()
	{
		super();
	}

	@Override
	public Pharmacy importToJuno(Demographics.PreferredPharmacy importStructure)
	{
		if(importStructure == null)
		{
			return null;
		}

		Pharmacy pharmacy = new Pharmacy();
		pharmacy.setName(importStructure.getName());
		pharmacy.setAddress(getAddress(importStructure.getAddress()));
		pharmacy.setPhone1(getPhoneNumber(importStructure.getPhoneNumber()));
		pharmacy.setFax(getPhoneNumber(importStructure.getFaxNumber()));
		pharmacy.setEmail(importStructure.getEmailAddress());
		pharmacy.setStatusActive();

		return pharmacy;
	}
}
