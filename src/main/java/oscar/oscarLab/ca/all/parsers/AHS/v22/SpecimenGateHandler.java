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

public class SpecimenGateHandler extends AHSHandler
{
	private static Logger logger = Logger.getLogger(SpecimenGateHandler.class);
	protected ORU_R01 msg;

	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.2"))
		{
			ORU_R01 msh = (ORU_R01) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = messageHeaderSegment.getSendingApplication().getValue();

			return "SpecimenGate".equalsIgnoreCase(sendingApplication);
		}
		return false;
	}

	public SpecimenGateHandler()
	{
		super();
	}
	public SpecimenGateHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
		this.msg = (ORU_R01) this.message;
	}
	public SpecimenGateHandler(Message msg) throws HL7Exception
	{
		super(msg);
		this.msg = (ORU_R01) this.message;
	}

    /* ===================================== MSH ====================================== */

	@Override
	public String getMsgType()
	{
		return "AHS";
	}
	@Override
	public String getPatientLocation()
	{
		//return getString(get("/.PV1-3"));
		return "SpecimenGate";
	}

	@Override
	public String getAccessionNum() {

		// unique labs are a combination of accessionNumber, fillerOrderNumber, universalServiceId, and observation year
		// fillerOrderNumber is separate (for lab versions), but the other fields are combined here to ensure uniqueness
		String accessionNum = get("/.OBR-20");
		String serviceId = get("/.OBR-4");
		String obrYear = getServiceDate().substring(0,4);
		// there could be a potential error here if the date is missing the year and defaults to 1111 (required by conformance spec)
		// in two labs with a repeated accession number

		return accessionNum + "-" + serviceId + "-" + obrYear;
	}

    /* ===================================== PID ====================================== */

    /* ===================================== OBR ====================================== */

	/**
	 *  Return the number of OBR Segments in the message
	 */
	public int getOBRCount()
	{
		return getReps("PATIENT_RESULT", 0, "ORDER_OBSERVATION");
	}
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

    /* ===================================== OBX ====================================== */

	/* ===================================== MISC ===================================== */

    /* =============================== Upload Handling ================================ */

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
