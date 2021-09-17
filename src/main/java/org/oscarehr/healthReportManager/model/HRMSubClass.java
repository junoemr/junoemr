/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.healthReportManager.model;

import lombok.Data;
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Entity
public class HRMSubClass extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String className;
	private String subClassName;
	private String subClassMnemonic;
	private String subClassDescription;
	private String sendingFacilityId;
	
	@ManyToOne
	@JoinColumn(name="hrmCategoryId")
	private HRMCategory hrmCategory;
	
	@Override
	public Integer getId()
	{
		return id;
	}
}
