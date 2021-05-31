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
package org.oscarehr.ws.external.soap.v1.converter;

import org.apache.log4j.Logger;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.demographic.dao.DemographicExtDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.external.soap.v1.transfer.DemographicTransfer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DemographicModelToSoapTransferConverter extends AbstractModelConverter<Demographic, DemographicTransfer>
{
	@Override
	public DemographicTransfer convert(Demographic demographic)
	{
		if (demographic == null)
		{
			return null;
		}

		DemographicTransfer demographicTransfer = new DemographicTransfer();
		String[] ignoreProperties = {
				"activeCount",
				"cellPhone",
				"demographicNo",
				"familyDoctor",
				"familyDoctor2",
				"effDate",
				"yearOfBirth",
				"monthOfBirth",
				"dateOfBirth"
		};
		BeanUtils.copyProperties(demographic, demographicTransfer, ignoreProperties);

		// Manually assign stuff that couldn't be bean copied
		demographicTransfer.setActiveCount(demographic.isActive() ? 1 : 0);
		demographicTransfer.setDemographicNo(demographic.getDemographicId());
		demographicTransfer.setFamilyDoctor(demographic.getReferralDoctor());
		demographicTransfer.setFamilyDoctor2(demographic.getFamilyDoctor());
		demographicTransfer.setEffDate(demographic.getHcEffectiveDate());
		demographicTransfer.setYearOfBirth(demographic.getYearOfBirth());
		demographicTransfer.setMonthOfBirth(demographic.getMonthOfBirth());
		demographicTransfer.setDateOfBirth(demographic.getDayOfBirth());
		// Attempt to set extra demographic fields
		try
		{
			DemographicExtDao demographicExtDao = SpringUtils.getBean(DemographicExtDao.class);
			Map<String,String> demoExt = demographicExtDao.getAllValuesForDemo(demographic.getDemographicId());
			demographicTransfer.setCellPhone(demoExt.get("demo_cell"));
		}
		catch (Exception e)
		{
			Logger logger = Logger.getLogger(DemographicTransfer.class);
			logger.error("Failed to get extended demographic data into DemographicTransform.", e);
		}

		return demographicTransfer;
	}
}
