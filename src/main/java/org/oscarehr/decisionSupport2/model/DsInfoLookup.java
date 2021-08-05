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
package org.oscarehr.decisionSupport2.model;

import java.util.Optional;

public interface DsInfoLookup
{
	/**
	 * get the number of months since the last recorded case
	 * @param typeCode the type of item
	 * @return the number of months since the last recorded data, or -1 if never recorded
	 */
	int getMonthsSinceLastRecordedDate(String typeCode);

	/**
	 * gets an optional numeric representation of the last recorded data, if possible
	 * @return the optional numeric value
	 */
	Optional<Double> getLatestValueNumeric(String typeCode);

	/**
	 * gets a string representation of the last recorded data, or null if never recorded
	 * @return the string value, or null
	 */
	String getLatestValue(String typeCode);

	/**
	 * gets the gender assigned to the patient record, as a string
	 * @return the gender string representation
	 */
	String getGender();

	/**
	 * get the patient age in years
	 * @return the age value
	 */
	int getAgeInYears();
}
