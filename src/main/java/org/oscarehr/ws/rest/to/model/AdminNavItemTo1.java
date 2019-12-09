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
package org.oscarehr.ws.rest.to.model;

import java.io.Serializable;

public class AdminNavItemTo1 implements Serializable
{
	private String name;
	private String transitionState;
	// if true perform a raw html transition with no angular magic
	private boolean rawTransition;

	public AdminNavItemTo1(String name, String transition)
	{
		this(name, transition, false);
	}

	public AdminNavItemTo1(String name, String transition, boolean rawTransition)
	{
		this.name = name;
		this.transitionState = transition;
		this.rawTransition = rawTransition;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getTransitionState()
	{
		return transitionState;
	}

	public void setTransitionState(String transitionState)
	{
		this.transitionState = transitionState;
	}

	public boolean isRawTransition()
	{
		return rawTransition;
	}

	public void setRawTransition(boolean rawTransition)
	{
		this.rawTransition = rawTransition;
	}
}
