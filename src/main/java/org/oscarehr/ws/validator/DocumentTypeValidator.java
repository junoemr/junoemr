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

import org.oscarehr.common.dao.CtlDocTypeDao;
import org.oscarehr.common.model.CtlDocType;
import org.oscarehr.util.SpringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Custom validator to ensure provider numbers match existing providers
 */
public class DocumentTypeValidator implements ConstraintValidator<DocumentTypeConstraint, String>
{
	private CtlDocTypeDao ctlDocTypeDao = SpringUtils.getBean(CtlDocTypeDao.class);
	private boolean allowNull;
	private String module;

	@Override
	public void initialize(DocumentTypeConstraint documentTypeConstraint)
	{
		allowNull = documentTypeConstraint.allowNull();
		module = documentTypeConstraint.module();
	}

	@Override
	public boolean isValid(String documentType, ConstraintValidatorContext constraintValidatorContext)
	{
		if(documentType == null)
		{
			return allowNull;
		}
		List<CtlDocType> docTypes = ctlDocTypeDao.findByModule(module);
		for(CtlDocType docType : docTypes)
		{
			if(documentType.equals(docType.getDocType()))
			{
				return true;
			}
		}
		return false;
	}
}
