/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.hospitalReportManager.model;

import lombok.Getter;
import lombok.Setter;
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
public class HRMCategory extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String categoryName;

	@Column(name="disabled_at")
	private LocalDateTime disabledAt;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "hrmCategory")
	private List<HRMDocument> documentList;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "hrmCategory", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<HRMSubClass> subClassList;

	@Override
	public Integer getId()
	{
		return id;
	}

	public boolean isDisabled()
	{
		return this.disabledAt != null;
	}

	public List<HRMSubClass> getActiveSubClasses()
	{
		return this.subClassList
			.stream()
			.filter(subClass -> !subClass.isDisabled())
			.collect(Collectors.toList());
	}
}