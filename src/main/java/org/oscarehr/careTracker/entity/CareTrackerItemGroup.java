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

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "care_tracker_item_group")
@Where(clause = "deleted_at IS NULL")
public class CareTrackerItemGroup extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "group_name")
	private String name;

	@Column(name = "description")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "care_tracker_id")
	private CareTracker careTracker;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "careTrackerItemGroup", cascade = CascadeType.ALL)
	private List<CareTrackerItem> careTrackerItems;

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

	public CareTrackerItemGroup()
	{
		careTrackerItems = new ArrayList<>();
	}

	public CareTrackerItemGroup(CareTrackerItemGroup toCopy, CareTracker careTrackerReference)
	{
		this.id = null;
		this.name = toCopy.name;
		this.description = toCopy.description;
		this.careTracker = careTrackerReference;
		this.careTrackerItems = toCopy.careTrackerItems
				.stream()
				.map((item) -> new CareTrackerItem(item, careTrackerReference, this))
				.collect(Collectors.toList());

		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		this.deletedAt = null;
		this.createdBy = null;
		this.updatedBy = null;
		this.deletedBy = null;
	}

	public void addItem(CareTrackerItem careTrackerItem)
	{
		this.careTrackerItems.add(careTrackerItem);
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
