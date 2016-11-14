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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.oscarehr.common.dao.Hl7TextMessageDao;
import org.oscarehr.common.model.Hl7TextMessage;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.OscarProperties;

public final class Factory {

	private static Logger logger = MiscUtils.getLogger();

	private Factory() {
		// static methods no need for instance
	}

	/**
	 * Find the lab corresponding to segmentID and return the appropriate MessageHandler for it
	 */
	public static MessageHandler getHandler(String segmentID) {
		return getHandler(Integer.parseInt(segmentID));
	}
	/**
	 * Find the lab corresponding to segmentID and return the appropriate MessageHandler for it
	 */
	public static MessageHandler getHandler(int segmentID) {
		try {
			Hl7TextMessageDao hl7TextMessageDao = (Hl7TextMessageDao) SpringUtils.getBean("hl7TextMessageDao");
			Hl7TextMessage hl7TextMessage = hl7TextMessageDao.find(segmentID);

			String type = hl7TextMessage.getType();
			String hl7Body = MiscUtils.decodeBase64StoString(hl7TextMessage.getBase64EncodedeMessage());
			return getHandler(type, hl7Body);
		} catch (Exception e) {
			logger.error("Could not retrieve lab for segmentID(" + segmentID + ")", e);
		}

		return getHandler("", "");
	}

	public static String getHL7Body(String segmentID) {
		String ret = null;
		try {
			Hl7TextMessageDao hl7TextMessageDao = (Hl7TextMessageDao) SpringUtils.getBean("hl7TextMessageDao");
			Hl7TextMessage hl7TextMessage = hl7TextMessageDao.find(Integer.parseInt(segmentID));

			ret = MiscUtils.decodeBase64StoString(hl7TextMessage.getBase64EncodedeMessage());
		} catch (Exception e) {
			logger.error("Could not retrieve lab for segmentID(" + segmentID + ")", e);
		}
		return ret;
	}

	/*
	 * Create and return the message handler corresponding to the message type
	 */
	public static MessageHandler getHandler(String type, String hl7Body) {
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
				if (msgType.equals(type)) msgHandler = "oscar.oscarLab.ca.all.parsers." + e.getAttributeValue("className");
			}

			// create and return the message handler
			if (msgHandler.equals("")) {
				logger.info("No message handler specified for type: " + type + "\nUsing default message handler instead");
				MessageHandler mh = new DefaultGenericHandler();
				mh.init(hl7Body);
				return (mh);
			} else {
				try {
					Class<?> classRef = Class.forName(msgHandler);
					MessageHandler mh = (MessageHandler) classRef.newInstance();
					logger.info("Message handler '" + msgHandler + "' created successfully");
					logger.debug("Message: " + hl7Body);
					mh.init(hl7Body);
					return (mh);
				} catch (ClassNotFoundException e) {
					logger.info("Could not find message handler: " + msgHandler + "\nUsing default message handler instead");
					MessageHandler mh = new DefaultGenericHandler();
					mh.init(hl7Body);
					return (mh);
				} catch (Exception e1) {
					logger.error("Could not create message handler: " + msgHandler + "\nUsing default message handler instead. Error message: " + e1.getMessage(), e1);
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
