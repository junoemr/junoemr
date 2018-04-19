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


package org.oscarehr.ws.external.soap.v1;

import org.apache.cxf.annotations.GZIP;
import org.apache.log4j.Logger;
import org.oscarehr.common.Gender;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.PHRVerification;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.transfer_objects.DemographicTransfer;
import org.oscarehr.ws.transfer_objects.PhrVerificationTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@WebService
@Component
@GZIP(threshold= AbstractWs.GZIP_THRESHOLD)
public class DemographicWs extends AbstractWs {
	private static Logger logger=MiscUtils.getLogger();

	@Resource
	private WebServiceContext wsContext;

	@Autowired
	private DemographicManager demographicManager;
	
	public DemographicTransfer getDemographic(Integer demographicId)
	{
		Demographic demographic=demographicManager.getDemographic(getLoggedInInfo(),demographicId);
		return(DemographicTransfer.toTransfer(demographic));
	}

	public DemographicTransfer getDemographicByMyOscarUserName(String myOscarUserName)
	{
		Demographic demographic=demographicManager.getDemographicByMyOscarUserName(getLoggedInInfo(),myOscarUserName);
		return(DemographicTransfer.toTransfer(demographic));
	}
	
	public DemographicTransfer[] searchDemographicByName(String searchString, int startIndex, int itemsToReturn)
	{
		List<Demographic> demographics=demographicManager.searchDemographicByName(getLoggedInInfo(),searchString, startIndex, itemsToReturn);
		return(DemographicTransfer.toTransfers(demographics));
	}
	
	public PhrVerificationTransfer getLatestPhrVerificationByDemographic(Integer demographicId)
	{
		PHRVerification phrVerification=demographicManager.getLatestPhrVerificationByDemographicId(getLoggedInInfo(),demographicId);
		return(PhrVerificationTransfer.toTransfer(phrVerification));
	}
	
	/**
	 * This method should only return true if the demographic passed in is "phr verified" to a sufficient level to allow a provider to send this phr account messages.
	 */
	public boolean isPhrVerifiedToSendMessages(Integer demographicId)
	{
		boolean result=demographicManager.isPhrVerifiedToSendMessages(getLoggedInInfo(),demographicId);
		return(result);
	}

	/**
	 * This method should only return true if the demographic passed in is "phr verified" to a sufficient level to allow a provider to send this phr account medicalData.
	 */
	public boolean isPhrVerifiedToSendMedicalData(Integer demographicId)
	{
		boolean result=demographicManager.isPhrVerifiedToSendMedicalData(getLoggedInInfo(),demographicId);
		return(result);		
	}
	
	/**
	 * see DemographicManager.searchDemographicsByAttributes for parameter details
	 */
	public DemographicTransfer[] searchDemographicsByAttributes(String hin, String firstName, String lastName, Gender gender, Calendar dateOfBirth, String city, String province, String phone, String email, String alias, int startIndex, int itemsToReturn) {
		List<Demographic> demographics=demographicManager.searchDemographicsByAttributes(getLoggedInInfo(),hin, firstName, lastName, gender, dateOfBirth, city, province, phone, email, alias, startIndex, itemsToReturn);
		return(DemographicTransfer.toTransfers(demographics));	
	}
	
	/**
	 * programId can be null for all / any program
	 */
	public Integer[] getAdmittedDemographicIdsByProgramProvider(Integer programId, String providerNo)
	{
		logger.debug("programId="+programId+", providerNo="+providerNo);
		List<Integer> results=demographicManager.getAdmittedDemographicIdsByProgramAndProvider(getLoggedInInfo(), programId, providerNo);
		return(results.toArray(new Integer[0]));
	}
	
	public Integer[] getDemographicIdsWithMyOscarAccounts(@WebParam(name="startDemographicIdExclusive") Integer startDemographicIdExclusive, @WebParam(name="itemsToReturn") int itemsToReturn)
	{
		List<Integer> results=demographicManager.getDemographicIdsWithMyOscarAccounts(getLoggedInInfo(), startDemographicIdExclusive, itemsToReturn);
		return(results.toArray(new Integer[0]));
	}
	
	public DemographicTransfer[] getDemographics(Integer[] demographicIds)
	{
		ArrayList<Integer> ids=new ArrayList<Integer>();
		for(Integer i : demographicIds)
		{
			ids.add(i);
		}
		
		List<Demographic> demographics=demographicManager.getDemographics(getLoggedInInfo(),ids);
		return(DemographicTransfer.toTransfers(demographics));	
	}

	/**
	 * @return the ID of the demographic just added
	 */
	public Integer addDemographic(DemographicTransfer demographicTransfer) throws Exception
	{
		MessageContext mc = wsContext.getMessageContext();
		HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
		LoggedInInfo loggedInInfo = getLoggedInInfo();

		LogAction.addLogEntrySynchronous("DemographicWs.addDemographic", "Client IP = " + req.getRemoteAddr());

		Demographic demographic = new Demographic();
		demographicTransfer.copyTo(demographic);

		if (demographic.getDemographicNo() != null)
		{
			throw new Exception("Demographic number can not be specified on creation. It is automatically generated.");
		}

		demographicManager.addDemographicWithValidation(loggedInInfo, demographic);
		demographicManager.addDemographicExtras(loggedInInfo, demographic);
		demographicManager.addDemographicExts(loggedInInfo, demographic, demographicTransfer);

		return demographic.getDemographicNo();
	}

	public void updateDemographic(DemographicTransfer demographicTransfer) throws Exception
	{
		LoggedInInfo loggedInInfo = getLoggedInInfo();

		Demographic demographic = new Demographic();
		demographicTransfer.copyTo(demographic);

		Integer demo_no = demographic.getDemographicNo();

		if (demo_no == null)
		{
			throw new Exception("You must specify a demographic number.");
		}

		if (demographicManager.getDemographic(loggedInInfo, demo_no) == null)
		{
			throw new Exception("Demographic " + demo_no + " doesn't exist.");
		}

		demographicManager.addDemographicWithValidation(loggedInInfo, demographic);
		demographicManager.updateDemographicExtras(loggedInInfo, demographic);
		demographicManager.addDemographicExts(loggedInInfo, demographic, demographicTransfer);

	}
}
