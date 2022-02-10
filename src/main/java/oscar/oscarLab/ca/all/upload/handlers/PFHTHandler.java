/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package oscar.oscarLab.ca.all.upload.handlers;

import org.apache.log4j.Logger;
import org.oscarehr.util.LoggedInInfo;
import oscar.oscarLab.ca.all.upload.MessageUploader;
import oscar.oscarLab.ca.all.util.PFHTUtilities;

import java.util.ArrayList;


public class PFHTHandler implements MessageHandler
{

    Logger logger = Logger.getLogger(PFHTHandler.class);

    public String parse(LoggedInInfo loggedInInfo, String serviceName, String fileName, int fileId, String ipAddr)
    {

        PFHTUtilities u = new PFHTUtilities();

        int i = 0;
        try
        {
            ArrayList messages = u.separateMessages(fileName);
            for (i = 0; i < messages.size(); i++)
            {

                String msg = (String) messages.get(i);
                MessageUploader.routeReport(loggedInInfo, serviceName, "PFHT", msg, fileId);

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
