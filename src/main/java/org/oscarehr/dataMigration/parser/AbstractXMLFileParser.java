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
package org.oscarehr.dataMigration.parser;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.XMLFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.nio.file.Path;

public abstract class AbstractXMLFileParser<T> extends AbstractFileParser<T>
{
	@Override
	@SuppressWarnings("unchecked")
	public T parse(GenericFile genericFile) throws IOException
	{
		if(!(genericFile instanceof XMLFile))
		{
			throw new IOException("Invalid File: Not xml format");
		}

		try
		{
			JAXBContext jaxbContext = getNewInstance();
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			return (T) jaxbUnmarshaller.unmarshal(genericFile.getFileObject());
		}
		catch (JAXBException e)
		{
			throw new IOException(e);
		}
	}

	@Override
	public GenericFile write(T formatObject) throws IOException
	{
		return write(formatObject, (Path) null);
	}

	@Override
	public GenericFile write(T formatObject, Path directory) throws IOException
	{
		return write(formatObject, FileFactory.createTempFile(directory, ".xml"));
	}

	private GenericFile write(T formatObject, GenericFile tempFile) throws IOException
	{
		try
		{
			JAXBContext jaxbContext = getNewInstance();
			Marshaller marshaller = jaxbContext.createMarshaller();

			// output pretty printed
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// set correct namespaces
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", getNamespaceMapper());

			String customHeaders = getHeadersForMarshaller();
			if(customHeaders != null)
			{
				marshaller.setProperty("com.sun.xml.bind.xmlHeaders", "\n<!-- " + customHeaders + " -->");
			}
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

	public abstract String getSchemaVersion();

	protected abstract JAXBContext getNewInstance() throws JAXBException;

	protected abstract NamespacePrefixMapper getNamespaceMapper();

	protected String getHeadersForMarshaller()
	{
		return null;
	}
}
