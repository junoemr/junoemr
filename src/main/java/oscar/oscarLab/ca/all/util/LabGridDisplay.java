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
package oscar.oscarLab.ca.all.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/*
 * Helper class to better encapsulate what we want to display.
 * Values get pulled from measurements/measurementsExt.
 */
public class LabGridDisplay
{
	private Integer measurementId;
	private String abnormal;
	private String result;
	private String dateObserved;
	private String labId;
	private String testName;

	@Override
	public String toString()
	{
		return "lab measurement: " + dateObserved + " | " + testName + " | " + result;
	}
}
