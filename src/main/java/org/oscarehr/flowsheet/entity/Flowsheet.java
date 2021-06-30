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
package org.oscarehr.flowsheet.entity;

import lombok.Data;
import org.hibernate.annotations.Where;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.common.model.Icd9;
import org.oscarehr.decisionSupport2.entity.Drools;

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
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity(name = "entity.Flowsheet")
@Table(name = "flowsheet")
@Where(clause = "deleted_at IS NULL")
public class Flowsheet extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "flowsheet_name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "system_managed")
	private boolean systemManaged;

	@Column(name = "enabled")
	private boolean enabled;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "flowsheet", cascade = CascadeType.ALL)
	private List<FlowsheetItem> flowsheetItems;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "flowsheet", cascade = CascadeType.ALL)
	private List<FlowsheetItemGroup> flowsheetItemGroups;

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "flowsheet_drools", joinColumns = @JoinColumn(name="flowsheet_id"), inverseJoinColumns = @JoinColumn(name="drools_id"))
	private Set<Drools> drools = new HashSet<>();

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "flowsheet_triggers_icd9", joinColumns = @JoinColumn(name="flowsheet_id"), inverseJoinColumns = @JoinColumn(name="icd9_id"))
	private Set<Icd9> icd9Triggers = new HashSet<>();

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

	/**
	 * must be overridden to prevent default impl from infinite loading jpa links
	 */
	@Override
	public int hashCode()
	{
		return id;
	}

	@PrePersist
	private void prePersist()
	{
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
		this.setUpdatedAt(LocalDateTime.now());
		if(getUpdatedBy() == null)
		{
			throw new IllegalStateException("UpdatedBy can not be null");
		}
	}

}
