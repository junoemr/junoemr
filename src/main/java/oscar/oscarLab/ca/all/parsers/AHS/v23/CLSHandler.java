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
import ca.uhn.hl7v2.model.v23.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.model.v23.segment.NTE;
import ca.uhn.hl7v2.model.v23.segment.OBR;
import ca.uhn.hl7v2.model.v23.segment.OBX;
import ca.uhn.hl7v2.model.v23.segment.ORC;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.dao.Hl7TextMessageDao;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.util.SpringUtils;
import oscar.oscarLab.ca.all.parsers.AHS.AHSHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Dual message handler for both the manual and automated lab uploads in the Calgary Lab Service HL7 format.
 *
 */
public class CLSHandler extends AHSHandler
{
	private static Logger logger = Logger.getLogger(CLSHandler.class);
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

			return "OPEN ENGINE".equalsIgnoreCase(sendingApplication) &&
					"CLS".equalsIgnoreCase(sendingFacility);
		}
		return false;
	}

	public CLSHandler()
	{
		super();
	}
	public CLSHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
		this.msg = (ORU_R01) this.message;
	}
	public CLSHandler(Message msg) throws HL7Exception
	{
		super(msg);
		this.msg = (ORU_R01) this.message;
	}

	/* ===================================== MSH ====================================== */

	public String getMsgType() {
		return "CLS";
	}

	/* ===================================== PID ====================================== */

	public String getHealthNum() {
		return get("/.PID-2-1");
	}

	@Override
	public String getPatientName() {
		return (getFirstName() + " " + getLastName());
	}

	/* ===================================== OBR ====================================== */

	public String getTimeStamp(int i, int j) {
		try {
			return (formatDateTime(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBR().getObservationDateTime().getTimeOfAnEvent().getValue())));
		} catch (Exception e) {
			return ("");
		}
	}

	public String getUniqueVersionIdentifier() {
		// this is different from the filler order number in ORC
		return get("/.OBR-3-1");
	}

	public String getAssigningAuthority() {
		return get("/.PID-2-4");
	}

	/**
	 * Gets the ordering provider name.
	 *
	 * @return
	 * 		Returns the provider name or an empty string if it's not specified
	 */
	public String getOrderingProvider() {
		return get("/.OBR-16-2");
	}

	/**
	 * Gets the ordering provider ID for matching provider with the correct inbox routing.
	 *
	 * @return
	 * 		Returns the provider id or an empty string if it's not specified
	 */
	public String getOrderingProviderId() {
		return get("/.OBR-16-1");
	}

	/**
	 * Gets the date and time the specimen was collected
	 *
	 * @param i
	 * 		Segment count
	 * @return
	 * 		Returns the date / time of the specimen collection or null if it's not available.
	 */
	public String getOBRDateTime(int i) {
		return get("/.OBR-7-1");
	}

	public String getUniqueIdentifier() {
		return get("/.OBR-20");
	}

	public boolean isUnstructured()
	{
		String DATA_TYPE_TEXT = "TX";
		String DATA_TYPE_STRING = "ST";

		Set<String> unstructuredTypes = new HashSet<>();
		unstructuredTypes.add(DATA_TYPE_TEXT);
		unstructuredTypes.add(DATA_TYPE_STRING);

		for (int j = 0; j < this.getOBRCount(); j++)
		{
			for(int k = 0; k < this.getOBXCount(j); k++)
			{
				String obxValueType = getOBXValueType(j, k);
				if (!unstructuredTypes.contains(obxValueType)
						|| !getOBXReferenceRange(j, k).isEmpty()
						|| isAbnormal()
						|| !getOBXUnits(j, k).isEmpty())
				{
					return false;
				}
			}
		}

		return true;
	}


	/* =================================== Lab Uploads ==================================== */

    @Override
    public String preUpload(String hl7Message) throws HL7Exception
    {
	    Hl7TextInfoDao hl7TextInfoDao = (Hl7TextInfoDao)SpringUtils.getBean("hl7TextInfoDao");
		Hl7TextMessageDao hl7TextMessageDao = (Hl7TextMessageDao) SpringUtils.getBean("hl7TextMessageDao");

	    String accessionNumber = this.getUniqueIdentifier();
	    String fillerOrderNumber = this.getUniqueVersionIdentifier();
	    Hl7TextInfo hl7TextInfo = hl7TextInfoDao.findLatestVersionByAccessionNumberOrFillerNumber(
			    accessionNumber, fillerOrderNumber);

	    // Glucose labs come back with different accession numbers, but the same filler number.
	    // We are going to replace any successive accession numbers with the originals as
	    // suggested in the CLS conformance documentation
	    if(hl7TextInfo != null && hl7TextInfo.getUniqueVersionIdentifier().equals(fillerOrderNumber) &&
			    !hl7TextInfo.getUniqueIdentifier().equals(accessionNumber)) {

		    hl7Message = this.ReplaceAccessionNumber(hl7Message, accessionNumber, hl7TextInfo.getUniqueIdentifier());
	    }

	    if(hl7TextInfo != null) {
	    	String labType = hl7TextMessageDao.getHl7TextMessageType(hl7TextInfo.getLabNumber());
	    	if (labType.equals(getMsgType()))
			{// merge only if lab is of same type.
				String lastVersionLab = oscar.oscarLab.ca.all.parsers.Factory.getHL7Body(Integer.toString(hl7TextInfo.getLabNumber()));
				hl7Message = mergeLabs(lastVersionLab, hl7Message);
			}
	    }
	    return hl7Message;
    }
    @Override
    public boolean canUpload()
    {
    	return true;
    }
	@Override
	public void postUpload() {}

	private String mergeLabs(String oldVersion, String newVersion) throws HL7Exception
	{
		String lineDelimiter = "\r";
		String outLabString = newVersion;
		StringBuilder test = new StringBuilder(newVersion);

		CLSHandler oldVersionCLSParser = new CLSHandler(oldVersion);
		CLSHandler newVersionCLSParser = new CLSHandler(newVersion);

		int currentObrCount = newVersionCLSParser.getOBRCount();

		// Get all OBRs from the old version that don't exist in the new version
		// and append them to the current version
		ArrayList<OBR> oldObrs = this.getObrs(oldVersionCLSParser);
		int obrIndex = 0;
		for(OBR oldObr : oldObrs)
		{
			String fillerNumber = this.getObrFillerNumber(oldObr);
			if(!this.obrExists(fillerNumber, newVersionCLSParser))
			{
				currentObrCount++;
				// Remove the old OBR index so we can add the new one
				String tempObr = oldObr.encode();
				tempObr = tempObr.substring(tempObr.indexOf('|') + 1);
				tempObr = tempObr.substring(tempObr.indexOf('|') + 1);

				// Set the OBR index
				outLabString += lineDelimiter + "OBR|" + Integer.toString(currentObrCount) + "|" + tempObr;

				// Get OBR NTE records
				int obrNteCount = oldVersionCLSParser.getOBRCommentCount(obrIndex);
				for(int obrNteIndex = 0; obrNteIndex < obrNteCount; obrNteIndex++) {
					outLabString += lineDelimiter + oldVersionCLSParser.getOBRNTE(obrIndex, obrNteIndex).encode();
				}

				// Get Previous version OBX records
				int obxCount = oldVersionCLSParser.getOBXCount(obrIndex);
				for(int obxIndex = 0; obxIndex < obxCount; obxIndex++) {
					outLabString += lineDelimiter + oldVersionCLSParser.getOBX(obrIndex, obxIndex).encode();

					// Get Previous version OBX NTE records
					int nteCount = oldVersionCLSParser.getOBXCommentCount(obrIndex, obxIndex);
					for(int nteIndex = 0; nteIndex < nteCount; nteIndex++) {
						outLabString += lineDelimiter + oldVersionCLSParser.getNTE(obrIndex, obxIndex, nteIndex).encode();
					}
				}

				// Get Previous version ORC record if one exists
				ORC orc = oldVersionCLSParser.getORC(obrIndex);
				if(orc != null && orc.encode().length() > 5) {
					test.append(outLabString += lineDelimiter + orc.encode());
				}
			}
			obrIndex++;
		}
		return outLabString;
	}

	private boolean obrExists(String fillerNumber, CLSHandler parser)
			throws HL7Exception
	{
		ArrayList<OBR> searchObrs = this.getObrs(parser);
		for(OBR searchObr : searchObrs) {
			if(this.getObrFillerNumber(searchObr).equals(fillerNumber)) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<OBR> getObrs( CLSHandler parser)
			throws HL7Exception
	{
		ArrayList<OBR> outOBR = new ArrayList<OBR>();
		for(int obrIndex = 0; obrIndex < parser.getOBRCount(); obrIndex++) {
			OBR newObr = parser.getOBR(obrIndex).getOBR();
			outOBR.add(newObr);
		}
		return outOBR;
	}

	private String getObrFillerNumber(OBR obr)
	{
		return obr.getFillerOrderNumber().getEi1_EntityIdentifier().getValue() + "^" +
				obr.getFillerOrderNumber().getEi2_NamespaceID().getValue();
	}

	private String ReplaceAccessionNumber(String message, String oldAccessionNumber, String newAccessionNumber)
	{
		message = message.replace(oldAccessionNumber, newAccessionNumber);
		return message;
	}

	public ORU_R01_ORDER_OBSERVATION getOBR(int i) throws HL7Exception {
		return msg.getRESPONSE().getORDER_OBSERVATION(i);
	}

	public ORC getORC(int i) throws HL7Exception {
		return msg.getRESPONSE().getORDER_OBSERVATION(i).getORC();
	}

	public OBX getOBX(int i, int ii) throws HL7Exception {
		return msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(ii).getOBX();
	}

	public NTE getNTE(int i, int j, int k) throws HL7Exception {
		return msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getNTE(k);
	}

	public NTE getOBRNTE(int i, int j) throws HL7Exception {
		return msg.getRESPONSE().getORDER_OBSERVATION(i).getNTE(j);
	}
}
