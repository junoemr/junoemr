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


package org.oscarehr.ws.rest.to.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DemographicContactFewTo1 implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String role;
	private String sdm;
	private String ec;
	private String category;
	private String contactId;
	private int type;
	private String lastName;
	private String firstName;

	private String address;
	private String address2;
	private String city;
	private String postal;
	private String province;

	private String homePhone;
	private String cellPhone;
	private String workPhone;
	private String hPhoneExt;
	private String wPhoneExt;
	private String fax;
	private String email;
	private String note;
}
