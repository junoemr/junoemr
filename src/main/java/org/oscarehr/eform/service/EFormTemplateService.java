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
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.eform.dao.EFormDao;
import org.oscarehr.eform.model.EForm;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * EForm service for handling interactions with EForms.
 * These are the forms found in admin that are used as templates when adding new eforms to patients
 * See also - EForm service
 */
@Service
@Transactional
public class EFormTemplateService
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	private EFormDao eFormTemplateDao;

	@Autowired
	private UserPropertyDAO userPropertyDAO;

	public static Integer EFORM_DEFAULT_WIDTH = 700;
	public static Integer EFORM_DEFAULT_HEIGHT = 800;

	/**
	 * Save a new EForm template
	 * @param formName name of the eForm
	 * @param formSubject subject of the eForm
	 * @param fileName associated file on disc
	 * @param htmlStr html body of the eForm
	 * @param creator provider ID
	 * @param showLatestFormOnly true if only the latest version of the form gets displayed
	 * @param patientIndependent true if form is patient independent
	 * @param roleType role
	 * @return The newly saved model
	 */
	public EForm addEFormTemplate(String formName, String formSubject, String fileName, String htmlStr, String creator, boolean showLatestFormOnly, boolean patientIndependent, boolean instanced, String roleType)
	{
		EForm eFormTemplate = new EForm();
		eFormTemplate = copyToModel(eFormTemplate, formName, formSubject, fileName, htmlStr, creator, showLatestFormOnly, patientIndependent, instanced, roleType);

		eFormTemplateDao.persist(eFormTemplate);
		return eFormTemplate;
	}

	/**
	 * Update an EForm template
	 * @param id id of existing eForm template to update
	 * @param formName name of the eForm
	 * @param formSubject subject of the eForm
	 * @param fileName associated file on disc
	 * @param htmlStr html body of the eForm
	 * @param creator provider ID
	 * @param showLatestFormOnly true if only the latest version of the form gets displayed
	 * @param patientIndependent true if form is patient independent
	 * @param roleType role
	 * @return The newly updated model
	 * @throws IllegalArgumentException if id is invalid
	 */
	public EForm updateEFormTemplate(Integer id, String formName, String formSubject, String fileName, String htmlStr, String creator, boolean showLatestFormOnly, boolean patientIndependent, boolean instanced, String roleType)
	{
		EForm eFormTemplate = eFormTemplateDao.find(id);
		if(eFormTemplate == null)
		{
			throw new IllegalArgumentException("No EFormTemplate found for id " + id);
		}
		eFormTemplate = copyToModel(eFormTemplate, formName, formSubject, fileName, htmlStr, creator, showLatestFormOnly, patientIndependent, instanced, roleType);

		eFormTemplateDao.merge(eFormTemplate);
		return eFormTemplate;
	}
	private EForm copyToModel(EForm eFormTemplate, String formName, String formSubject, String fileName, String htmlStr, String creator, boolean showLatestFormOnly, boolean patientIndependent, boolean instanced, String roleType)
	{
		eFormTemplate.setFormName(formName);
		eFormTemplate.setFileName(fileName);
		eFormTemplate.setSubject(formSubject);
		eFormTemplate.setCreator(creator);
		eFormTemplate.setCurrent(true);
		eFormTemplate.setFormHtml(htmlStr);
		eFormTemplate.setShowLatestFormOnly(showLatestFormOnly);
		eFormTemplate.setPatientIndependent(patientIndependent);
		eFormTemplate.setInstanced(instanced);
		eFormTemplate.setRoleType(roleType);

		Date now = new Date();
		eFormTemplate.setFormDate(now);
		eFormTemplate.setFormTime(now);
		return eFormTemplate;
	}


	/**
	 * delete an eForm template with the given ID
	 *
	 * @param formId - id of the eform template to delete
	 * @throws IllegalArgumentException - if the id is invalid
	 */
	public EForm deleteTemplate(Integer formId)
	{
		return setTemplateDeleted(formId, true);
	}
	/**
	 * restore an eForm template with the given ID
	 * @param formId - id of the eform template to restore
	 * @throws IllegalArgumentException - if the id is invalid
	 */
	public EForm restoreTemplate(Integer formId)
	{
		return setTemplateDeleted(formId, false);
	}
	private EForm setTemplateDeleted(Integer formId, boolean isDeleted)
	{
		EForm eFormTemplate = eFormTemplateDao.find(formId);
		if (eFormTemplate == null) {
			throw new IllegalArgumentException("No EForm exists with id " + formId);
		}
		eFormTemplate.setCurrent(!isDeleted);
		eFormTemplateDao.merge(eFormTemplate);
		return eFormTemplate;
	}

	/**
	 * get the users configured eform popup width
	 * @param providerNo - the user to get the width for
	 * @return - the width. default 700.
	 */
	public Integer getEformPopupWidth(String providerNo)
	{
		try
		{
			UserProperty eformPopupWidthProp = userPropertyDAO.getProp(providerNo, UserProperty.EFORM_POPUP_WIDTH);

			if (eformPopupWidthProp != null)
			{
				return Integer.parseInt(eformPopupWidthProp.getValue());
			}
		}
		catch (NumberFormatException ne)
		{
			MiscUtils.getLogger().error("Failed to parse eform popup width with error: " + ne.getMessage() + " defaulting to " + EFORM_DEFAULT_WIDTH, ne);
		}
		return EFORM_DEFAULT_WIDTH;
	}

	/**
	 * get the users configured eform popup height
	 * @param providerNo - the user to get the height for
	 * @return - the height. default 800.
	 */
	public Integer getEformPopupHeight(String providerNo)
	{
		try
		{
			UserProperty eformPopupHeightProp = userPropertyDAO.getProp(providerNo, UserProperty.EFORM_POPUP_HEIGHT);

			if (eformPopupHeightProp != null)
			{
				return Integer.parseInt(eformPopupHeightProp.getValue());
			}
		}
		catch (NumberFormatException ne)
		{
			MiscUtils.getLogger().error("Failed to parse eform popup height with error: " + ne.getMessage() + " defaulting to " + EFORM_DEFAULT_HEIGHT, ne);
		}
		return EFORM_DEFAULT_HEIGHT;
	}
}
