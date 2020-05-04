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
import org.oscarehr.billing.CA.service.EligibilityCheckService;
import org.oscarehr.billing.CA.transfer.EligibilityCheckTransfer;
import org.oscarehr.common.Gender;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DemographicContact;
import org.oscarehr.common.model.PHRVerification;
import org.oscarehr.demographic.dao.DemographicCustDao;
import org.oscarehr.demographic.model.DemographicCust;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.demographic.service.HinValidationService;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.soap.v1.transfer.DemographicIntegrationTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.DemographicTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.PhrVerificationTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
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

	@Autowired
	private DemographicService demographicService;

	@Autowired
	private DemographicCustDao demographicCustDao;

	@Autowired
	private EligibilityCheckService eligibilityCheckService;

	@Autowired
	private HinValidationService hinValidationService;

	/**
	 * get demographic by ID if available
	 * @param demographicId demographic ID that we think there is an entry for
	 * @return transfer object if exists, null otherwise
	 */
	public DemographicTransfer getDemographic(Integer demographicId)
	{
		Demographic demographic = demographicManager.getDemographic(getLoggedInInfo(), demographicId);
		if (demographic == null)
		{
			return null;
		}
		DemographicCust custResult = demographicCustDao.find(demographic.getDemographicNo());

		DemographicTransfer transfer = DemographicTransfer.toTransfer(demographic);
		if (custResult != null)
		{
			transfer.setNotes(custResult.getParsedNotes());
		}

		return (transfer);
	}

	public DemographicTransfer getDemographicByMyOscarUserName(String myOscarUserName)
	{
		Demographic demographic=demographicManager.getDemographicByMyOscarUserName(getLoggedInInfo(),myOscarUserName);
		return(DemographicTransfer.toTransfer(demographic));
	}

	public List<DemographicTransfer> searchDemographicByName(String searchString, int startIndex, int itemsToReturn)
	{
		List<Demographic> demographics = demographicManager.searchDemographicByName(getLoggedInInfo(), searchString, startIndex, itemsToReturn);
		List<DemographicTransfer> transferList = new ArrayList<DemographicTransfer>();

		for (Demographic demographic : demographics)
		{
			DemographicCust custResult = demographicCustDao.find(demographic.getDemographicNo());

			DemographicTransfer transfer = DemographicTransfer.toTransfer(demographic);
			if (custResult != null)
			{
				transfer.setNotes(custResult.getParsedNotes());
			}
			transferList.add(transfer);
		}

		return (transferList);
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
	
	public Integer[] getDemographicIdsWithMyOscarAccounts(@WebParam(name="startDemographicIdExclusive") Integer startDemographicIdExclusive,
	                                                      @WebParam(name="itemsToReturn") int itemsToReturn)
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

	public List getDemographicsByHealthNum(String hin)
	{
		List<Demographic> demographicList = demographicManager.getDemographicsByHealthNum(hin);

		Iterator<Demographic> demographicListIterator = demographicList.iterator();
		List<DemographicTransfer> out = new ArrayList<DemographicTransfer>();
		while (demographicListIterator.hasNext())
		{
			Demographic demographic = demographicListIterator.next();

			DemographicCust custResult = demographicCustDao.find(demographic.getDemographicNo());

			DemographicTransfer transfer = DemographicTransfer.toTransfer(demographic);
			if (custResult != null)
			{
				transfer.setNotes(custResult.getParsedNotes());
			}
			out.add(transfer);
		}

		return (out);
	}

	public DemographicTransfer getDemographicByHealthNumber(String healthNumber) throws Exception
	{

		if (healthNumber == null || healthNumber.isEmpty())
		{
			throw new Exception("null or empty health numbers are not permitted");
		}

		Demographic demographic = demographicManager.getDemographicByHealthNumber(healthNumber);

		if (demographic != null)
		{
			DemographicTransfer result = DemographicTransfer.toTransfer(demographic);
			return result;
		}

		return null;
	}

	/**
	 * @return the ID of the demographic just added
	 */
	public Integer addDemographic(DemographicTransfer demographicTransfer,
	                              @Nullable DemographicIntegrationTransfer integrationTransfer) throws Exception
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

		hinValidationService.validateNoDuplication(demographic.getHin(), demographic.getVer(), demographic.getHcType());

		demographicManager.addDemographicWithValidation(loggedInInfo, demographic);
		demographicManager.addDemographicExtras(loggedInInfo, demographic, demographicTransfer);
		demographicManager.addDemographicExts(loggedInInfo, demographic, demographicTransfer);

		if(integrationTransfer != null)
		{
			demographicService.addDemographicIntegrationRecord(demographic.getDemographicNo(), integrationTransfer);
		}

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
		demographicManager.updateDemographicExtras(loggedInInfo, demographic, demographicTransfer);
		demographicManager.addDemographicExts(loggedInInfo, demographic, demographicTransfer);

	}

	/**
	 * SOAP endpoint to get demographic contacts. Searches are conducted based off contact type.
	 * @param demographicNo primary key for the demographic we're trying to pull for
	 * @param type identifier for the contact category we want to search.
	 *             DemographicContact doesn't explicitly have to support the type, but
	 *        	   we're treating a non-matched type as an error because we know it'll return nothing.
	 */
	public List<DemographicContact> getDemographicContact(int demographicNo, int type) throws Exception
	{
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		if (demographicManager.getDemographic(loggedInInfo, demographicNo) == null)
		{
			throw new Exception("Demographic " + demographicNo + " doesn't exist.");
		}

		if (!(type == DemographicContact.TYPE_PROVIDER
				|| type == DemographicContact.TYPE_DEMOGRAPHIC
				|| type == DemographicContact.TYPE_CONTACT
				|| type == DemographicContact.TYPE_PROFESSIONALSPECIALIST))
		{
			throw new Exception("Input type " + type + " doesn't match any of the expected types: " +
					DemographicContact.TYPE_PROVIDER + " (" + DemographicContact.TYPE_PROVIDER_TEXT + "), " +
					DemographicContact.TYPE_DEMOGRAPHIC + " (" + DemographicContact.TYPE_DEMOGRAPHIC_TEXT + "),  " +
					DemographicContact.TYPE_CONTACT + " (" + DemographicContact.TYPE_CONTACT_TEXT + "), " +
					DemographicContact.TYPE_PROFESSIONALSPECIALIST + " (" + DemographicContact.TYPE_PROFESSIONAL_SPECIALIST_TEXT + ")");
		}

		return demographicManager.getDemographicContactsByType(loggedInInfo, demographicNo, type);
	}

	public EligibilityCheckTransfer checkEligibility(DemographicTransfer demographicTransfer)
	{
		EligibilityCheckTransfer transfer;
		try
		{
			Demographic demographic = new Demographic();
			demographicTransfer.copyTo(demographic);

			transfer = eligibilityCheckService.checkEligibility(demographic);
		}
		catch(Exception e)
		{
			logger.error("check eligibility error", e);
			transfer = new EligibilityCheckTransfer();
			transfer.setError(e.getMessage());
		}
		return transfer;
	}
}
