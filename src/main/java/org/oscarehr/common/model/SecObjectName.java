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


package org.oscarehr.common.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="secObjectName")
public class SecObjectName extends AbstractModel<String> {

	public static final String _ADMIN = "_admin";
	public static final String _APPOINTMENT = "_appointment";
	public static final String _DEMOGRAPHIC = "_demographic";
	public static final String _TICKLER = "_tickler";

	@Id
	@Column(name="objectName")
	private String id;
	private String description;
	@Column(name="orgapplicable")
	private Boolean orgApplicable;

	public String getId() {
    	return id;
    }
	public void setId(String id) {
    	this.id = id;
    }
	public String getDescription() {
    	return description;
    }
	public void setDescription(String description) {
    	this.description = description;
    }
	public Boolean isOrgApplicable() {
    	return orgApplicable;
    }
	public void setOrgApplicable(Boolean orgApplicable) {
    	this.orgApplicable = orgApplicable;
    }


}
