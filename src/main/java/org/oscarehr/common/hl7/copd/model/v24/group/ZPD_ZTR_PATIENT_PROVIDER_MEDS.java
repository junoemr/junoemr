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
package org.oscarehr.common.hl7.copd.model.v24.group;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractGroup;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.v24.segment.ORC;
import ca.uhn.hl7v2.model.v24.segment.RXE;
import ca.uhn.hl7v2.model.v24.segment.RXO;
import ca.uhn.hl7v2.model.v24.segment.RXR;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZRX;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZST;

/**
 * Custom hl7 message class for TOPD formatted messages
 */
public class ZPD_ZTR_PATIENT_PROVIDER_MEDS extends AbstractGroup
{
	public ZPD_ZTR_PATIENT_PROVIDER_MEDS(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		try
		{
			this.add(ORC.class, true, false);
			this.add(RXO.class, true, false);
			this.add(ZPD_ZTR_PATIENT_PROVIDER_MEDS_TIMING_QUANTITY.class, false, false);
			this.add(ZPD_ZTR_PATIENT_PROVIDER_MEDS_NOTES.class, false, false);
			this.add(RXE.class, true, false);
			this.add(RXR.class, true, false);
			this.add(ZPD_ZTR_PATIENT_PROVIDER_MEDS_COMPONENT.class, false, false);
			this.add(ZPD_ZTR_PATIENT_PROVIDER_MEDS_OBSERVATION.class, false, false);
			this.add(ZRX.class, true, false);
			this.add(ZST.class, false, true);
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}
}
