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
package org.oscarehr.demographicImport.parser.cds;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.XMLFile;
import org.oscarehr.common.xml.cds.v5_0.model.OmdCds;
import org.oscarehr.common.xml.cds.v5_0.model.PatientRecord;
import org.oscarehr.demographicImport.parser.AbstractFileParser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;

public class CDSFileParser extends AbstractFileParser<OmdCds>
{
	@Override
	public OmdCds parse(GenericFile genericFile) throws IOException
	{
		if(!(genericFile instanceof XMLFile))
		{
			throw new IOException("Invalid File: Not xml format");
		}

		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(PatientRecord.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			return (OmdCds) jaxbUnmarshaller.unmarshal(genericFile.getFileObject());
		}
		catch (JAXBException e)
		{
			throw new IOException(e);
		}
	}

	@Override
	public GenericFile write(OmdCds formatObject) throws IOException
	{
		GenericFile tempFile = FileFactory.createTempFile(".xml");

		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(PatientRecord.class);
			Marshaller marshaller = jaxbContext.createMarshaller();

			// output pretty printed
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// set correct namespaces
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new CDSNamespaceMapper());
			// write to file
			marshaller.marshal(formatObject, tempFile.getFileObject());
		}
		catch(JAXBException e)
		{
			tempFile.deleteFile(); // clean up failed file write
			throw new IOException(e);
		}

		return tempFile;
	}

	protected static class CDSNamespaceMapper extends NamespacePrefixMapper
	{
		public static final String NAMESPACE_CDS_URI = "cds";
		public static final String NAMESPACE_CDS = "";
		public static final String NAMESPACE_CDS_DATA_URI = "cds_dt";
		public static final String NAMESPACE_CDS_DATA = "cdsd";

		@Override
		public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix)
		{
			switch(namespaceUri)
			{
				case NAMESPACE_CDS_URI: return NAMESPACE_CDS;
				case NAMESPACE_CDS_DATA_URI: return NAMESPACE_CDS_DATA;
				default: return namespaceUri;
			}
		}
	}
}
