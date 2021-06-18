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
package org.oscarehr.dataMigration.service.context;

import lombok.Data;
import org.oscarehr.ws.rest.transfer.common.ProgressBarPollingData;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
public class PollableContext
{
	private int total;
	private int processed;

	private ConcurrentMap<String, String> localProcessIdentifierMap;

	public PollableContext()
	{
		total = 0;
		processed = 0;
		localProcessIdentifierMap = new ConcurrentHashMap<>();
	}

	public synchronized void initialize(int total)
	{
		setTotal(total);
		setProcessed(0);
		localProcessIdentifierMap.clear();
	}

	public synchronized ProgressBarPollingData getProgress()
	{
		ProgressBarPollingData progressData = new ProgressBarPollingData();
		progressData.setTotal(getTotal());
		progressData.setProcessed(getProcessed());
		progressData.setMessage(getPollingMessage());
		progressData.setComplete(false);
		return progressData;
	}

	public synchronized void incrementProcessed()
	{
		this.processed += 1;
	}

	protected synchronized String getPollingMessage()
	{
		return "Processing " + getProcessed() + " of " + getTotal();
	}

	public synchronized void addProcessIdentifier(String identifier)
	{
		localProcessIdentifierMap.put(Thread.currentThread().getName(), identifier);
	}

	public synchronized String getCurrentProcessIdentifier()
	{
		return localProcessIdentifierMap.get(Thread.currentThread().getName());
	}

	public synchronized void clean()
	{
	}
}
