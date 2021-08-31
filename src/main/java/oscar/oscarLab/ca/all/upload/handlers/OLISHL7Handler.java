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

import org.apache.log4j.Logger;
import org.oscarehr.common.model.ProviderLabRoutingModel;
import org.oscarehr.olis.OLISUtils;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.LoggedInInfo;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.upload.MessageUploader;
import oscar.oscarLab.ca.all.upload.ProviderLabRouting;
import oscar.oscarLab.ca.all.upload.RouteReportResults;
import oscar.oscarLab.ca.all.util.Utilities;

import java.util.ArrayList;

import static oscar.oscarLab.ca.all.parsers.OLISHL7Handler.OLIS_MESSAGE_TYPE;

/**
 * 
 */
public class OLISHL7Handler implements MessageHandler
{
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
		String lastTimeStampAccessed = null;
		RouteReportResults results = new RouteReportResults();

		ArrayList<String> messages = Utilities.separateMessages(fileName);

		for(String msg : messages)
		{
			logger.info(msg);
			oscar.oscarLab.ca.all.parsers.OLISHL7Handler parser = (oscar.oscarLab.ca.all.parsers.OLISHL7Handler) Factory.getHandler(OLIS_MESSAGE_TYPE, msg);

			if(!parser.canUpload() || OLISUtils.isDuplicate(loggedInInfo, parser, msg))
			{
				continue;
			}
			lastTimeStampAccessed = parser.getLastUpdateInOLISUnformated();

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
}
