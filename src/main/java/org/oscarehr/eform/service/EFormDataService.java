/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.eform.service;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessages;
import org.oscarehr.common.exception.HtmlToPdfConversionException;
import org.oscarehr.eform.dao.EFormDao;
import org.oscarehr.eform.dao.EFormDataDao;
import org.oscarehr.eform.dao.EFormInstanceDao;
import org.oscarehr.eform.dao.EFormValueDao;
import org.oscarehr.eform.exception.EFormMeasurementException;
import org.oscarehr.eform.model.EForm;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.eform.model.EFormInstance;
import org.oscarehr.eform.model.EFormValue;
import org.oscarehr.eform.transfer.InstancedEFormListTransfer;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.util.ConversionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

	@Autowired
	private EFormDatabaseTagService databaseTagService;

	public EFormData saveExistingEForm(Integer oldFormDataId, Integer demographicNo, Integer providerNo, String subject,
	                                   Map<String,String> formOpenerMap, Map<String,String> eFormValueMap, String eformLink, Date formDate)
	{
		logger.info("Save Existing EForm (Previous eform_data fdid " + oldFormDataId + ")");
		EFormData oldVersion = eFormDataDao.find(oldFormDataId);
		if(oldVersion == null)
		{
			throw new IllegalArgumentException("No FormData found for fdid " + oldFormDataId);
		}
		EFormData newVersion = copyFromEFormData(oldVersion);

		return saveEForm(newVersion, demographicNo, providerNo, subject, formOpenerMap, eFormValueMap, eformLink, formDate);
	}

	public EFormData saveExistingEForm(Integer oldFormDataId, Integer demographicNo, Integer providerNo, String subject,
	                                   Map<String,String> formOpenerMap, Map<String,String> eFormValueMap, String eformLink)
	{
		return saveExistingEForm(oldFormDataId, demographicNo, providerNo, subject, formOpenerMap, eFormValueMap, eformLink, null);
	}

	/**
	 * Save a new EForm record to the database, with a custom creation date
	 */
	public EFormData saveNewEForm(Integer templateId, Integer demographicNo, Integer providerNo, String subject,
	                              Map<String,String> formOpenerMap, Map<String,String> eFormValueMap, String eformLink, Date formDate)
	{
		logger.info("Save New EForm (template id " + templateId + ")");
		EForm template = getEFormTemplate(templateId);
		EFormData newVersion = copyFromTemplate(template);
		// must have a persisted instance in order to save the id
		newVersion.setEFormInstance(getNewPersistedEFormInstance(template));

		return saveEForm(newVersion, demographicNo, providerNo, subject, formOpenerMap, eFormValueMap, eformLink, formDate);
	}

	/**
	 * Save a new EForm record to the database
	 */
	public EFormData saveNewEForm(Integer templateId, Integer demographicNo, Integer providerNo, String subject,
	                              Map<String,String> formOpenerMap, Map<String,String> eFormValueMap, String eformLink)
	{
		return saveNewEForm(templateId, demographicNo, providerNo, subject,formOpenerMap, eFormValueMap, eformLink, null);
	}

	/**
	 * Same as saveNewEForm but it also loads all databaseAP tags before saving.
	 * This should only be used in cases where a new eform is loaded without having the usual ui initialization steps (such as the API)
	 */
	public EFormData saveNewEFormWithDatabaseTags(Integer templateId, Integer demographicNo, Integer providerNo, String subject,
	                                              Map<String,String> formOpenerMap, Map<String,String> eFormValueMap, String eformLink)
	{
		logger.info("Save New EForm (template id " + templateId + ") - Include AP values");

		// in order to populate all oscar database tag fields, load them into a map, and add the values to the provided
		EForm template = getEFormTemplate(templateId);
		EFormData newVersion = copyFromTemplate(template);
		// must have a persisted instance in order to save the id
		newVersion.setEFormInstance(getNewPersistedEFormInstance(template));

		// hash maps use the latest value added when there are duplicate keys
		// specified keys in the incoming map should have priority over AP tag values
		Map<String, String> combinedValueMap = databaseTagService.getDatabaseTagNameValueMap(template.getFormHtml(), demographicNo, providerNo);
		combinedValueMap.putAll(eFormValueMap);

		return saveEForm(newVersion, demographicNo, providerNo, subject, formOpenerMap, combinedValueMap, eformLink, null);
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

		EFormInstance instance = eForm.getEFormInstance();
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
	 * print an eform to pdf
	 * @param loggedInInfo - the logged in user info
	 * @param contextPath - the current context path
	 * @param fdid - the fdid of the eform to print
	 * @return byte - binary pdf data
	 */
	public byte[] printEForm(LoggedInInfo loggedInInfo, String contextPath, Integer fdid) throws IOException, HtmlToPdfConversionException
	{
		String printUrl = WKHtmlToPdfUtils.getEformRequestUrl(loggedInInfo.getLoggedInProviderNo(), fdid.toString(), "http", contextPath);
		return WKHtmlToPdfUtils.convertToPdf(printUrl);
	}

	/**
	 * see printEForm
	 */
	public byte[] printEForm(LoggedInInfo loggedInInfo, String contextPath, EFormData eFormData) throws IOException, HtmlToPdfConversionException
	{
		return printEForm(loggedInInfo, contextPath, eFormData.getId());
	}

	/**
	 * Checks that this eForm is the latest version of an instanced eForm.
	 * @param eForm - the eForm model to check
	 * @return - true if the eForm is the latest instance version, or true if the eForm is not instanced. false otherwise
	 */
	public boolean isLatestInstancedVersion(EFormData eForm)
	{
		// this needs to be within a transaction to load the instance object correctly
		EFormInstance instance = eForm.getEFormInstance();
		return (instance == null || (eForm.getId().equals(instance.getCurrentEFormData().getId())));
	}
	/**
	 * Checks that this eForm is the latest version of an instanced eForm.
	 * @param eFormId - the eForm id to check
	 * @return - true if the eForm is the latest instance version, or true if the eForm is not instanced. false otherwise
	 */
	public boolean isLatestInstancedVersion(Integer eFormId)
	{
		return isLatestInstancedVersion(eFormDataDao.find(eFormId));
	}

	public List<InstancedEFormListTransfer> getInstancedEformsForDemographic(Integer demographicId, Integer offset, Integer limit)
	{
		List<EFormData> eFormDataList = eFormDataDao.findInstancedVersionsByDemographicId(demographicId, offset, limit, false);
		List<InstancedEFormListTransfer> transferList = new ArrayList<>(eFormDataList.size());

		//Convert to transfer objects
		for(EFormData eForm : eFormDataList)
		{
			InstancedEFormListTransfer transfer = new InstancedEFormListTransfer();
			transfer.setDemographicId(eForm.getDemographicId());
			transfer.setFormDataId(eForm.getId());
			transfer.setFormName(eForm.getFormName());
			transfer.setFormSubject(eForm.getSubject());
			Date legacyDateTime = ConversionUtils.combineDateAndTime(eForm.getFormDate(), eForm.getFormTime());
			transfer.setFormDateTime(ConversionUtils.toLocalDateTime(legacyDateTime));
			transfer.setInstanceCreationDateTime(ConversionUtils.toLocalDateTime(eForm.getEFormInstance().getCreatedAt()));

			transferList.add(transfer);
		}
		return transferList;
	}

	/**
	 * Check an eForm and its param value map it was instantiated from for any measurements that may not have a mapping.
	 * @param eFormData eForm object to use to check validations against
	 * @param eFormValueMap a map containing eFormFields, eFormValues pairings
	 * @return a list of any eFormFields that we could not find a mapping for
	 */
	public List<String> checkUnmappedMeasurements(EFormData eFormData, Map<String,String> eFormValueMap)
	{
		oscar.eform.data.EForm curForm = new oscar.eform.data.EForm(eFormData);
		ArrayList<String> eFormFields = new ArrayList<>(eFormValueMap.keySet());
		ArrayList<String> eFormValues = new ArrayList<>(eFormValueMap.values());

		return curForm.checkMeasurements(eFormFields, eFormValues);
	}

	/**
	 * Handle all of the major eForm creation logic. save an eForm data model for a demographic.
	 */
	private EFormData saveEForm(EFormData eForm, Integer demographicNo, Integer providerNo, String subject, Map<String,String> formOpenerMap,
	                            Map<String,String> eFormValueMap, String eformLink, Date formDate)
	{
		if(formDate == null)
		{
			formDate = new Date();
		}
		eForm.setFormDate(formDate);
		eForm.setFormTime(formDate);
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
		eFormDataDao.persist(eForm);

		// now that the eForm data is persisted, update the id in the instance table;
		EFormInstance eFormInstance = eForm.getEFormInstance();
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
	 * create a new eForm instance model. new models get persisted to ensure the id exists
	 * @return the persisted model object, or null if the template is not flagged as an instanced eForm.
	 */
	private EFormInstance getNewPersistedEFormInstance(EForm eFormTemplate)
	{
		EFormInstance eFormInstance = null;
		if(eFormTemplate.isInstanced())
		{
			eFormInstance = new EFormInstance();
			eFormInstance.setCreatedAt(new Date());
			eFormInstance.setEFormTemplate(eFormTemplate);
			eFormInstanceDao.persist(eFormInstance);
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
