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
package org.oscarehr.ws.rest.filter;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.filter.exception.RateLimitException;
import oscar.OscarProperties;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class RateLimitFilter implements ContainerRequestFilter
{
	private static Logger logger = MiscUtils.getLogger();

	private static final OscarProperties props = OscarProperties.getInstance();
	private static final boolean enabled = props.isPropertyActive("rate_limit_filter.rate_limit_enabled");
	private static final Integer rateLimit = Integer.parseInt(props.getProperty("rate_limit_filter.rate_limit"));
	private static final Integer period = Integer.parseInt(props.getProperty("rate_limit_filter.period_in_ms"));

	/* ArrayBlockingQueue is used because it supports concurrency and a finite size,
	 * but we don't use any of the thread blocking methods that it offers.
	 */
	private Queue<Long> queue = new ArrayBlockingQueue<>(rateLimit, true);

	/**
	 * Request filter
	 */
	public void filter(ContainerRequestContext request)
	{
		if(enabled)
		{
			long currentTimestamp = System.currentTimeMillis();
			long expireyTimestamp = currentTimestamp - period;

			// remove expired entries from the queue
			Long oldestTimestamp = queue.peek();
			while(oldestTimestamp != null && (oldestTimestamp < expireyTimestamp))
			{
				queue.poll();
				oldestTimestamp = queue.peek();
			}
			// if the queue is full, remove the oldest entry so the newest can be added.
			if(queue.size() >= rateLimit)
			{
				queue.poll();
			}

			// add the new timestamp
			queue.add(currentTimestamp);

			// if the queue is full, the rate limit has been reached
			logger.debug("Request Rate Limit reached?: " + (queue.size() >= rateLimit));
			logger.debug("Queue Size: " + queue.size());

			// if the queue is full, the rate limit has been reached
			if(!queue.isEmpty() && queue.size() >= rateLimit)
			{
				oldestTimestamp = queue.peek();// won't ever be null with a full queue
				request.setProperty(LoggingFilter.PROP_SKIP_LOGGING, true);
				throw new RateLimitException(queue.size(), rateLimit, period, (oldestTimestamp - expireyTimestamp));
			}
		}
	}
}
