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

import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.XMLFile;
import org.oscarehr.common.xml.cds.v5_0.CDSRootElement;
import org.oscarehr.demographicImport.parser.AbstractFileParser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;

public class CDSFileParser extends AbstractFileParser<CDSRootElement>
{

	@Override
	public CDSRootElement parse(GenericFile genericFile) throws IOException
	{
		if(!(genericFile instanceof XMLFile))
		{
			throw new IOException("Invalid File: Not xml format");
		}

		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(CDSRootElement.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			return (CDSRootElement) jaxbUnmarshaller.unmarshal(genericFile.getFileObject());
		}
		catch (JAXBException e)
		{
			throw new IOException(e);
		}
	}

	@Override
	public GenericFile write(CDSRootElement formatObject) throws IOException
	{
		GenericFile tempFile = FileFactory.createTempFile(".xml");

		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(CDSRootElement.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(formatObject, tempFile.getFileObject());
		}
		catch(JAXBException e)
		{
			tempFile.deleteFile(); // clean up failed file write
			throw new IOException(e);
		}

		return tempFile;
	}
}
