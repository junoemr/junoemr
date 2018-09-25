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
package org.oscarehr.document.model;

import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.common.model.CtlDocumentPK;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="ctl_document")
public class CtlDocument extends AbstractModel<CtlDocumentPK>
{
	
	public static final String MODULE_DEMOGRAPHIC = "demographic";
	public static final String MODULE_PROVIDER = "provider";

	@EmbeddedId
	private CtlDocumentPK id;
	
	@Column(nullable=true)
	private String status;
	
	public CtlDocument() {
		id = new CtlDocumentPK();
	}

	public CtlDocumentPK getId() {
		return id;
	}

	public void setId(CtlDocumentPK id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isDemographicDocument()
	{
		return (id.getModule() != null && id.getModule().equals(MODULE_DEMOGRAPHIC));
	}
}
