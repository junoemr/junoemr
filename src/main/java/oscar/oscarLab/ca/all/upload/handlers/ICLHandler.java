/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

/*
 * ICLHandler.java
 * Created on Feb. 23, 2009
 * Modified by David Daley, Indivica
 * Derived from GDMLHandler.java, by wrighd
 */
package oscar.oscarLab.ca.all.upload.handlers;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.oscarLab.ca.all.upload.MessageUploader;
import oscar.oscarLab.ca.all.util.ICLUtilities;

import java.util.ArrayList;

/*
 * @author David Daley, Ithream
 */
public class ICLHandler implements MessageHandler
{

    Logger logger = Logger.getLogger(ICLHandler.class);
    Hl7TextInfoDao hl7TextInfoDao = SpringUtils.getBean(Hl7TextInfoDao.class);

    public String parse(LoggedInInfo loggedInInfo, String serviceName, String fileName, int fileId, String ipAddr)
    {

        ICLUtilities u = new ICLUtilities();
        int i = 0;
        try
        {
            ArrayList<String> messages = u.separateMessages(fileName);
            for (i = 0; i < messages.size(); i++)
            {

                String msg = messages.get(i);
                MessageUploader.routeReport(loggedInInfo, serviceName, "ICL", msg, fileId);

            }
        }
        catch (Exception e)
        {

            logger.error("Could not upload message", e);
            return null;
        }
        return ("success");

    }
}
