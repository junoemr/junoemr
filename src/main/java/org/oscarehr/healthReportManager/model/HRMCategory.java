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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Data
@Entity
public class HRMCategory extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String categoryName;
	private String subClassNameMnemonic;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "hrmCategory")
	private List<HRMDocument> documentList;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "hrmCategory")
	private List<HRMSubClass> subClassList;

	public HRMCategory() {

	}

	@Override
	public Integer getId()
	{
		return id;
	}
}
