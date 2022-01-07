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


import org.apache.commons.lang.StringUtils;
import org.drools.FactException;
import org.drools.RuleBase;
import org.oscarehr.careTracker.converter.CareTrackerEntityToModelConverter;
import org.oscarehr.careTracker.converter.CareTrackerItemEntityToModelConverter;
import org.oscarehr.careTracker.converter.MeasurementToCareTrackerItemDataConverter;
import org.oscarehr.careTracker.converter.MeasurementsDataBeanToCareTrackerItemDataConverter;
import org.oscarehr.careTracker.converter.PreventionToCareTrackerItemDataConverter;
import org.oscarehr.careTracker.dao.CareTrackerDao;
import org.oscarehr.careTracker.dao.CareTrackerItemDao;
import org.oscarehr.careTracker.entity.CareTrackerItem;
import org.oscarehr.careTracker.model.CareTracker;
import org.oscarehr.careTracker.model.CareTrackerItemAlert;
import org.oscarehr.careTracker.model.CareTrackerItemData;
import org.oscarehr.careTracker.transfer.CareTrackerItemDataCreateTransfer;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.careTrackerDecisionSupport.converter.DsRuleDbToModelConverter;
import org.oscarehr.careTrackerDecisionSupport.entity.Drools;
import org.oscarehr.careTrackerDecisionSupport.model.DsInfoCache;
import org.oscarehr.careTrackerDecisionSupport.model.consequence.SeverityLevel;
import org.oscarehr.careTrackerDecisionSupport.service.DroolsCachingService;
import org.oscarehr.careTrackerDecisionSupport.service.DsRuleService;
import org.oscarehr.measurements.service.MeasurementsService;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.prevention.model.PreventionExt;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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
	private CareTrackerItemEntityToModelConverter careTrackerItemEntityToModelConverter;

	@Autowired
	private PreventionToCareTrackerItemDataConverter preventionToCareTrackerItemDataConverter;

	@Autowired
	private MeasurementToCareTrackerItemDataConverter measurementToCareTrackerItemDataConverter;

	@Autowired
	private MeasurementsDataBeanToCareTrackerItemDataConverter measurementsDataBeanToCareTrackerItemDataConverter;

	@Autowired
	private DsRuleDbToModelConverter dsRuleDbToModelConverter;

	/**
	 * add new care tracker data with the given values, based on the rules defined by the given care tracker item
	 * @param providerId the adding provider identifier
	 * @param demographicId the demographic identifier
	 * @param careTrackerItemId the care tracker item identifier
	 * @param itemData the data to be saved
	 * @return the new data item created
	 */
	public CareTrackerItemData addCareTrackerItemData(
			String providerId,
			Integer demographicId,
			Integer careTrackerItemId,
			CareTrackerItemDataCreateTransfer itemData)
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


	/**
	 * get the care tracker for the given demographic, with all of the attached item data filled out
	 * @param demographicId the demographic identifier
	 * @param careTrackerId the care tracker identifier
	 * @return the care tracker
	 * @throws Exception on error
	 */
	public CareTracker getCareTrackerForDemographic(Integer demographicId, Integer careTrackerId) throws Exception
	{
		org.oscarehr.careTracker.entity.CareTracker careTrackerEntity = careTrackerDao.find(careTrackerId);
		CareTracker careTracker = careTrackerEntityToModelConverter.convert(careTrackerEntity);

		MeasurementInfo measurementInfo = loadMeasurementInfoWithDrools(careTrackerEntity.getCareTrackerItems(), careTrackerEntity.getDrools(), demographicId);
		oscar.oscarPrevention.Prevention preventionInfo = loadPreventionInfoWithDrools(careTrackerEntity.getCareTrackerItems(), demographicId);

		careTracker.getCareTrackerItemGroups().forEach((group) ->
		{
			group.getCareTrackerItems().forEach((item) -> fillItem(item, measurementInfo, preventionInfo, demographicId));
		});
		return careTracker;
	}

	/**
	 * get a single item within a care tracker for the given demographic, with all of the attached item data filled out
	 * @param demographicId the demographic identifier
	 * @param careTrackerItemId the care tracker item identifier
	 * @return the care tracker item
	 * @throws Exception on error
	 */
	public org.oscarehr.careTracker.model.CareTrackerItem getCareTrackerItemForDemographic(Integer demographicId, Integer careTrackerItemId) throws Exception
	{
		CareTrackerItem careTrackerItem = careTrackerItemDao.find(careTrackerItemId);
		org.oscarehr.careTracker.entity.CareTracker careTrackerEntity = careTrackerItem.getCareTracker();

		List<CareTrackerItem> items = new ArrayList<>(1);
		items.add(careTrackerItem);

		MeasurementInfo measurementInfo = loadMeasurementInfoWithDrools(items, careTrackerEntity.getDrools(), demographicId);
		oscar.oscarPrevention.Prevention preventionInfo = loadPreventionInfoWithDrools(items, demographicId);

		org.oscarehr.careTracker.model.CareTrackerItem itemModel = careTrackerItemEntityToModelConverter.convert(careTrackerItem);
		fillItem(itemModel, measurementInfo, preventionInfo, demographicId);

		return itemModel;
	}

	private void fillItem(
			org.oscarehr.careTracker.model.CareTrackerItem item,
			MeasurementInfo measurementInfo,
			oscar.oscarPrevention.Prevention preventionInfo,
			Integer demographicId)
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

	private MeasurementInfo loadMeasurementInfoWithDrools(
			Collection<CareTrackerItem> careTrackerItemEntities,
			Set<Drools> droolsSet,
			Integer demographicId)
			throws Exception
	{
		MeasurementInfo measurementInfo = new MeasurementInfo(String.valueOf(demographicId));

		// fill measurementInfo measurement codes. prereq for applying drools
		List<String> careTrackerMeasurementCodes = careTrackerItemEntities
				.stream()
				.filter(CareTrackerItem::isMeasurementType)
				.map(CareTrackerItem::getTypeCode)
				.collect(Collectors.toList());
		measurementInfo.getMeasurements(careTrackerMeasurementCodes);

		// load drools alerts. the measurementInfo object alerts/recommendations will be filled
		for(Drools drools : droolsSet)
		{
			RuleBase ruleBase = droolsCachingService.getDroolsRuleBase(drools.getFilename());
			dsRuleService.applyRuleBase(ruleBase, measurementInfo);
		}

		// load the database alerts, similar to the drools alerts above
		for(CareTrackerItem careTrackerItem : careTrackerItemEntities)
		{
			dsRuleService.applyRules(measurementInfo, measurementInfo, careTrackerItem.getTypeCode(), dsRuleDbToModelConverter.convert(careTrackerItem.getDsRules()));
		}

		return measurementInfo;
	}

	private oscar.oscarPrevention.Prevention loadPreventionInfoWithDrools(Collection<CareTrackerItem> careTrackerItemEntities, Integer demographicId)
			throws FactException
	{
		oscar.oscarPrevention.Prevention prevention = PreventionData.getPrevention(new LoggedInInfo(), demographicId);
		dsRuleService.applyRuleBase(PreventionDS.ruleBase, prevention);

		// load the database alerts, similar to the drools alerts above
		for(CareTrackerItem careTrackerItem : careTrackerItemEntities)
		{
			dsRuleService.applyRules(prevention, prevention, careTrackerItem.getTypeCode(), dsRuleDbToModelConverter.convert(careTrackerItem.getDsRules()));
		}

		return prevention;
	}

	private CareTrackerItemData addCareTrackerMeasurement(
			String providerId,
			Integer demographicId,
			CareTrackerItem careTrackerItem,
			CareTrackerItemDataCreateTransfer itemData)
	{
		String value = itemData.getValue();
		if(careTrackerItem.isNumericValueType())
		{
			// parse numeric types as a double/long to both ensure their formatting and standardize the double string format. this removes valid cases like '.5'
			if(value.contains("."))
			{
				Double numericValue = Double.parseDouble(value);
				value = String.valueOf(numericValue);
			}
			else
			{
				Long numericValue = Long.parseLong(value);
				value = String.valueOf(numericValue);
			}
		}

		List<String> validationErrors = measurementsService.getValidationErrors(careTrackerItem.getTypeCode(), value);

		if(validationErrors.isEmpty())
		{
			Measurement measurement = measurementsService.createNewMeasurementAndPersist(
					demographicId,
					providerId,
					careTrackerItem.getTypeCode(),
					value,
					ConversionUtils.toNullableLegacyDateTime(itemData.getObservationDateTime()),
					itemData.getComment());

			return measurementToCareTrackerItemDataConverter.convert(measurement);
		}
		else
		{
			//TODO make validation exception handle a list
			throw new ValidationException(String.join(",\n", validationErrors));
		}
	}

	private CareTrackerItemData addCareTrackerPrevention(
			String providerId,
			Integer demographicId,
			CareTrackerItem careTrackerItem,
			CareTrackerItemDataCreateTransfer itemData)
	{
		if(itemData.getObservationDateTime() == null)
		{
			throw new ValidationException("Preventions must have an observation date");
		}

		// set up the comment extension
		List<PreventionExt> extList = new ArrayList<>();
		String commentText = StringUtils.trimToNull(itemData.getComment());
		if(commentText != null)
		{
			PreventionExt commentExt = new PreventionExt();
			commentExt.setKeyval(PreventionExt.KEY_COMMENT);
			commentExt.setVal(commentText);
			extList.add(commentExt);
		}

		Prevention prevention = PreventionData.insertPreventionData(
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
				extList);
		return preventionToCareTrackerItemDataConverter.convert(prevention);
	}
}
