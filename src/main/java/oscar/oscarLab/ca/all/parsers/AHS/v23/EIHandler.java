/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package oscar.oscarLab.ca.all.parsers.AHS.v23;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import org.oscarehr.common.hl7.AHS.model.v23.message.ORU_R01;

import org.apache.log4j.Logger;

public class EIHandler extends CLSHandler
{
    private static Logger logger = Logger.getLogger(EIHandler.class);
    protected ORU_R01 msg;

    protected static final String EI_SENDING_APPLICATION = "OPEN ENGINE";
    protected static final String EI_SENDING_FACILITY = "EI";

    public static boolean handlerTypeMatch(Message message)
    {
        String version = message.getVersion();
        if (version.equals("2.3"))
        {
            ORU_R01 msh = (ORU_R01) message;
            MSH messageHeaderSegment = msh.getMSH();

            String sendingApplication = messageHeaderSegment.getSendingApplication().getNamespaceID().getValue();
            String sendingFacility = messageHeaderSegment.getSendingFacility().getNamespaceID().getValue();

            return EI_SENDING_APPLICATION.equalsIgnoreCase(sendingApplication) &&
                    EI_SENDING_FACILITY.equalsIgnoreCase(sendingFacility);
        }
        return false;
    }

    public EIHandler()
    {
        super();
    }

    public EIHandler(String hl7Body) throws HL7Exception
    {
        super(hl7Body);
        this.msg = (ORU_R01)this.message;
    }

    public EIHandler(Message msg) throws HL7Exception
    {
        super(msg);
    }

    /* ===================================== MSH ====================================== */

    public String getMsgType()
    {
        return "EI";
    }

}
