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
package org.oscarehr.security.model;

import lombok.Data;
import org.hibernate.annotations.Where;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.provider.model.ProviderData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="secDemographicSet")
@Where(clause="deleted_at IS NULL")
public class SecDemographicSet extends AbstractModel<Integer>
{
	enum SET_TYPE {
		BLACKLIST,
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "set_name")
	private String setName;

	@Column(name = "set_type")
	@Enumerated(EnumType.STRING)
	private SET_TYPE setType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "provider_id", referencedColumnName = "provider_no", nullable = false)
	private ProviderData provider;

	@Column(name = "created_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime createdAt;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime deletedAt;

	@Column(name = "deleted_by")
	private String deletedBy;

	@Override
	public Integer getId()
	{
		return id;
	}

	@PrePersist
	public void setCreatedTime()
	{
		this.setCreatedAt(LocalDateTime.now());
		this.setUpdatedAt(LocalDateTime.now());
	}

	@PreUpdate
	public void setUpdateTime()
	{
		this.setUpdatedAt(LocalDateTime.now());
	}

	public void setSetTypeBlacklist()
	{
		this.setSetType(SET_TYPE.BLACKLIST);
	}
}
