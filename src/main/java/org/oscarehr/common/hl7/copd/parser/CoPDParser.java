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
package org.oscarehr.common.hl7.copd.parser;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.XMLParser;
import ca.uhn.hl7v2.util.XMLUtils;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Implementation copied from the DefaultXMLParser.
 * Had to override in order to add custom group names to the ourForceGroupNames hash
 * the rest should be the same as the default parser
 */
public class CoPDParser extends DefaultXMLParser
{
	private static final Logger log = MiscUtils.getLogger();
	private static final Set<String> ourForceGroupNames = new HashSet<>();

	public CoPDParser()
	{
	}

	public CoPDParser(HapiContext context)
	{
		super(context);
	}

	public CoPDParser(ModelClassFactory theFactory)
	{
		super(theFactory);
	}

	@Override
	public Document encodeDocument(Message source) throws HL7Exception
	{
		String messageClassName = source.getClass().getName();
		String messageName = messageClassName.substring(messageClassName.lastIndexOf(46) + 1);

		try
		{
			Document doc = XMLUtils.emptyDocument(messageName);
			this.encode(source, doc.getDocumentElement());
			return doc;
		}
		catch(Exception var5)
		{
			throw new HL7Exception("Can't create XML document - " + var5.getClass().getName(), var5);
		}
	}

	private void encode(Group groupObject, Element groupElement) throws HL7Exception
	{
		String[] childNames = groupObject.getNames();
		String messageName = groupObject.getMessage().getName();

		try
		{
			String[] var5 = childNames;
			int var6 = childNames.length;

			for(int var7 = 0; var7 < var6; ++var7)
			{
				String name = var5[var7];
				Structure[] reps = groupObject.getAll(name);
				Structure[] var10 = reps;
				int var11 = reps.length;

				for(int var12 = 0; var12 < var11; ++var12)
				{
					Structure rep = var10[var12];
					String elementName = makeGroupElementName(messageName, name);

					Element childElement;
					try
					{
						childElement = groupElement.getOwnerDocument().createElement(elementName);
					}
					catch(DOMException var17)
					{
						throw new HL7Exception("Can't encode element " + elementName + " in group " + groupObject.getClass().getName(), var17);
					}

					groupElement.appendChild(childElement);
					if(rep instanceof Group)
					{
						this.encode((Group) rep, childElement);
					}
					else if(rep instanceof Segment)
					{
						this.encode((Segment) rep, childElement);
					}
				}
			}

		}
		catch(DOMException var18)
		{
			throw new HL7Exception("Can't encode group " + groupObject.getClass().getName(), var18);
		}
	}

	@Override
	public Message parseDocument(Document xmlMessage, String version) throws HL7Exception
	{
		this.assertNamespaceURI(xmlMessage.getDocumentElement().getNamespaceURI());
		Message message = this.instantiateMessage(xmlMessage.getDocumentElement().getLocalName(), version, true);
		this.parse((Group) message, (Element) xmlMessage.getDocumentElement());
		return message;
	}

	private void parse(Group groupObject, Element groupElement) throws HL7Exception
	{
		String[] childNames = groupObject.getNames();
		String messageName = groupObject.getMessage().getName();
		NodeList allChildNodes = groupElement.getChildNodes();
		List<String> unparsedElementList = new ArrayList<>();

		String segIndexName;
		for(int i = 0; i < allChildNodes.getLength(); ++i)
		{
			Node node = allChildNodes.item(i);
			segIndexName = node.getLocalName();
			if(node.getNodeType() == 1 && !unparsedElementList.contains(segIndexName))
			{
				this.assertNamespaceURI(node.getNamespaceURI());
				unparsedElementList.add(segIndexName);
			}
		}

		String[] var12 = childNames;
		int var14 = childNames.length;

		for(int var16 = 0; var16 < var14; ++var16)
		{
			String nextChildName = var12[var16];
			String childName = nextChildName;
			if(groupObject.isGroup(nextChildName))
			{
				childName = makeGroupElementName(groupObject.getMessage().getName(), nextChildName);
			}

			unparsedElementList.remove(childName);
			if(nextChildName.length() == 4 && Character.isDigit(nextChildName.charAt(3)))
			{
//				log.trace("Skipping rep segment: {}", nextChildName);
			}
			else
			{
				this.parseReps(groupElement, groupObject, messageName, nextChildName, nextChildName);
			}
		}

		Iterator var13 = unparsedElementList.iterator();

		while(var13.hasNext())
		{
			String segName = (String) var13.next();
			segIndexName = groupObject.addNonstandardSegment(segName);
			this.parseReps(groupElement, groupObject, messageName, segName, segIndexName);
		}

	}

	private void parseReps(Element groupElement, Group groupObject, String messageName, String childName, String childIndexName) throws HL7Exception
	{
		String groupName = makeGroupElementName(messageName, childName);
		List<Element> reps = this.getChildElementsByTagName(groupElement, groupName);
//		log.trace("# of elements matching {}: {}", groupName, reps.size());
		if(groupObject.isRepeating(childIndexName))
		{
			for(int i = 0; i < reps.size(); ++i)
			{
				this.parseRep((Element) reps.get(i), groupObject.get(childIndexName, i));
			}
		}
		else
		{
			if(reps.size() > 0)
			{
				this.parseRep((Element) reps.get(0), groupObject.get(childIndexName, 0));
			}

			if(reps.size() > 1)
			{
				byte i = 1;

				String newIndexName;
				try
				{
					for(int j = 1; j < reps.size(); ++j)
					{
						newIndexName = childName + (j + 1);
						Structure st = groupObject.get(newIndexName);
						this.parseRep((Element) reps.get(j), st);
					}
				}
				catch(Throwable var12)
				{
					log.info("Issue Parsing: " + var12);
					newIndexName = groupObject.addNonstandardSegment(childName);

					for(int j = i; j < reps.size(); ++j)
					{
						this.parseRep((Element) reps.get(j), groupObject.get(newIndexName, j - i));
					}
				}
			}
		}
	}

	private void parseRep(Element theElem, Structure theObj) throws HL7Exception
	{
		if(theObj instanceof Group)
		{
			this.parse((Group) theObj, theElem);
		}
		else if(theObj instanceof Segment)
		{
			this.parse((Segment) ((Segment) theObj), (Element) theElem);
		}

//		log.trace("Parsed element: {}", theElem.getNodeName());
	}

	private List<Element> getChildElementsByTagName(Element theElement, String theName) throws HL7Exception
	{
		List<Element> result = new ArrayList<>(10);
		NodeList children = theElement.getChildNodes();

		for(int i = 0; i < children.getLength(); ++i)
		{
			Node child = children.item(i);
			if(child.getNodeType() == 1 && child.getLocalName().equals(theName))
			{
				this.assertNamespaceURI(child.getNamespaceURI());
				result.add((Element) child);
			}
		}

		return result;
	}

	protected static String makeGroupElementName(String messageName, String className)
	{
		String ret;
		if(className.length() <= 4 && !ourForceGroupNames.contains(className))
		{
			if(className.length() == 4)
			{
				ret = className.substring(0, 3);
			}
			else
			{
				ret = className;
			}
		}
		else
		{
			StringBuilder elementName = new StringBuilder();
			elementName.append(messageName);
			elementName.append('.');
			elementName.append(className);
			ret = elementName.toString();
		}

		return ret;
	}

	@Override
	public void parse(Message theMessage, String theString) throws HL7Exception
	{
		Document doc = this.parseStringIntoDocument(theString);
		this.parse((Group) theMessage, (Element) doc.getDocumentElement());
		this.applySuperStructureName(theMessage);
	}

	public static XMLParser getInstanceWithNoValidation()
	{
		HapiContext context = new DefaultHapiContext(ValidationContextFactory.noValidation());
		XMLParser retVal = context.getXMLParser();
		return retVal;
	}

	static
	{
		ourForceGroupNames.add("DIET");
		ourForceGroupNames.add("LAB");
		ourForceGroupNames.add("MEDS");
	}
}
