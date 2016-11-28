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


package org.oscarehr.ws;

import javax.jws.WebService;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import org.oscarehr.common.model.EForm;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.common.model.EFormValue;
import org.oscarehr.managers.EFormManager;
import org.oscarehr.managers.EFormDataManager;
import org.oscarehr.managers.EFormValueManager;
import org.oscarehr.ws.transfer_objects.EFormTransfer;
import org.oscarehr.ws.transfer_objects.EFormDataTransfer;
import org.oscarehr.ws.transfer_objects.EFormValueTransfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;


@WebService
@Component
public class EFormWs extends AbstractWs {

	private static final Logger logger=MiscUtils.getLogger();

	@Resource
	private WebServiceContext wsContext;

	@Autowired
	private EFormManager eformManager;

	@Autowired
	private EFormDataManager eformDataManager;

	@Autowired
	private EFormValueManager eformValueManager;

	
	public EFormTransfer getEForm(Integer eformId)
	{
		EForm eform = eformManager.getEForm(eformId);
		return(EFormTransfer.toTransfer(eform));
	}

	public List getEFormList()
	{
		List<EForm> eformList = eformManager.getEForms();
		
		Iterator<EForm> eformListIterator = eformList.iterator();
		List<EFormTransfer> out = new ArrayList<EFormTransfer>();
		while(eformListIterator.hasNext())
		{
			EForm eform = eformListIterator.next();
			out.add(EFormTransfer.toTransfer(eform));
		}

		return(out);
	}

	public EFormDataTransfer getEFormData(Integer eformDataId)
	{
		EFormData eformData = eformDataManager.getEFormData(eformDataId);
		return(EFormDataTransfer.toTransfer(eformData));
	}

	public List<EFormDataTransfer> getEFormDataList(Integer eformId, Integer demographicNo)
		throws ParseException
	{
		return getEFormData(eformId, demographicNo, null, false);
	}

	public List<EFormDataTransfer> getEFormDataListFiltered(Integer eformId, Integer demographicNo, Map<String, String> filter)
		throws ParseException
	{
		return getEFormData(eformId, demographicNo, filter, true);
	}

	private List<EFormDataTransfer> getEFormData(Integer eformId, Integer demographicNo, Map<String, String> filter, boolean enablePaging)
		throws ParseException
	{
		Date startDate = null;
		Date endDate = null;
		Integer itemsPerPage = null;
		Integer page = null;

		if(filter != null)
		{
			Iterator<String> i = filter.keySet().iterator();
			while(i.hasNext())
			{
				String key = i.next();
				SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
				if(key.equals("start_date"))
				{
					startDate = parser.parse(filter.get(key));
				}
				else if(key.equals("end_date"))
				{
					endDate = parser.parse(filter.get(key));
				}
				else if(key.equals("items_per_page"))
				{
					itemsPerPage = Integer.parseInt(filter.get(key));
				}
				else if(key.equals("page"))
				{
					page = Integer.parseInt(filter.get(key));
				}
			}
		}

		List<EFormData> eformDataList = 
			eformDataManager.getEFormDataList(eformId, demographicNo, startDate, 
				endDate, itemsPerPage, page, enablePaging);
		
		Iterator<EFormData> eformDataListIterator = eformDataList.iterator();
		List<EFormDataTransfer> out = new ArrayList<EFormDataTransfer>();
		while(eformDataListIterator.hasNext())
		{
			EFormData eformData = eformDataListIterator.next();
			out.add(EFormDataTransfer.toTransfer(eformData));
		}

		return(out);
	}

	public EFormValueTransfer getEFormValue(Integer eformValueId)
	{
		EFormValue eformValue = eformValueManager.getEFormValue(eformValueId);
		return(EFormValueTransfer.toTransfer(eformValue));
	}

	public List getEFormValueList(Integer eformDataId)
	{
		List<EFormValue> eformValueList = 
			eformValueManager.getEFormValueList(eformDataId);
		
		Iterator<EFormValue> eformValueListIterator = eformValueList.iterator();
		List<EFormValueTransfer> out = new ArrayList<EFormValueTransfer>();
		while(eformValueListIterator.hasNext())
		{
			EFormValue eformValue = eformValueListIterator.next();
			out.add(EFormValueTransfer.toTransfer(eformValue));
		}

		return(out);
	}
}
