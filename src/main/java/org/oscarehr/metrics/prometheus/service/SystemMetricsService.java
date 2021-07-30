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

package org.oscarehr.metrics.prometheus.service;

import io.prometheus.client.Histogram;
import org.springframework.stereotype.Service;

@Service("SystemMetricsService")
public class SystemMetricsService
{
	// ==========================================================================
	// Metrics
	// ==========================================================================

	static final Histogram apiRequestsLatency = Histogram.build().name("api_request_latency").help("The average amount of time an API request takes").register();

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * record the latency (duration) of an api request.
	 * @param requestDurationMs - the duration of the request in milliseconds.
	 */
	public void recordApiRequestLatency(long requestDurationMs)
	{
		synchronized (apiRequestsLatency) {
			apiRequestsLatency.observe(requestDurationMs);
		}
	}

}
