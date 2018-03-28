/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package org.oscarehr.consultations.service;

import org.oscarehr.consultations.dao.ServiceSpecialistsDao;
import org.oscarehr.common.model.ServiceSpecialists;
import org.oscarehr.common.model.ServiceSpecialistsPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ServiceSpecialistsService
{
	@Autowired
	private ServiceSpecialistsDao dao;

	/**
	 * Update specialists associated with the given service to match the given list.
	 *
	 * @param serviceId    the service id
	 * @param specialists  a list of specialist ids
	 */
	public void updateServiceSpecialists(Integer serviceId, List<String> specialists)
	{
		List<Integer> specialistIds = new ArrayList<Integer>();

		for (String specialist : specialists)
		{
			specialistIds.add(Integer.parseInt(specialist));
		}

		dao.removeSpecialistsNotInList(serviceId, specialistIds);

		for (Integer specId : specialistIds)
		{
			ServiceSpecialists ss = new ServiceSpecialists();
			ss.setId(new ServiceSpecialistsPK(serviceId, specId));
			dao.merge(ss);
		}
	}

}
