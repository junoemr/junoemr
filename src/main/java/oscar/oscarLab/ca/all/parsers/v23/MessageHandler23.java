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

package oscar.oscarLab.ca.all.parsers.v23;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v23.group.ORU_R01_PATIENT;
import ca.uhn.hl7v2.model.v23.group.ORU_R01_RESPONSE;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import org.apache.log4j.Logger;
import oscar.oscarLab.ca.all.parsers.MessageHandler;

public abstract class MessageHandler23 extends MessageHandler
{
	private static Logger logger = Logger.getLogger(MessageHandler23.class);

	protected ORU_R01 msg;
	protected ORU_R01_PATIENT patient;
	protected ORU_R01_RESPONSE response;

	public MessageHandler23()
	{
		super();
	}
	public MessageHandler23(String hl7Body) throws HL7Exception
	{
		Parser p = new PipeParser();
		p.setValidationContext(new NoValidation());
		this.msg = (ORU_R01) p.parse(hl7Body);
		this.terser = new Terser(msg);
		init(hl7Body);
	}
	public MessageHandler23(ORU_R01 msg) throws HL7Exception
	{
		this.msg = msg;
		this.terser = new Terser(msg);
		init(null);
	}
	@Override
	public void init(String hl7Body) throws HL7Exception
	{
		response = msg.getRESPONSE();
		patient = msg.getRESPONSE().getPATIENT();
	}

	/* ===================================== OBR ====================================== */

	/**
	 *  Return the number of OBR Segments in the message
	 */
	@Override
	public int getOBRCount() {
		return (response.getORDER_OBSERVATIONReps());
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
			return (formatDateTime(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBR().getObservationDateTime().getTimeOfAnEvent().getValue())));
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
			return (msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATIONReps());
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
			return (getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getIdentifier().getValue()));
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
		    return (getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getValueType().getValue()));
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
			return (getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getText().getValue()));
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
			return (getString(Terser.get(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX(), 5, 0, 1, 1)));
		}
		catch(Exception e)
		{
			return ("");
		}
	}

	@Override
	public boolean isUnstructured() {
		return false;
	}
}
