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
package org.oscarehr.flowsheet.service;


import org.oscarehr.common.model.Measurement;
import org.oscarehr.flowsheet.converter.PreventionToFlowsheetItemDataConverter;
import org.oscarehr.flowsheet.dao.FlowsheetItemDao;
import org.oscarehr.flowsheet.entity.FlowsheetItem;
import org.oscarehr.flowsheet.model.FlowsheetItemData;
import org.oscarehr.measurements.service.MeasurementsService;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.model.Prevention;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.oscarPrevention.PreventionData;
import oscar.util.ConversionUtils;

import javax.validation.ValidationException;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class FlowsheetDataService
{
	@Autowired
	private FlowsheetItemDao flowsheetItemDao;

	@Autowired
	private MeasurementsService measurementsService;

	@Autowired
	private PreventionDao preventionDao;

	@Autowired
	private PreventionToFlowsheetItemDataConverter preventionToFlowsheetItemDataConverter;

	public FlowsheetItemData addFlowsheetItemData(String providerId, Integer demographicId, Integer flowsheetItemId, FlowsheetItemData itemData)
	{
		FlowsheetItem flowsheetItem = flowsheetItemDao.find(flowsheetItemId);
		if(flowsheetItem.isMeasurementType())
		{
			return addFlowsheetMeasurement(providerId, demographicId, flowsheetItem, itemData);
		}
		else if(flowsheetItem.isPreventionType())
		{
			return addFlowsheetPrevention(providerId, demographicId, flowsheetItem, itemData);
		}
		else
		{
			throw new ValidationException("Invalid flowsheet item type: " + flowsheetItem.getType());
		}
	}

	private FlowsheetItemData addFlowsheetMeasurement(String providerId, Integer demographicId, FlowsheetItem flowsheetItem, FlowsheetItemData itemData)
	{
		List<String> validationErrors = measurementsService.getValidationErrors(flowsheetItem.getTypeCode(), itemData.getValue());

		if(validationErrors.isEmpty())
		{
			Measurement measurement = measurementsService.createNewMeasurementAndPersist(
					demographicId,
					providerId,
					flowsheetItem.getTypeCode(),
					itemData.getValue(),
					ConversionUtils.toLegacyDateTime(itemData.getObservationDateTime()));

			FlowsheetItemData flowsheetItemData = new FlowsheetItemData();
			flowsheetItemData.setId(measurement.getId());
			flowsheetItemData.setValue(measurement.getDataField());
			flowsheetItemData.setObservationDateTime(ConversionUtils.toLocalDateTime(measurement.getDateObserved()));
			flowsheetItemData.setCreatedDateTime(ConversionUtils.toLocalDateTime(measurement.getCreateDate()));
			flowsheetItemData.setUpdatedDateTime(ConversionUtils.toLocalDateTime(measurement.getCreateDate()));

			return flowsheetItemData;
		}
		else
		{
			//TODO make validation exception handle a list
			throw new ValidationException(String.join(",\n", validationErrors));
		}
	}

	private FlowsheetItemData addFlowsheetPrevention(String providerId, Integer demographicId, FlowsheetItem flowsheetItem, FlowsheetItemData itemData)
	{
		Integer preventionId = PreventionData.insertPreventionData(
				providerId,
				demographicId,
				ConversionUtils.toLegacyDateTime(itemData.getObservationDateTime()),
				providerId,
				null,
				flowsheetItem.getTypeCode(),
				false,
				false,
				null,
				false,
				null);

		Prevention prevention = preventionDao.find(preventionId);
		return preventionToFlowsheetItemDataConverter.convert(prevention);
	}
}
