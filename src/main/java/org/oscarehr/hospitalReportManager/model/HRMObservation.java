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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name="HRMDocumentSubClass")
public class HRMObservation extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name="subClass")
	private String accompanyingSubClassName;

	@Column(name="subClassMnemonic")
	private String accompanyingSubClassMnemonic;

	@Column(name="subClassDescription")
	private String accompanyingSubClassDescription;

	@Column(name="subClassDateTime")
	private Date accompanyingSubClassObrDate;

	@Column(name="isActive")
	private boolean active;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hrmDocumentId")
	private HRMDocument hrmDocument;

	@Override
	public Integer getId()
	{
		return id;
	}
}