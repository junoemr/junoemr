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


import org.drools.FactException;
import org.drools.RuleBase;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.decisionSupport2.converter.DsRuleDbToModelConverter;
import org.oscarehr.decisionSupport2.entity.Drools;
import org.oscarehr.decisionSupport2.model.DsInfoCache;
import org.oscarehr.decisionSupport2.model.consequence.SeverityLevel;
import org.oscarehr.decisionSupport2.service.DroolsCachingService;
import org.oscarehr.decisionSupport2.service.DsRuleService;
import org.oscarehr.flowsheet.converter.FlowsheetEntityToModelConverter;
import org.oscarehr.flowsheet.converter.PreventionToFlowsheetItemDataConverter;
import org.oscarehr.flowsheet.dao.FlowsheetDao;
import org.oscarehr.flowsheet.dao.FlowsheetItemDao;
import org.oscarehr.flowsheet.entity.FlowsheetItem;
import org.oscarehr.flowsheet.model.Flowsheet;
import org.oscarehr.flowsheet.model.FlowsheetItemAlert;
import org.oscarehr.flowsheet.model.FlowsheetItemData;
import org.oscarehr.flowsheet.model.FlowsheetItemGroup;
import org.oscarehr.measurements.service.MeasurementsService;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.oscarEncounter.oscarMeasurements.MeasurementInfo;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBean;
import oscar.oscarPrevention.PreventionDS;
import oscar.oscarPrevention.PreventionData;
import oscar.util.ConversionUtils;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class FlowsheetDataService
{
	@Autowired
	private FlowsheetDao flowsheetDao;

	@Autowired
	private FlowsheetItemDao flowsheetItemDao;

	@Autowired
	private MeasurementsService measurementsService;

	@Autowired
	private PreventionDao preventionDao;

	@Autowired
	private DroolsCachingService droolsCachingService;

	@Autowired
	private DsRuleService dsRuleService;

	@Autowired
	private FlowsheetEntityToModelConverter flowsheetEntityToModelConverter;

	@Autowired
	private PreventionToFlowsheetItemDataConverter preventionToFlowsheetItemDataConverter;

	@Autowired
	private DsRuleDbToModelConverter dsRuleDbToModelConverter;

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


	public Flowsheet getFlowsheetForDemographic(Integer flowsheetId, Integer demographicId) throws Exception
	{
		org.oscarehr.flowsheet.entity.Flowsheet flowsheetEntity = flowsheetDao.find(flowsheetId);
		Flowsheet flowsheet = flowsheetEntityToModelConverter.convert(flowsheetEntity);

		MeasurementInfo measurementInfo = loadMeasurementInfoWithDrools(flowsheetEntity, demographicId);
		oscar.oscarPrevention.Prevention preventionInfo = loadPreventionInfoWithDrools(flowsheetEntity, demographicId);

		for(FlowsheetItemGroup group : flowsheet.getFlowsheetItemGroups())
		{
			for(org.oscarehr.flowsheet.model.FlowsheetItem item : group.getFlowsheetItems())
			{
				if(item.isMeasurementType())
				{
					fillItemAlerts(measurementInfo, item);
					fillMeasurementItemData(measurementInfo, item);
				}
				else
				{
					fillItemAlerts(preventionInfo, item);
					fillPreventionItemData(demographicId, item);
				}
			}
		}
		return flowsheet;
	}

	private void fillItemAlerts(DsInfoCache dsInfoCache, org.oscarehr.flowsheet.model.FlowsheetItem item)
	{
		// set item specific alerts
		String typeCode = item.getTypeCode();
		if(dsInfoCache.hasRecommendation(typeCode))
		{
			dsInfoCache.getWarnings(typeCode).forEach((recommendation) -> {
				FlowsheetItemAlert alert = new FlowsheetItemAlert(recommendation, SeverityLevel.RECOMMENDATION);
				item.addFlowsheetItemAlert(alert);
			});
		}
		if(dsInfoCache.hasWarning(typeCode))
		{
			dsInfoCache.getWarnings(typeCode).forEach((warning) -> {
				FlowsheetItemAlert alert = new FlowsheetItemAlert(warning, SeverityLevel.WARNING);
				item.addFlowsheetItemAlert(alert);
			});

		}
		item.setHidden(dsInfoCache.getHidden(typeCode));
	}

	private void fillMeasurementItemData(MeasurementInfo measurementInfo, org.oscarehr.flowsheet.model.FlowsheetItem item)
	{
		// set existing data
		List<EctMeasurementsDataBean> measurementsDataBeans = measurementInfo.getMeasurementData(item.getTypeCode());
		for(EctMeasurementsDataBean dataBean : measurementsDataBeans)
		{
			FlowsheetItemData itemData = new FlowsheetItemData();
			itemData.setId(dataBean.getId());
			itemData.setValue(dataBean.getDataField());
			itemData.setObservationDateTime(ConversionUtils.toLocalDateTime(dataBean.getDateObservedAsDate()));
			itemData.setCreatedDateTime(ConversionUtils.toLocalDateTime(dataBean.getDateEnteredAsDate()));
			itemData.setUpdatedDateTime(ConversionUtils.toLocalDateTime(dataBean.getDateEnteredAsDate()));

			item.addFlowsheetItemData(itemData);
		}
	}

	private void fillPreventionItemData(Integer demographicId, org.oscarehr.flowsheet.model.FlowsheetItem item)
	{
		List<Prevention> preventions = preventionDao.findByTypeAndDemoNo(item.getTypeCode(), demographicId);
		for(Prevention prevention : preventions)
		{
			item.addFlowsheetItemData(preventionToFlowsheetItemDataConverter.convert(prevention));
		}
	}

	private MeasurementInfo loadMeasurementInfoWithDrools(org.oscarehr.flowsheet.entity.Flowsheet flowsheetEntity, Integer demographicId)
			throws Exception
	{
		MeasurementInfo measurementInfo = new MeasurementInfo(String.valueOf(demographicId));

		// fill measurementInfo measurement codes. prereq for applying drools
		List<String> flowsheetMeasurementCodes = flowsheetEntity.getFlowsheetItems()
				.stream()
				.filter(org.oscarehr.flowsheet.entity.FlowsheetItem::isMeasurementType)
				.map(org.oscarehr.flowsheet.entity.FlowsheetItem::getTypeCode)
				.collect(Collectors.toList());
		measurementInfo.getMeasurements(flowsheetMeasurementCodes);

		// load drools alerts. the measurementInfo object alerts/recommendations will be filled
		for(Drools drools : flowsheetEntity.getDrools())
		{
			RuleBase ruleBase = droolsCachingService.getDroolsRuleBase(drools.getFilename());
			dsRuleService.applyRuleBase(ruleBase, measurementInfo);
		}

		// load the database alerts, similar to the drools alerts above
		for(org.oscarehr.flowsheet.entity.FlowsheetItem flowsheetItem : flowsheetEntity.getFlowsheetItems())
		{
			dsRuleService.applyRules(measurementInfo, measurementInfo, flowsheetItem.getTypeCode(), dsRuleDbToModelConverter.convert(flowsheetItem.getDsRules()));
		}

		return measurementInfo;
	}

	private oscar.oscarPrevention.Prevention loadPreventionInfoWithDrools(org.oscarehr.flowsheet.entity.Flowsheet flowsheetEntity, Integer demographicId)
			throws FactException
	{
		oscar.oscarPrevention.Prevention prevention = PreventionData.getPrevention(new LoggedInInfo(), demographicId);
		dsRuleService.applyRuleBase(PreventionDS.ruleBase, prevention);

		// load the database alerts, similar to the drools alerts above
		for(org.oscarehr.flowsheet.entity.FlowsheetItem flowsheetItem : flowsheetEntity.getFlowsheetItems())
		{
			dsRuleService.applyRules(prevention, prevention, flowsheetItem.getTypeCode(), dsRuleDbToModelConverter.convert(flowsheetItem.getDsRules()));
		}

		return prevention;
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
				null,
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
