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

package org.oscarehr.ws.rest.util;

import java.io.FilterWriter;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.oscarehr.common.model.RestServiceLog;

import oscar.log.LogAction;

public class RestLoggingOutInterceptor extends AbstractLoggingInterceptor {
	
	private static Logger logger = Logger.getLogger(RestLoggingOutInterceptor.class);

	public RestLoggingOutInterceptor() {
		super(Phase.PRE_STREAM);
	}
	@Override
	public void handleMessage(Message message) throws Fault {
		
        final OutputStream os = message.getContent(OutputStream.class);
        final Writer iowriter = message.getContent(Writer.class);
        if (os == null && iowriter == null) {
        	logger.info("Null Writer and OutputStream");
            return;
        }
        // Write the output while caching it for the log message
        if (os != null) {
            final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
            if (threshold > 0) {
                newOut.setThreshold(threshold);
            }
            message.setContent(OutputStream.class, newOut);
            newOut.registerCallback(new LoggingOutCallback(logger, message, os));
        } 
        else {
            message.setContent(Writer.class, new RestLogOutWriter(logger, message, iowriter));
        }
	}

	@Override
	protected java.util.logging.Logger getLogger() {
		return null;
	}
}

class RestLogOutWriter extends FilterWriter {
    StringWriter out2;
    int count;
    Logger logger; //NOPMD
    Message message;
    
    public RestLogOutWriter(Logger logger, Message message, Writer writer) {
        super(writer);
        this.logger = logger;
        this.message = message;
        if (!(writer instanceof StringWriter)) {
            out2 = new StringWriter();
        }
        logger.info("REST LOGGING IN RestLogOutWriter!!!");
    }
}

class LoggingOutCallback implements CachedOutputStreamCallback {
	
    private final Message message;
    private final OutputStream origStream;
    private final Logger logger; //NOPMD
    
    public LoggingOutCallback(Logger logger, Message message, OutputStream os) {
        this.logger = logger;
        this.message = message;
        this.origStream = os;
    }
	
	public void onFlush(CachedOutputStream cos) {
	}

	public void onClose(CachedOutputStream cos) {
		
		logger.info("REST LOGGING IN LoggingOutCallback!!!");
		
		try {
			StringBuilder builder = new StringBuilder();
			cos.writeCacheTo(builder);
			// get the message body
			String messageBody = builder.toString();
			logger.info("REST LOGGING!!!\n" + messageBody);
			logMessage(message, messageBody);
		}
		catch (Exception e) {
			logger.error("Error in outgoing REST Interceptor", e);
		}
	}
	
	private void logMessage(Message message, String messageBody) {
		HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
		
		RestServiceLog restLog = (RestServiceLog)message.getExchange().get("org.oscarehr.ws.rest.util.RestLoggingInInterceptor");
		
		Date createdAt = restLog.getCreatedAt();
		long duration  = new Date().getTime() - createdAt.getTime();
		
		restLog.setDuration(duration);
		restLog.setRawOutput(messageBody);
		
		
		LogAction.updateRestLogEntry(restLog);
	}
}




