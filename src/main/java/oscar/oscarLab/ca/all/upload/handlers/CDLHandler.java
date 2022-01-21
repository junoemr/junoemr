/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


/*
 * CDLHandler.java
 *
 * Created on May 23, 2007, 4:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package oscar.oscarLab.ca.all.upload.handlers;

import org.apache.log4j.Logger;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import oscar.oscarLab.ca.all.upload.MessageUploader;
import oscar.oscarLab.ca.all.util.Utilities;

import java.util.ArrayList;

public class CDLHandler implements MessageHandler
{

    Logger logger = Logger.getLogger(CDLHandler.class);

    public String parse(LoggedInInfo loggedInInfo, String serviceName, String fileName, int fileId, String ipAddr)
    {
        int i = 0;
        try
        {
            ArrayList<String> messages = Utilities.separateMessages(fileName);
            for (i = 0; i < messages.size(); i++)
            {
                String msg = messages.get(i);
                MessageUploader.routeReport(loggedInInfo, serviceName, "CDL", msg, fileId);
            }
        }
        catch (Exception e)
        {
            logger.error("Could not upload message: ", e);
            MiscUtils.getLogger().error("Error", e);
            return null;
        }
        return ("success");
    }
}
