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

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class HRMProviderConfidentialityStatement extends AbstractModel<String>
{
	@Id
	private String providerNo;
	private String statement;

	@Override
	public String getId()
	{
		return providerNo;
	}
}
