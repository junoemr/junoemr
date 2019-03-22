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
package org.oscarehr.common.server.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.common.server.model.SlaveStatus;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class SlaveStatusDao extends AbstractDao<SlaveStatus>
{
	public SlaveStatusDao()
	{
		super(SlaveStatus.class);
	}

	/**
	 * Tries to determine if the current server is running in slave mode.
	 * If show slave status returns results that look like a slave server,
	 * this method returns true, false if the state is not definitively a slave state
	 *
	 * The mysql database user requires replication client privileges to run this.
	 * @return true if the server is running as a slave, false otherwise
	 */
	public boolean inSlaveMode()
	{
		SlaveStatus slaveStatus = getSlaveStatus();
		return  slaveStatus != null
				&& slaveStatus.getSlaveIORunning().equalsIgnoreCase("Yes")
				&& slaveStatus.getSlaveSQLRunning().equalsIgnoreCase("Yes");
	}

	/**
	 * runs get slave status on the database.
	 *
	 * The mysql database user requires replication client privileges to run this.
	 * @return the status objects returned, or null if no result is returned
	 */
	private SlaveStatus getSlaveStatus()
	{
		Query query = entityManager.createNativeQuery("SHOW SLAVE STATUS");

		List<Object[]> resultList = query.getResultList();
		if(resultList.isEmpty())
		{
			return null;
		}
		return toMappedEntity(resultList.get(0));
	}

	private static SlaveStatus toMappedEntity(Object[] result)
	{
		SlaveStatus slaveStatus = new SlaveStatus();

		slaveStatus.setId(null);
		slaveStatus.setSlaveIORunning((String) result[10]);
		slaveStatus.setSlaveSQLRunning((String) result[11]);

		return slaveStatus;
	}
}
