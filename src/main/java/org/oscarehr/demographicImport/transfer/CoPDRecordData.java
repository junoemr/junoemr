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

package org.oscarehr.demographicImport.transfer;

import org.apache.log4j.Logger;
import java.util.HashMap;

/**
 * A transfer object for managing/recording information across an entire import.
 * This allows recording information from all stages of the import, including before the file is parsed
 * It also allows pre-process data to be linked with a record created during the import
 */
public class CoPDRecordData
{
	private static final Logger logger = Logger.getLogger(CoPDRecordData.class);

	private Integer demographicId;
	private final HashMap<String, CoPDRecordMessage> observationMessages;

	public CoPDRecordData()
	{
		this.observationMessages = new HashMap<>();
	}

	public Integer getDemographicId()
	{
		return demographicId;
	}

	public void setDemographicId(Integer demographicId)
	{
		this.demographicId = demographicId;
	}

	public CoPDRecordMessage getObservationMessage(String segmentId)
	{
		return observationMessages.get(segmentId);
	}

	public void addObservationMessage(String segmentId, String message)
	{
		this.observationMessages.put(segmentId, new CoPDRecordMessage(segmentId, message));
	}

	public void print()
	{
		if(!observationMessages.isEmpty())
		{
			for(CoPDRecordMessage message : this.observationMessages.values())
			{
				logger.info(getDemographicId() + "," + message.toString());
			}
		}
	}
}
