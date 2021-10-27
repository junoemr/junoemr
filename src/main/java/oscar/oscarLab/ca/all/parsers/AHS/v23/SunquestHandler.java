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
import org.apache.log4j.Logger;
import oscar.oscarLab.ca.all.parsers.AHS.AHSHandler;

public class SunquestHandler extends AHSHandler
{
	private static Logger logger = Logger.getLogger(SunquestHandler.class);

	protected static final String SUNQUEST_SENDING_APPLICATION = "OADD";
	protected static final String SUNQUEST_SENDING_FACILITY = "SUNQUEST";
	protected static final String COPATH_SENDING_FACILITY = "COPATH";

	protected ORU_R01 msg;

	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.3"))
		{
			ORU_R01 msh = (ORU_R01) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = messageHeaderSegment.getSendingApplication().getNamespaceID().getValue();
			String sendingFacility = messageHeaderSegment.getSendingFacility().getNamespaceID().getValue();

			return SUNQUEST_SENDING_APPLICATION.equalsIgnoreCase(sendingApplication) &&
					(SUNQUEST_SENDING_FACILITY.equalsIgnoreCase(sendingFacility) ||
							COPATH_SENDING_FACILITY.equalsIgnoreCase(sendingFacility));
		}
		return false;
	}

	public SunquestHandler()
	{
		super();
	}
	public SunquestHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
		this.msg = (ORU_R01) this.message;
	}
	public SunquestHandler(Message msg) throws HL7Exception
	{
		super(msg);
		this.msg = (ORU_R01) this.message;
	}

    /* ===================================== MSH ====================================== */

	public String getAccessionNum() {

		// unique labs are a combination of accessionNumber, fillerOrderNumber, universalServiceId, and observation year
		// fillerOrderNumber is separate (for lab versions), but the other fields are combined here to ensure uniqueness
		String accessionNum = get("/.OBR-20");
		String serviceId = get("/.OBR-4");
		String obrYear = getServiceDate().substring(0,4);

		return accessionNum + "-" + serviceId + "-" + obrYear;
	}

    /* ===================================== PID ====================================== */

	/* ===================================== OBR ====================================== */

	@Override
	protected String getOrderingProvider(int i, int k)
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBR-16("+k+")-2"));
	}
	@Override
	protected String getResultCopiesTo(int i, int k)
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBR-28("+k+")-2"));
	}

	/* ===================================== OBX ====================================== */

	public String getOBXResult(int i, int j)
	{
		// for whatever reason, the OBX-5 field uses ~ to denote a line break (breaking hl7 standards).
		// so here we concat all the segment repetitions into a single result string.
		String newline = "<br />";

		int reps = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObx5_ObservationValueReps();
		String[] lines = new String[reps];

		// gather all the strings into a list. join the list with the newline delimiter
		for(int k=0; k<reps; k++)
		{
			lines[k] = getString(get("/.ORDER_OBSERVATION(" + i + ")/OBSERVATION(" + j + ")/OBX-5(" + k + ")"));
		}
		return String.join(newline, lines);
	}

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
