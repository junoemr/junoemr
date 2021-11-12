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
import ca.uhn.hl7v2.model.v231.segment.PID;
import ca.uhn.hl7v2.model.v231.segment.PV1;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.common.hl7.OLIS.model.v231.segment.ZPD;

import java.util.List;

public class ERP_R09_PATIENT extends AbstractGroup
{
	public ERP_R09_PATIENT(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		this.init(factory);
	}

	private void init(ModelClassFactory factory)
	{
		try
		{
			this.add(PID.class, true, false, false);
			this.add(ZPD.class, false, false, false);
			this.add(ERP_R09_NOTE.class, false, true, false);
			this.add(PV1.class, true, false, false);
		}
		catch(HL7Exception e)
		{
			log.error("Unexpected error creating ERP_R09_PATIENT - this is probably a bug in the source code generator.", e);
		}
	}

	public PID getPID()
	{
		return this.getTyped("PID", PID.class);
	}

	public ZPD getZPD()
	{
		return this.getTyped("ZPD", ZPD.class);
	}

	public PV1 getPV1()
	{
		return this.getTyped("PV1", PV1.class);
	}

	public ERP_R09_NOTE getNOTE(int rep)
	{
		return this.getTyped("NOTE", rep, ERP_R09_NOTE.class);
	}
	public int getNOTEReps()
	{
		return this.getReps("ZNT");
	}
	public List<ERP_R09_NOTE> getNOTEAll() throws HL7Exception
	{
		return this.getAllAsList("NOTE", ERP_R09_NOTE.class);
	}
}
