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
package org.oscarehr.common.hl7.AHS.model.v23.message;

import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;

/**
 * A Custom HL7 segment sent by AHS labs for lab cancellation messages.
 * Is basically the same as ORU_R01 except for ORC segment use.
 */
public class ORM_002 extends ORU_R01
{
	// the CustomModelClassFactory requires the root package for the message as a string. exclude the version and sub-folders
	public static final String ROOT_PACKAGE = "org.oscarehr.common.hl7.AHS.model";

	public ORM_002()
	{
		this(new DefaultModelClassFactory());
	}
	public ORM_002(ModelClassFactory theFactory)
	{
		super(theFactory);
	}
}
