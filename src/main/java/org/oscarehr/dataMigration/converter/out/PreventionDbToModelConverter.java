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
package org.oscarehr.dataMigration.converter.out;

import org.oscarehr.common.dao.PartialDateDao;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.immunization.Immunization;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.prevention.model.PreventionExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.List;

import static org.oscarehr.prevention.model.PreventionExt.KEY_COMMENT;
import static org.oscarehr.prevention.model.PreventionExt.KEY_DOSE;
import static org.oscarehr.prevention.model.PreventionExt.KEY_LOCATION;
import static org.oscarehr.prevention.model.PreventionExt.KEY_LOT;
import static org.oscarehr.prevention.model.PreventionExt.KEY_MANUFACTURE;
import static org.oscarehr.prevention.model.PreventionExt.KEY_NAME;
import static org.oscarehr.prevention.model.PreventionExt.KEY_REASON;
import static org.oscarehr.prevention.model.PreventionExt.KEY_RESULT;
import static org.oscarehr.prevention.model.PreventionExt.KEY_ROUTE;

// can't extend the base class because the base uses this converter
@Component
public class PreventionDbToModelConverter extends BaseDbToModelConverter<Prevention, Immunization>
{
	@Autowired
	private PartialDateDao partialDateDao;

	@Override
	public Immunization convert(Prevention input)
	{
		Immunization immunization = new Immunization();

		immunization.setId(input.getId());
		immunization.setPreventionType(input.getPreventionType());
//		immunization.setDrugIdentificationNumber();
		immunization.setNextDate(ConversionUtils.toNullableLocalDate(input.getNextDate()));
		immunization.setLastUpdateDate(ConversionUtils.toNullableLocalDateTime(input.getLastUpdateDate()));

		org.oscarehr.common.model.PartialDate dbPartialDate = partialDateDao.getPartialDate(
				org.oscarehr.common.model.PartialDate.TABLE_PREVENTIONS,
				input.getId(),
				org.oscarehr.common.model.PartialDate.PREVENTION_DATE);
		immunization.setAdministrationDate(PartialDateTime.from(ConversionUtils.toNullableLocalDateTime(input.getPreventionDate()), dbPartialDate));

		immunization.setCreatedAt(ConversionUtils.toNullableLocalDateTime(input.getCreationDate()));
		immunization.setCreatedBy(findProvider(input.getCreatorProviderNo()));
		immunization.setProvider(findProvider(input.getProviderNo()));

		immunization.setRefused(input.isRefused());
		immunization.setNever(input.isNever());

		List<PreventionExt> preventionExtList = input.getPreventionExtensionList();

		for(PreventionExt preventionExt : preventionExtList)
		{
			switch(preventionExt.getkeyval())
			{
				case KEY_NAME: immunization.setName(preventionExt.getVal()); break;
				case KEY_DOSE: immunization.setDose(preventionExt.getVal()); break;
				case KEY_MANUFACTURE: immunization.setManufacture(preventionExt.getVal()); break;
				case KEY_ROUTE: immunization.setRoute(preventionExt.getVal()); break;
				case KEY_LOT: immunization.setLot(preventionExt.getVal()); break;
				case KEY_LOCATION: immunization.setLocation(preventionExt.getVal()); break;
				case KEY_COMMENT: immunization.setComments(preventionExt.getVal()); break;
				case KEY_REASON: immunization.setReason(preventionExt.getVal()); break;
				case KEY_RESULT: immunization.setResult(preventionExt.getVal()); break;
			}
		}

		return immunization;
	}
}
