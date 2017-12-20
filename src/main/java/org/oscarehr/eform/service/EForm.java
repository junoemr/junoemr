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
import org.oscarehr.eform.dao.EFormValueDao;
import org.oscarehr.eform.model.EFormData;
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
public class EForm
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	private EFormDataDao eFormDataDao;

	@Autowired
	private EFormValueDao eFormValueDao;

	@Autowired
	private EFormDao eFormTemplateDao;

	public EFormData saveExistingEForm(Integer oldFormDataId, Integer demographicNo, Integer providerNo, Integer formId, String subject, Map<String,String> formOpenerMap, Map<String,String> eFormValueMap, String eformLink)
	{
		logger.info("Save Existing EForm (template id " + formId + ")");
		EFormData oldVersion = eFormDataDao.find(oldFormDataId);
		if(oldVersion == null)
		{
			throw new IllegalArgumentException("No FormData found for fdid " + oldFormDataId);
		}
		EFormData newVersion = copyFromTemplate(formId);

		// this could be expensive for larger eforms
		boolean sameForm = oldVersion.getFormData().equals(newVersion.getFormData());

		if(!sameForm)
		{
			logger.info("EForm html does not match, save a new copy");
			return saveEForm(newVersion, demographicNo, providerNo, subject, formOpenerMap, eFormValueMap, eformLink);
		}
		return null;
	}
	public EFormData saveNewEForm(Integer demographicNo, Integer providerNo, Integer formId, String subject, Map<String,String> formOpenerMap, Map<String,String> eFormValueMap, String eformLink)
	{
		logger.info("Save New EForm (template id " + formId + ")");
		EFormData newVersion = copyFromTemplate(formId);
		return saveEForm(newVersion, demographicNo, providerNo, subject, formOpenerMap, eFormValueMap, eformLink);
	}

	private EFormData saveEForm(EFormData eForm, Integer demographicNo, Integer providerNo, String subject, Map<String,String> formOpenerMap, Map<String,String> eFormValueMap, String eformLink)
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

		ActionMessages errors = curForm.setMeasurements(eFormFields, eFormValues);
		if(!errors.isEmpty())
		{
			throw new RuntimeException("Errors Saving measurements");
		}

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

		// must update the html after running the image/action etc. changes on curForm
		eForm.setFormData(curForm.getFormHtml());

		eFormDataDao.persist(eForm);
		saveEformValues(eForm.getFormId(), eForm.getId(), eForm.getDemographicId(), eFormValueMap);
		logger.info("EForm data saved with id " + eForm.getId());
		return eForm;
	}

	private void saveEformValues(Integer formId, Integer formDataId, Integer demographicNo, Map<String,String> formValueMap)
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
	private EFormData copyFromTemplate(Integer templateId)
	{
		org.oscarehr.eform.model.EForm template = eFormTemplateDao.find(templateId);
		if(template == null)
		{
			throw new IllegalArgumentException("No EForm Template found for fid " + templateId);
		}
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
}
