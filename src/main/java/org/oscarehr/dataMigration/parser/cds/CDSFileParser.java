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
package org.oscarehr.dataMigration.parser.cds;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.oscarehr.dataMigration.parser.AbstractXMLFileParser;
import xml.cds.v5_0.OmdCds;
import xml.cds.v5_0.PatientRecord;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class CDSFileParser extends AbstractXMLFileParser<OmdCds>
{
	public static final String SCHEMA_VERSION = "5.1";

	@Override
	protected JAXBContext getNewInstance() throws JAXBException
	{
		return JAXBContext.newInstance(PatientRecord.class);
	}

	@Override
	protected NamespacePrefixMapper getNamespaceMapper()
	{
		return new CDSNamespaceMapper();
	}

	@Override
	public String getSchemaVersion()
	{
		return SCHEMA_VERSION;
	}
}
