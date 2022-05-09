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
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.model.Hl7TextInfo;
import oscar.oscarLab.ca.all.parsers.AHS.AHSHandler;

import java.util.ArrayList;
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

	private MultiKeyMap<Integer, Integer> obrParentMap;

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
		obrParentMap = new MultiKeyMap<>();
		int obrCount = getOBRCount();

		for(int i = 0; i < obrCount; i++)
		{
			String parentPlacerNo = getString(get("/.ORDER_OBSERVATION(" + i + ")/OBR-29-1"));
			String parentResultId = getString(get("/.ORDER_OBSERVATION(" + i + ")/OBR-26-2"));

			if(StringUtils.isNotBlank(parentPlacerNo) && StringUtils.isNotBlank(parentResultId))
			{
				// find index of parent based on matching placer order number (child obr.29 matches parent obr.2)
				Integer parentObr = null;
				Integer parentObx = null;
				for(int ii = 0; ii < obrCount; ii++)
				{
					String placerOrderNo = get("/.ORDER_OBSERVATION(" + ii + ")/OBR-2-1");
					if(parentPlacerNo.equals(placerOrderNo))
					{
						parentObr = ii;
						// find the index of the parent result. (child obr.26 matches parent obx.4)
						for(int jj = 0; jj < getOBXCount(ii); jj++)
						{
							String serviceId = get("/.ORDER_OBSERVATION(" + ii + ")/OBSERVATION(" + jj + ")/OBX-4-1");
							if(parentResultId.equals(serviceId))
							{
								parentObx = jj;
								break;
							}
						}
						break;
					}
				}
				if(parentObr != null && parentObx != null)
				{
					// multi-key map, map obr+obx to this obr segment. All 0 indexed to match regular index lookups
					obrParentMap.put(parentObr, parentObx, i);
				}
			}
		}
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
		return formatDateTime(getString(get("/.MSH-7-1")));
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
		String specimenSource = getString(get("/.ORDER_OBSERVATION("+i+")/OBR-15-2"));
		String collectionDate = formatDateTime(get("/.ORDER_OBSERVATION("+i+")/OBR-7-1"));

		return specimenSource + (StringUtils.isNotBlank(collectionDate) ? (" (Collected: " + collectionDate) + ")" : "");
	}

	@Override
	public boolean isOBRUnstructured(int obr)
	{
		return isMicroLabResult(obr) || isBloodBankProductsResult(obr);
	}

	@Override
	public ArrayList<String> getHeaders()
	{
		// order must match obr order if some obr segments are unstructured. for... reasons?
		ArrayList<String> headers = new ArrayList<>();
		for(int i = 0; i < getOBRCount(); i++)
		{
			String obrName = getOBRName(i);
			if(!headers.contains(obrName))
			{
				headers.add(obrName);
			}
		}
		return headers;
	}

	public boolean isChildOBR(int obr)
	{
		return obrParentMap.containsValue(obr);
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
	public String getTimeStamp(int i, int j)
	{
		if (i < 0 || j < 0)
		{
			// some fun peaces of code like to ask for negative values
			return null;
		}
		// rural labs want you to use OBR-14 instead of obx-14
		return formatDateTime(get("/.ORDER_OBSERVATION("+i+")/OBR-14"));
	}

	@Override
	public boolean isOBXAbnormal(int i, int j)
	{
		String abnormalFlags = getOBXAbnormalFlag(i,j);
		return "A".equals(abnormalFlags);
	}


	@Override
	public boolean hasChildOBR(int obr, int obx)
	{
		return obrParentMap.containsKey(obr, obx);
	}

	@Override
	public int getChildOBR(int obr, int obx)
	{
		if(hasChildOBR(obr, obx))
		{
			return obrParentMap.get(obr, obx);
		}
		return -1;
	}

	/* ===================================== private methods etc. ====================================== */

	private String getDiagnosticServicesCode(int obr)
	{
		return get("/.ORDER_OBSERVATION("+obr+")/OBR-24-1");
	}

	private boolean isMicroLabResult(int obr)
	{
		return "MC".equals(getDiagnosticServicesCode(obr));
	}

	private boolean isBloodBankProductsResult(int obr)
	{
		return "BB-BP".equals(getDiagnosticServicesCode(obr));
	}

}
