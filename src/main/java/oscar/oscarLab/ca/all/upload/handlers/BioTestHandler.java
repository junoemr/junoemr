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


package oscar.oscarLab.ca.all.upload.handlers;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.OscarAuditLogger;
import org.oscarehr.util.SpringUtils;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.upload.MessageUploader;
import oscar.oscarLab.ca.all.upload.RouteReportResults;
import oscar.oscarLab.ca.all.util.Utilities;

import java.util.ArrayList;
import java.util.List;

public class BioTestHandler implements MessageHandler
{

    Logger logger = Logger.getLogger(BioTestHandler.class);
    Hl7TextInfoDao hl7TextInfoDao = (Hl7TextInfoDao) SpringUtils.getBean("hl7TextInfoDao");


    public String parse(LoggedInInfo loggedInInfo, String serviceName, String fileName, int fileId, String ipAddr)
    {

        int i = 0;
        RouteReportResults routeResults;
        try
        {
            ArrayList<String> messages = Utilities.separateMessages(fileName);
            for (i = 0; i < messages.size(); i++)
            {

                String msg = messages.get(i);
                if (isDuplicate(loggedInInfo, msg))
                {
                    return ("success");
                }

                routeResults = new RouteReportResults();
                MessageUploader.routeReport(loggedInInfo, serviceName, "BIOTEST", msg, fileId, routeResults);

                oscar.oscarLab.ca.all.parsers.MessageHandler msgHandler = Factory.getHandler(String.valueOf(routeResults.segmentId));

                if (msgHandler == null)
                {
                    logger.error("Saved lab but could not parse base64 value");
                    return null;
                }

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

    private boolean isDuplicate(LoggedInInfo loggedInInfo, String msg)
    {
        //OLIS requirements - need to see if this is a duplicate
        oscar.oscarLab.ca.all.parsers.MessageHandler h = Factory.getHandler("BIOTEST", msg);
        //if final
        if (h.getOrderStatus().equals("F"))
        {
            String fullAcc = h.getAccessionNum();
            String acc = h.getAccessionNum();
            if (acc.indexOf("-") != -1)
            {
                acc = acc.substring(acc.indexOf("-") + 1);
            }
            //do we have this?
            List<Hl7TextInfo> dupResults = hl7TextInfoDao.searchByAccessionNumber(acc);
            for (Hl7TextInfo dupResult : dupResults)
            {
                if (dupResult.equals(fullAcc))
                {
                    //if(h.getHealthNum().equals(dupResult.getHealthNumber())) {
                    OscarAuditLogger.getInstance().log(loggedInInfo, "Lab", "Skip", "Duplicate lab skipped - accession " + fullAcc + "\n" + msg);
                    return true;
                    //}
                }
                if (dupResult.getAccessionNumber().length() > 4 && dupResult.getAccessionNumber().substring(4).equals(acc))
                {
                    //if(h.getHealthNum().equals(dupResult.getHealthNumber())) {
                    OscarAuditLogger.getInstance().log(loggedInInfo, "Lab", "Skip", "Duplicate lab skipped - accession " + fullAcc + "\n" + msg);
                    return true;
                    //}
                }
            }
        }
        return false;
    }
}
