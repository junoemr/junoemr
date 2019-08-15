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


package oscar.oscarEncounter.oscarMeasurements.util;
//used by eforms for writing measurements

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.oscarehr.common.dao.MeasurementDao;
import org.oscarehr.common.dao.MeasurementDao.SearchCriteria;
import org.oscarehr.common.dao.MeasurementTypeDao;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.common.model.MeasurementType;
import org.oscarehr.common.model.Validations;
import org.oscarehr.util.SpringUtils;
import oscar.oscarEncounter.oscarMeasurements.pageUtil.EctValidation;
import oscar.util.ConversionUtils;
import oscar.util.UtilDateUtilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class WriteNewMeasurements {
	
	private static MeasurementTypeDao measurementTypeDao = SpringUtils.getBean(MeasurementTypeDao.class);
	private static MeasurementDao dao = SpringUtils.getBean(MeasurementDao.class);

	public static List<String> validateMeasurements(List<String> names, List<String> values)
	{
		Vector measures = createMeasurementVector(names, values);
		preProcess(measures);
		return checkMappedMeasurements(measures);
	}

    /**
     * Common method to generate a vector of measurements from a set of name, value mappings.
     * @param names list of measurement names to map
     * @param values list of measurement values associated with each name
     * @return a vector containing the measurement/value mappings
     */
    private static Vector createMeasurementVector(List<String> names, List<String> values)
    {
        Vector measures = new Vector();
        for (int i = 0; i < names.size(); i++)
        {
            if ((values.get(i) == null) || (values.get(i)).isEmpty())
            {
                continue;
            }
            String name = names.get(i);
            int typeStart = name.indexOf("m$");
            int fieldStart = name.indexOf("#");
            if ((typeStart < 0) || (fieldStart < 0))
            {
                continue;
            }
            typeStart += 2;
            fieldStart++;

            String type = name.substring(typeStart, fieldStart-1);
            String field = name.substring(fieldStart);
            //--------type (i.e. vital signs) and field (i.e. BP) are extracted
            int index;
            index = getMeasurement(measures, type);  //checks if exists in our ArrayList

            if (index >= 0)
            {
                Hashtable currMeasure = (Hashtable) measures.get(index);
                currMeasure.put(field, values.get(i));
            }
            else
            {
                Hashtable measure = new Hashtable();
                measure.put("type", type);
                measure.put(field, values.get(i));
                measures.addElement(measure);
            }
        }
        return measures;
    }

    public static ActionMessages addMeasurements(List<String> names, List<String> values, String demographicNo, String providerNo) {
        // Must be called on the same eForm object because it retrieves some of its properties
        Vector measures = createMeasurementVector(names, values);
        //--------All measurement values are organized in Hashtables in an arraylist
        //--------validation......
        preProcess(measures);
        ActionMessages errors;
        errors = validate(measures, demographicNo);
        if (errors.isEmpty())
        {
            write(measures, demographicNo, providerNo);
        }
        return errors;
    }

    static private int getMeasurement(Vector measures, String type) {
        for (int i = 0; i < measures.size(); i++)
        {
            Hashtable currMeasure = (Hashtable) measures.get(i);
            if ((currMeasure.get("type")).equals(type))
            {
                return i;
            }
        }
        return -1;
    }

    static private void preProcess(Vector measures) {
        // Fills in required values

        for (int i = 0; i < measures.size(); i++)
        {
            Hashtable currMeasure = (Hashtable) measures.get(i);
            String type = (String) currMeasure.get("type");
            String measuringInst = (String)currMeasure.get("measuringInstruction");
            String comments = (String)currMeasure.get("comments");
            String dateObserved = (String)currMeasure.get("dateObserved");
            java.util.Date now = new Date();
            String dateEntered = ConversionUtils.toDateString(now, ConversionUtils.DEFAULT_TS_PATTERN);

            if (measuringInst == null || measuringInst.equals(""))
            {
            	List<MeasurementType> tmp = measurementTypeDao.findByType(type);
            	if(tmp.size() > 0)
            	{
            		 measuringInst = tmp.get(0).getMeasuringInstruction();
                     currMeasure.put("measuringInstruction", measuringInst);
            	}
            	else
            	{
                     continue;
            	}

            }
            if (comments == null)
            {
                currMeasure.put("comments", "");
            }

            if (dateObserved == null || dateObserved.equals(""))
            {
                currMeasure.put("dateObserved", dateEntered);
            }
            currMeasure.put("dateEntered", dateEntered);
        }
    }

    /**
     * Pre-validation to check which measurements don't have mappings
     * These measurements can be saved in the database pending any further issues and
     * will sit in the background until the user creates an appropriate measurement mapping.
     * @param measures vector of measurements to check against
     * @return list of measurement types that don't have mappings set on the system
     */
    static private List<String> checkMappedMeasurements(Vector measures)
    {
        EctValidation ectValidation = new EctValidation();

        List<String> problemTypes = new ArrayList<>();

        for (int i = 0; i < measures.size(); i++)
        {
            Hashtable measure = (Hashtable) measures.get(i);
            String inputType = (String)measure.get("type");
            String instruction = (String)measure.get("measuringInstruction");
            List<Validations> vs = ectValidation.getValidationType(inputType, instruction);
            if (vs.isEmpty())
            {
                problemTypes.add(inputType);
            }
        }

        return problemTypes;
    }

    static private ActionMessages validate(Vector measures, String demographicNo) {
        ActionMessages errors = new ActionMessages();
        EctValidation ectValidation = new EctValidation();
        List<Validations> vs;
        boolean valid = true;
        for (int i = 0; i < measures.size(); i++)
        {
            Hashtable measure = (Hashtable) measures.get(i);
            String inputType = (String) measure.get("type");
            String inputValue = (String) measure.get("value");
            String dateObserved = ConversionUtils.padDateTimeString((String) measure.get("dateObserved"));
            String comments = (String) measure.get("comments");
            String instruction;
            String regExp = "";
            Double dMax = 0.0;
            Double dMin = 0.0;
            Integer iMax = 0;
            Integer iMin = 0;
            boolean skip = false;

            if (GenericValidator.isBlankOrNull(inputValue))
            {
                measures.removeElementAt(i);
                i--;
                continue;
            }

            instruction = (String) measure.get("measuringInstruction");
            vs = ectValidation.getValidationType(inputType, instruction);

            if (vs.isEmpty())
            {
                //if type with instruction does not exist
                skip = true;
            }
            else
            {
                Validations validation = vs.iterator().next();
                dMax = validation.getMaxValue();
                dMin = validation.getMinValue();
                iMax = validation.getMaxLength();
                iMin = validation.getMinLength();
                regExp = validation.getRegularExp();
            }



            if (!skip && !ectValidation.isInRange(dMax, dMin, inputValue))
            {
                errors.add(inputType,
                new ActionMessage("errors.range", inputType, Double.toString(dMin), Double.toString(dMax)));
                valid = false;
            }

            if (!skip && !ectValidation.maxLength(iMax, inputValue))
            {
                errors.add(inputType,
                new ActionMessage("errors.maxlength", inputType, Integer.toString(iMax)));
                valid = false;
            }

            if (!skip && !ectValidation.minLength(iMin, inputValue))
            {
                errors.add(inputType,
                new ActionMessage("errors.minlength", inputType, Integer.toString(iMin)));
                valid = false;
            }

            if (!skip && !ectValidation.matchRegExp(regExp, inputValue))
            {
                errors.add(inputType,
                new ActionMessage("errors.invalid", inputType));
                valid = false;
            }

            if (!skip && !ectValidation.isValidBloodPressure(regExp, inputValue))
            {
                errors.add(inputType,
                new ActionMessage("error.bloodPressure"));
                valid = false;
            }

            if (!skip && !ectValidation.isDate(dateObserved)&&inputValue.compareTo("")!=0)
            {
                errors.add(inputType,
                new ActionMessage("errors.invalidDate", inputType));
                valid = false;
            }

            if (!valid)
            {
                continue;
            }

            inputValue = org.apache.commons.lang.StringEscapeUtils.escapeSql(inputValue);
            instruction = org.apache.commons.lang.StringEscapeUtils.escapeSql(instruction);
            comments = org.apache.commons.lang.StringEscapeUtils.escapeSql(comments);

            // Find if the same data has already been entered into the system
            MeasurementDao dao = SpringUtils.getBean(MeasurementDao.class);
            SearchCriteria sc = new SearchCriteria();
            sc.setDemographicNo(demographicNo);
            sc.setDataField(inputValue);
            sc.setMeasuringInstrc(instruction);
            sc.setComments(comments);
            sc.setDateObserved(ConversionUtils.fromDateString(dateObserved));
            List<Measurement> ms = dao.find(sc);

            if (!ms.isEmpty())
            {
                measures.remove(i);
                i--;
            }
        }

        return errors;
    }

    static public void write(Vector measures, String demographicNo, String providerNo) {

        for (int i=0; i<measures.size(); i++)
        {
            Hashtable measure = (Hashtable) measures.get(i);
            write(measure, demographicNo, providerNo);
        }

    }

    static public void write(Hashtable measure, String demographicNo, String providerNo) {
        String inputValue = (String) measure.get("value");
        String inputType = (String) measure.get("type");
        String mInstrc = (String) measure.get("measuringInstruction");
        String comments = (String) measure.get("comments");
        String dateObserved = ConversionUtils.padDateTimeString((String) measure.get("dateObserved"));
        //write....
        Measurement measurement = new Measurement();
        measurement.setType(inputType);
        measurement.setDemographicId(Integer.parseInt(demographicNo));
        measurement.setProviderNo(providerNo);
        measurement.setDataField(inputValue);
        measurement.setMeasuringInstruction(mInstrc);
        measurement.setComments(comments);
        measurement.setDateObserved(ConversionUtils.getLegacyDateFromDateString(dateObserved));
        dao.persist(measurement);
    }

     public void write(final String followUpType, final String followUpValue, final String demographicNo, final String providerNo,final java.util.Date dateObserved,final String comment ) {
        Hashtable measure = new Hashtable();
        measure.put("value",followUpValue);
        measure.put("type",followUpType);
        measure.put("measuringInstruction","");
        measure.put("comments",  comment == null ? "" : comment);
        measure.put("dateObserved",UtilDateUtilities.DateToString(dateObserved, "yyyy-MM-dd HH:mm:ss"));
        measure.put("dateEntered",UtilDateUtilities.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
        write(measure,demographicNo,providerNo);
    }
}
