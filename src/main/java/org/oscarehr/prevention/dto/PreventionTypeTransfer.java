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
package org.oscarehr.prevention.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

@Data
public class PreventionTypeTransfer
{
	private String name;
	private String description;
	private String code;
	private String atc;
	private String healthCanadaType;

	public static class SearchComparator implements Comparator<PreventionTypeTransfer>
	{
		@Override
		public int compare(PreventionTypeTransfer o1, PreventionTypeTransfer o2)
		{
			if(o1 == null && o2 == null)
			{
				return 0;
			}
			// nulls first
			else if(o1 == null)
			{
				return -1;
			}
			else if(o2 == null)
			{
				return 1;
			}

			Comparator<Integer> intComparator = Comparator.comparing(Integer::intValue);

			// shortest names first
			int result = intComparator.compare(StringUtils.trimToEmpty(o1.getName()).length(), StringUtils.trimToEmpty(o2.getName()).length());
			if(result == 0)
			{
				// alphabetical
				Comparator<String> stringComparator = Comparator.nullsFirst(Comparator.comparing(String::toString));
				result = stringComparator.compare(o1.getName(), o2.getName());
			}

			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			return false;
		}
	}

}
