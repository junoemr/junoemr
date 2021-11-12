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
package org.oscarehr.common.hl7.OLIS.model.v231.group;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractGroup;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.v231.segment.BLG;
import ca.uhn.hl7v2.model.v231.segment.DG1;
import ca.uhn.hl7v2.model.v231.segment.OBR;
import ca.uhn.hl7v2.model.v231.segment.ORC;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.common.hl7.OLIS.model.v231.segment.ZBR;

import java.util.List;

public class ERP_R09_ORDER_OBSERVATION extends AbstractGroup
{
	public ERP_R09_ORDER_OBSERVATION(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		this.init(factory);
	}

	private void init(ModelClassFactory factory)
	{
		try
		{
			this.add(ORC.class, true, false, false);
			this.add(OBR.class, true, false, false);
			this.add(ZBR.class, true, false, false);
			this.add(ERP_R09_NOTE.class, false, true, false);
			this.add(DG1.class, false, true, false);
			this.add(ERP_R09_OBSERVATION.class, true, true, false);
			this.add(BLG.class, true, false, false);
		}
		catch(HL7Exception e)
		{
			log.error("Unexpected error creating ERP_R09_ORDER_OBSERVATION - this is probably a bug in the source code generator.", e);
		}
	}

	public ORC getORC()
	{
		return this.getTyped("ORC", ORC.class);
	}

	public OBR getOBR()
	{
		return this.getTyped("OBR", OBR.class);
	}

	public ZBR getZBR()
	{
		return this.getTyped("ZBR", ZBR.class);
	}

	public DG1 getDG1()
	{
		return this.getTyped("DG1", DG1.class);
	}

	public ERP_R09_NOTE getNOTE(int rep)
	{
		return this.getTyped("NOTE", rep, ERP_R09_NOTE.class);
	}
	public int getNOTEReps()
	{
		return this.getReps("NOTE");
	}
	public List<ERP_R09_NOTE> getNOTEAll() throws HL7Exception
	{
		return this.getAllAsList("NOTE", ERP_R09_NOTE.class);
	}

	public ERP_R09_OBSERVATION getOBSERVATION(int rep)
	{
		return this.getTyped("OBSERVATION", rep, ERP_R09_OBSERVATION.class);
	}
	public int getOBSERVATIONReps()
	{
		return this.getReps("OBSERVATION");
	}
	public List<ERP_R09_OBSERVATION> getOBSERVATIONAll() throws HL7Exception
	{
		return this.getAllAsList("OBSERVATION", ERP_R09_OBSERVATION.class);
	}

	public BLG getBLG()
	{
		return this.getTyped("BLG", BLG.class);
	}
}
