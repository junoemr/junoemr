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
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import oscar.oscarLab.ca.all.parsers.AHS.AHSHandler;

import java.util.HashSet;

/**
 * Handler for:
 * AHS Rural Diagnostic Imaging
 *
 * @author Robert
 */
public class AHSRuralDIHandler extends AHSHandler
{
	public static final String AHS_RURAL_DI_LAB_TYPE = "AHS-RDI";

	protected static final String AHS_RDI_SENDING_APPLICATION = "RAD";
	protected static final HashSet<String> AHS_RDI_SENDING_FACILITY_PREFIXES = Sets.newHashSet(
		"AHR-", // .arad
		"CHR-", // .crad
		"DTHR-", // .drad
		"ECHR-", // .erad
		"PHR-", // .lrad
		"NLHR-", // .nrad
		"PCHR-" // .prad
	);

	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.3"))
		{
			ORU_R01 msh = (ORU_R01) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = messageHeaderSegment.getMsh3_SendingApplication().getNamespaceID().getValue();
			String sendingFacility = StringUtils.trimToEmpty(messageHeaderSegment.getMsh4_SendingFacility().getNamespaceID().getValue());

			return AHS_RDI_SENDING_APPLICATION.equalsIgnoreCase(sendingApplication) &&
					AHS_RDI_SENDING_FACILITY_PREFIXES.stream().anyMatch(sendingFacility::startsWith);
		}
		return false;
	}

	public AHSRuralDIHandler()
	{
		super();
	}

	public AHSRuralDIHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
	}

	public AHSRuralDIHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	@Override
	public boolean canUpload()
	{
		return true;
	}

    /* ===================================== Hl7 Parsing ====================================== */

	/* ===================================== MSH ====================================== */

	@Override
	public String getMsgType()
	{
		return AHS_RURAL_DI_LAB_TYPE;
	}

	/* ===================================== OBR ====================================== */

	@Override
	public String getUniqueIdentifier()
	{
		// use the filler order number as the unique lab identifier apparently
		return get("/.OBR-3-1");
	}

	@Override
	public String getUniqueVersionIdentifier()
	{
		return StringUtils.trimToEmpty(get("/.OBR-18-2") + " " + get("/.OBR-4-5"));
	}

	@Override
	public boolean isUnstructured()
	{
		return true;
	}

	@Override
	public String getServiceDate()
	{
		return formatDateTime(getString(get("/.OBR-7-1")));
	}

	@Override
	public String getOrderStatus()
	{
		return get("/.OBR-25-1");
	}

	@Override
	public String getOrderStatusDisplayValue()
	{
		if("X".equals(getOrderStatus()))
		{
			return "Cancelled";
		}
		return "Final";
	}

	/* ===================================== OBX ====================================== */

	@Override
	public String getTimeStamp(int i, int j)
	{
		if (i < 0 || j < 0)
		{
			return null;
		}
		return formatDateTime(get("/.ORDER_OBSERVATION("+i+")/OBR-8-1"));
	}

}
