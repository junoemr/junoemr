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
package org.oscarehr.careTracker.entity;

import lombok.Data;
import org.hibernate.annotations.Where;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.common.model.Icd9;
import org.oscarehr.decisionSupport2.entity.Drools;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.provider.model.ProviderData;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "care_tracker")
@Where(clause = "deleted_at IS NULL")
public class CareTracker extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "care_tracker_name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "system_managed")
	private boolean systemManaged;

	@Column(name = "enabled")
	private boolean enabled;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "careTracker", cascade = CascadeType.ALL)
	private List<CareTrackerItem> careTrackerItems;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "careTracker", cascade = CascadeType.ALL)
	private List<CareTrackerItemGroup> careTrackerItemGroups;

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "care_tracker_drools", joinColumns = @JoinColumn(name="care_tracker_id"), inverseJoinColumns = @JoinColumn(name="drools_id"))
	private Set<Drools> drools = new HashSet<>();

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "care_tracker_triggers_icd9", joinColumns = @JoinColumn(name="care_tracker_id"), inverseJoinColumns = @JoinColumn(name="icd9_id"))
	private Set<Icd9> icd9Triggers = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_care_tracker_id", referencedColumnName = "id")
	private CareTracker parentCareTracker;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_provider_id", referencedColumnName = "provider_no")
	private ProviderData ownerProvider;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_demographic_id", referencedColumnName = "demographic_no")
	private Demographic ownerDemographic;

	@Column(name = "created_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime createdAt;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime updatedAt;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "deleted_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime deletedAt;

	@Column(name = "deleted_by")
	private String deletedBy;

	public CareTracker()
	{
	}

	public CareTracker(CareTracker toCopy)
	{
		this.id = null;
		this.name = toCopy.name;
		this.description = toCopy.description;
		this.enabled = toCopy.enabled;
		this.systemManaged = toCopy.systemManaged;

		// copy item groups (and contained items)
		this.careTrackerItemGroups = toCopy.careTrackerItemGroups
				.stream()
				.map((group) -> new CareTrackerItemGroup(group, this))
				.collect(Collectors.toList());

		// add items in groups
		this.careTrackerItems = this.careTrackerItemGroups
				.stream()
				.filter((group) -> group.getCareTrackerItems() != null)
				.flatMap((group) -> group.getCareTrackerItems().stream())
				.collect(Collectors.toList());

		// add items in original care tracker the have no group
		this.careTrackerItems.addAll(toCopy.getCareTrackerItems()
				.stream()
				.filter((item) -> item.getCareTrackerItemGroup() == null)
				.map((item) -> new CareTrackerItem(item, this, null))
				.collect(Collectors.toList()));

		// copy triggers and drools sets. set must be a new object
		this.icd9Triggers = new HashSet<>(toCopy.icd9Triggers);
		this.drools = new HashSet<>(toCopy.drools);

		this.parentCareTracker = toCopy.getParentCareTracker();
		this.ownerProvider = toCopy.getOwnerProvider();
		this.ownerDemographic = toCopy.getOwnerDemographic();

		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		this.deletedAt = null;
		this.createdBy = null;
		this.updatedBy = null;
		this.deletedBy = null;
	}

	/**
	 * must be overridden to prevent default impl from infinite loading jpa links
	 */
	@Override
	public int hashCode()
	{
		return id;
	}

	@Override
	public String toString()
	{
		return this.getClass().getName() + "{id: " + id + ", name: " + name + "}";
	}

	public Optional<CareTracker> getOptionalParentCareTracker()
	{
		return Optional.ofNullable(getParentCareTracker());
	}

	public Optional<Demographic> getOptionalOwnerDemographic()
	{
		return Optional.ofNullable(getOwnerDemographic());
	}

	public Optional<ProviderData> getOptionalOwnerProvider()
	{
		return Optional.ofNullable(getOwnerProvider());
	}

	@PrePersist
	private void prePersist()
	{
		this.checkOwnerState();
		this.setCreatedAt(LocalDateTime.now());
		this.setUpdatedAt(LocalDateTime.now());

		if(getCreatedBy() == null)
		{
			throw new IllegalStateException("CreatedBy can not be null");
		}
		if(getUpdatedBy() == null)
		{
			setUpdatedBy(getCreatedBy());
		}
	}

	@PreUpdate
	private void preUpdate()
	{
		this.checkOwnerState();
		this.setUpdatedAt(LocalDateTime.now());
		if(getUpdatedBy() == null)
		{
			throw new IllegalStateException("UpdatedBy can not be null");
		}
	}

	private void checkOwnerState()
	{
		if(this.getOwnerDemographic() != null && this.getOwnerProvider() != null)
		{
			throw new IllegalStateException("Care Tracker can not be owned by both a provider and demographic");
		}
	}

}
