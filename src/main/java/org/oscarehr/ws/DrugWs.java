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

import org.oscarehr.common.model.Drug;
import org.oscarehr.managers.DrugManager;
import org.oscarehr.ws.transfer_objects.DrugTransfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;


@WebService
@Component
public class DrugWs extends AbstractWs {

	private static final Logger logger=MiscUtils.getLogger();

	@Resource
	private WebServiceContext wsContext;

	@Autowired
	private DrugManager drugManager;

	
	public DrugTransfer getDrug(Integer drugId)
	{
		Drug drug = drugManager.getDrug(drugId);
		return(DrugTransfer.toTransfer(drug));
	}

	public List getDrugList(Integer demographicId)
	{
		List<Drug> drugList = drugManager.getDrugList(demographicId);
		
		Iterator<Drug> drugListIterator = drugList.iterator();
		List<DrugTransfer> out = new ArrayList<DrugTransfer>();
		while(drugListIterator.hasNext())
		{
			Drug drug = drugListIterator.next();
			out.add(DrugTransfer.toTransfer(drug));
		}

		return(out);
	}
}

