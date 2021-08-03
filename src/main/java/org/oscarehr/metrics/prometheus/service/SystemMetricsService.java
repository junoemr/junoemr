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
import io.prometheus.client.Gauge;
import org.springframework.stereotype.Service;

@Service("SystemMetricsService")
public class SystemMetricsService
{
	// ==========================================================================
	// Metrics
	// ==========================================================================

	static final Histogram apiRestRequestsLatency = Histogram.build().name("api_rest_request_latency").help("The amount of time a REST API request takes").register();
	static final Histogram apiSoapRequestsLatency = Histogram.build().name("api_soap_request_latency").help("The amount of time a SOAP API request takes").register();
	static final Histogram rbtRequestLatency = Histogram.build().name("rbt_request_latency").help("The amount of time RBT queries take").register();
	static final Gauge rbtCurrentRequests = Gauge.build().name("rbt_current_requests").help("The number of currently running RBT queries").register();

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * record the latency (duration) of a REST api request.
	 * @param requestDurationMs - the duration of the request in milliseconds.
	 */
	public void recordRestApiRequestLatency(long requestDurationMs)
	{
		synchronized (apiRestRequestsLatency)
		{
			apiRestRequestsLatency.observe(requestDurationMs);
		}
	}

	/**
	 * record the latency (duration) of a SOAP api request.
	 * @param requestDurationMs - the duration of the request in milliseconds.
	 */
	public void recordSoapApiRequestLatency(long requestDurationMs)
	{
		synchronized (apiSoapRequestsLatency)
		{
			apiSoapRequestsLatency.observe(requestDurationMs);
		}
	}

	/**
	 * record the latency (duration) of an RBT run.
	 * @param rbtDurationMs - the duration of the RBT report.
	 */
	public void recordRbtRequestLatency(long rbtDurationMs)
	{
		synchronized (rbtRequestLatency)
		{
			rbtRequestLatency.observe(rbtDurationMs);
		}
	}

	/**
	 * increment the number of currently running RBTs.
	 * Don't forget to call decrementCurrentRunningRbtCount() when the RBT is done.
	 */
	public void incrementCurrentRunningRbtCount()
	{
		synchronized (rbtCurrentRequests)
		{
			rbtCurrentRequests.inc();
		}
	}

	/**
	 * decrement the number of currently running RBTs.
	 */
	public void decrementCurrentRunningRbtCount()
	{
		synchronized (rbtCurrentRequests)
		{
			rbtCurrentRequests.dec();
		}
	}

}
