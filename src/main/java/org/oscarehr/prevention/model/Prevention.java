/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.prevention.model;

import org.oscarehr.common.model.AbstractModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "preventions")
public class Prevention extends AbstractModel<Integer> implements Serializable
{
	// The names are weird, these are all the possible values for the "refused" column
	public static final char REFUSED_STATUS_COMPLETED = '0';
	public static final char REFUSED_STATUS_REFUSED = '1';
	public static final char REFUSED_STATUS_INELIGIBLE = '2';

	public static final char IS_ACTIVE = '0';
	public static final char IS_DELETED = '1';

	// for the 'never' column, seems to refer to whether to send a reminder to patient
	public static final char SEND_REMINDER = '0';
	public static final char NEVER_SEND_REMINDER = '1';

	public static final String REFUSED = "refused";
	public static final String NEVER = "never";
	public static final String INELIGIBLE = "ineligible";
	public static final String PREVIOUS = "previous";
	public static final String NEVER_REMIND = "neverRemind";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id = null;

	@Column(name = "demographic_no")
	private Integer demographicId = null;

	@Column(name = "creation_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate = new Date();

	@Column(name = "prevention_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date preventionDate = null;

	@Column(name = "provider_no")
	private String providerNo = null;

	@Column(name = "provider_name")
	private String providerName = null;

	@Column(name = "prevention_type")
	private String preventionType = null;

	@Column(name = "deleted")
	private char deleted = IS_ACTIVE;

	@Column(name = "refused")
	private char refused = REFUSED_STATUS_COMPLETED;

	@Column(name = "never")
	private char never = SEND_REMINDER;

	@Column(name = "next_date")
	@Temporal(TemporalType.DATE)
	private Date nextDate = null;

	@Column(name = "creator")
	private String creatorProviderNo = null;

	@Column(name = "lastUpdateDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateDate = null;

	// with cascade, these entities will be persisted when this class is.
	@OneToMany(fetch=FetchType.LAZY, mappedBy = "prevention", cascade = CascadeType.PERSIST)
	private List<PreventionExt> preventionExtensionList;

	public Integer getDemographicId() {
		return demographicId;
	}

	public void setDemographicId(Integer demographicId) {
		this.demographicId = demographicId;
	}

	public Date getPreventionDate() {
		return preventionDate;
	}

	public void setPreventionDate(Date preventionDate) {
		this.preventionDate = preventionDate;
	}

	public String getProviderNo() {
		return providerNo;
	}

	public void setProviderNo(String providerNo) {
		this.providerNo = providerNo;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getPreventionType() {
		return preventionType;
	}

	public void setPreventionType(String preventionType) {
		this.preventionType = preventionType;
	}

	public boolean isDeleted()
	{
		return deleted == IS_DELETED;
	}

	public void setDeleted(boolean deleted)
	{
		this.deleted = deleted ? IS_DELETED : IS_ACTIVE;
	}

	public boolean isRefused()
	{
		return refused == REFUSED_STATUS_REFUSED;
	}
	
	public boolean isIneligible()
	{
		return refused == REFUSED_STATUS_INELIGIBLE;
	}

	// should be refactored to an enum instead of having booleans
	public void setRefused(boolean refused)
	{
		this.refused = refused ? REFUSED_STATUS_REFUSED : REFUSED_STATUS_COMPLETED;
	}

	// should be refactored to an enum instead of having booleans
	public void setIneligible(boolean ineligible)
	{
		this.refused = ineligible ? REFUSED_STATUS_INELIGIBLE : REFUSED_STATUS_COMPLETED;
	}

	public Date getNextDate() {
		return nextDate;
	}

	public void setNextDate(Date nextDate) {
		this.nextDate = nextDate;
	}

	public boolean isNever()
	{
		return never == NEVER_SEND_REMINDER;
	}

	public void setNever(boolean never)
	{
		this.never = never ? NEVER_SEND_REMINDER : SEND_REMINDER;
	}

	public String getCreatorProviderNo() {
		return creatorProviderNo;
	}

	public void setCreatorProviderNo(String creatorProviderNo) {
		this.creatorProviderNo = creatorProviderNo;
	}
	
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	@Override
    public Integer getId() {
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	@PreUpdate
	@PrePersist
	protected void autoSetUpdateTime()
	{
		lastUpdateDate = new Date();
		if (preventionDate == null)
		{
			preventionDate = getCreationDate();
		}
	}
	
	public String getDeletedRawValue() {
		return String.valueOf(deleted);
	}

	public List<PreventionExt> getPreventionExtensionList()
	{
		return preventionExtensionList;
	}

	public void setPreventionExtensionList(List<PreventionExt> preventionExtensionList)
	{
		this.preventionExtensionList = preventionExtensionList;
	}

	public void addExtension(PreventionExt ext)
	{
		if(preventionExtensionList == null)
		{
			preventionExtensionList = new ArrayList<>(1);
		}
		preventionExtensionList.add(ext);
	}
}
