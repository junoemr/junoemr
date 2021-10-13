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
class OLISRequestSortKey implements Comparable<OLISRequestSortKey>
{
	private ZonedDateTime collectionDateTime;
	private String placerGroupNumber;
	private String testRequestKey;
	private String nomenclatureKey;
	private String alternateName;
	private Integer sequenceId;

	private Integer originalIndex;

	public OLISRequestSortKey(ZonedDateTime collectionDateTime, String placerGroupNumber, String testRequestKey, String nomenclatureKey, String alternateName, Integer sequenceId)
	{
		this.collectionDateTime = collectionDateTime;
		this.placerGroupNumber = StringUtils.trimToNull(placerGroupNumber);
		this.testRequestKey = StringUtils.trimToNull(testRequestKey);
		this.nomenclatureKey = StringUtils.trimToNull(nomenclatureKey);
		this.alternateName = StringUtils.trimToNull(alternateName);
		this.sequenceId = sequenceId;
	}

	/**
	 * OLIS DEFAULT SORTING:
	 * Sort test requests as follows:
	 * 1.  Collection date/time (OBR.7) in reverse chronological order (Summary and Details view only)
	 * 2.  Placer group number (ORC.4)
	 * then sort the following in ascending alphanumeric order:
	 * 3.  Test request sort key in HL7 message (ZBR.11)
	 * 4.  If no ZBR.11, then lookup and use OLIS nomenclature sort key
	 * 5.  If duplicate ZBR.11 sort key, then sort by OLIS nomenclature sort key (if no OLIS sort key, then lookup and use test request alternate name 1) within duplicates, then sort by ZBR.11 sort key
	 * 6.  If no OLIS sort key, then lookup and use OLIS test request alternate name 1
	 * 7.  If duplicate alternate name 1, then sort within duplicates by physical sequence of OBR in the HL7 message, then sort by alternate name 1
	 * Note:  It is not required to sort by report category - sort keys accommodate this automatically
	 * Refer to requirement 7.2 (Test Result Sort Keys) for test result sorting requirements
	 * Refer to OLIS FAQs, #42.
	 */
	@Override
	public int compareTo(@Nullable OLISRequestSortKey o2)
	{
		OLISRequestSortKey o1 = this;

		// nulls last
		if(o2 == null)
		{
			return -1;
		}

		// comapare values in olis preferred ordering
		Comparator<ZonedDateTime> dateTimeComparator = Comparator.nullsLast(Comparator.comparing(zdt -> zdt.truncatedTo(ChronoUnit.SECONDS), Comparator.reverseOrder()));
		int result = dateTimeComparator.compare(o1.getCollectionDateTime(), o2.getCollectionDateTime());

		if(result == 0)
		{
			Comparator<String> stringComparator = Comparator.nullsLast(Comparator.comparing(String::toString));
			result = stringComparator.compare(o1.getPlacerGroupNumber(), o2.getPlacerGroupNumber());

			if(result == 0)
			{
				result = stringComparator.compare(o1.getTestRequestKey(), o2.getTestRequestKey());
			}
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
				Comparator<Integer> intComparator = Comparator.nullsLast(Comparator.comparing(Integer::intValue));
				result = intComparator.compare(o1.getSequenceId(), o2.getSequenceId());
			}
		}
		return result;
	}
}
