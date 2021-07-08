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
package org.oscarehr.decisionSupport2.model.condition;

import lombok.Data;
import org.oscarehr.decisionSupport2.model.DsInfoLookup;

import java.util.Optional;

@Data
public class ConditionLatestValueLessThan extends DsCondition
{
	public ConditionLatestValueLessThan()
	{
		super(ConditionType.VALUE_LT);
	}

	@Override
	public boolean meetsRequirements(String typeCode, DsInfoLookup dsInfoLookup)
	{
		Optional<Double> latestValueOption = Optional.ofNullable(dsInfoLookup.getLatestValueNumeric(typeCode));
		Optional<Double> numericValue = getNumericValue();
		if(latestValueOption.isPresent() && numericValue.isPresent())
		{
			return latestValueOption.get() < numericValue.get();
		}
		return false;
	}
}
