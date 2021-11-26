/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.hospitalReportManager.model;

import lombok.Data;
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@Entity
public class HRMSubClass extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String className;

	private String subClassName;

	@Column(name="acc_subclass_name")
	private String accompanyingSubClassName;

	private String sendingFacilityId;

	@Column(name="disabled_at")
	private LocalDateTime disabledAt;
	
	@ManyToOne
	@JoinColumn(name="hrmCategoryId")
	private HRMCategory hrmCategory;
	
	@Override
	public Integer getId()
	{
		return id;
	}

	public boolean isDisabled()
	{
		return this.disabledAt == null;
	}
}