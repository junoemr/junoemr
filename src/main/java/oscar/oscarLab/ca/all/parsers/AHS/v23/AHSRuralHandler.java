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
import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.model.Hl7TextInfo;
import oscar.oscarLab.ca.all.parsers.AHS.AHSHandler;

import java.util.HashSet;

/**
 * Handler for:
 * AHS Rural Labs
 *
 * @author Robert
 */
public class AHSRuralHandler extends AHSHandler
{
	public static final String AHS_RURAL_LAB_TYPE = "AHS-RURAL";

	protected static final HashSet<String> AHS_RURAL_SENDING_APPLICATION_PREFIXES = Sets.newHashSet(
		"LAB",
		"BBK",
		"MIC",
		"PTH"
	);
	protected static final HashSet<String> AHS_RURAL_SENDING_FACILITY_PREFIXES = Sets.newHashSet(
		"AHR-",
		"CHR-",
		"DTHR-",
		"ECHR-",
		"PCHR-",
		"PHR-"
	);

	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.3"))
		{
			ORU_R01 msh = (ORU_R01) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = StringUtils.trimToEmpty(messageHeaderSegment.getMsh3_SendingApplication().getNamespaceID().getValue());
			String sendingFacility = StringUtils.trimToEmpty(messageHeaderSegment.getMsh4_SendingFacility().getNamespaceID().getValue());

			return AHS_RURAL_SENDING_APPLICATION_PREFIXES.stream().anyMatch(sendingApplication::startsWith) &&
					AHS_RURAL_SENDING_FACILITY_PREFIXES.stream().anyMatch(sendingFacility::startsWith);
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

	@Override
	public boolean isUnstructured()
	{
		return false;
	}

    /* ===================================== Hl7 Parsing ====================================== */

	/* ===================================== MSH ====================================== */

	@Override
	public String getMsgType()
	{
		return AHS_RURAL_LAB_TYPE;
	}

	/* ===================================== PID ====================================== */

	@Override
	public String getHealthNumProvince()
	{
		return "(" + getString(get("/.PID-2-4")) + ")";
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
	public String getUniqueIdentifier()
	{
		// append the hin and service year/month/day to the accession number to make it unique
		String dateStr = formatDate(getString(get("/.OBR-7-1"))); // yyyy-MM-dd
		String hinStr = getHealthNum();
		return get("/.OBR-20") + "|" + hinStr + "|" + dateStr;
	}

	@Override
	public String getAccessionNumber()
	{
		// not unique, for display use only
		return get("/.OBR-20");
	}

	@Override
	public String getServiceDate()
	{
		return formatDateTime(getString(get("/.OBR-7-1")));
	}

	@Override
	public String getOrderStatus()
	{
		String originalStatus = getString(get("/.OBR-25-1"));

		// map to the juno standard codes so they show correctly
		switch (originalStatus)
		{
			case "A": return Hl7TextInfo.REPORT_STATUS.P.name(); // partial
			case "P": return Hl7TextInfo.REPORT_STATUS.E.name(); // preliminary
			default: return originalStatus;
		}
	}

	@Override
	public String getOrderStatusDisplayValue()
	{
		String orderStatusCode = getString(getOrderStatus());
		switch (orderStatusCode)
		{
			case "P": return "Partial";
			case "F": return "Final";
			case "E": return "Preliminary";
			case "X": return "Cancelled";
			default: return orderStatusCode;
		}
	}

	@Override
	public String getSubHeader(int i)
	{
		// specimen source
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBR-15-2"));
	}

	@Override
	public boolean isOBRUnstructured(int obr)
	{
		return isMicroLabResult(obr);
	}

	/* ===================================== OBX ====================================== */

	@Override
	public String getOBXResultStatusDisplayValue(int i, int j)
	{
		String resultStatusCode = getString(getOBXResultStatus(i, j));
		switch (resultStatusCode)
		{
			case "P": return "Preliminary";
			case "F": return "Final";
			case "C": return "Correction";
			case "D": return "Delete";
			default: return resultStatusCode;
		}
	}

	/**
	 *  Return the result from the jth OBX segment of the ith OBR group
	 */
	@Override
	public String getOBXResult(int i, int j)
	{
		// conformance: use obx-2 for micro-bio culture labs
		if(isMicroLabResult(i))
		{
			String result = getOBXResult(i, j, 2);
			if(StringUtils.isNotBlank(result))
			{
				return result;
			}
		}
		return getOBXResult(i, j, 1);
	}

	@Override
	public boolean isOBXAbnormal(int i, int j)
	{
		String abnormalFlags = getOBXAbnormalFlag(i,j);
		return "A".equals(abnormalFlags);
	}

	/* ===================================== private methods etc. ====================================== */

	private boolean isMicroLabResult(int i)
	{
		return "MC".equals(get("/.ORDER_OBSERVATION("+i+")/OBR-24-1"));
	}

}
