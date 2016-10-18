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


package org.oscarehr.managers;

import org.oscarehr.common.dao.EFormDataDao;
import org.oscarehr.common.model.EFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oscar.log.LogAction;


@Service
public class EFormDataManager
{
	@Autowired
	private EFormDataDao eformDataDao;
	
	
	public EFormData getEFormData(Integer eformDataId)
	{
		int test = 1;
		//List input = null;
		List input = new ArrayList<Integer>();
		input.add(eformDataId);
		List<EFormData> resultArray = 
			eformDataDao.findByFdids(input);
		
		if(resultArray == null)
		{
			return null;
		}

		if(resultArray.size() > 0)
		{
			LogAction.addLogSynchronous("EFormDataManager.getEFormData", 
				"eformDataId=" + eformDataId);
		}

		return (resultArray.get(0));
	}

	public List getEFormDataList(Integer eformId, Integer demographicId, 
		Date startDate, Date endDate, Integer itemsPerPage, Integer page, boolean enablePaging)
	{
		List result = 
			eformDataDao.findFiltered(demographicId, eformId, startDate, endDate, itemsPerPage, page, enablePaging);

		if(result != null)
		{
			LogAction.addLogSynchronous("EFormDataManager.getEforms", "List");
		}

		return (result);
	}
}

