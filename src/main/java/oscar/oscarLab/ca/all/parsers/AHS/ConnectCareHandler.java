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
package oscar.oscarLab.ca.all.parsers.AHS;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.datatype.CX;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.oscarehr.common.hl7.AHS.model.v23.message.ORU_R01;
import org.oscarehr.common.model.Hl7TextInfo;
import oscar.oscarLab.ca.all.parsers.MessageHandler;
import oscar.oscarLab.ca.all.parsers.messageTypes.ORU_R01MessageHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ConnectCareHandler extends ORU_R01MessageHandler
{
	/**
	 * map connect care status codes to internal status codes as per, IMG OUT – Status Table [3101] in
	 * "HL7 Message Processing Guidelines Appendix.pdf"
	 */
	private static final Map<String, Hl7TextInfo.REPORT_STATUS> orderStatusMap = new HashMap<String, Hl7TextInfo.REPORT_STATUS>();

	static
	{
		orderStatusMap.put("P", Hl7TextInfo.REPORT_STATUS.E);
		orderStatusMap.put("PA", Hl7TextInfo.REPORT_STATUS.E);
		orderStatusMap.put("A", Hl7TextInfo.REPORT_STATUS.F);
		orderStatusMap.put("F", Hl7TextInfo.REPORT_STATUS.F);
		orderStatusMap.put("C", Hl7TextInfo.REPORT_STATUS.C);
		orderStatusMap.put("CA", Hl7TextInfo.REPORT_STATUS.X);
	}

	/**
	 * Check if a given message handler is a connect care message handler
	 *
	 * @param handler
	 * @return - true if handler is connect care handler
	 */
	public static boolean isConnectCareHandler(MessageHandler handler)
	{
		String handlerType = handler.getMsgType();
		return getConnectCareLabTypes().contains(handlerType);
	}

	/**
	 * Get a list of connect care lab types
	 * @return - the list of connect care lab types
	 */
	public static List<String> getConnectCareLabTypes()
	{
		return Arrays.asList("CCLAB", "CCIMAGING", "CCCARDIOLOGY", "CCENDO", "CCDOC");
	}

	public ConnectCareHandler(Message msg) throws HL7Exception
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
	public void postUpload()
	{
	}

	@Override
	public void init(String hl7Body) throws HL7Exception
	{
	}

	/**
	 * Connect Care labs send more than just health card number, they can also send, EPI, ABH, NWT, BKR
	 * @param appendNamespace - if true append namespace to end of identifier
	 * @return - list of paris patient identification &lt;id type , id + assigning authority >
	 */
	public ArrayList<Pair<String, String>> getPatientIdentificationList(boolean appendNamespace)
	{
		ArrayList<Pair<String, String>> ids = new ArrayList<Pair<String, String>>();
		if (message instanceof ORU_R01)
		{
			ORU_R01 msg = (ORU_R01) message;
			for (int i =0; i < msg.getPID().getPatientIDInternalIDReps(); i ++)
			{
				CX id = msg.getPID().getPatientIDInternalID(i);
				if (id.getAssigningAuthority().getNamespaceID().getValue() != null && id.getID().getValue() != null &&
					id.getIdentifierTypeCode().getValue() != null)
				{
					String idString = id.getID().getValue();
					if (appendNamespace)
					{
						idString += " " + id.getAssigningAuthority().getNamespaceID().getValue();
					}
					ids.add(Pair.of(id.getIdentifierTypeCode().getValue(), idString));
				}
			}
			return ids;
		}
		else
		{
			return new ArrayList<Pair<String, String>>();
		}
	}

	/**
	 * pull patient health number from the patient identifier list
	 * @return - the patients health number
	 */
	@Override
	public String getHealthNum()
	{
		ArrayList<Pair<String, String>> patientIds = getPatientIdentificationList(false);
		for (Pair<String, String> id : patientIds)
		{
			if (id.getLeft().equalsIgnoreCase("PHN") || id.getLeft().equalsIgnoreCase("ULI"))
			{
				return id.getRight();
			}
		}
		return null;
	}

	@Override
	public String getHealthNumProvince()
	{
		return "ABH";
	}

	/**
	 *  call getPatientIdentificationList default behavior of appending namespace
	 * @return - list of paris patient identification &lt;id type , id + assigning authority >
	 */
	public ArrayList<Pair<String, String>> getPatientIdentificationList()
	{
		return getPatientIdentificationList(true);
	}

	@Override
	public String getMsgType()
	{
		return "AHS";
	}

	/* ===================================== OBR ====================================== */

	@Override
	public String getUniqueIdentifier()
	{
		return get("/.ORDER_OBSERVATION/OBR-3-1");
	}

	@Override
	public String getUniqueVersionIdentifier()
	{
		return get("/.ORDER_OBSERVATION/ORC-3-1");
	}

	@Override
	public String getNteForPID() {
		return get("/.NTE-3");
	}

	@Override
	public String getPatientLocation()
	{
		return getAssignedPatientLocation();
	}

	@Override
	public List<String> getDocNums()
	{
		List<String> docNums = new ArrayList<>();
		try
		{
			String providerId = getOrderingProviderNo(0, 0);
			docNums.add(providerId);

			int obr = 0;
			int subCount = getReps("ORDER_OBSERVATION", obr, "OBR-28");
			for(int k = 0; k < subCount; k++)
			{
				docNums.add(getString(get("/.ORDER_OBSERVATION(" + obr + ")/OBR-28(" + k + ")-1")));
			}

			// add pv1 provider to cc docs
			String pv1ProviderNo = getString(get("/.PD1-4"));
			if(StringUtils.isNotBlank(pv1ProviderNo))
			{
				docNums.add(pv1ProviderNo);
			}

		}
		catch(Exception e)
		{
			logger.error("Could not return doctor nums", e);
		}
		return docNums;
	}

	@Override
	public String getCCDocs()
	{
		return String.join(", ", getCCDocNames());
	}

	protected List<String> getCCDocNames()
	{
		List<String> docNames = new ArrayList<>();
		int obr = 0;
		int subCount = getReps("ORDER_OBSERVATION", obr, "OBR-28");
		for(int k = 0; k < subCount; k++)
		{
//			docNames.add(getFullDocName("ORDER_OBSERVATION", obr, "OBR-28", k, ""));
		}

		// add pv1 provider to cc docs
		String pv1ProviderNo = getString(get("/.PD1-4"));
		if(StringUtils.isNotBlank(pv1ProviderNo))
		{
			String familyName = getString(get("/.PD1-4-2"));
			String givenName = getString(get("/.PD1-4-3"));
			String middleName = getString(get("/.PD1-4-4"));
			String suffix = getString(get("/.PD1-4-5"));
			String prefix = getString(get("/.PD1-4-6"));
			String degree = getString(get("/.PD1-4-7"));

			String fullName = String.join(" ", prefix, givenName, middleName, familyName, suffix, degree).trim().replaceAll("\\s+", " ");
			docNames.add(fullName);
		}
		return docNames;
	}

	/**
	 * invalid for connect care as one result may contain multiple cc providers in multiple fields
	 * rely on getCCDocs list results instead
	 */
	@Override
	protected String getResultCopiesTo(int i, int k)
	{
		throw new IllegalStateException("Not valid for ConnectCare");
	}

	/**
	 * invalid for connect care as one result may contain multiple cc providers in multiple fields
	 * rely on getDocNums list results instead
	 */
	@Override
	protected String getResultCopiesToProviderNo(int i, int k)
	{
		throw new IllegalStateException("Not valid for ConnectCare");
	}

	/* ===================================== OBX ====================================== */

	@Override
	public String getNteForOBX(int i, int j)
	{
		return get("/.ORDER_OBSERVATION("+ i +")/OBSERVATION("+ j +")/NTE-3");
	}

	/**
	 *  Retrieve the abnormal flag if any from the OBX segment specified by j in
	 *  the ith OBR group.
	 */
	@Override
	public String getOBXAbnormalFlag( int i, int j)
	{
		String ab = StringUtils.trimToNull(getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-8")));
		if (ab == null)
		{ // no flag == normal result
			ab = "N";
		}
		return ab;
	}

	// connect care labs support embedded PDFs
	@Override
	public boolean supportsEmbeddedDocuments()
	{
		return true;
	}

	/**
	 * map OBR order status to juno internal order status
	 * @return - juno internal report status
	 */
	@Override
	public Hl7TextInfo.REPORT_STATUS getJunoOrderStatus()
	{
		return orderStatusMap.get(getOrderStatus());
	}
}
