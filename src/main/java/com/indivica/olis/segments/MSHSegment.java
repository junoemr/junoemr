/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package com.indivica.olis.segments;

import com.indivica.olis.queries.QueryType;
import org.oscarehr.config.JunoProperties;
import org.oscarehr.util.SpringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MSHSegment implements Segment
{
	private static final JunoProperties junoProperties = SpringUtils.getBean(JunoProperties.class);

	private final QueryType queryType;
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmssZZZZZ");
	private final String uuidString = UUID.randomUUID().toString();
	
	public MSHSegment(QueryType queryType) {
		this.queryType = queryType;
	}
	
	public String getUuidString() {
		return uuidString;
	}
	
	@Override
	public String getSegmentHL7String() {
		
		String sendingApplication  = junoProperties.getOlis().getSendingApplication();
		String processingId = junoProperties.getOlis().getProcessingId();
		
		return "MSH|^~\\&|"+sendingApplication+"|MCMUN2|" +
			"^OLIS^X500||" + dateFormatter.format(new Date()) + "||SPQ^" + queryType.toString() + "^SPQ_Q08|" + uuidString + "|"+processingId+"|2.3.1||||||8859/1";
	}

}
