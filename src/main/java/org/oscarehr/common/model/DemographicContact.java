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

import com.google.common.collect.Sets;

import java.util.Date;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
public class DemographicContact extends AbstractModel<Integer> {

	//link to the provider table
	public static final int TYPE_PROVIDER = 0;
	public static final String TYPE_PROVIDER_TEXT = "Provider";
	//link to the demographic table
	public static final int TYPE_DEMOGRAPHIC = 1;
	public static final String TYPE_DEMOGRAPHIC_TEXT = "Demographic";
	//link to the contact table
	public static final int TYPE_CONTACT = 2;
	public static final String TYPE_CONTACT_TEXT = "Contact";
	//link to the professional specialists table
	public static final int TYPE_PROFESSIONALSPECIALIST = 3;
	public static final String TYPE_PROFESSIONAL_SPECIALIST_TEXT = "Professional Specialist";

	public static final String CATEGORY_PERSONAL = "personal";
	public static final String CATEGORY_PROFESSIONAL = "professional";

	public static final HashSet<String> ALL_CATEGORIES = Sets.newHashSet(CATEGORY_PERSONAL, CATEGORY_PROFESSIONAL);


	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	@Temporal(TemporalType.TIMESTAMP)
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

	private Integer facilityId;
	private String creator;

	private Boolean consentToContact = true;
	private Boolean active = true;
	
	@Transient
	private String contactName;
	@Transient
	private Contact details;

	@Override
	public Integer getId() {
		return this.id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public int getDemographicNo() {
		return demographicNo;
	}

	public void setDemographicNo(int demographicNo) {
		this.demographicNo = demographicNo;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}


	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}


	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCategory() {
    	return category;
    }

	public void setCategory(String category) {
    	this.category = category;
    }

	public String getContactName() {
    	return contactName;
    }

	public void setContactName(String contactName) {
    	this.contactName = contactName;
    }

	public String getSdm() {
    	return sdm;
    }

	public void setSdm(String sdm) {
    	this.sdm = sdm;
    }

	public String getEc() {
    	return ec;
    }

	public void setEc(String ec) {
    	this.ec = ec;
    }

	public String getNote() {
    	return note;
    }

	public void setNote(String note) {
    	this.note = note;
    }

	public int getFacilityId() {
    	return facilityId;
    }

	public void setFacilityId(int facilityId) {
    	this.facilityId = facilityId;
    }

	public String getCreator() {
    	return creator;
    }

	public void setCreator(String creator) {
    	this.creator = creator;
    }

	@PreRemove
	protected void jpa_preventDelete() {
		throw (new UnsupportedOperationException("Remove is not allowed for this type of item."));
	}

	@PrePersist
	@PreUpdate
	protected void jpa_updateTimestamp() {
		this.setUpdateDate(new Date());
	}

	public boolean isConsentToContact() {
		return consentToContact;
	}

	public void setConsentToContact(boolean consentToContact) {
		this.consentToContact = consentToContact;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Contact getDetails() {
	    return details;
    }

	public void setDetails(Contact details) {
	    this.details = details;
    }
}
