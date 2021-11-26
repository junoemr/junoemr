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
package oscar.oscarLab.ca.all.parsers.OLIS;

import lombok.Data;

@Data
public class OLISError
{
	private final OLISHL7Handler olishl7Handler;

	private String segment, sequence, field, identifier, text;

	public OLISError(OLISHL7Handler olishl7Handler, String segment, String sequence, String field, String identifier, String text)
	{
		super();
		this.olishl7Handler = olishl7Handler;
		this.segment = segment;
		this.sequence = sequence;
		this.field = field;
		this.identifier = identifier;
		this.text = text;
	}

	public String userFriendlyToString()
	{
		String message = getText();

		message = message.replaceAll("'@ZRP\\.1\\.1'", "Requesting Physician CPSID");
		message = message.replaceAll("'@ZRP\\.1\\.13'", "Requesting Physician Id Type");
		message = message.replaceAll("'@ZRP\\.1\\.2'", "Requesting Physician First Name");
		message = message.replaceAll("'@ZRP\\.1\\.3'", "Requesting Physician Last Name");
		message = message.replaceAll("'@ZRP\\.1\\.4'", "Requesting Physician Middle Name");
		message = message.replaceAll("'@PID\\.3\\.1'", "Patient Health Number");
		message = message.replaceAll("'@PID\\.7'", "Patient Date of Birth");
		message = message.replaceAll("'@PID\\.8'", "Patient Sex");
		message = message.replaceAll("'@OBR\\.16\\.1'", "Ordering Practitioner CPSID");
		message = message.replaceAll("'@OBR\\.16\\.13'", "Ordering Practitioner Id Type");
		message = message.replaceAll("'@OBR\\.28\\.1'", "Copied To Practitioner CPSID");
		message = message.replaceAll("'@OBR\\.28\\.13'", "Copied To Practitioner Id Type");
		message = message.replaceAll("'@PV1\\.7\\.1'", "Attending Practitioner CPSID");
		message = message.replaceAll("'@PV1\\.7\\.13'", "Attending Practitioner Id Type");
		message = message.replaceAll("'@PV1\\.17\\.1'", "Admitting Practitioner CPSID");
		message = message.replaceAll("'@PV1\\.17\\.13'", "Admitting Practitioner Id Type");

		return "Error " + identifier + ": " + message;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + getOuterType().hashCode();
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((segment == null) ? 0 : segment.hashCode());
		result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
		return result;
	}

	/**
	 * OLIS Errors are identified by the error code for global errors or the segment, sequence and field of the error for localised errors.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj) return true;
		if(obj == null) return false;
		if(obj instanceof String)
		{
			return this.identifier.equals(obj);
		}
		if(getClass() != obj.getClass())
		{
			return false;
		}
		OLISError other = (OLISError) obj;
		if(!getOuterType().equals(other.getOuterType()))
		{
			return false;
		}
		if(field == null)
		{
			if(other.field != null)
			{
				return false;
			}
		}
		else if(!field.equals(other.field))
		{
			return false;
		}
		if(segment == null)
		{
			if(other.segment != null)
			{
				return false;
			}
		}
		else if(!segment.equals(other.segment))
		{
			return false;
		}
		if(sequence == null)
		{
			return other.sequence == null;
		}
		else
		{
			return sequence.equals(other.sequence);
		}
	}

	private OLISHL7Handler getOuterType()
	{
		return olishl7Handler;
	}
}
