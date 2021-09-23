/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


/*
 * Factory.java
 *
 * Created on June 4, 2007, 10:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oscar.oscarLab.ca.all.parsers;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.CustomModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import com.google.common.collect.Sets;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.oscarehr.common.dao.Hl7TextMessageDao;
import org.oscarehr.common.hl7.AHS.model.v23.message.ORM_002;
import org.oscarehr.common.hl7.AHS.model.v251.message.ORU_R01;
import org.oscarehr.common.model.Hl7TextMessage;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.oscarLab.ca.all.parsers.AHS.v22.SpecimenGateHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.AHSMeditechHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.AHSRuralDIHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.AHSRuralHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.AITLHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.CLSDIHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.CLSDIORMHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.CLSHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.ConnectCareCardiologyCancelHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.ConnectCareCardiologyHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.ConnectCareDiagnosticImagingCancelHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.ConnectCareDiagnosticImagingHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.ConnectCareDocumentationAddHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.ConnectCareDocumentationCancelHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.ConnectCareDocumentationEditHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.ConnectCareEndoscopyCancelHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.ConnectCareEndoscopyHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.ConnectCareLabCancelHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.EIHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.GLSHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.ProvlabHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.SunquestHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v23.SunquestORMHandler;
import oscar.oscarLab.ca.all.parsers.AHS.v251.ConnectCareLabHandler;
import oscar.oscarLab.ca.all.parsers.other.JunoGenericLabHandler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;

public final class Factory {

	private static final Logger logger = MiscUtils.getLogger();

	private static final HashSet<String> REFACTORED_LAB_TYPES = Sets.newHashSet(
		"AHS",
		AHSRuralHandler.AHS_RURAL_LAB_TYPE,
		AHSMeditechHandler.AHS_MEDITECH_LAB_TYPE,
		AHSRuralDIHandler.AHS_RURAL_DI_LAB_TYPE,
		"CCLAB",
		"CCENDO",
		"CCCARDIOLOGY",
		"CCIMAGING",
		"CCDOC",
		"CLS",
		"CLSDI",
		"EI",
		JunoGenericLabHandler.LAB_TYPE_VALUE);

	private Factory() {
		// static methods no need for instance
	}

	/**
	 * Find the lab corresponding to segmentID and return the appropriate MessageHandler for it
	 */
	public static MessageHandler getHandler(String segmentID)
	{
		return getHandler(Integer.parseInt(segmentID));
	}
	/**
	 * Find the lab corresponding to segmentID and return the appropriate MessageHandler for it
	 */
	public static MessageHandler getHandler(Integer segmentID)
	{
		Hl7TextMessageDao hl7TextMessageDao = (Hl7TextMessageDao) SpringUtils.getBean("hl7TextMessageDao");
		Hl7TextMessage hl7TextMessage = hl7TextMessageDao.find(segmentID);
		return getHandler(hl7TextMessage);
	}

	public static MessageHandler getHandler(Hl7TextMessage hl7TextMessage)
	{
		try
		{
			String type = hl7TextMessage.getType();
			String hl7Body = new String(Base64.decodeBase64(hl7TextMessage.getBase64EncodedeMessage()), StandardCharsets.UTF_8);
			return getHandler(type, hl7Body);
		}
		catch(Exception e)
		{
			logger.error("Could not retrieve lab for segmentID(" + hl7TextMessage.getId() + ")", e);
		}
		return new DefaultGenericHandler();
	}

	public static String getHL7Body(String segmentID) {
		String ret = null;
		try {
			Hl7TextMessageDao hl7TextMessageDao = (Hl7TextMessageDao) SpringUtils.getBean("hl7TextMessageDao");
			Hl7TextMessage hl7TextMessage = hl7TextMessageDao.find(Integer.parseInt(segmentID));

			ret = new String(Base64.decodeBase64(hl7TextMessage.getBase64EncodedeMessage()), StandardCharsets.UTF_8);
		} catch (Exception e) {
			logger.error("Could not retrieve lab for segmentID(" + segmentID + ")", e);
		}
		return ret;
	}

	public static MessageHandler getHandler(String type, String hl7Body)
	{
		MessageHandler handler;
		try
		{
			if(REFACTORED_LAB_TYPES.contains(type))
			{
				handler = getHandlerNew(hl7Body);
			}
			else
			{
				handler = getHandlerOld(type, hl7Body);
			}
		}
		catch(HL7Exception e)
		{
			//TODO-legacy - the error should not get caught here but most of oscar does not expect a checked exception when calling this method
			logger.error("Parse Error", e);
			throw new RuntimeException("Hl7 Parse Error");
		}
		return handler;
	}

	private static MessageHandler getHandlerNew(String hl7Body) throws HL7Exception {
		MessageHandler handler = null;

		HapiContext context = new DefaultHapiContext();
		context.setValidationContext(new NoValidation());
		Parser p = context.getPipeParser();

		String[] mshSplit = hl7Body.split("\\|", 12);
		if(mshSplit[8].equals("ORM^002"))
		{
			/* We need to use a custom HL7 message object because ORM^002 is not an official message type */
			context.getParserConfiguration().setDefaultObx2Type("ST");
			// this package string needs to match the custom model location in the oscar source code.
			ModelClassFactory modelClassFactory = new CustomModelClassFactory(ORM_002.ROOT_PACKAGE);
			context.setModelClassFactory(modelClassFactory);
			Message msg = p.parse(hl7Body);

			if(SunquestORMHandler.handlerTypeMatch(msg))
				handler = new SunquestORMHandler(msg);
			else if(CLSDIORMHandler.handlerTypeMatch(msg))
				handler = new CLSDIORMHandler(msg);
		}
		else if (mshSplit[8].equals("ORM^O01"))
		{
			Message msg = p.parse(hl7Body);
			if (ConnectCareDiagnosticImagingCancelHandler.handlerTypeMatch(msg))
			{
				handler = new ConnectCareDiagnosticImagingCancelHandler(msg);
			}
			else if (ConnectCareEndoscopyCancelHandler.handlerTypeMatch(msg))
			{
				handler = new ConnectCareEndoscopyCancelHandler(msg);
			}
			else if (ConnectCareCardiologyCancelHandler.handlerTypeMatch(msg))
			{
				handler = new ConnectCareCardiologyCancelHandler(msg);
			}
			else if (ConnectCareLabCancelHandler.handlerTypeMatch(msg))
			{
				handler = new  ConnectCareLabCancelHandler(msg);
			}
		}
		else if (mshSplit[8].equals("MDM^T02"))
		{
			Message msg = p.parse(hl7Body);
			if (ConnectCareDocumentationAddHandler.handlerTypeMatch(msg))
			{
				handler = new ConnectCareDocumentationAddHandler(msg);
			}
		}
		else if(mshSplit[8].equals("MDM^T08"))
		{
			Message msg = p.parse(hl7Body);
			if (ConnectCareDocumentationEditHandler.handlerTypeMatch(msg))
			{
				handler = new ConnectCareDocumentationEditHandler(msg);
			}
		}
		else if (mshSplit[8].equals("MDM^T11"))
		{
			Message msg = p.parse(hl7Body);
			if (ConnectCareDocumentationCancelHandler.handlerTypeMatch(msg))
			{
				handler = new ConnectCareDocumentationCancelHandler(msg);
			}
		}
		else //handle default ORU^R01 messages
		{
			/* We need to use a custom HL7 message object because Connect Care Includes non standard segments */
			// this package string needs to match the custom model location in the oscar source code.
			ModelClassFactory modelClassFactory = new CustomModelClassFactory(ORU_R01.ROOT_PACKAGE);
			context.setModelClassFactory(modelClassFactory);
			Message msg = p.parse(hl7Body);

			// attempt to read the msh header and determine lab type handler
			if(ConnectCareDiagnosticImagingHandler.handlerTypeMatch(msg))
				handler = new ConnectCareDiagnosticImagingHandler(msg);
			else if (ConnectCareEndoscopyHandler.handlerTypeMatch(msg))
				handler = new ConnectCareEndoscopyHandler(msg);
			else if (ConnectCareCardiologyHandler.handlerTypeMatch(msg))
				handler = new ConnectCareCardiologyHandler(msg);
			else if(ConnectCareLabHandler.handlerTypeMatch(msg))
				handler = new ConnectCareLabHandler(msg);
			else if(CLSHandler.handlerTypeMatch(msg))
				handler = new CLSHandler(msg);
			else if(EIHandler.handlerTypeMatch(msg))
				handler = new EIHandler(msg);
			else if(CLSDIHandler.handlerTypeMatch(msg))
				handler = new CLSDIHandler(msg);
			else if(AHSRuralHandler.handlerTypeMatch(msg))
				handler = new AHSRuralHandler(msg);
			else if(AHSMeditechHandler.handlerTypeMatch(msg))
				handler = new AHSMeditechHandler(msg);
			else if(AHSRuralDIHandler.handlerTypeMatch(msg))
				handler = new AHSRuralDIHandler(msg);
			else if(SunquestHandler.handlerTypeMatch(msg))
				handler = new SunquestHandler(msg);
			else if(SpecimenGateHandler.handlerTypeMatch(msg))
				handler = new SpecimenGateHandler(msg);
			else if(AITLHandler.handlerTypeMatch(msg))
				handler = new AITLHandler(msg);
			else if(GLSHandler.handlerTypeMatch(msg))
				handler = new GLSHandler(msg);
			else if(ProvlabHandler.handlerTypeMatch(msg))
				handler = new ProvlabHandler(msg);
			else if(JunoGenericLabHandler.handlerTypeMatch(msg))
				handler = new JunoGenericLabHandler(msg);
		}

		if(handler == null)
			throw new RuntimeException("Hl7 message/type does not match a known lab handler.");

		logger.info("Loaded " + handler.getMsgType() + " HL7 Handler " + handler.getClass().getSimpleName());
		return handler;
	}

	/*
	 * Create and return the message handler corresponding to the message type
	 */
	private static MessageHandler getHandlerOld(String type, String hl7Body) {
		Document doc = null;
		String msgType;
		String msgHandler = "";

		try {

			// return default handler if the type is not specified
			if (type == null) {
				MessageHandler handler = new DefaultGenericHandler();
				handler.init(hl7Body);
				return (handler);
			}

			InputStream is = Factory.class.getClassLoader().getResourceAsStream("oscar/oscarLab/ca/all/upload/message_config.xml");

			if (OscarProperties.getInstance().getProperty("LAB_TYPES") != null) {
				String filename = OscarProperties.getInstance().getProperty("LAB_TYPES");
				is = new FileInputStream(filename);
			}
			SAXBuilder parser = new SAXBuilder();
			doc = parser.build(is);

			Element root = doc.getRootElement();
			List<?> items = root.getChildren();
			for (int i = 0; i < items.size(); i++) {
				Element e = (Element) items.get(i);
				msgType = e.getAttributeValue("name");
				if (msgType.equals(type)) {
					String className = e.getAttributeValue("className");
					
					// in case we have dots in the handler class name (i.e. package 
					// is specified), don't assume default package
					if (className.indexOf(".") != -1) {
						msgHandler = className;
					} else {
						msgHandler = "oscar.oscarLab.ca.all.parsers." + className;
					}
				}
			}

			// create and return the message handler
			if (msgHandler.equals("")) {
				logger.debug("No message handler specified for type: " + type + "\nUsing default message handler instead");
				MessageHandler mh = new DefaultGenericHandler();
				mh.init(hl7Body);
				return (mh);
			} else {
				try {
					Class classRef = Class.forName(msgHandler);
					MessageHandler mh = (MessageHandler) classRef.newInstance();
					logger.debug("Message handler '" + msgHandler + "' created successfully");
					logger.debug("Message: " + hl7Body);
					mh.init(hl7Body);
					return (mh);
				} catch (ClassNotFoundException e) {
					logger.debug("Could not find message handler: " + msgHandler + "\nUsing default message handler instead");
					MessageHandler mh = new DefaultGenericHandler();
					mh.init(hl7Body);
					return (mh);
				} catch (Exception e1) {
					logger.debug("Could not create message handler: " + msgHandler + "\nUsing default message handler instead", e1);
					MessageHandler mh = new DefaultGenericHandler();
					mh.init(hl7Body);
					return (mh);
				}
			}
		} catch (Exception e) {
			logger.error("Could not create message handler", e);
			return (null);
		}
	}

}
