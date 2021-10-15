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
package oscar.util.Jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.dataMigration.model.common.PartialDate;

import java.io.IOException;

// Deserialize dates in format yyyy-MM-dd, yyyy-MM, or yyyy. will ignore info after first 10 digits
public class PartialDateDeserializer extends StdDeserializer<PartialDate>
{
	public PartialDateDeserializer()
	{
		this(null);
	}

	public PartialDateDeserializer(Class<PartialDate> pd)
	{
		super(pd);
	}

	@Override
	public PartialDate deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException
	{
		String jsonDate = StringUtils.trimToNull(jsonparser.getText());
		if(jsonDate != null && jsonDate.length() > 10)
		{
			jsonDate = jsonDate.substring(0, 10); // only take 10 digits, should be formatted yyyy-MM-dd
		}
		return PartialDate.parseDate(jsonDate);
	}
}