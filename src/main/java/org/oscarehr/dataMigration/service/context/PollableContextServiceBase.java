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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

public abstract class PollableContextServiceBase<T extends PollableContext>
{
	private final ConcurrentMap<String, T> contextMap;

	public PollableContextServiceBase()
	{
		contextMap = new ConcurrentHashMap<>();
	}

	/**
	 * register the given context object with the current thread process
	 * @param context - the context to register
	 * @return - the context identifier
	 */
	public synchronized String register(T context)
	{
		String key = getCurrentThreadKey();
		contextMap.put(key, context);
		this.notifyAll(); // wake any threads waiting for context to exist
		return key;
	}

	public synchronized T unregister()
	{
		return unregister(getCurrentThreadKey());
	}
	public synchronized T unregister(String identifier)
	{
		return contextMap.remove(identifier);
	}

	/**
	 * retrieve the context object that is registered with this thread
	 * @return - the registered context
	 */
	public synchronized T getContext()
	{
		return getContext(getCurrentThreadKey());
	}

	/**
	 * retrieve the context object that is registered with the given identifier
	 * @param identifier - the thread identifier
	 * @return - the registered context
	 */
	public synchronized T getContext(String identifier)
	{
		return contextMap.get(identifier);
	}

	/**
	 * wait for another thread to register the context with the specified identifier before returning
	 * @param identifier - the thread identifier
	 * @param timeout - the maximum length to wait.
	 * @return - the context
	 * @throws InterruptedException - if another thread has interrupted the current thread
	 * @throws TimeoutException - if the timeout duration is exceeded
	 */
	public synchronized T waitForContext(String identifier, long timeout) throws InterruptedException, TimeoutException
	{
		T context = getContext(identifier);
		if(context == null)
		{
			this.wait(timeout);
			context = getContext(identifier);

			if(context == null)
			{
				throw new TimeoutException("thread wait for context timed out (max wait time " + timeout + ")");
			}
		}
		return context;
	}

	private String getCurrentThreadKey()
	{
		return Thread.currentThread().getName();
	}
}
