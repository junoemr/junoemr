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

package org.oscarehr.dataMigration.converter.in.hrm;

import org.oscarehr.dataMigration.model.hrm.HrmSubClassModel;
import org.oscarehr.hospitalReportManager.model.HRMCategory;
import org.oscarehr.hospitalReportManager.model.HRMSubClass;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HrmSubClassModelToDbConverter
{
	public List<HRMSubClass> convert(Collection<HrmSubClassModel> models, HRMCategory parentEntity)
	{
		if (models == null)
		{
			return null;
		}

		return models.stream().map(entity -> convert(entity, parentEntity)).collect(Collectors.toList());
	}

	public HRMSubClass convert(HrmSubClassModel model, HRMCategory parentEntity)
	{
		HRMSubClass entity = new HRMSubClass();
		entity.setHrmCategory(parentEntity);
		entity.setId(model.getId());
		entity.setSendingFacilityId(model.getFacilityNumber());
		entity.setClassName(model.getClassName());
		entity.setSubClassName(model.getSubClassName());
		entity.setAccompanyingSubClassName(model.getAccompanyingSubClassName());
		entity.setDisabledAt(model.getDisabledAt());

		return entity;
	}
}