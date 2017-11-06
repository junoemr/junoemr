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

package oscar.oscarLab.ca.all.parsers.v22;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v22.group.ORU_R01_PATIENT;
import ca.uhn.hl7v2.model.v22.group.ORU_R01_PATIENT_RESULT;
import ca.uhn.hl7v2.model.v22.message.ORU_R01;
import ca.uhn.hl7v2.model.v22.datatype.FT;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import org.apache.log4j.Logger;
import oscar.oscarLab.ca.all.parsers.MessageHandler;

import java.util.ArrayList;

public abstract class MessageHandler22 extends MessageHandler
{
	private static Logger logger = Logger.getLogger(MessageHandler22.class);

	protected ORU_R01 msg;
	protected ORU_R01_PATIENT patient;
	protected ORU_R01_PATIENT_RESULT response;

	public MessageHandler22()
	{
		super();
	}
	public MessageHandler22(String hl7Body) throws HL7Exception
	{
		Parser p = new PipeParser();
		p.setValidationContext(new NoValidation());
		this.msg = (ORU_R01) p.parse(hl7Body);
		this.terser = new Terser(msg);
		init(hl7Body);
	}
	public MessageHandler22(ORU_R01 msg) throws HL7Exception
	{
		this.msg = msg;
		this.terser = new Terser(msg);
		init(null);
	}

	@Override
	public void init(String hl7Body) throws HL7Exception
	{
		response = msg.getPATIENT_RESULT();
		patient = msg.getPATIENT_RESULT().getPATIENT();
	}
	/* ===================================== OBR ====================================== */

	/**
	 *  Return the number of OBR Segments in the message
	 */
	@Override
	public int getOBRCount() {
		return (response.getORDER_OBSERVATIONReps());
	}

	/**
	 *  Methods to get information from observation notes
	 */
	@Override
	public int getOBRCommentCount(int i) {
		// OBR comments are not provided - comments are provided in NTE segment following OBX segment
		int count = 0;
		try {
			count = response.getORDER_OBSERVATION(i).getNTEReps();
		} catch(Exception e) {
			return 0;
		}

		return count;
	}

	@Override
	public String getOBRComment(int i, int j) {
		try {
			FT[] tmp = response.getORDER_OBSERVATION(i).getNTE(j).getComment();
			StringBuilder comment = new StringBuilder();
			for(FT t:tmp) {
				comment.append(t.getValue());
			}
			return comment.toString();
		} catch (Exception e) {
			return ("");
		}
	}

	/**
	 *  Retrieve the possible segment headers from the OBR fields
	 */
	@Override
	public ArrayList<String> getHeaders() {
		ArrayList<String> headers = new ArrayList<String>();
		for (int i = 0; i < getOBRCount(); i++) {
			String obrName = getOBRName(i);
			if (!headers.contains(obrName)) {
				headers.add(obrName);
			}
		}
		return headers;
	}

	/* ===================================== OBX ====================================== */

	/**
	 *  Return the date and time of the observation refered to by the jth obx
	 *  segment of the ith obr group. If the date and time is not specified
	 *  within the obx segment it should be specified within the obr segment.
	 */
	@Override
	public String getTimeStamp(int i, int j) {
		try {
			return (formatDateTime(getString(response.getORDER_OBSERVATION(i).getOBR().getObservationDateTime().getTimeOfAnEvent().getValue())));
		} catch (Exception e) {
			return ("");
		}
	}

	/**
	 *  Return the number of OBX Segments within the OBR group specified by i.
	 */
	@Override
	public int getOBXCount(int i)
	{
		try
		{
			return (response.getORDER_OBSERVATION(i).getOBSERVATIONReps());
		}
		catch(Exception e)
		{
			return (0);
		}
	}

	/**
	 *  Return the identifier from jth OBX segment of the ith OBR group. It is
	 *  usually stored in the first component of the third field of the OBX
	 *  segment.
	 */
	@Override
	public String getOBXIdentifier(int i, int j)
	{
		try
		{
			return (getString(response.getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getIdentifier().getValue()));
		}
		catch(Exception e)
		{
			return ("");
		}
	}

	/**
	 * Return the obx value type
	 * @param i
	 * @param j
	 * @return String the obx value
	 */
	@Override
	public String getOBXValueType(int i, int j)
	{
		try
		{
			return (getString(response.getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getValueType().getValue()));
		}
		catch(Exception e)
		{
			return ("");
		}
	}

	/**
	 *  Return the name of the jth OBX segment of the ith OBR group. It is
	 *  usually stored in the second component of the third field of the OBX
	 *  segment.
	 */
	@Override
	public String getOBXName( int i, int j)
	{
		try
		{
			return (getString(response.getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getText().getValue()));
		}
		catch(Exception e)
		{
			return ("");
		}
	}

	/**
	 *  Return the result from the jth OBX segment of the ith OBR group
	 */
	@Override
	public String getOBXResult(int i, int j)
	{
		try
		{
			return (getString(Terser.get(response.getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX(), 5, 0, 1, 1)));
		}
		catch(Exception e)
		{
			return ("");
		}
	}

	@Override
	public String getOBXReferenceRange(int i, int j) {
		try {
			return (getString(response.getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getReferencesRange().getValue()));
		} catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getOBXUnits(int i, int j) {
		try {
			return (getString(response.getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getUnits().getIdentifier().getValue()));
		} catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getOBXResultStatus(int i, int j) {
		String status = "";
		try {
			status = getString(response.getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationResultStatus().getValue());
			if (status.equalsIgnoreCase("C")) {
				status = "Corrected";
			} else if (status.equalsIgnoreCase("F")) {
				status = "Final";
			} else if (status.equalsIgnoreCase("P")) {
				status = "Preliminary";
			}
			// TODO find out about "Cancelled" status
		} catch (Exception e) {
			logger.error("Error retrieving obx result status", e);
			return status;
		}
		return status;
	}

	@Override
	public int getOBXFinalResultCount() {
		int obrCount = getOBRCount();
		int obxCount;
		int count = 0;
		for (int i = 0; i < obrCount; i++) {
			obxCount = getOBXCount(i);
			for (int j = 0; j < obxCount; j++) {
				if (getOBXResultStatus(i, j).equals("Final") ||
						getOBXResultStatus(i, j).equals("Corrected"))
				{
					count++;
				}
			}
		}
		return count;
	}

	@Override
	public boolean isOBXAbnormal(int i, int j) {
		try {
			return getOBXAbnormalFlag(i, j).equals("C") || getOBXAbnormalFlag(i, j).equals("H")
					|| getOBXAbnormalFlag(i, j).equals("L") || getOBXAbnormalFlag(i, j).equals("A");
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String getOBXAbnormalFlag(int i, int j) {
		try {
			return (getString(response.getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getAbnormalFlags(0).getValue()));
		} catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getObservationHeader(int i, int j) {
		return getOBRName(i);
	}

	/**
	 *  Methods to get information from observation notes
	 */
	@Override
	public int getOBXCommentCount(int i, int j) {
		int count = 0;
		try {
			count = response.getORDER_OBSERVATION(i).getOBSERVATION(j).getNTEReps();

			// a bug in getNTEReps() causes it to return 1 instead of 0 so we check to make
			// sure there actually is a comment there
			if (count == 1) {
				String comment = response.getORDER_OBSERVATION(i).getOBSERVATION(j).getNTE().getComment(0).getValue();
				if (comment == null) count = 0;
			}

		} catch (Exception e) {
			logger.error("Error retrieving obx comment count", e);
		}
		return count;
	}

	@Override
	public String getOBXComment(int i, int j, int k) {
		try {
			return (getString(response.getORDER_OBSERVATION(i).getOBSERVATION(j).getNTE(k).getComment(0).getValue()));
		} catch (Exception e) {
			return ("");
		}
	}
	/* ===================================== MISC ====================================== */

	@Override
	public String getServiceDate()
	{
		return "";
	}

	@Override
	public String getRequestDate(int i)
	{
		return "";
	}

	@Override
	public String getOrderStatus()
	{
		return "";
	}

	@Override
	public String getClientRef()
	{
		return "";
	}

	@Override
	public String getAccessionNum()
	{
		return "";
	}

	@Override
	public String getDocName()
	{
		return "";
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

	@Override
	public String getFillerOrderNumber()
	{
		return "";
	}

	@Override
	public String getEncounterId()
	{
		return "";
	}

	@Override
	public String getRadiologistInfo()
	{
		return "";
	}

	@Override
	public String getNteForOBX(int i, int j)
	{
		return "";
	}

	@Override
	public String getNteForPID() {
		return "";
	}
	@Override
	public boolean isUnstructured() {
		return true;
	}
}
