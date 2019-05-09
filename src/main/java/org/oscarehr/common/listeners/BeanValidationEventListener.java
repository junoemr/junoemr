/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.common.listeners;

import org.oscarehr.util.MiscUtils;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

public class BeanValidationEventListener
{
	@PrePersist
	@PreUpdate
	@PreRemove
	public void validate(Object entity)
	{
		Validator validator = Validation.buildDefaultValidatorFactory().usingContext().getValidator();

		final Set<ConstraintViolation<Object>> constraintViolations = validator.validate(entity);
		if (constraintViolations.size() > 0)
		{
			Set<ConstraintViolation<ConstraintViolation>> propagatedViolations = new HashSet<>(constraintViolations.size());
			Set<String> classNames = new HashSet<>();
			for (ConstraintViolation violation : constraintViolations)
			{
				propagatedViolations.add(violation);
				classNames.add(violation.getLeafBean().getClass().getName());
				MiscUtils.getLogger().error("CONSTRAINT VIOLATION: " + violation.getMessage());
			}
			StringBuilder builder = new StringBuilder();
			builder.append("validation failed for classes ");
			builder.append(classNames);
			throw new ConstraintViolationException(builder.toString(), propagatedViolations);
		}
	}
}
