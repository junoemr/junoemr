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


package org.oscarehr.hospitalReportManager.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Holds aggregate statistics pertaining to the HRM Fetch task
 */
@Data
public class HRMFetchResults
{
	private int reportsDownloaded = 0;
	private int reportsProcessed = 0;
	
	private LocalDateTime startTime = LocalDateTime.now();
	private LocalDateTime endTime = null;
	
	private boolean loginSuccess = false;
	private boolean downloadSuccess = false;
	private boolean processingSuccess = false;
	
	Duration getDuration()
	{
		if (endTime == null)
		{
			return Duration.ZERO;
		}
		
		return Duration.between(startTime, endTime);
	}
}
