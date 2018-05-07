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

package org.oscarehr.eform.service;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessages;
import org.oscarehr.eform.dao.EFormDao;
import org.oscarehr.eform.dao.EFormDataDao;
import org.oscarehr.eform.dao.EFormInstanceDao;
import org.oscarehr.eform.dao.EFormValueDao;
import org.oscarehr.eform.exception.EFormMeasurementException;
import org.oscarehr.eform.model.EForm;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.eform.model.EFormInstance;
import org.oscarehr.eform.model.EFormValue;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * EForm service for handling interactions with patient EForms.
 * These are the forms attached to patients which contain patient data
 * See also - EFormTemplate service
 */
@Service
@Transactional
public class EFormDataService
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	private EFormDataDao eFormDataDao;

	@Autowired
	private EFormInstanceDao eFormInstanceDao;

	@Autowired
	private EFormValueDao eFormValueDao;

	@Autowired
	private EFormDao eFormTemplateDao;

	public EFormData saveExistingEForm(Integer oldFormDataId, Integer demographicNo, Integer providerNo, String subject, Map<String,String> formOpenerMap, Map<String,String> eFormValueMap, String eformLink)
	{
		logger.info("Save Existing EForm (Previous eform_data fdid " + oldFormDataId + ")");
		EFormData oldVersion = eFormDataDao.find(oldFormDataId);
		if(oldVersion == null)
		{
			throw new IllegalArgumentException("No FormData found for fdid " + oldFormDataId);
		}
		EForm template = getEFormTemplate(oldVersion.getFormId());
		EFormData newVersion = copyFromEFormData(oldVersion);

		return saveEForm(newVersion, template, demographicNo, providerNo, subject, formOpenerMap, eFormValueMap, eformLink);
	}
	public EFormData saveNewEForm(Integer templateId, Integer demographicNo, Integer providerNo, String subject, Map<String,String> formOpenerMap, Map<String,String> eFormValueMap, String eformLink)
	{
		logger.info("Save New EForm (template id " + templateId + ")");
		EForm template = getEFormTemplate(templateId);
		EFormData newVersion = copyFromTemplate(template);
		return saveEForm(newVersion, template, demographicNo, providerNo, subject, formOpenerMap, eFormValueMap, eformLink);
	}

	/**
	 * flag an eForm as deleted
	 * @param formDataId the eForm id to flag as deleted
	 */
	public void deleteEForm(Integer formDataId)
	{
		flagAsDeleted(formDataId, true);
	}

	/**
	 * un flag an eForm as deleted
	 * @param formDataId the eForm id to flag as not deleted
	 */
	public void restoreEForm(Integer formDataId)
	{
		flagAsDeleted(formDataId, false);
	}

	private void flagAsDeleted(Integer formDataId, boolean deleted)
	{
		EFormData eForm = eFormDataDao.find(formDataId);
		EForm template = getEFormTemplate(eForm.getFormId());

		EFormInstance instance = getPersistedEFormInstance(template, eForm);
		// instanced eForms use the instanced table deleted flag. regular eForms use the "status" isCurrent flag
		if(instance != null)
		{
			instance.setDeleted(deleted);
			eFormInstanceDao.merge(instance);
		}
		else
		{
			eForm.setCurrent(!deleted);
			eFormDataDao.merge(eForm);
		}
	}

	/**
	 * Handle all of the major eForm creation logic. save an eForm data model for a demographic.
	 */
	private EFormData saveEForm(EFormData eForm, EForm template, Integer demographicNo, Integer providerNo, String subject, Map<String,String> formOpenerMap, Map<String,String> eFormValueMap, String eformLink)
	{
		Date currentDate = new Date();
		eForm.setFormDate(currentDate);
		eForm.setFormTime(currentDate);
		eForm.setProviderNo(String.valueOf(providerNo));
		eForm.setDemographicId(demographicNo);
		eForm.setSubject(subject);

		oscar.eform.data.EForm curForm = new oscar.eform.data.EForm(eForm);

		ArrayList<String> openerNames = new ArrayList<>(formOpenerMap.keySet());
		ArrayList<String> openerValues = new ArrayList<>(formOpenerMap.values());
		ArrayList<String> eFormFields = new ArrayList<>(eFormValueMap.keySet());
		ArrayList<String> eFormValues = new ArrayList<>(eFormValueMap.values());

		// attempt to save measurements first. This is contained in the transaction
		ActionMessages measurementErrors = curForm.setMeasurements(eFormFields, eFormValues);

		curForm.setFormSubject(subject);
		curForm.setValues(eFormFields, eFormValues);
		if(!openerNames.isEmpty())
		{
			curForm.setOpenerValues(openerNames, openerValues);
		}
		if(eformLink != null)
		{
			curForm.setEformLink(eformLink);
		}
		curForm.setImagePath();
		curForm.setAction();
		curForm.setNowDateTime();

		// because of how the curForm gets populated, and because the values are used on error,
		// this has to be done after some of the curForm operations are performed.
		if(!measurementErrors.isEmpty())
		{
			throw new EFormMeasurementException("Errors Saving measurements", measurementErrors, curForm);
		}

		// must update the html after running the image/action etc. changes on curForm
		eForm.setFormData(curForm.getFormHtml());

		// must have a persisted instance in order to save the id
		EFormInstance eFormInstance = getPersistedEFormInstance(template, eForm);
		eForm.setEFormInstance(eFormInstance);

		eFormDataDao.persist(eForm);

		// now that the eForm data is persisted, update the id in the instance table;
		if(eFormInstance != null)
		{
			eFormInstance.setCurrentEFormData(eForm);
			eFormInstanceDao.merge(eFormInstance);
		}

		// save the eForm values
		saveEFormValues(eForm.getFormId(), eForm.getId(), eForm.getDemographicId(), eFormValueMap);

		logger.info("EForm data saved with id " + eForm.getId());
		return eForm;
	}

	/**
	 * save the eForm values from the value map
	 */
	private void saveEFormValues(Integer formId, Integer formDataId, Integer demographicNo, Map<String,String> formValueMap)
	{
		for(Map.Entry<String, String> entry : formValueMap.entrySet())
		{
			EFormValue eFormValue = new EFormValue();
			eFormValue.setFormId(formId);
			eFormValue.setFormDataId(formDataId);
			eFormValue.setDemographicId(demographicNo);
			eFormValue.setVarName(entry.getKey());
			eFormValue.setVarValue(entry.getValue());

			eFormValueDao.persist(eFormValue);
		}
	}

	/**
	 * get an existing eForm instance model, or create a new one.
	 * new models get persisted to ensure the id exists
	 * @return the persisted model object, or null if the template is not flagged as an instanced eForm.
	 */
	private EFormInstance getPersistedEFormInstance(EForm eFormTemplate, EFormData eForm)
	{
		EFormInstance eFormInstance = null;
		if(eFormTemplate.isInstanced())
		{
			eFormInstance = eForm.getEFormInstance();
			if(eFormInstance == null)
			{
				eFormInstance = new EFormInstance();
				eFormInstance.setCreatedAt(new Date());
				eFormInstance.setEFormTemplate(eFormTemplate);
				eFormInstanceDao.persist(eFormInstance);
			}
		}
		return eFormInstance;
	}

	/**
	 * load the eForm template from the primary key
	 * @param templateId - eForm primary key
	 * @throws IllegalArgumentException if the entity is not found
	 */
	private EForm getEFormTemplate(Integer templateId)
	{
		EForm template = eFormTemplateDao.find(templateId);
		if(template == null)
		{
			throw new IllegalArgumentException("No EForm Template found for fid " + templateId);
		}
		return template;
	}

	/**
	 * copy stuff from the template to the data model
	 */
	private EFormData copyFromTemplate(EForm template)
	{
		EFormData eFormCopy = new EFormData();
		eFormCopy.setFormId(template.getId());
		eFormCopy.setFormName(template.getFormName());
		eFormCopy.setFormDate(template.getFormDate());
		eFormCopy.setFormTime(template.getFormTime());
		eFormCopy.setRoleType(template.getRoleType());
		eFormCopy.setFormData(template.getFormHtml());
		eFormCopy.setCurrent(template.isCurrent());
		eFormCopy.setSubject(template.getSubject());
		eFormCopy.setShowLatestFormOnly(template.isShowLatestFormOnly());
		eFormCopy.setPatientIndependent(template.isPatientIndependent());

		return eFormCopy;
	}
	/**
	 * copy stuff from the old eForm to a new model
	 */
	private EFormData copyFromEFormData(EFormData eFormData)
	{
		EFormData eFormCopy = new EFormData();
		eFormCopy.setFormId(eFormData.getFormId());
		eFormCopy.setFormName(eFormData.getFormName());
		eFormCopy.setFormDate(eFormData.getFormDate());
		eFormCopy.setFormTime(eFormData.getFormTime());
		eFormCopy.setRoleType(eFormData.getRoleType());
		eFormCopy.setFormData(eFormData.getFormData());
		eFormCopy.setCurrent(eFormData.isCurrent());
		eFormCopy.setSubject(eFormData.getSubject());
		eFormCopy.setShowLatestFormOnly(eFormData.isShowLatestFormOnly());
		eFormCopy.setPatientIndependent(eFormData.isPatientIndependent());
		eFormCopy.setEFormInstance(eFormData.getEFormInstance());

		return eFormCopy;
	}
}
