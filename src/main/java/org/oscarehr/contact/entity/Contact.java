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


package org.oscarehr.contact.entity;

import lombok.Data;
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type")
public class Contact extends AbstractModel<Integer>
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;	
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;
	private String lastName;
	private String firstName;
	private String middleName;
	private String address;
	private String address2;
	private String city;
	private String province;
	private String country;
	private String postal;
	private String residencePhone;
	private String residencePhoneExtension;
	private String cellPhone;
	private String cellPhoneExtension;
	private String workPhone;
	private String workPhoneExtension;
	private String email;
	private String fax;
	private String note;
	private String specialty;
	private String cpso;
	private String systemId;
	boolean deleted=false;


	@PreRemove
	protected void jpa_preventDelete() {
		throw (new UnsupportedOperationException("Remove is not allowed for this type of item."));
	}

	@PrePersist
	@PreUpdate
	protected void jpa_updateTimestamp() {
		this.setUpdateDate(new Date());
	}

	@Override
	public String toString() {
		return "Contact - id:"+getId();
	}
	
	public String getFormattedName() {
		return getLastName() + "," + getFirstName();
	}
}
