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

import org.oscarehr.common.dao.EFormValueDao;
import org.oscarehr.common.model.EFormValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import oscar.log.LogAction;


@Service
public class EFormValueManager
{
	@Autowired
	private EFormValueDao eformValueDao;

	public EFormValue getEFormValue(Integer eformValueId)
	{
		EFormValue result = 
			eformValueDao.findById(eformValueId);
		
		if(result == null)
		{
			return null;
		}

		if(result != null)
		{
			LogAction.addLogSynchronous("EFormValueManager.getEFormValue", 
				"eformValueId=" + eformValueId);
		}

		return (result);
	}
	
	public List<EFormValue> getEFormValueList(Integer eformDataId)
	{
		List<EFormValue> result = 
			eformValueDao.findByFormDataId(eformDataId);

		if(result != null)
		{
			LogAction.addLogSynchronous("EFormValueManager.getEformValueList",
				"List");
		}

		return (result);
	}
}


