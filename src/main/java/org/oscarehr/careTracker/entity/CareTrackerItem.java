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
import org.oscarehr.decisionSupport2.entity.DsRule;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "care_tracker_item")
@Where(clause = "deleted_at IS NULL")
public class CareTrackerItem extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "item_name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "guideline")
	private String guideline;

	@Column(name = "item_type")
	@Enumerated(value = EnumType.STRING)
	private ItemType type;

	@Column(name = "item_type_code")
	private String typeCode;

	@Column(name = "value_type")
	@Enumerated(value = EnumType.STRING)
	private ValueType valueType;

	@Column(name = "value_label")
	private String valueLabel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "care_tracker_id")
	private CareTracker careTracker;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "care_tracker_item_group_id")
	private CareTrackerItemGroup careTrackerItemGroup;

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "care_tracker_item_ds_rule", joinColumns = @JoinColumn(name="care_tracker_item_id"), inverseJoinColumns = @JoinColumn(name="ds_rule_id"))
	private Set<DsRule> dsRules;

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

	public CareTrackerItem()
	{
		this.dsRules = new HashSet<>();
	}

	public CareTrackerItem(CareTrackerItem toCopy, CareTracker careTrackerReference, CareTrackerItemGroup groupReference)
	{
		this.id = null;
		this.name = toCopy.name;
		this.description = toCopy.description;
		this.guideline = toCopy.guideline;
		this.type = toCopy.type;
		this.typeCode = toCopy.typeCode;

		this.valueType = toCopy.valueType;
		this.valueLabel = toCopy.valueLabel;
		this.dsRules = new HashSet<>(toCopy.dsRules);
		this.careTracker = careTrackerReference;
		this.careTrackerItemGroup = groupReference;

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

	public boolean isMeasurementType()
	{
		return ItemType.MEASUREMENT.equals(this.type);
	}
	public boolean isPreventionType()
	{
		return ItemType.PREVENTION.equals(this.type);
	}

	@PrePersist
	private void prePersist()
	{
		this.setCreatedAt(LocalDateTime.now());
		this.setUpdatedAt(LocalDateTime.now());

		if(getCreatedBy() == null)
		{
			setCreatedBy(getCareTracker().getCreatedBy());
		}
		if(getUpdatedBy() == null)
		{
			setUpdatedBy(getCreatedBy());
		}
	}

	@PreUpdate
	private void preUpdate()
	{
		this.setUpdatedAt(LocalDateTime.now());
		setUpdatedBy(getCareTracker().getUpdatedBy());
	}
}
