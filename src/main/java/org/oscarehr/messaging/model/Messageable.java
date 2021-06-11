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

package org.oscarehr.messaging.model;

public interface Messageable<T>
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	// ==========================================================================
	// Getters
	// ==========================================================================

	/**
	 * get the id of this messageable entity
	 * @return the id
	 */
	public String getId();

	/**
	 * get the type of this messageable.
	 * @return the type of the messageable.
	 */
	public MessageableType getType();

	/**
	 * get the name of the messageable. This should be a full name like,
	 * "LastName, FirstName".
	 * @return the name of the messageable
	 */
	public String getName();

	/**
	 * Get the "identification name" of the mssageable. This is similar to name
	 * but includes additional information for identification purposes. for example
	 * Jon, Wick (9594562543) (2021-01-01)
	 * @return a descriptive name of the messageable.
	 */
	public String getIdentificationName();

	/**
	 * get the entity which this messageable represents.
	 * @return returns the entity. This could be a new object or could just be 'this'.
	 */
	public T getEntity();

}
