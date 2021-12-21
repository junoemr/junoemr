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


package org.oscarehr.contact.transfer;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DemographicContactTo1 implements Serializable
{
	//link to the provider table
	public static final int TYPE_PROVIDER = 0;
	//link to the demographic table
	public static final int TYPE_DEMOGRAPHIC = 1;
	//link to the contact table
	public static final int TYPE_CONTACT = 2;
	//link to the professional specialists table
	public static final int TYPE_PROFESSIONALSPECIALIST = 3;

	public static final String CATEGORY_PERSONAL = "personal";
	public static final String CATEGORY_PROFESSIONAL = "professional";


	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date created;
	private Date updateDate;
	private boolean deleted;
	private int demographicNo;
	private String contactId;
	private String role;
	private int type;
	private String category;
	private String sdm;
	private String ec;
	private String note;

	private int facilityId;
	private String creator;

	private Boolean consentToContact = true;
	private Boolean active = true;
	
	private String contactName;
}
