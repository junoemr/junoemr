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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ctl_doctype")
public class CtlDocType extends AbstractModel<Integer>{

	public static final String MODULE_DEMOGRAPHIC = "demographic";
	public static final String MODULE_PROVIDER = "provider";

	public enum Status
	{
		Active ("A"),
		Inactive ("I");

		private String shortName;

		private Status(String shortName)
		{
			this.shortName = shortName;
		}

		@Override
		public String toString()
		{
			return this.shortName;
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String module;

	@Column(name="doctype")
	private String docType;

	private String status;

	public Integer getId() {
    	return id;
    }

	public void setId(Integer id) {
    	this.id = id;
    }

	public String getModule() {
    	return module;
    }

	public void setModule(String module) {
    	this.module = module;
    }

	public String getDocType() {
    	return docType;
    }

	public void setDocType(String docType) {
    	this.docType = docType;
    }

	public String getStatus() {
    	return status;
    }

	public boolean isActive()
	{
		return this.getStatus().equals(Status.Active.toString());
	}

	public void setStatus(String status) {
    	this.status = status;
    }

}
