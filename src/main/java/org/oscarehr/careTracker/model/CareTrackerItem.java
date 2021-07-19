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
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.decisionSupport2.model.DsRule;
import org.oscarehr.careTracker.entity.ItemType;
import org.oscarehr.careTracker.entity.ValueType;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CareTrackerItem extends AbstractTransientModel
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
	private List<CareTrackerItemAlert> careTrackerItemAlerts;
	private List<CareTrackerItemData> data;

	public CareTrackerItem()
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

	public void addCareTrackerItemAlert(CareTrackerItemAlert alert)
	{
		careTrackerItemAlerts.add(alert);
	}

	public void addCareTrackerItemData(CareTrackerItemData itemData)
	{
		data.add(itemData);
	}
}
