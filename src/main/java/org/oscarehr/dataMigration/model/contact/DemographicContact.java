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
package org.oscarehr.dataMigration.model.contact;

import lombok.Data;
import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.dataMigration.model.provider.Provider;

import java.time.LocalDateTime;

import static org.oscarehr.contact.entity.DemographicContact.CATEGORY_PERSONAL;
import static org.oscarehr.contact.entity.DemographicContact.CATEGORY_PROFESSIONAL;


@Data
public class DemographicContact extends AbstractTransientModel
{
	public enum CATEGORY
	{
		PERSONAL(CATEGORY_PERSONAL),
		PROFESSIONAL(CATEGORY_PROFESSIONAL);

		private final String value;

		CATEGORY(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}

		public static CATEGORY fromStringIgnoreCase(String enumString)
		{
			if(EnumUtils.isValidEnumIgnoreCase(CATEGORY.class, enumString))
			{
				return CATEGORY.valueOf(enumString.toUpperCase());
			}
			return null;
		}
	}

	private Contact contact;
	private String role;
	private String note;
	private CATEGORY category;
	private boolean consentToContact;
	private boolean emergencyContact;
	private boolean substituteDecisionMaker;

	private LocalDateTime updateDateTime;
	private boolean deleted;
	private Provider createdBy;
	private LocalDateTime createdAt;


	public DemographicContact(Contact contact)
	{
		this.contact = contact;
	}

	public void setCategoryPersonal()
	{
		this.setCategory(CATEGORY.PERSONAL);
	}

	public void setCategoryProfessional()
	{
		this.setCategory(CATEGORY.PROFESSIONAL);
	}

	public boolean isCategoryPersonal()
	{
		return CATEGORY.PERSONAL.equals(this.category);
	}

	public boolean isCategoryProfessional()
	{
		return CATEGORY.PROFESSIONAL.equals(this.category);
	}
}
