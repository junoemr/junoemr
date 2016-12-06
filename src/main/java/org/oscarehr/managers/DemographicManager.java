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

import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.DemographicExtDao;
import org.oscarehr.common.dao.DemographicCustDao;
import org.oscarehr.PMmodule.dao.AdmissionDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DemographicCust;
import org.oscarehr.ws.transfer_objects.DemographicTransfer;
import org.oscarehr.PMmodule.model.Admission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
import java.util.List;

import oscar.log.LogAction;


@Service
public class DemographicManager
{
	@Autowired
	private DemographicDao demographicDao;

	@Autowired
	private DemographicExtDao demographicExtDao;

	@Autowired
	private DemographicCustDao demographicCustDao;

	@Autowired
	private AdmissionDao admissionDao;
	
	
	public Demographic getDemographic(Integer demographicId)
	{
		Demographic result=demographicDao.getDemographicById(demographicId);
		
		//--- log action ---
		if (result!=null)
		{
			LogAction.addLogSynchronous("DemographicManager.getDemographic", "demographicId="+demographicId);
		}
		
		return(result);
	}
	
	public Demographic getDemographicByMyOscarUserName(String myOscarUserName)
	{
		Demographic result=demographicDao.getDemographicByMyOscarUserName(myOscarUserName);
		
		//--- log action ---
		if (result!=null)
		{
			LogAction.addLogSynchronous("DemographicManager.getDemographic", 
				"demographicId="+result.getDemographicNo());
		}
		
		return(result);
	}

	public List getDemographics(int pageSize, int pageNumber, Date startDate)
	{
		List result = demographicDao.getDemographics(pageSize, pageNumber, startDate);

		if(result != null)
		{
			LogAction.addLogSynchronous(
				"DemographicManager.getDemographicsByHealthNum", 
				"List");
		}
		
		return(result);
	}

	public List getDemographicsByHealthNum(String hin)
	{
		List result = 
			demographicDao.getDemographicsByHealthNum(hin);
		
		//--- log action ---
		if (result!=null)
		{
			LogAction.addLogSynchronous(
				"DemographicManager.getDemographicsByHealthNum", 
				"List");
				//"demographicId="+result.getDemographicNo());
		}
		
		return(result);
	}

	public Integer addDemographic(Demographic demographic)
		throws Exception
	{
		validateDemographic(demographic);

		filterDemographic(demographic);

		demographicDao.save(demographic);

		//--- log action ---
		LogAction.addLogSynchronous("DemographicManager.addDemographic", 
			"demographicId=" + demographic.getDemographicNo());

		return(demographic.getDemographicNo());
	}

	public Integer addDemographicExts(Demographic demographic, 
		DemographicTransfer demographicTransfer)
	{
		boolean saved = false;
		if(demographicTransfer.getCellPhone() != null)
		{
			demographicExtDao.addKey(demographic.getProviderNo(), 
				demographic.getDemographicNo().toString(), "demo_cell", 
				demographicTransfer.getCellPhone());

			saved = true;
		}

		if(saved)
		{
			return(demographic.getDemographicNo());
		}
		else
		{
			return(null);
		}
	}

	public void updateDemographicExtras(Demographic demographic, DemographicTransfer demographicTransfer)
	{
		DemographicCust demoCust = demographicCustDao.find(demographic.getDemographicNo());

		demoCust.setParsedNotes(demographicTransfer.getNotes());

		demographicCustDao.merge(demoCust);
	}

	// When adding a demographic, entries are required in other tables.  This
	// method adds those entries.
	public Integer addDemographicExtras(Demographic demographic, DemographicTransfer demographicTransfer)
		throws Exception
	{
		DemographicCust demoCust = new DemographicCust();
		demoCust.setId(demographic.getDemographicNo());
		demoCust.setAlert("");
		demoCust.setResident("");
		demoCust.setMidwife("");
		demoCust.setNurse("");
		demoCust.setParsedNotes(demographicTransfer.getNotes());

		demographicCustDao.persist(demoCust);

		Date date = new Date();

		Admission admission = new Admission();

		admission.setProviderNo("");
		admission.setClientId(demographic.getDemographicNo());
		admission.setAdmissionDate(date);
		admission.setDischargeDate(date);
		admission.setProgramId(10016);
		admission.setTemporaryAdmission(false);
		admission.setClientStatusId(0);
		admission.setTeamId(0);
		admission.setClientId(demographic.getDemographicNo());
		admission.setTeamName("test");
		admission.setDischargeFromTransfer(false);
		admission.setAdmissionFromTransfer(false);
		admission.setAutomaticDischarge(false);
		admission.setAdmissionStatus("current");

		admissionDao.saveAdmission(admission);

		return(demographic.getDemographicNo());
	}

	public void filterDemographic(Demographic demographic)
	{
		// Set some default values
		if(demographic.getPatientStatus() == null)
		{
			demographic.setPatientStatus("AC");

			if(demographic.getPatientStatusDate() == null)
			{
				Date date = new Date();
				demographic.setPatientStatusDate(date);
			}
		}

		if(demographic.getFamilyDoctor() == null)
		{
			demographic.setFamilyDoctor("<rdohip></rdohip><rd></rd>");
		}

		// Set nulls to blank
		if(demographic.getTitle() == null)
		{
			demographic.setTitle("");
		}

		if(demographic.getAddress() == null)
		{
			demographic.setAddress("");
		}

		if(demographic.getCity() == null)
		{
			demographic.setCity("");
		}

		if(demographic.getProvince() == null)
		{
			demographic.setProvince("");
		}

		if(demographic.getPostal() == null)
		{
			demographic.setPostal("");
		}

		if(demographic.getPhone2() == null)
		{
			demographic.setPhone2("");
		}

		if(demographic.getHin() == null)
		{
			demographic.setHin("");
		}

		if(demographic.getVer() == null)
		{
			demographic.setVer("");
		}

		if(demographic.getRosterStatus() == null)
		{
			demographic.setRosterStatus("");
		}

		if(demographic.getChartNo() == null)
		{
			demographic.setChartNo("");
		}

		if(demographic.getSpokenLanguage() == null)
		{
			demographic.setSpokenLanguage("");
		}

		if(demographic.getOfficialLanguage() == null)
		{
			demographic.setOfficialLanguage("");
		}

		if(demographic.getProviderNo() == null)
		{
			demographic.setProviderNo("");
		}

		if(demographic.getSin() == null)
		{
			demographic.setSin("");
		}
	}

	public void validateDemographic(Demographic demographic)
		throws Exception
	{
		boolean has_error = false;
		String error_string = "";
		if(demographic.getFirstName() == null)	
		{
			error_string += "firstName is a required field.  ";
			has_error = true;
		}

		if(demographic.getLastName() == null)	
		{
			error_string += "lastName is a required field.  ";
			has_error = true;
		}

		if(!demographic.getSex().equals("M") 
			&& !demographic.getSex().equals("F"))
		{
			error_string += "sex must be either \"M\" or \"F\" (received " + 
				demographic.getSex() + ").  ";

			has_error = true;
		}

		if(demographic.getPhone() == null)	
		{
			error_string += "phone is a required field.  ";
			has_error = true;
		}

		if(demographic.getYearOfBirth() == null)	
		{
			error_string += "yearOfBirth is a required field.  ";
			has_error = true;
		}

		if(demographic.getMonthOfBirth() == null)	
		{
			error_string += "monthOfBirth is a required field.  ";
			has_error = true;
		}

		if(demographic.getDateOfBirth() == null)	
		{
			error_string += "dateOfBirth is a required field.  ";
			has_error = true;
		}

		if(!validateFamilyDoctor(demographic.getFamilyDoctor()))
		{
			error_string += "familyDoctor is formatted incorrectly.  It must ";
			error_string += "be a string like <rdohip>{family doctor number}";
			error_string += "</rdohip><rd>{last name},{first name}</rd>.  ";
			error_string += "Also no other tags and no quotes, line breaks ";
			error_string += "or semicolons are allowed.";
			has_error = true;
		}

		if(
			!validateString(demographic.getPhone()) ||
			!validateString(demographic.getPatientStatus()) ||
			!validateString(demographic.getRosterStatus()) ||
			!validateString(demographic.getProviderNo()) ||
			!validateString(demographic.getMyOscarUserName()) ||
			!validateString(demographic.getHin()) ||
			!validateString(demographic.getAddress()) ||
			!validateString(demographic.getProvince()) ||
			!validateString(demographic.getMonthOfBirth()) ||
			!validateString(demographic.getVer()) ||
			!validateString(demographic.getDateOfBirth()) ||
			!validateString(demographic.getSex()) ||
			!validateString(demographic.getSexDesc()) ||
			!validateString(demographic.getCity()) ||
			!validateString(demographic.getFirstName()) ||
			!validateString(demographic.getPostal()) ||
			!validateString(demographic.getPhone2()) ||
			!validateString(demographic.getPcnIndicator()) ||
			!validateString(demographic.getLastName()) ||
			!validateString(demographic.getHcType()) ||
			!validateString(demographic.getChartNo()) ||
			!validateString(demographic.getEmail()) ||
			!validateString(demographic.getYearOfBirth()) ||
			!validateString(demographic.getRosterTerminationReason()) ||
			!validateString(demographic.getLinks()) ||
			!validateString(demographic.getAlias()) ||
			!validateString(demographic.getPreviousAddress()) ||
			!validateString(demographic.getChildren()) ||
			!validateString(demographic.getSourceOfIncome()) ||
			!validateString(demographic.getCitizenship()) ||
			!validateString(demographic.getSin()) ||
			!validateString(demographic.getAnonymous()) ||
			!validateString(demographic.getSpokenLanguage()) ||
			!validateString(demographic.getDisplayName()) ||
			!validateString(demographic.getLastUpdateUser()) ||
			!validateString(demographic.getTitle()) ||
			!validateString(demographic.getOfficialLanguage()) ||
			!validateString(demographic.getCountryOfOrigin()) ||
			!validateString(demographic.getNewsletter()) ||
			!validateString(demographic.getScannedChart())
		)
		{
			error_string += "No html tags and no quotes, line breaks ";
			error_string += "or semicolons are allowed.";
			has_error = true;
		}

		if(has_error)
		{
			throw new Exception(error_string);
		}
	}

	public boolean validateFamilyDoctor(String familyDoctor)
	{
		if(familyDoctor == null)
		{
			return true;
		}

		// Make sure it is formatted correctly
        Pattern p = Pattern.compile("<rdohip>(.*)</rdohip><rd>(.*)</rd>");
        Matcher m = p.matcher(familyDoctor);

        if(m.matches())
        {
			// Fail if there are invalid characters in the contents
        	if(!validateString(m.group(1)))
			{
				return false;
			}

        	if(!validateString(m.group(2)))
			{
				return false;
			}

            return true;
        }

		return true;
	}

	public boolean validateString(String testValue)
	{
		if(testValue == null)
		{
			return true;
		}

        Pattern p = Pattern.compile(".*\\<.*?>.*");
        Matcher m = p.matcher(testValue);

		if(m.matches())
		{
			return false;
		}

		if(
			testValue.matches("(?s).*;.*") ||
			testValue.matches("(?s).*\".*") ||
			testValue.matches("(?s).*'.*") ||
			testValue.matches("(?s).*--.*") ||
			testValue.matches("(?s).*\\n.*") ||
			testValue.matches("(?s).*\\r.*") ||
			testValue.matches("(?s).*\\\\.*") ||
			testValue.matches("(?s).*\\x00.*") ||
			testValue.matches("(?s).*\\x1a.*")
		)
		{
			return false;
		}

		return true;
	}
}
