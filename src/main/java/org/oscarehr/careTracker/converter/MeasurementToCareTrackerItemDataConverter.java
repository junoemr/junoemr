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
package org.oscarehr.careTracker.converter;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.careTracker.model.CareTrackerItemData;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.common.model.Measurement;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class MeasurementToCareTrackerItemDataConverter extends AbstractModelConverter<Measurement, CareTrackerItemData>
{
	@Override
	public CareTrackerItemData convert(Measurement input)
	{
		CareTrackerItemData careTrackerItemData = new CareTrackerItemData();
		careTrackerItemData.setId(input.getId());
		careTrackerItemData.setValue(input.getDataField());
		careTrackerItemData.setComment(StringUtils.trimToNull(input.getComments()));
		careTrackerItemData.setObservationDateTime(ConversionUtils.toLocalDateTime(input.getDateObserved()));
		careTrackerItemData.setCreatedDateTime(ConversionUtils.toLocalDateTime(input.getCreateDate()));
		careTrackerItemData.setUpdatedDateTime(ConversionUtils.toLocalDateTime(input.getCreateDate()));

		return careTrackerItemData;
	}
}
