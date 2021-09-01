/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package org.oscarehr.olis.model;

import lombok.Getter;
import lombok.Setter;
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Optional;

@Entity
@Setter
public class OLISProviderPreferences extends AbstractModel<String>
{
	@Id
	private String providerId;

	@Getter
	private String startTime;


	public OLISProviderPreferences()
	{
		super();
	}

	@Override
	public String getId()
	{
		return providerId;
	}

	public Optional<String> getOptionalStartDateTime()
	{
		return Optional.ofNullable(this.startTime);
	}
}
