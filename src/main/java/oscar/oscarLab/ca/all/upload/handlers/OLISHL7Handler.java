/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

/*
 * HL7Handler
 * Upload handler
 * 
 */
package oscar.oscarLab.ca.all.upload.handlers;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.model.ProviderLabRoutingModel;
import org.oscarehr.olis.OLISUtils;
import org.oscarehr.olis.exception.OLISAckFailedException;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.LoggedInInfo;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.upload.MessageUploader;
import oscar.oscarLab.ca.all.upload.ProviderLabRouting;
import oscar.oscarLab.ca.all.upload.RouteReportResults;
import oscar.oscarLab.ca.all.util.Utilities;
import oscar.util.ConversionUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.oscarehr.olis.OLISPollingUtil.OLIS_DATE_FORMAT;
import static oscar.oscarLab.ca.all.parsers.OLISHL7Handler.OLIS_MESSAGE_TYPE;

/**
 * 
 */
public class OLISHL7Handler implements MessageHandler
{
	public static final String ALL_DUPLICATES_MARKER = "All Labs Duplicates";
	private static final Logger logger = Logger.getLogger(OLISHL7Handler.class);
	private int lastSegmentId = 0;
	
	public OLISHL7Handler()
	{
		logger.info("NEW OLISHL7Handler UPLOAD HANDLER instance just instantiated. ");
	}

	@Override
	public String parse(LoggedInInfo loggedInInfo, String serviceName, String fileName, int fileId, String ipAddr) throws Exception
	{
		return parse(loggedInInfo, serviceName, fileName, fileId, false);
	}

	public String parse(LoggedInInfo loggedInInfo, String serviceName, String fileName, int fileId, boolean routeToCurrentProvider) throws Exception
	{
		String lastTimeStampAccessed = ALL_DUPLICATES_MARKER;
		RouteReportResults results = new RouteReportResults();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(OLIS_DATE_FORMAT);

		ArrayList<String> messages = Utilities.separateMessages(fileName);

		// Only the first olis message in a batch will have a QAK acknowledge segment.
		// The message is spit up so that each PID becomes its own message in the system,
		// with a copy of the initial MSH segment (OLIS only sends 1 in the results batch).
		// so if the ack is bad fail the batch.
		if(!messages.isEmpty())
		{
			String messageWithQAK = messages.get(0);
			oscar.oscarLab.ca.all.parsers.OLISHL7Handler parser = (oscar.oscarLab.ca.all.parsers.OLISHL7Handler) Factory.getHandler(OLIS_MESSAGE_TYPE, messageWithQAK);

			if(!parser.canUpload())
			{
				throw new OLISAckFailedException("OLIS QAK status is not OK", parser.getAckStatus(), parser.getReportErrors());
			}
		}

		// process all messages
		for(String msg : messages)
		{
			oscar.oscarLab.ca.all.parsers.OLISHL7Handler parser = (oscar.oscarLab.ca.all.parsers.OLISHL7Handler) Factory.getHandler(OLIS_MESSAGE_TYPE, msg);

			// skip uploading duplicates
			if(OLISUtils.isDuplicate(loggedInInfo, parser, msg))
			{
				continue;
			}
			// always use the last obr date as the latest timestamp seen
			lastTimeStampAccessed = getLatest(dateTimeFormatter, lastTimeStampAccessed, StringUtils.trimToNull(parser.getLastUpdateInOLISUnformatted()));

			MessageUploader.routeReport(
					loggedInInfo.getLoggedInProviderNo(),
					serviceName,
					parser,
					OLIS_MESSAGE_TYPE,
					msg.replace("\\E\\", "\\SLASHHACK\\").replace("µ", "\\MUHACK\\").replace("\\H\\", "\\.H\\").replace("\\N\\", "\\.N\\"),
					fileId,
					results);
			if(routeToCurrentProvider)
			{
				ProviderLabRouting routing = new ProviderLabRouting();
				routing.route(results.segmentId, loggedInInfo.getLoggedInProviderNo(), DbConnectionFilter.getThreadLocalDbConnection(), ProviderLabRoutingModel.LAB_TYPE_LABS);
				this.lastSegmentId = results.segmentId;
			}
		}
		logger.info("Parsed OK");
		return lastTimeStampAccessed;
	}

	public int getLastSegmentId()
	{
		return this.lastSegmentId;
	}

	private String getLatest(DateTimeFormatter dateTimeFormatter, String timeStamp1, String timeStamp2)
	{
		timeStamp1 = (ALL_DUPLICATES_MARKER.equals(timeStamp1)) ? null : timeStamp1;
		timeStamp2 = (ALL_DUPLICATES_MARKER.equals(timeStamp2)) ? null : timeStamp2;

		if(timeStamp1 == null && timeStamp2 == null)
		{
			return null;
		}
		else if (timeStamp1 == null)
		{
			return timeStamp2;
		}
		else if (timeStamp2 == null)
		{
			return timeStamp1;
		}
		else
		{
			ZonedDateTime zonedTime1 = ConversionUtils.toZonedDateTime(timeStamp1, dateTimeFormatter);
			ZonedDateTime zonedTime2 = ConversionUtils.toZonedDateTime(timeStamp2, dateTimeFormatter);
			ZonedDateTime latest = (zonedTime1.isAfter(zonedTime2)) ? zonedTime1 : zonedTime2;
			return ConversionUtils.toDateTimeString(latest, dateTimeFormatter);
		}
	}
}
