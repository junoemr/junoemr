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
package org.oscarehr.common.hl7.OLIS.model.v231.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v231.datatype.HD;
import ca.uhn.hl7v2.model.v231.datatype.ST;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.util.MiscUtils;

/**
 * 10.2.5.3 ZPD – PID Extension Segment
 */
public class ZPD extends AbstractSegment
{
	public ZPD(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(ST.class, false, 1,20, new Object[]{message}, "Patient Consent Indicator");
			this.add(ST.class, false, 1,1, new Object[]{message}, "Patient Identification Verified Flag");
			this.add(ST.class, false, 1,1, new Object[]{message}, "Patient Consent Block-All Indicator");
		}
		catch (HL7Exception var)
		{
			MiscUtils.getLogger().error("Can't instantiate " + this.getClass().getName(), var);
		}
	}

	public HD getZpd1_patientConsentIndicator() throws HL7Exception
	{
		return (HD) this.getField(1, 0);
	}
	public HD getZpd2_patientIdentificationVerifiedFlag() throws HL7Exception
	{
		return (HD) this.getField(2, 0);
	}
	public HD getZpd3_patientConsentBlockAllIndicator() throws HL7Exception
	{
		return (HD) this.getField(3, 0);
	}
}
