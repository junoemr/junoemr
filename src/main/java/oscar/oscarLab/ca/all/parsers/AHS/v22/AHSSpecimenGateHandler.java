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

package oscar.oscarLab.ca.all.parsers.AHS.v22;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v22.message.ORU_R01;
import ca.uhn.hl7v2.model.v22.segment.MSH;
import org.apache.log4j.Logger;
import oscar.oscarLab.ca.all.parsers.AHS.AHSHandler;

import java.util.ArrayList;

public class AHSSpecimenGateHandler extends AHSHandler
{
	private static Logger logger = Logger.getLogger(AHSSpecimenGateHandler.class);
	protected ORU_R01 msg;

	public static boolean headerTypeMatch(MSH messageHeaderSegment)
	{
		String sendingApplication = messageHeaderSegment.getSendingApplication().getValue();
		return "SpecimenGate".equalsIgnoreCase(sendingApplication);
	}

	public AHSSpecimenGateHandler()
	{
		super();
	}
	public AHSSpecimenGateHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
		this.msg = (ORU_R01) this.message;
	}
	public AHSSpecimenGateHandler(Message msg) throws HL7Exception
	{
		super(msg);
		this.msg = (ORU_R01) this.message;
	}

	@Override
	public String preUpload(String hl7Message) throws HL7Exception
	{
		return hl7Message;
	}
	@Override
	public boolean canUpload()
	{
		//get("/.PID-2")
		return true;
	}
	@Override
	public void postUpload() {}

    /* ===================================== MSH ====================================== */

	@Override
	public String getMsgType()
	{
		return "AHS";
	}
	@Override
	public String getAccessionNum()
	{
		return getString(get("/.MSH-10"));
	}

    /* ===================================== PID ====================================== */

    /* ===================================== OBR ====================================== */

	@Override
	public String getServiceDate()
	{
		String collectionDate = formatDate(getString(get("/.OBR-7")));
		String collectionTime = formatTime(getString(get("/.OBR-7")));

		// use collectionDate, birth-date, or all 1's for date
		if(collectionDate.isEmpty())
		{
			collectionDate = getDOB();
			if(collectionDate.isEmpty())
				collectionDate = "1111-11-11";
		}
		// use collectionTime or all 0's for time
		if(collectionTime.isEmpty())
			collectionTime = "00:00";

		return collectionDate + " " + collectionTime;
	}

	@Override
	public boolean isUnstructured() {
		return true;
	}

	@Override
	public String getClientRef()
	{
		return ""; //not sent
	}


	@Override
	public String getDocName()
	{
		return ""; //not sent
	}

	@Override
	public String getCCDocs()
	{
		return "";
	}

	@Override
	public ArrayList getDocNums()
	{
		return null;
	}

    /* ===================================== OBX ====================================== */

	/* ===================================== MISC ===================================== */
}
