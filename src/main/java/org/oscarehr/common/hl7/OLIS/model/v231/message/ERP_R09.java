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
package org.oscarehr.common.hl7.OLIS.model.v231.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.v231.segment.DSC;
import ca.uhn.hl7v2.model.v231.segment.ERQ;
import ca.uhn.hl7v2.model.v231.segment.ERR;
import ca.uhn.hl7v2.model.v231.segment.MSA;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.QAK;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.common.hl7.OLIS.model.v231.group.ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI;

import java.util.List;

/**
 * Custom message definition for OLIS labs. Almost all fields are optional since there many variations,
 * and we only really keep the ERP stuff in the first lab in a download batch.
 */
public class ERP_R09 extends AbstractMessage
{
	// the CustomModelClassFactory requires the root package for the message as a string. exclude the version and sub-folders
	public static final String ROOT_PACKAGE = "org.oscarehr.common.hl7.OLIS.model";

	public ERP_R09()
	{
		this(new DefaultModelClassFactory());
	}
	public ERP_R09(ModelClassFactory theFactory)
	{
		super(theFactory);
		try
		{
			this.add(MSH.class, true, false);
			this.add(MSA.class, false, false);
			this.add(ERR.class, false, false);
			this.add(QAK.class, false, false);
			this.add(ERQ.class, false, false);
			this.add(ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI.class, false, true);
			this.add(DSC.class, false, false);
		}
		catch (HL7Exception e)
		{
			log.error("Unexpected error creating Custom ERP_R09 - this is probably a bug in the source code generator.", e);
		}
	}

	@Override
	public String getVersion()
	{
		return "2.3.1";
	}

	public MSH getMSH()
	{
		return this.getTyped("MSH", MSH.class);
	}

	public MSA getMSA()
	{
		return this.getTyped("MSA", MSA.class);
	}

	public ERR getERR()
	{
		return this.getTyped("ERR", ERR.class);
	}

	public QAK getQAK()
	{
		return this.getTyped("QAK", QAK.class);
	}

	public ERQ getERQ()
	{
		return this.getTyped("ERQ", ERQ.class);
	}

	public ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI getPIDPD1NK1NTEPV1PV2ORCOBRNTEOBXNTECTI()
	{
		return this.getTyped("ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI", ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI.class);
	}

	public ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI getPIDPD1NK1NTEPV1PV2ORCOBRNTEOBXNTECTI(int rep)
	{
		return this.getTyped("ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI", rep, ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI.class);
	}

	public int getORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTIReps()
	{
		return this.getReps("ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI");
	}

	public List<ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI> getORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTIAll() throws HL7Exception
	{
		return this.getAllAsList("ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI", ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI.class);
	}

	public DSC getDSC()
	{
		return (DSC) this.getTyped("DSC", DSC.class);
	}
}
