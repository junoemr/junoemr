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

package org.oscarehr.ws;

import javax.jws.WebService;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.common.dao.ValidationsDao;
import org.oscarehr.common.dao.MeasurementDao;
import org.oscarehr.common.model.Validations;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.common.model.MeasurementInvalidException;
import org.oscarehr.common.model.MeasurementNotFoundException;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import oscar.oscarEncounter.oscarMeasurements.pageUtil.EctValidation;


import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.ServletContext;
import javax.xml.ws.handler.MessageContext;

@WebService
public class MeasurementWs extends AbstractWs {

    private static final Logger logger=MiscUtils.getLogger();
	
	private static final Map<String, String> messages;

	static {
		messages = new HashMap<String, String>();
		messages.put("errors.range", "{0} is not in the range {1} through {2}");
		messages.put("errors.maxlength", "{0} cannot be more than {1} characters");
		messages.put("errors.minlength", "{0} cannot be more than {1} characteres");
		messages.put("errors.invalid", "{0} is invalid");
		messages.put("errors.bloodPressure", "Blood Pressure must be in ###/### format");
		messages.put("errors.invalidDate", "The date of {0} is invalid");
	}

    public String addMeasurement(String providerNo, String demographicNo, String inputType,
			String inputTypeDisplay, String mInstrc, String inputValue, String dateObserved, 
			String comments)
		throws IOException, MeasurementNotFoundException, 
			MeasurementInvalidException, ParseException
    {
		ServletContext servletContext = 
			(ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);

		WebApplicationContext ctx = 
			WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

		ValidationsDao validationsDao = (ValidationsDao) ctx.getBean("validationsDao");

    	List<Validations> validations = validationsDao.getFromMeasurementType(inputType, mInstrc);

		Iterator iter = validations.iterator();

		// Technically there could be more than one validation for the given
		// inputType and instruction, but that really shouldn't happen so
		// we'll just take the first one.  Ideally we'd put a unique index on
		// (type, measuringInstruction).
		if(!iter.hasNext()){
			throw new MeasurementNotFoundException(
				"No measurement found for type '" + inputType + 
				"' and instruction '" + mInstrc + "'");
		}

		Validations val = (Validations) iter.next();

        String regExp = val.getRegularExp();
		double dMax = (val.getMaxValue() != null ? val.getMaxValue() : 0);
		double dMin = (val.getMinValue() != null ? val.getMinValue() : 0);
		int iMax = (val.getMaxLength() != null ? val.getMaxLength() : 0);
		int iMin = (val.getMinLength() != null ? val.getMinLength() : 0);


		// Validate the measurement
		
		boolean valid = true;
		EctValidation ectValidation = new EctValidation();
		ActionMessages errors = new ActionMessages();
		String inputValueName = "errors";

		if(!ectValidation.isInRange(dMax, dMin, inputValue)){
			errors.add(inputValueName, new ActionMessage("errors.range", inputTypeDisplay, Double.toString(dMin), Double.toString(dMax)));
			valid = false;
		}
		if(!ectValidation.maxLength(iMax, inputValue)){
			errors.add(inputValueName, new ActionMessage("errors.maxlength", inputTypeDisplay, Integer.toString(iMax)));
			valid = false;
		}
		if(!ectValidation.minLength(iMin, inputValue)){
			errors.add(inputValueName, new ActionMessage("errors.minlength", inputTypeDisplay, Integer.toString(iMin)));
			valid = false;
		}

		if(!ectValidation.matchRegExp(regExp, inputValue)){
			errors.add(inputValueName,
					new ActionMessage("errors.invalid", inputTypeDisplay));
			valid = false;
		}
		if(!ectValidation.isValidBloodPressure(regExp, inputValue)){
			errors.add(inputValueName,
					new ActionMessage("error.bloodPressure"));
			valid = false;
		}
		if(!ectValidation.isDate(dateObserved)&&inputValue.compareTo("")!=0){
			errors.add(inputValueName,
					new ActionMessage("errors.invalidDate", inputTypeDisplay));
			valid = false;
		}

		if(!valid){
			Iterator errorIter = errors.get();

			StringBuilder errorMessages = new StringBuilder();
			errorMessages.append("");

			while(errorIter.hasNext()){

				ActionMessage message = (ActionMessage)errorIter.next();

				MessageFormat messageFormat = 
					new MessageFormat(messages.get(message.getKey()));

				if(!errorMessages.toString().equals("")){
					errorMessages.append(", ");
				}

				errorMessages.append(
					messageFormat.format(message.getValues(), new StringBuffer(), null).toString());
			}

			throw new MeasurementInvalidException(errorMessages.toString());
		}


		// Add the measurement to the database
		
		Date cleanDateObserved = null; 
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cleanDateObserved = formatter.parse(dateObserved);

		MeasurementDao measurementDao =
			(MeasurementDao) SpringUtils.getBean("measurementDao");

		Measurement measurement = new Measurement();

    	measurement.setDemographicId(Integer.parseInt(demographicNo));
    	measurement.setProviderNo(providerNo);

    	measurement.setDataField(inputValue);
    	measurement.setMeasuringInstruction(mInstrc);
    	measurement.setComments(comments);
    	measurement.setDateObserved(cleanDateObserved);
    	measurement.setType(inputType);
		measurement.setAppointmentNo(0);

    	measurementDao.persist(measurement);

        return "{\"success\":1,\"message\":\"\"}";
    }
}
