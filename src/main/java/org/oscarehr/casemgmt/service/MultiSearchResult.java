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

package org.oscarehr.casemgmt.service;

public abstract class MultiSearchResult implements Comparable<MultiSearchResult>
{
	public abstract String getText();

	public abstract void setText(String text);

	public abstract String getOnClick();

	public abstract void setOnClick(String onClick);

	public int compareTo(MultiSearchResult result)
	{
		return MultiSearchResult.compareText(this, result);
	}

	public static int compareText(Object o1, Object o2)
	{
		MultiSearchResult i1 = (MultiSearchResult)o1;
		MultiSearchResult i2 = (MultiSearchResult)o2;
		String t1 = i1.getText();
		String t2 = i2.getText();

		if( t1 == null && t2 != null )
		{
			return -1;
		}
		else if( t1 != null && t2 == null )
		{
			return 1;
		}
		else if( t1 == null && t2 == null )
		{
			return 0;
		}
		else
		{
			return t1.compareToIgnoreCase(t2);
		}
	}
}
