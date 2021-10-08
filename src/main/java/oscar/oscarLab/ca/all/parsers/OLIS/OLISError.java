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

public class OLISError
{
	private final OLISHL7Handler olishl7Handler;

	public OLISError(OLISHL7Handler olishl7Handler, String segment, String sequence, String field, String indentifer, String text)
	{
		super();
		this.olishl7Handler = olishl7Handler;
		this.segment = segment;
		this.sequence = sequence;
		this.field = field;
		this.indentifer = indentifer;
		this.text = text;
	}

	String segment, sequence, field, indentifer, text;

	public String getSegment()
	{
		return segment;
	}

	public void setSegment(String segment)
	{
		this.segment = segment;
	}

	public String getSequence()
	{
		return sequence;
	}

	public void setSequence(String sequence)
	{
		this.sequence = sequence;
	}

	public String getField()
	{
		return field;
	}

	public void setField(String field)
	{
		this.field = field;
	}

	public String getIndentifer()
	{
		return indentifer;
	}

	public void setIndentifer(String indentifer)
	{
		this.indentifer = indentifer;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + getOuterType().hashCode();
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((indentifer == null) ? 0 : indentifer.hashCode());
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
			return this.indentifer.equals(obj);
		}
		if(getClass() != obj.getClass()) return false;
		OLISError other = (OLISError) obj;
		if(!getOuterType().equals(other.getOuterType())) return false;
		if(field == null)
		{
			if(other.field != null) return false;
		}
		else if(!field.equals(other.field)) return false;
		if(segment == null)
		{
			if(other.segment != null) return false;
		}
		else if(!segment.equals(other.segment)) return false;
		if(sequence == null)
		{
			if(other.sequence != null) return false;
		}
		else if(!sequence.equals(other.sequence)) return false;
		return true;
	}

	private OLISHL7Handler getOuterType()
	{
		return olishl7Handler;
	}

}
