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
package org.oscarehr.careTracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oscarehr.careTracker.entity.ItemType;
import org.oscarehr.careTracker.entity.ValueType;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.careTrackerDecisionSupport.model.DsRule;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CareTrackerItemModel extends AbstractTransientModel
{
	private Integer id;
	private String name;
	private String description;
	private String guideline;
	private ItemType type;
	private String typeCode;

	private ValueType valueType;
	private String valueLabel;
	private List<DsRule> rules;

	// transient
	private boolean hidden;
	private List<CareTrackerItemAlertModel> careTrackerItemAlerts;
	private List<CareTrackerItemDataModel> data;

	public CareTrackerItemModel()
	{
		rules = new ArrayList<>();
		careTrackerItemAlerts = new ArrayList<>();
		data = new ArrayList<>();
	}

	public boolean isMeasurementType()
	{
		return ItemType.MEASUREMENT.equals(this.type);
	}

	public boolean isPreventionType()
	{
		return ItemType.PREVENTION.equals(this.type);
	}

	public void addCareTrackerItemAlert(CareTrackerItemAlertModel alert)
	{
		careTrackerItemAlerts.add(alert);
	}

	public void addCareTrackerItemData(CareTrackerItemDataModel itemData)
	{
		data.add(itemData);
	}
	public void addAllCareTrackerItemData(List<CareTrackerItemDataModel> itemDataList)
	{
		data.addAll(itemDataList);
	}
}
