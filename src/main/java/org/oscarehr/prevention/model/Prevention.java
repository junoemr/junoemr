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
public class Prevention extends AbstractModel<Integer> implements Serializable {

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

	private char deleted = '0';
	private char refused = '0';
	private char never = '0';

	@Column(name = "next_date")
	@Temporal(TemporalType.DATE)
	private Date nextDate = null;

	@Column(name = "creator")
	private String creatorProviderNo = null;
	
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

	public boolean isDeleted() {
		return deleted=='1';
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted ? '1' : '0';
	}

	public boolean isRefused() {
		return refused=='1';
	}
	
	public boolean isIneligible(){
		return refused == '2';
	}

	public void setRefused(boolean refused) {
		this.refused = refused ? '1' : '0';
	}
	
	public void setIneligible(boolean ineligible){
		this.refused = ineligible ? '2' : '0';
	}

	public Date getNextDate() {
		return nextDate;
	}

	public void setNextDate(Date nextDate) {
		this.nextDate = nextDate;
	}

	public boolean isNever() {
		return never=='1';
	}

	public void setNever(boolean never) {
		this.never = never ? '1' : '0';
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

	public Date getCreationDate() {
		return creationDate;
	}

	@PreUpdate
	@PrePersist
	protected void autoSetUpdateTime()
	{
		lastUpdateDate=new Date();
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
