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
package org.oscarehr.ws.validator;

import org.oscarehr.eform.dao.EFormDao;
import org.oscarehr.eform.model.EForm;
import org.oscarehr.util.SpringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Custom validator to ensure provider numbers match existing providers
 */
public class EFormTemplateIdValidator implements ConstraintValidator<EFormTemplateIdConstraint, Integer>
{
	private EFormDao eFormTemplateDao = SpringUtils.getBean(EFormDao.class);
	private boolean allowNull;

	@Override
	public void initialize(EFormTemplateIdConstraint eFormTemplateIdConstraint)
	{
		allowNull = eFormTemplateIdConstraint.allowNull();
	}

	@Override
	public boolean isValid(Integer templateId, ConstraintValidatorContext constraintValidatorContext)
	{
		if(templateId == null)
		{
			return allowNull;
		}

		EForm eform = eFormTemplateDao.find(templateId);
		return (eform != null);
	}
}
