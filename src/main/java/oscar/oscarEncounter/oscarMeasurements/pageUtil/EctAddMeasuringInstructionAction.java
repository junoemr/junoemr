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


package oscar.oscarEncounter.oscarMeasurements.pageUtil;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.oscarehr.common.dao.MeasurementTypeDao;
import org.oscarehr.common.model.MeasurementType;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;


public class EctAddMeasuringInstructionAction extends Action {
	
	static Logger log = MiscUtils.getLogger();

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
    	log.info("ADD MEASURING INSTRUCTION");
    	
        EctAddMeasuringInstructionForm frm = (EctAddMeasuringInstructionForm) form;
        MessageResources mr = getResources(request);
        List<String> messages = new LinkedList<String>();

        request.getSession().setAttribute("EctAddMeasuringInstructionForm", frm);
        
        try {
        	MeasurementTypeDao measurementTypeDao = (MeasurementTypeDao) SpringUtils.getBean("measurementTypeDao");
            ActionMessages errors = new ActionMessages();  
        	
            String typeDisplayName = frm.getTypeDisplayName();
            String measuringInstrc = frm.getMeasuringInstrc();
            String validation = frm.getValidation();

    		if ( measuringInstrc == null ) measuringInstrc = "";
        	String errorField = "The measuring instruction " + measuringInstrc;
        	
        	// can't exceed the table max length
        	if (!GenericValidator.maxLength(measuringInstrc, 255)) {
                errors.add(measuringInstrc, new ActionMessage("errors.maxlength", errorField, "255"));
                saveErrors(request, errors);
                return (new ActionForward(mapping.getInput()));
        	}
        	// can't have empty instructions
        	if (!GenericValidator.minLength(measuringInstrc, 1)) {
                errors.add(measuringInstrc, new ActionMessage("errors.minlength", errorField, "1"));
                saveErrors(request, errors);
                return (new ActionForward(mapping.getInput()));
        	}
        	
        	// check database for duplicates by matching name and instruction strings
        	if(measurementTypeDao.isDuplicate(typeDisplayName, measuringInstrc)) {
        		errors.add(measuringInstrc, new ActionMessage("error.oscarEncounter.Measurements.duplicateTypeName"));
                saveErrors(request, errors);
                return (new ActionForward(mapping.getInput()));
        	}

            /* What's happening here is a query to grab the type and type descriptions from a previous 
             * measurement type so we can duplicate them. Not a great way to do this */
            List<MeasurementType> typeByNameList = measurementTypeDao.findByDisplayName(typeDisplayName);
            
            // apparently we can assume this is never an empty list.
            String type = typeByNameList.get(0).getType();
            String typeDesc = typeByNameList.get(0).getTypeDescription();
            
            //Write to database
            MeasurementType measurementType = measurementTypeDao.saveNewMeasurementType(type, typeDesc, typeDisplayName, measuringInstrc, validation);
            request.setAttribute("requestId", measurementType.getId());
            log.info("Measurement instruction saved successfully");
            
            messages.add(mr.getMessage("oscarEncounter.oscarMeasurements.AddMeasuringInstruction.successful", "!"));
        }
        catch(Exception e)
        {
            MiscUtils.getLogger().error("Error", e);
            messages.add("An Unexpected Error occured while attempting to save the measurement type!");
        }            
        request.setAttribute("messages", messages);
        return mapping.findForward("success");

    }
}
