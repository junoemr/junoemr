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
package oscar.oscarLab.ca.all.parsers.AHS.v23;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.model.v23.segment.MSH;

/**
 * Handler for:
 * AHS Physician Documentation Meditech (PDOC)
 *
 * @author Robert
 */
public class AHSRuralDIHandlerV1 extends AHSRuralDIHandler
{
	protected static final String CLSDI_SENDING_APPLICATION = "PDOC";
	protected static final String CLSDI_SENDING_FACILITY = "PHR-LMHA";

	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.3"))
		{
			ORU_R01 msh = (ORU_R01) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = messageHeaderSegment.getSendingApplication().getNamespaceID().getValue();
			String sendingFacility = messageHeaderSegment.getSendingFacility().getNamespaceID().getValue();

			return CLSDI_SENDING_APPLICATION.equalsIgnoreCase(sendingApplication) &&
					CLSDI_SENDING_FACILITY.equalsIgnoreCase(sendingFacility);
		}
		return false;
	}

	public AHSRuralDIHandlerV1()
	{
		super();
	}

	public AHSRuralDIHandlerV1(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
	}

	public AHSRuralDIHandlerV1(Message msg) throws HL7Exception
	{
		super(msg);
	}

    /* ===================================== Hl7 Parsing ====================================== */

}
