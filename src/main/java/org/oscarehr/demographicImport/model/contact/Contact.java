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
package org.oscarehr.demographicImport.model.contact;

import org.oscarehr.demographicImport.model.common.Address;
import org.oscarehr.demographicImport.model.common.PhoneNumber;

public interface Contact
{
	String getFirstName();
	void setFirstName(String firstName);

	String getLastName();
	void setLastName(String lastName);

	Address getAddress();
	void setAddress(Address address);

	PhoneNumber getHomePhone();
	void setHomePhone(PhoneNumber phone);

	PhoneNumber getWorkPhone();
	void setWorkPhone(PhoneNumber phone);

	PhoneNumber getCellPhone();
	void setCellPhone(PhoneNumber phone);

	String getEmail();
	void setEmail(String email);
}
