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

package oscar.oscarLab.ca.all.parsers;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.model.v23.segment.MSH;

public class AHSSunquestHandler extends AHSHandler
{

	public static boolean headerTypeMatch(MSH messageHeaderSegment)
	{
		String sendingApplication = messageHeaderSegment.getSendingApplication().getNamespaceID().getValue();
		String sendingFacility = messageHeaderSegment.getSendingFacility().getNamespaceID().getValue();

		return "OADD".equalsIgnoreCase(sendingApplication) &&
				("SUNQUEST".equalsIgnoreCase(sendingFacility) || "COPATH".equalsIgnoreCase(sendingFacility));
	}

	public AHSSunquestHandler()
	{
		super();
	}
	public AHSSunquestHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
	}
	public AHSSunquestHandler(ORU_R01 msg) throws HL7Exception
	{
		super(msg);
	}
	@Override
	public String preUpload(String hl7Message) throws HL7Exception
	{
		return hl7Message;
	}
	@Override
	public boolean canUpload()
	{
		return true;
	}
	@Override
	public void postUpload() {}
}
