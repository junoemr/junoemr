/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package org.oscarehr.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class DateMapComparator implements Comparator<Map<String, Serializable>>
{

	private final String key;

	public DateMapComparator(String key)
	{
		this.key = key;
	}

	public int compare(Map<String, Serializable> first,
					   Map<String, Serializable> second) throws RuntimeException
	{
		// TODO: Null checking, both for maps and values
		String firstValue = first.get(key).toString();
		String secondValue = second.get(key).toString();
		DateFormat dateInputFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		
		try
		{
			Date formattedFirstDate = dateInputFormat.parse(firstValue);
			Date formattedSecondDate = dateInputFormat.parse(secondValue);

			return formattedFirstDate.compareTo(formattedSecondDate);
		} catch (ParseException e)
		{
			MiscUtils.getLogger().error("Cannot sort lab dates: " + firstValue + " " + secondValue);
			throw new RuntimeException(e);
		}
	}
}
