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




import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.eform.dao.EFormDao;
import org.oscarehr.eform.dao.EFormDao.EFormSortOrder;
import org.oscarehr.eform.dao.EFormDataDao;
import org.oscarehr.common.dao.EFormGroupDao;
import org.oscarehr.common.dao.EncounterFormDao;
import org.oscarehr.eform.model.EForm;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.common.model.EncounterForm;
import org.oscarehr.eform.service.EFormDataService;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.log.LogAction;
import oscar.oscarEncounter.data.EctFormData;

/**
 * 
 * This class will change soon to incorporate dealing with forms
 *
 */
@Service
@Transactional
public class FormsManager {
	   
	
	@Autowired
	private EFormDao eformDao;
	
	@Autowired
	private EFormGroupDao eFormGroupDao;

	@Autowired
	private EFormDataDao eFormDataDao; 
	
	@Autowired
	private EncounterFormDao encounterFormDao;

	@Autowired
	EFormDataService eFormDataService;

	public static final String EFORM = "eform"; 
	public static final String FORM = "form";
	
	
	
	/**
	 * Finds all eforms based on the status. 
	 * 
	 * @param status	
	 * 		Status to be used when looking up forms. 
	 * @param sortOrder
	 * 		Order how records should be sorted. Providing no sort order delegates to the default sorting order of the persistence provider 
	 * @return
	 * 		Returns the list of all forms with the specified status.
	 */

	public List<EForm> findByStatus(LoggedInInfo loggedInInfo, boolean status, EFormSortOrder sortOrder) {
		List<EForm> results = eformDao.findByStatus(status, sortOrder);
		
		if (results.size() > 0) {
			String resultIds = EForm.getIdsAsStringList(results);
			LogAction.addLogSynchronous(loggedInInfo, "FormsManager.findByStatus", "ids returned=" + resultIds);
		}

		return (results);
	}


    /**
     * get eform in group by group name
     * @param groupName
     * @return list of EForms
     */
    public List<EForm> getEfromInGroupByGroupName(LoggedInInfo loggedInInfo, String groupName){
    	List<EForm> results = eformDao.getEfromInGroupByGroupName(groupName);
    	if (results.size() > 0) {
			String resultIds = EForm.getIdsAsStringList(results);
			LogAction.addLogSynchronous(loggedInInfo, "FormsManager.getEfromInGroupByGroupName", "ids returned=" + resultIds);
		}

		return (results);
    }

	/**
	 * return eform revisions for the given demographic
	 * @param demographicId - the demographic you whish to get eform revisions for
	 * @return - a list of eform data objects. each being a revision of an eform.
	 */
	public List<EFormData> getEFormRevisionsInstances(Integer demographicId)
	{
		return eFormDataDao.findInstancedVersionsByDemographicId(demographicId, null, null, false);
	}

	/**
	 * get all delete eform instances
	 * @param demographicId - demographic no
	 * @return - deleted eform instances
	 */
	public List<EFormData> getDeletedEFormInstances(Integer demographicId)
	{
		return eFormDataDao.findInstancedByDemographicId(demographicId, null, null, false);
	}
    
    public List<String> getGroupNames(){
    	return eFormGroupDao.getGroupNames();
    }


	public List<EFormData> findByDemographicId(LoggedInInfo loggedInInfo, Integer demographicId)
	{
		List<EFormData> results = eFormDataDao.findByDemographicId(demographicId);
		if(results.size() > 0)
		{
			String resultIds = EForm.getIdsAsStringList(results);
			LogAction.addLogSynchronous(loggedInInfo, "FormsManager.findByDemographicId", "ids returned=" + resultIds);
		}

		return (results);
	}

	public List<EFormData> findInstancedByDemographicId(LoggedInInfo loggedInInfo, Integer demographicId)
	{
		List<EFormData> results = eFormDataDao.findInstancedByDemographicId(demographicId);
		if(results.size() > 0)
		{
			String resultIds = EForm.getIdsAsStringList(results);
			LogAction.addLogSynchronous(loggedInInfo, "FormsManager.findInstancedByDemographicId", "ids returned=" + resultIds);
		}

		return (results);
	}
    
	public List<EncounterForm> getAllEncounterForms() {
		List<EncounterForm> results = encounterFormDao.findAll();
		Collections.sort(results, EncounterForm.FORM_NAME_COMPARATOR);
		return (results);
	}
	
	public List<EncounterForm> getSelectedEncounterForms() {
		List<EncounterForm> results = encounterFormDao.findAllNotHidden();
		Collections.sort(results, EncounterForm.FORM_NAME_COMPARATOR);
		return (results);
	}

	/**
	 * get completed encounter froms for the demographic
	 * @param demographicNo - demographic
	 * @return - list of encounter froms
	 */
	public List<EctFormData.PatientForm> getCompletedEncounterForms(String demographicNo)
	{
		return getPatientEncounterForms(demographicNo, true);
	}

	/**
	 * get all encounter from revisions for the demographic
	 * @param demographicNo - demographic
	 * @return - list of encounter froms
	 */
	public List<EctFormData.PatientForm> getEncounterFormRevisions(String demographicNo)
	{
		return getPatientEncounterForms(demographicNo, false);
	}

	/**
	 * get patient encounter froms
	 * @param demographicNo - demographic
	 * @param onlyMostRecent - if true only the most recent version will be returned
	 * @return - patient encounters
	 */
	public List<EctFormData.PatientForm> getPatientEncounterForms(String demographicNo, boolean onlyMostRecent)
	{
		List<EctFormData.PatientForm> outList = new ArrayList<>();

		List<EncounterForm> encounterForms = getAllEncounterForms();

		for (EncounterForm encounterForm : encounterForms) {
			String table = StringUtils.trimToNull(encounterForm.getFormTable());
			if (table != null) {

				EctFormData.PatientForm[] pforms = EctFormData.getPatientForms(demographicNo, table);

				for(EctFormData.PatientForm form : pforms)
				{
					form.formName = encounterForm.getFormName();
					outList.add(form);

					if (onlyMostRecent)
					{
						break;
					}
				}
			}
		}
		return outList;
	}
	
}
