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
package org.oscarehr.careTracker.service;


import org.drools.FactException;
import org.drools.RuleBase;
import org.oscarehr.careTracker.converter.CareTrackerEntityToModelConverter;
import org.oscarehr.careTracker.converter.MeasurementsDataBeanToCareTrackerItemDataConverter;
import org.oscarehr.careTracker.converter.PreventionToCareTrackerItemDataConverter;
import org.oscarehr.careTracker.dao.CareTrackerDao;
import org.oscarehr.careTracker.dao.CareTrackerItemDao;
import org.oscarehr.careTracker.entity.CareTrackerItem;
import org.oscarehr.careTracker.model.CareTracker;
import org.oscarehr.careTracker.model.CareTrackerItemAlert;
import org.oscarehr.careTracker.model.CareTrackerItemData;
import org.oscarehr.careTracker.model.CareTrackerItemGroup;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.decisionSupport2.converter.DsRuleDbToModelConverter;
import org.oscarehr.decisionSupport2.entity.Drools;
import org.oscarehr.decisionSupport2.model.DsInfoCache;
import org.oscarehr.decisionSupport2.model.consequence.SeverityLevel;
import org.oscarehr.decisionSupport2.service.DroolsCachingService;
import org.oscarehr.decisionSupport2.service.DsRuleService;
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
public class CareTrackerDataService
{
	@Autowired
	private CareTrackerDao careTrackerDao;

	@Autowired
	private CareTrackerItemDao careTrackerItemDao;

	@Autowired
	private MeasurementsService measurementsService;

	@Autowired
	private PreventionDao preventionDao;

	@Autowired
	private DroolsCachingService droolsCachingService;

	@Autowired
	private DsRuleService dsRuleService;

	@Autowired
	private CareTrackerEntityToModelConverter careTrackerEntityToModelConverter;

	@Autowired
	private PreventionToCareTrackerItemDataConverter preventionToCareTrackerItemDataConverter;

	@Autowired
	private MeasurementsDataBeanToCareTrackerItemDataConverter measurementsDataBeanToCareTrackerItemDataConverter;

	@Autowired
	private DsRuleDbToModelConverter dsRuleDbToModelConverter;

	public CareTrackerItemData addCareTrackerItemData(String providerId, Integer demographicId, Integer careTrackerItemId, CareTrackerItemData itemData)
	{
		CareTrackerItem careTrackerItem = careTrackerItemDao.find(careTrackerItemId);
		if(careTrackerItem.isMeasurementType())
		{
			return addCareTrackerMeasurement(providerId, demographicId, careTrackerItem, itemData);
		}
		else if(careTrackerItem.isPreventionType())
		{
			return addCareTrackerPrevention(providerId, demographicId, careTrackerItem, itemData);
		}
		else
		{
			throw new ValidationException("Invalid care tracker item type: " + careTrackerItem.getType());
		}
	}


	public CareTracker getCareTrackerForDemographic(Integer careTrackerId, Integer demographicId) throws Exception
	{
		org.oscarehr.careTracker.entity.CareTracker careTrackerEntity = careTrackerDao.find(careTrackerId);
		CareTracker careTracker = careTrackerEntityToModelConverter.convert(careTrackerEntity);

		MeasurementInfo measurementInfo = loadMeasurementInfoWithDrools(careTrackerEntity, demographicId);
		oscar.oscarPrevention.Prevention preventionInfo = loadPreventionInfoWithDrools(careTrackerEntity, demographicId);

		for(CareTrackerItemGroup group : careTracker.getCareTrackerItemGroups())
		{
			for(org.oscarehr.careTracker.model.CareTrackerItem item : group.getCareTrackerItems())
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
		return careTracker;
	}

	private void fillItemAlerts(DsInfoCache dsInfoCache, org.oscarehr.careTracker.model.CareTrackerItem item)
	{
		// set item specific alerts
		String typeCode = item.getTypeCode();
		if(dsInfoCache.hasRecommendation(typeCode))
		{
			dsInfoCache.getRecommendations(typeCode).forEach((recommendation) -> {
				CareTrackerItemAlert alert = new CareTrackerItemAlert(recommendation, SeverityLevel.RECOMMENDATION);
				item.addCareTrackerItemAlert(alert);
			});
		}
		if(dsInfoCache.hasWarning(typeCode))
		{
			dsInfoCache.getWarnings(typeCode).forEach((warning) -> {
				CareTrackerItemAlert alert = new CareTrackerItemAlert(warning, SeverityLevel.WARNING);
				item.addCareTrackerItemAlert(alert);
			});
		}
		if(dsInfoCache.hasCriticalAlert(typeCode))
		{
			dsInfoCache.getCriticalAlerts(typeCode).forEach((warning) -> {
				CareTrackerItemAlert alert = new CareTrackerItemAlert(warning, SeverityLevel.DANGER);
				item.addCareTrackerItemAlert(alert);
			});
		}
		item.setHidden(dsInfoCache.getHidden(typeCode));
	}

	private void fillMeasurementItemData(MeasurementInfo measurementInfo, org.oscarehr.careTracker.model.CareTrackerItem item)
	{
		List<EctMeasurementsDataBean> measurementsDataBeans = measurementInfo.getMeasurementData(item.getTypeCode());
		item.addAllCareTrackerItemData(measurementsDataBeanToCareTrackerItemDataConverter.convert(measurementsDataBeans));
	}

	private void fillPreventionItemData(Integer demographicId, org.oscarehr.careTracker.model.CareTrackerItem item)
	{
		List<Prevention> preventions = preventionDao.findByTypeAndDemoNo(item.getTypeCode(), demographicId);
		item.addAllCareTrackerItemData(preventionToCareTrackerItemDataConverter.convert(preventions));
	}

	private MeasurementInfo loadMeasurementInfoWithDrools(org.oscarehr.careTracker.entity.CareTracker careTrackerEntity, Integer demographicId)
			throws Exception
	{
		MeasurementInfo measurementInfo = new MeasurementInfo(String.valueOf(demographicId));

		// fill measurementInfo measurement codes. prereq for applying drools
		List<String> careTrackerMeasurementCodes = careTrackerEntity.getCareTrackerItems()
				.stream()
				.filter(CareTrackerItem::isMeasurementType)
				.map(CareTrackerItem::getTypeCode)
				.collect(Collectors.toList());
		measurementInfo.getMeasurements(careTrackerMeasurementCodes);

		// load drools alerts. the measurementInfo object alerts/recommendations will be filled
		for(Drools drools : careTrackerEntity.getDrools())
		{
			RuleBase ruleBase = droolsCachingService.getDroolsRuleBase(drools.getFilename());
			dsRuleService.applyRuleBase(ruleBase, measurementInfo);
		}

		// load the database alerts, similar to the drools alerts above
		for(CareTrackerItem careTrackerItem : careTrackerEntity.getCareTrackerItems())
		{
			dsRuleService.applyRules(measurementInfo, measurementInfo, careTrackerItem.getTypeCode(), dsRuleDbToModelConverter.convert(careTrackerItem.getDsRules()));
		}

		return measurementInfo;
	}

	private oscar.oscarPrevention.Prevention loadPreventionInfoWithDrools(org.oscarehr.careTracker.entity.CareTracker careTrackerEntity, Integer demographicId)
			throws FactException
	{
		oscar.oscarPrevention.Prevention prevention = PreventionData.getPrevention(new LoggedInInfo(), demographicId);
		dsRuleService.applyRuleBase(PreventionDS.ruleBase, prevention);

		// load the database alerts, similar to the drools alerts above
		for(CareTrackerItem careTrackerItem : careTrackerEntity.getCareTrackerItems())
		{
			dsRuleService.applyRules(prevention, prevention, careTrackerItem.getTypeCode(), dsRuleDbToModelConverter.convert(careTrackerItem.getDsRules()));
		}

		return prevention;
	}

	private CareTrackerItemData addCareTrackerMeasurement(String providerId, Integer demographicId, CareTrackerItem careTrackerItem, CareTrackerItemData itemData)
	{
		List<String> validationErrors = measurementsService.getValidationErrors(careTrackerItem.getTypeCode(), itemData.getValue());

		if(validationErrors.isEmpty())
		{
			Measurement measurement = measurementsService.createNewMeasurementAndPersist(
					demographicId,
					providerId,
					careTrackerItem.getTypeCode(),
					itemData.getValue(),
					ConversionUtils.toLegacyDateTime(itemData.getObservationDateTime()));

			CareTrackerItemData careTrackerItemData = new CareTrackerItemData();
			careTrackerItemData.setId(measurement.getId());
			careTrackerItemData.setValue(measurement.getDataField());
			careTrackerItemData.setObservationDateTime(ConversionUtils.toLocalDateTime(measurement.getDateObserved()));
			careTrackerItemData.setCreatedDateTime(ConversionUtils.toLocalDateTime(measurement.getCreateDate()));
			careTrackerItemData.setUpdatedDateTime(ConversionUtils.toLocalDateTime(measurement.getCreateDate()));

			return careTrackerItemData;
		}
		else
		{
			//TODO make validation exception handle a list
			throw new ValidationException(String.join(",\n", validationErrors));
		}
	}

	private CareTrackerItemData addCareTrackerPrevention(String providerId, Integer demographicId, CareTrackerItem careTrackerItem, CareTrackerItemData itemData)
	{
		Integer preventionId = PreventionData.insertPreventionData(
				providerId,
				demographicId,
				ConversionUtils.toLegacyDateTime(itemData.getObservationDateTime()),
				null,
				providerId,
				null,
				careTrackerItem.getTypeCode(),
				false,
				false,
				null,
				false,
				null);

		Prevention prevention = preventionDao.find(preventionId);
		return preventionToCareTrackerItemDataConverter.convert(prevention);
	}
}
