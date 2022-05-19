/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package com.indivica.olis.segments;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.model.Provider;
import org.oscarehr.ws.rest.exception.MissingArgumentException;

public class ZSHSegment implements Segment
{
	private final Provider provider;

	public ZSHSegment(Provider provider)
	{
		this.provider = provider;
	}

	@Override
	public String getSegmentHL7String()
	{
		String practitionerNo = provider.getOlisPractitionerNo();
		if(StringUtils.isBlank(practitionerNo))
		{
			throw new MissingArgumentException("Requesting provider must have an assigned practitioner number");
		}
		return "ZSH|" + practitionerNo + "|" + provider.getLastName() + " " + provider.getFirstName();
	}
}
