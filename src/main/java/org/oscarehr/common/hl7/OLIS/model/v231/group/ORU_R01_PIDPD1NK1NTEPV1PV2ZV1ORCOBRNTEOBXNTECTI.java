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
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.v231.group.ORU_R01_ORCOBRNTEOBXNTECTI;
import ca.uhn.hl7v2.model.v231.group.ORU_R01_PIDPD1NK1NTEPV1PV2;
import ca.uhn.hl7v2.model.v231.group.ORU_R01_PIDPD1NK1NTEPV1PV2ORCOBRNTEOBXNTECTI;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.common.hl7.OLIS.model.v231.segment.ZNT;

public class ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI extends ORU_R01_PIDPD1NK1NTEPV1PV2ORCOBRNTEOBXNTECTI
{
	public ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI(Group parent, ModelClassFactory factory) {
		super(parent, factory);
		this.init(factory);
	}

	private void init(ModelClassFactory factory)
	{
		try
		{
			this.add(ORU_R01_PIDPD1NK1NTEPV1PV2.class, false, false, false);
			this.add(ZNT.class, false, false, false); // custom OLIS segment
			this.add(ORU_R01_ORCOBRNTEOBXNTECTI.class, true, true, false);
		}
		catch(HL7Exception e)
		{
			log.error("Unexpected error creating ORU_R01_PIDPD1NK1NTEPV1PV2ZV1ORCOBRNTEOBXNTECTI - this is probably a bug in the source code generator.", e);
		}
	}
}
