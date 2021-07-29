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

package org.oscarehr.ws.rest.conversion;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.common.model.Contact;
import org.oscarehr.ws.rest.to.model.DemographicContactFewTo1;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class DemographicContactFewToContactDomainConverter extends AbstractModelConverter<DemographicContactFewTo1, Contact>
{

    @Override
    public Contact convert(DemographicContactFewTo1 transfer)
    {
        Contact contact = new Contact();
        String[] ignoreProperties = {
				"role",
				"category",
				"contactId"
		};

        if (transfer == null)
        {
            return null;
        }

        BeanUtils.copyProperties(transfer, contact, ignoreProperties);
		return contact;
    }
}
