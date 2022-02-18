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
package org.oscarehr.demographic.converter;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.dataMigration.converter.in.BaseModelToDbConverter;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographic.model.DemographicModel;
import org.oscarehr.demographic.transfer.DemographicCreateInput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.oscarehr.demographic.entity.Demographic.STATUS_ACTIVE;

@Component
public class DemographicCreateInputToEntityConverter
		extends BaseModelToDbConverter<DemographicCreateInput, Demographic>
{
	@Autowired
	protected DemographicModelToDbConverter demographicModelToDbConverter;

	@Override
	public Demographic convert(DemographicCreateInput input)
	{
		if (input == null)
		{
			return null;
		}

		DemographicModel model = new DemographicModel();
		BeanUtils.copyProperties(input, model);

		model.setPatientStatus(STATUS_ACTIVE);
		model.setPatientStatusDate(LocalDate.now());
		model.setDateJoined(LocalDate.now());

		if(StringUtils.isNotBlank(input.getMrpProviderId()))
		{
			ProviderModel mrpProvider = new ProviderModel();
			mrpProvider.setId(input.getMrpProviderId());
			model.setMrpProvider(mrpProvider);
		}

		//TODO direct conversion
		Demographic entity = demographicModelToDbConverter.convert(model);
		BaseModelToDbConverter.clearProviderCache(); // don't keep the provider cache for simple demographic creation
		return entity;
	}
}
