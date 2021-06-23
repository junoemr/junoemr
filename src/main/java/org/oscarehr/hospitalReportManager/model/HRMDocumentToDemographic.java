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
import org.oscarehr.demographic.model.Demographic;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Data
@Entity
public class HRMDocumentToDemographic extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer demographicNo;
	private Integer hrmDocumentId;
	private Date timeAssigned;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hrmDocumentId", insertable = false, updatable = false)
	private HRMDocument hrmDocument;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "demographicNo", insertable = false, updatable = false)
	private Demographic demographic;

	@Override
	public Integer getId()
	{
		return id;
	}
}
