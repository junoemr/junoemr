/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package com.indivica.olis;

import com.indivica.olis.queries.Query;
import com.indivica.olis.segments.DSCSegment;
import com.indivica.olis.segments.MSHSegment;
import com.indivica.olis.segments.SPRSegment;
import com.indivica.olis.segments.ZSHSegment;
import org.oscarehr.common.model.Provider;

public class OLISMessage
{
	private final MSHSegment mshSegment;
	private final ZSHSegment zshSegment;
	private final SPRSegment sprSegment;
	private final DSCSegment dscSegment;

	public OLISMessage()
	{
		this(null, null);
	}

	public OLISMessage(Provider provider, Query query)
	{
		this(provider, query, null);
	}
	
	public OLISMessage(Provider provider, Query query, String continuationPointer)
	{
		mshSegment = new MSHSegment(query.getQueryType());
		zshSegment = new ZSHSegment(provider);
		sprSegment = new SPRSegment(query.getQueryType(), query);
		dscSegment = (continuationPointer != null) ? new DSCSegment(continuationPointer) : null;
	}

	public String getTransactionId()
	{
		return mshSegment.getUuidString();
	}

	public String getOlisHL7String()
	{
		String output = "";

		output += mshSegment.getSegmentHL7String() + "\r";
		output += zshSegment.getSegmentHL7String() + "\r";
		output += sprSegment.getSegmentHL7String();

		if(dscSegment != null)
		{
			output += "\r" + dscSegment.getSegmentHL7String();
		}

		return output;
	}
	
}
