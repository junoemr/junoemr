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
import ca.uhn.hl7v2.model.v22.datatype.CN;
import ca.uhn.hl7v2.model.v22.datatype.FT;
import ca.uhn.hl7v2.model.v22.group.ORU_R01_PATIENT;
import ca.uhn.hl7v2.model.v22.group.ORU_R01_PATIENT_RESULT;
import ca.uhn.hl7v2.model.v22.message.ORU_R01;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import org.apache.log4j.Logger;
import oscar.oscarLab.ca.all.parsers.MessageHandler;

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

	@Override
	protected String getClientRef(int i, int k) throws HL7Exception
	{
		return getString(response.getORDER_OBSERVATION(i).getOBR().getOrderingProvider().getIDNumber().getValue());
	}
	@Override
	protected String getOrderingProvider(int i, int k) throws HL7Exception
	{
		return getFullDocName(response.getORDER_OBSERVATION(i).getOBR().getOrderingProvider());
	}
	@Override
	protected String getResultCopiesTo(int i, int k) throws HL7Exception
	{
		return getFullDocName(response.getORDER_OBSERVATION(i).getOBR().getResultCopiesTo(k));
	}
	@Override
	protected String getOrderingProviderNo(int i, int k) throws HL7Exception
	{
		return response.getORDER_OBSERVATION(i).getOBR().getOrderingProvider().getIDNumber().getValue();
	}
	@Override
	protected String getResultCopiesToProviderNo(int i, int k) throws HL7Exception
	{
		return response.getORDER_OBSERVATION(i).getOBR().getResultCopiesTo(k).getIDNumber().getValue();
	}

	/**
	 *  Return the number of OBR Segments in the message
	 */
	@Override
	public int getOBRCount() {
		return response.getORDER_OBSERVATIONReps();
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

	/* ===================================== OBX ====================================== */

	/**
	 *  Return the number of OBX Segments within the OBR group specified by i.
	 */
	@Override
	public int getOBXCount(int i)
	{
		try
		{
			return response.getORDER_OBSERVATION(i).getOBSERVATIONReps();
		}
		catch(Exception e)
		{
			return (0);
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

	protected String getFullDocName(CN docSeg) {
		String docName = "";

		if (docSeg.getPrefixEgDR().getValue() != null) docName = docSeg.getPrefixEgDR().getValue();

		if (docSeg.getGivenName().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getGivenName().getValue();
			else docName = docName + " " + docSeg.getGivenName().getValue();
		}
		if (docSeg.getMiddleInitialOrName().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getMiddleInitialOrName().getValue();
			else docName = docName + " " + docSeg.getMiddleInitialOrName().getValue();
		}
		if (docSeg.getFamilyName().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getFamilyName().getValue();
			else docName = docName + " " + docSeg.getFamilyName().getValue();
		}
		if (docSeg.getSuffixEgJRorIII().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getSuffixEgJRorIII().getValue();
			else docName = docName + " " + docSeg.getSuffixEgJRorIII().getValue();
		}
		if (docSeg.getDegreeEgMD().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getDegreeEgMD().getValue();
			else docName = docName + " " + docSeg.getDegreeEgMD().getValue();
		}

		return (docName);
	}
}
