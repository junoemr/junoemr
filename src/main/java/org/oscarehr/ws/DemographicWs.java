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
import javax.xml.ws.handler.MessageContext;
import javax.servlet.http.HttpServletRequest;
//import com.sun.net.httpserver.HttpExchange;

import org.oscarehr.common.model.Demographic;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.ws.transfer_objects.DemographicTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import oscar.log.LogAction;

@WebService
@Component
public class DemographicWs extends AbstractWs {

	@Resource
	private WebServiceContext wsContext;

	@Autowired
	private DemographicManager demographicManager;
	
	public DemographicTransfer getDemographic(Integer demographicId)
	{
		Demographic demographic=demographicManager.getDemographic(demographicId);
		return(DemographicTransfer.toTransfer(demographic));
	}

	public DemographicTransfer getDemographicByMyOscarUserName(String myOscarUserName)
	{
		Demographic demographic=demographicManager.getDemographicByMyOscarUserName(myOscarUserName);
		return(DemographicTransfer.toTransfer(demographic));
	}

	public List getDemographicsByHealthNum(String hin)
	{
		List<Demographic> demographicList = 
			demographicManager.getDemographicsByHealthNum(hin);

		Iterator<Demographic> demographicListIterator = demographicList.iterator();
		List<DemographicTransfer> out = new ArrayList<DemographicTransfer>();
		while(demographicListIterator.hasNext())
		{
			Demographic demographic = demographicListIterator.next();
			out.add(DemographicTransfer.toTransfer(demographic));
		}

		return(out);
	}

	/**
	 * @return the ID of the demographic just added
	 */
	public Integer addDemographic(DemographicTransfer demographicTransfer) 
		throws Exception
	{
		MessageContext mc = wsContext.getMessageContext();
		HttpServletRequest req = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST); 
		LogAction.addLogSynchronous("DemographicWs.addDemographic", 
			"Client IP = " + req.getRemoteAddr());

		Demographic demographic = new Demographic();
		demographicTransfer.copyTo(demographic);

		if(demographic.getDemographicNo() != null)
		{
			Integer demo_no = demographic.getDemographicNo();

			throw new Exception("Demographic " + demo_no + " already exists.");
		}

		demographicManager.addDemographic(demographic);
		demographicManager.addDemographicExtras(demographic);

		return(demographic.getDemographicNo());
	}
	
	public void updateDemographic(DemographicTransfer demographicTransfer)
		throws Exception
	{
		Demographic demographic = new Demographic();
		demographicTransfer.copyTo(demographic);

		Integer demo_no = demographic.getDemographicNo();

		Demographic existingDemographic = 
			demographicManager.getDemographic(demo_no);

		if(existingDemographic == null)
		{
			throw new Exception("Demographic " + demo_no + " doesn't exist.");
		}
		
		demographicManager.addDemographic(demographic);
	}
}
