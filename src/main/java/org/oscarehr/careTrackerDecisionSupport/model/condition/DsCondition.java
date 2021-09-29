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
package org.oscarehr.careTrackerDecisionSupport.model.condition;

import lombok.Data;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.careTrackerDecisionSupport.model.DsInfoLookup;
import org.oscarehr.util.MiscUtils;

import java.util.Optional;

@Data
public abstract class DsCondition extends AbstractTransientModel
{
	private Integer id;
	private String value;
	private ConditionType type;

	protected DsCondition()
	{
		this(ConditionType.NEVER_GIVEN);
	}

	public DsCondition(ConditionType type)
	{
		this.type = type;
	}

	protected Optional<Double> getNumericValue()
	{
		Double numericValue = null;
		try
		{
			numericValue = Double.parseDouble(this.getValue());
		}
		catch(NumberFormatException e)
		{
			MiscUtils.getLogger().warn("invalid ds rule condition value: '" + getValue() + "' is not numeric");
		}
		return Optional.ofNullable(numericValue);
	}

	public abstract boolean meetsRequirements(String typeCode, DsInfoLookup dsInfoLookup);
}
