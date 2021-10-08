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
package oscar.oscarLab.ca.all.parsers.OLIS;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

@Getter
@Setter
class OLISSortKey
{
	private String msgKey;
	private String olisKey;
	private String altName1;
	private String subId;
	private ZonedDateTime relativeDateTime;

	public OLISSortKey(String msgKey, String olisKey, String altName1, String subId, ZonedDateTime relativeDateTime)
	{
		this.msgKey = StringUtils.trimToEmpty(msgKey);
		this.olisKey = StringUtils.trimToEmpty(olisKey);
		this.altName1 = StringUtils.trimToEmpty(altName1);
		this.subId = StringUtils.trimToEmpty(subId);
		this.relativeDateTime = relativeDateTime;
	}

	public static Comparator<OLISSortKey> getKeyComparator()
	{
		return (o1, o2) -> {
			Comparator<String> stringComparator = Comparator.comparing(String::toString);

			// check null input cases
			if(o1 == null && o2 == null)
			{
				return 0; // equal
			}
			if(o1 == null)
			{
				return -1;
			}
			if(o2 == null)
			{
				return 1;
			}

			// comapare values in olis preferred ordering
			int result = stringComparator.compare(o1.getMsgKey(), o2.getMsgKey());
			if(result == 0)
			{
				result = stringComparator.compare(o1.getOlisKey(), o2.getOlisKey());
			}
			if(result == 0)
			{
				result = stringComparator.compare(o1.getAltName1(), o2.getAltName1());
			}
			if(result == 0)
			{
				result = stringComparator.compare(o1.getSubId(), o2.getSubId());
			}
			if(result == 0 && o1.getRelativeDateTime() != null && o2.getRelativeDateTime() != null) //TODO null checking comparison
			{
				Comparator<ZonedDateTime> dateTimeComparator = Comparator.comparing(zdt -> zdt.truncatedTo(ChronoUnit.SECONDS));
				result = dateTimeComparator.compare(o1.getRelativeDateTime(), o2.getRelativeDateTime());
			}
			return result;
		};
	}
}
