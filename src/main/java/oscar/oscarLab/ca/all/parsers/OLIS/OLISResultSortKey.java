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

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

@Data
class OLISResultSortKey implements Comparable<OLISResultSortKey>
{
	public static final String ANCILLARY_STATUS = "Z";

	private String resultStatus;
	private String resultKey;
	private String nomenclatureKey;
	private String alternateName;
	private String subId;
	private ZonedDateTime releaseDateTime;

	private Integer originalIndex;

	public OLISResultSortKey(String resultStatus, String resultKey, String nomenclatureKey, String alternateName, String subId, ZonedDateTime releaseDateTime)
	{
		this.resultStatus = StringUtils.trimToNull(resultStatus);
		this.resultKey = StringUtils.trimToNull(resultKey);
		this.nomenclatureKey = StringUtils.trimToNull(nomenclatureKey);
		this.alternateName = StringUtils.trimToNull(alternateName);
		this.subId = StringUtils.trimToNull(subId);
		this.releaseDateTime = releaseDateTime;
	}

	/**
	 * OLIS DEFAULT SORTING:
	 * After sorting test requests (see requirement 7.1.1), sort test results as follows:
	 * 1.  If OBX.11 = Z (ancillary), then sort this test result first
	 * 2.  If multiple ancillary test results, sort by test result sort key (either from ZBX.2 or OLIS) within ancillary test results
	 * 3.  If no sort key, then sort by OLIS test result alternate name 1
	 * then sort the following in ascending alphanumeric order:
	 * 4.  Test result sort key in HL7 message (ZBX.2)
	 * 5.  If no ZBX.2, then lookup and use OLIS nomenclature sort key
	 * 6.  If duplicate ZBX.2, then sort by OLIS nomenclature sort key (if no OLIS sort key, then lookup and use test result alternate name 1) within duplicates, then sort by ZBX.2 sort key
	 * 7.  If no OLIS sort key, then lookup and use OLIS test result alternate name 1
	 * 8.  If duplicate alternate name 1, then sort by observation sub-ID (OBX.4) within results that share the same alternate name 1, then sort by alternate name 1
	 * 9.  If no or duplicate sub-ID, then sort by test result release date/time (ZBX.1) within results that share the same sub-ID, then sort by alternate name 1
	 * Note:  OLIS will reject an HL7 message (data collection) if more than one OBX segment has the same value in OBX.3, OBX.4 and ZBX.1.  Refer to OLIS FAQs, #42.
	 */
	@Override
	public int compareTo(@Nullable OLISResultSortKey o2)
	{
		OLISResultSortKey o1 = this;

		// nulls last
		if(o2 == null)
		{
			return -1;
		}

		// comapare values in olis preferred ordering
		int result;
		// if status = Z (ancillary), then sort this test result first
		if(ANCILLARY_STATUS.equalsIgnoreCase(o1.getResultStatus()) && !ANCILLARY_STATUS.equalsIgnoreCase(o2.getResultStatus()))
		{
			result = -1;
		}
		else if(ANCILLARY_STATUS.equalsIgnoreCase(o2.getResultStatus()) && !ANCILLARY_STATUS.equalsIgnoreCase(o1.getResultStatus()))
		{
			result = 1;
		}
		else
		{
			Comparator<String> stringComparator = Comparator.nullsLast(Comparator.comparing(String::toString));

			result = stringComparator.compare(o1.getResultKey(), o2.getResultKey());
			if(result == 0)
			{
				result = stringComparator.compare(o1.getNomenclatureKey(), o2.getNomenclatureKey());
			}
			if(result == 0)
			{
				result = stringComparator.compare(o1.getAlternateName(), o2.getAlternateName());
			}
			if(result == 0)
			{
				result = stringComparator.compare(o1.getSubId(), o2.getSubId());
			}
			if(result == 0)
			{
				Comparator<ZonedDateTime> dateTimeComparator = Comparator.nullsLast(Comparator.comparing(zdt -> zdt.truncatedTo(ChronoUnit.SECONDS)));
				result = dateTimeComparator.compare(o1.getReleaseDateTime(), o2.getReleaseDateTime());
			}
		}
		return result;
	}
}
