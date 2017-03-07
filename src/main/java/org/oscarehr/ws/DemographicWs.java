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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DemographicCust;
import org.oscarehr.managers.DemographicCustManager;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.ws.transfer_objects.DemographicTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import oscar.log.LogAction;
import oscar.oscarDemographic.data.DemographicRelationship;

@WebService
@Component
public class DemographicWs extends AbstractWs {

	@Resource
	private WebServiceContext wsContext;

	@Autowired
	private DemographicManager demographicManager;

	@Autowired
	private DemographicCustManager demographicCustManager;
	
	public DemographicTransfer getDemographic(Integer demographicId)
	{
		Demographic demographic=demographicManager.getDemographic(demographicId);
		DemographicCust custResult = demographicCustManager.getDemographicCust(demographic.getDemographicNo());

		DemographicTransfer transfer = DemographicTransfer.toTransfer(demographic);
		if(custResult != null) {
			transfer.setNotes(custResult.getParsedNotes());
		}
		return transfer;
	}
	/** Retrieve a list of demographics matched by lastname, fisrtname */
	public List<DemographicTransfer> getDemographicsByName(String lastName, String firstName)  {
		List<Demographic> demographicList=demographicManager.getDemographicsByName(lastName, firstName, 50);
		List<DemographicTransfer> transferList = new ArrayList<DemographicTransfer>();
		
		for(Demographic demographic: demographicList) {
			DemographicCust custResult = demographicCustManager.getDemographicCust(demographic.getDemographicNo());

			DemographicTransfer transfer = DemographicTransfer.toTransfer(demographic);
			if(custResult != null) {
				transfer.setNotes(custResult.getParsedNotes());
			}
			transferList.add(transfer);
		}
		return transferList;
	}

	public DemographicTransfer getDemographicByMyOscarUserName(String myOscarUserName)
	{
		Demographic demographic=demographicManager.getDemographicByMyOscarUserName(myOscarUserName);
		DemographicCust custResult = demographicCustManager.getDemographicCust(demographic.getDemographicNo());

		DemographicTransfer transfer = DemographicTransfer.toTransfer(demographic);
		if(custResult != null) {
			transfer.setNotes(custResult.getParsedNotes());
		}
		return transfer;
	}

	public List getDemographics(Integer pageSize, Integer pageNumber, String earliestUpdatedDate)
		throws Exception
	{
		if(pageSize == null)
		{
			throw new Exception("Page size is required.");
		}

		if(pageNumber == null)
		{
			throw new Exception("Page number is required.");
		}

		if(pageSize > 100)
		{
			throw new Exception("Maximum page size is 100.");
		}

		Date startDate = null;
		if(earliestUpdatedDate != null)
		{
			SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
			startDate = parser.parse(earliestUpdatedDate);
		}

		List<Demographic> demographicList = 
			demographicManager.getDemographics(pageSize, pageNumber, startDate);

		Iterator<Demographic> demographicListIterator = demographicList.iterator();
		List<DemographicTransfer> out = new ArrayList<DemographicTransfer>();
		while(demographicListIterator.hasNext())
		{
			Demographic demographic = demographicListIterator.next();
			out.add(DemographicTransfer.toTransfer(demographic));
		}

		return(out);
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
		demographicManager.addDemographicExtras(demographic, demographicTransfer);
		demographicManager.addDemographicExts(demographic, demographicTransfer);

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
		demographicManager.updateDemographicExtras(demographic, demographicTransfer);
		demographicManager.addDemographicExts(demographic, demographicTransfer);
	}

	public void addRelationship(Integer demographicId, Integer relationDemographicId, String relationship, boolean isSubstituteDecisionMaker, boolean isEmergencyContact, String notes, Integer creatorProviderId, Integer facilityId, boolean linkBothDirections)
		throws Exception
	{
		if(demographicId == null)
		{
			throw new Exception("demographicId cannot be null.");
		}

		if(relationDemographicId == null)
		{
			throw new Exception("relationDemographicId cannot be null.");
		}

		if(relationship == null)
		{
			throw new Exception("relationship cannot be null.");
		}

		// Convert provider number to a string, default to 0.
		String providerNo = "0";
		if(creatorProviderId != null)
		{
			providerNo = creatorProviderId.toString();
		}



		// Make sure demographics exist
		Demographic validatedDemographic = demographicManager.getDemographic(demographicId);

		if(validatedDemographic == null)
		{
			throw new Exception("Demographic " + demographicId + " doesn't exist.");
		}

		Demographic validatedRelation = demographicManager.getDemographic(relationDemographicId);

		if(validatedRelation == null)
		{
			throw new Exception("Relation Demographic " + relationDemographicId + " doesn't exist.");
		}

		// Make sure it's a valid relationship

		if(!DemographicRelationship.isValidRelationship(relationship))
		{
			throw new Exception("Relationship " + relationship + " is not valid.");
		}


        DemographicRelationship demo = new DemographicRelationship();

		if(linkBothDirections)
		{
			demo.addDemographicRelationships(demographicId.toString(), relationDemographicId.toString(), 
				relationship, isSubstituteDecisionMaker, isEmergencyContact, notes, providerNo, 
				facilityId);
		}
		else
		{
			demo.addDemographicRelationship(demographicId.toString(), relationDemographicId.toString(), 
				relationship, isSubstituteDecisionMaker, isEmergencyContact, notes, providerNo, 
				facilityId);
		}
	}
}
