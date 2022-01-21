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
import org.oscarehr.util.LoggedInInfo;
import oscar.oscarLab.ca.all.upload.MessageUploader;
import oscar.oscarLab.ca.all.util.Utilities;

import java.util.ArrayList;

/**
 *
 */
public class HRMXMLHandler implements MessageHandler
{

    Logger logger = Logger.getLogger(HL7Handler.class);

    public HRMXMLHandler()
    {
        logger.info("NEW HRM XML UPLOAD HANDLER instance just instantiated. ");
    }

    public String parse(LoggedInInfo loggedInInfo, String serviceName, String fileName, int fileId, String ipAddr)
    {
        logger.info("ABOUT TO PARSE HRM XML " + fileName);

        int i = 0;
        try
        {
            ArrayList<String> messages = Utilities.separateMessages(fileName);
            for (i = 0; i < messages.size(); i++)
            {

                String msg = messages.get(i);
                MessageUploader.routeReport(loggedInInfo, "", "HRMXML", msg, fileId);

            }

            logger.info("Parsed OK");
        }
        catch (Exception e)
        {
            logger.error("Could not upload message", e);
            return null;
        }
        return ("success");

    }
}
