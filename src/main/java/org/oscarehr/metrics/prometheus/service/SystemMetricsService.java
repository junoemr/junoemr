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

	// ========= API =========
	static final Histogram apiRestRequestsLatency = Histogram.build().name("api_rest_request_latency").help("The amount of time a REST API request takes").register();
	static final Histogram apiSoapRequestsLatency = Histogram.build().name("api_soap_request_latency").help("The amount of time a SOAP API request takes").register();

	// ========= RBT ========
	static final Histogram rbtRequestLatency = Histogram.build().name("rbt_request_latency").help("The amount of time RBT queries take").register();
	static final Gauge rbtCurrentRequest = Gauge.build().name("rbt_current_requests").help("The current number of running RBT requests").register();

	// ========= RBE ========
	static final Histogram rbeRequestLatency = Histogram.build().name("rbe_request_latency").help("The amount of time RBE queries take").register();
	static final Gauge rbeCurrentRequest = Gauge.build().name("rbe_current_requests").help("The current number of running RBE requests").register();

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	// ==========================================================================
	// API Metrics
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

	// ==========================================================================
	// RBT Metrics
	// ==========================================================================

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
	 * increment the count of currently running RBT reports.
	 * Be sure to call decrementCurrentRunningRbtCount once the report completes.
	 */
	public void incrementCurrentRunningRbtCount()
	{
		synchronized (rbtCurrentRequest)
		{
			rbtCurrentRequest.inc();
		}
	}

	/**
	 * decrement the count of currently running RBT reports.
	 */
	public void decrementCurrentRunningRbtCount()
	{
		synchronized (rbtCurrentRequest)
		{
			rbtCurrentRequest.dec();
		}
	}

	// ==========================================================================
	// RBE Metrics (Report By Example)
	// ==========================================================================

	/**
	 * record the latency (duration) of an RBE run.
	 * @param rbtDurationMs - the duration of the RBE report.
	 */
	public void recordRbeRequestLatency(long rbtDurationMs)
	{
		synchronized (rbtRequestLatency)
		{
			rbeRequestLatency.observe(rbtDurationMs);
		}
	}

	/**
	 * increment the count of currently running RBE reports.
	 * Be sure to call decrementCurrentRunningRbtCount once the report completes.
	 */
	public void incrementCurrentRunningRbeCount()
	{
		synchronized (rbtCurrentRequest)
		{
			rbeCurrentRequest.inc();
		}
	}

	/**
	 * decrement the count of currently running RBE reports.
	 */
	public void decrementCurrentRunningRbeCount()
	{
		synchronized (rbtCurrentRequest)
		{
			rbeCurrentRequest.dec();
		}
	}

}
