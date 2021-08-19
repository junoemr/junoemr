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
import oscar.oscarLab.ca.all.parsers.AHS.AHSHandler;

/**
 * Handler for:
 * combined Rural DI (v2.0) + PDOC (v1.0) Conformance Pack
 *
 * @author Robert
 */
public abstract class AHSRuralDIHandler extends AHSHandler
{
	public static final String AHS_RURAL_LAB_TYPE = "AHSRDI";
	protected final ORU_R01 msg;

	public AHSRuralDIHandler() {
		super();
		this.msg = (ORU_R01) this.message;
	}
	public AHSRuralDIHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
		this.msg = (ORU_R01) this.message;
	}
	public AHSRuralDIHandler(Message msg) throws HL7Exception
	{
		super(msg);
		this.msg = (ORU_R01) this.message;
	}

	@Override
	public boolean canUpload()
	{
		return true;
	}

    /* ===================================== Hl7 Parsing ====================================== */

	public String getMsgType()
	{
		return AHS_RURAL_LAB_TYPE;
	}

	@Override
	public String getServiceDate() {
		try {
			String serviceDate = getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getObr8_ObservationEndDateTime().getTimeOfAnEvent().getValue());
			return (formatDateTime(serviceDate));
		}
		catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getOrderStatus()
	{
		try
		{
			return get("/.OBR-25-1");
		}
		catch(Exception e)
		{
			return ("");
		}
	}

	@Override
	public String getFillerOrderNumber()
	{
		return get("/.OBR-20");
	}

	@Override
	public String getAccessionNum()
	{
		/* Unique for every message. */
		return getMsgSendingApplication() + "_" + getPatientLocation() + "_" + get("/.MSH-10");
	}

	@Override
	public boolean isUnstructured()
	{
		return true;
	}
}
