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
import java.util.HashSet;
import oscar.oscarLab.ca.all.parsers.AHS.AHSHandler;

/**
 * Handler for:
 * AHS Rural Labs
 *
 * @author Robert
 */
public class AHSRuralHandler extends AHSHandler
{
	public static final String AHS_RURAL_LAB_TYPE = "AHS-RURAL";

	protected static final HashSet<String> AHS_RURAL_SENDING_APPLICATIONS = Sets.newHashSet(
		"LAB",
		"LAB:POSP",
		"BBK",
		"BBK:POSP",
		"MIC",
		"MIC:POSP",
		"PTH",
		"PTH:POSP"
	);
	protected static final HashSet<String> AHS_RURAL_SENDING_FACILITIES = Sets.newHashSet(
		"AHR-ABVA",
		"CHR-CLRH",
		"DTHR-DRDH",
		"ECHR-EWAA",
		"PCHR-PQEA",
		"PHR-LMHA"
	);

	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.3"))
		{
			ORU_R01 msh = (ORU_R01) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = messageHeaderSegment.getMsh3_SendingApplication().getNamespaceID().getValue();
			String sendingFacility = messageHeaderSegment.getMsh4_SendingFacility().getNamespaceID().getValue();

			return AHS_RURAL_SENDING_APPLICATIONS.contains(sendingApplication.toUpperCase()) &&
				AHS_RURAL_SENDING_FACILITIES.contains(sendingFacility.toUpperCase());
		}
		return false;
	}

	public AHSRuralHandler()
	{
		super();
	}

	public AHSRuralHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
	}

	public AHSRuralHandler(Message msg) throws HL7Exception
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
		return AHS_RURAL_LAB_TYPE;
	}

	/* ===================================== OBR ====================================== */

	/**
	 * From the spec:
	 * Meditech populates this field with the specimen accession number. The specimen number must
	 * always be used in conjunction with other patient identifiers as some specimen accession numbers in Meditech
	 * can repeat. I.e. accession number 0101:B00001R where the numerals directly preceding the colon (0101) signify
	 * day and month (in this instance the first day of January). Note that this specimen accession number will repeat
	 * every once per year on January 1 (0101). In other specimen accn numbers the digits prior to the colon signify the
	 * year and in these cases the specimen numbers are unique and will not repeat i.e. 18:MR0012263U where 18
	 * signifies 2018, S14-104 where 14 signifies 2014 etc.
	 */
	@Override
	public String getAccessionNum()
	{
		// append the service year/month to the accession number to make it unique
		String dateStr = formatDate(getString(get("/.OBR-7-1")));
		return get("/.OBR-20") + "|" + dateStr;
	}

	@Override
	public String getServiceDate()
	{
		return formatDateTime(getString(get("/.OBR-7-1")));
	}

	@Override
	public String getOrderStatus()
	{
		return getString(get("/.OBR-25-1"));
	}

	@Override
	public String getOrderStatusDisplayValue()
	{
		String orderStatusCode = getString(getOrderStatus());
		switch (orderStatusCode)
		{
			case "A": return "Partial";
			case "F": return "Final";
			case "P": return "Preliminary";
			case "X": return "Cancelled";
			default: return orderStatusCode;
		}
	}

	/* ===================================== OBX ====================================== */

}
