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
import org.oscarehr.provider.model.ProviderData;

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
public class HRMDocumentToProvider extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String providerNo;
	private boolean viewed;
	private boolean signedOff;
	private Date signedOffTimestamp;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hrmDocumentId")
	private HRMDocument hrmDocument;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "providerNo", insertable = false, updatable = false)
	private ProviderData provider;

	@Override
	public Integer getId()
	{
		return id;
	}
}
