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


package oscar.oscarEncounter.oscarMeasurements;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.decisionSupport2.model.DsInfoCache;
import org.oscarehr.decisionSupport2.model.DsInfoLookup;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBean;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author jay
 */
public class MeasurementInfo implements DsInfoCache, DsInfoLookup
{
    private static final Logger log = MiscUtils.getLogger();

    private final List<String> warning = new ArrayList<>();
    private final Map<String, List<String>> criticalAlertHash = new HashMap<>();
    private final Map<String, List<String>> warningHash = new HashMap<>();
    private final List<String> recommendations = new ArrayList<>();
    private final Map<String, List<String>> recommendationHash = new HashMap<>();
    private final Map<String, Boolean> hiddens = new HashMap<>();

    List<EctMeasurementsDataBean> measurementList = new ArrayList<>();
    Hashtable<String, ArrayList<EctMeasurementsDataBean>> measurementHash = new Hashtable<>();
    String demographicNo = "";

    /** Creates a new instance of MeasurementInfo */
    public MeasurementInfo(String demographic) {
        demographicNo = demographic;
    }

    public ArrayList getList()
    {
        ArrayList<String> list = new ArrayList<>(warningHash.keySet());
        list.addAll(recommendationHash.keySet());
        return list;
    }

    public void log(String logMessage){
        log.debug(logMessage);
    }

    public List<String> getWarnings()
    {
        return warning;
    }

    public List<String> getRecommendations()
    {
        return recommendations;
    }

    @Override
    public boolean getHidden(String measurement)
    {
        if(hiddens.get(measurement) == null) return false;
        return hiddens.get(measurement);
    }

    public void setDemographic(String demographic)
    {
        demographicNo = demographic;
    }

    /** Creates a new instance of MeasurementInfo */
    public MeasurementInfo() {
    }


    public void getMeasurements(List<String> list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            String measurement = list.get(i);
            EctMeasurementsDataBeanHandler ect = new EctMeasurementsDataBeanHandler(Integer.valueOf(demographicNo), measurement);
            List<EctMeasurementsDataBean> measurementsData = ect.getMeasurementsData();
            measurementList.addAll(measurementsData);
            measurementHash.put(measurement, new ArrayList<>(measurementsData));
        }
    }

    public ArrayList<EctMeasurementsDataBean> getMeasurementData(String measurement)
    {
        return measurementHash.get(measurement);
    }

    @Override
    public void addRecommendation(String measurement, String recommendationMessage)
    {
        if(recommendationHash.containsKey(measurement))
        {
            recommendationHash.get(measurement).add(recommendationMessage);
        }
        else
        {
            List<String> messageList = new ArrayList<>();
            messageList.add(recommendationMessage);
            recommendationHash.put(measurement, messageList);
        }
        recommendations.add(recommendationMessage);
    }

    @Override
    public void addWarning(String measurement, String warningMessage)
    {
        if(warningHash.containsKey(measurement))
        {
            warningHash.get(measurement).add(warningMessage);
        }
        else
        {
            List<String> messageList = new ArrayList<>();
            messageList.add(warningMessage);
            warningHash.put(measurement, messageList);
        }
        warning.add(warningMessage);
    }

    @Override
    public void addCriticalAlert(String measurement, String warningMessage)
    {
        if(criticalAlertHash.containsKey(measurement))
        {
            criticalAlertHash.get(measurement).add(warningMessage);
        }
        else
        {
            List<String> messageList = new ArrayList<>();
            messageList.add(warningMessage);
            criticalAlertHash.put(measurement, messageList);
        }
    }

    @Override
    public void addHidden(String measurement, boolean hidden)
    {
        hiddens.put(measurement, hidden);
    }

    @Override
    public boolean hasWarning(String measurement)
    {
        return warningHash.get(measurement) != null;
    }

    @Override
    public boolean hasRecommendation(String measurement)
    {
        return recommendationHash.get(measurement) != null;
    }

    @Override
    public boolean hasCriticalAlert(String measurement)
    {
        return criticalAlertHash.get(measurement) != null;
    }

    @Deprecated
    public String getRecommendation(String measurement)
    {
        List<String> recommendations = recommendationHash.get(measurement);
        return (recommendations != null && !recommendations.isEmpty()) ? String.join(", ", recommendations) : null;
    }

    @Override
    public List<String> getRecommendations(String measurement)
    {
        return recommendationHash.get(measurement);
    }

    @Deprecated
    public String getWarning(String measurement)
    {
        List<String> warnings = warningHash.get(measurement);
        return (warnings != null && !warnings.isEmpty()) ? String.join(", ", warnings) : null;
    }

    @Override
    public List<String> getWarnings(String measurement)
    {
        return warningHash.get(measurement);
    }

    @Override
    public List<String> getCriticalAlerts(String measurement)
    {
        return criticalAlertHash.get(measurement);
    }

    @Override
    public int getMonthsSinceLastRecordedDate(String measurement)
    {
        return getLastDateRecordedInMonths(measurement);
    }

    public int getLastDateRecordedInMonths(String measurement)
    {
        int numMonths = -1;
        List<EctMeasurementsDataBean> list = getMeasurementData(measurement);
        if ( list != null && list.size() > 0){
            EctMeasurementsDataBean mdata = list.get(0);
            Date date = mdata.getDateObservedAsDate();
            numMonths = getNumMonths(date, Calendar.getInstance().getTime());
        }
        log.debug("Returning the number of months "+numMonths);
        return numMonths;
    }
    
    public String getLastDateRecordedInMonthsMsg (String measurement){
        String message = "";
        int numMonths = -1;
        List<EctMeasurementsDataBean> list = getMeasurementData(measurement);

        if ( list != null && list.size() > 0){
           
            EctMeasurementsDataBean mdata = list.get(0);
            Date date = mdata.getDateObservedAsDate();
            numMonths = getNumMonths(date, Calendar.getInstance().getTime());
        }

        log.debug("Returning the number of months "+message);
        
        if(numMonths == -1){
        	message = "has never been reviewed";
        }else{
        	message = "hasn't been reviewed in " +numMonths+" months";
        }
        
        return message;
    }

    @Override
    public Optional<Double> getLatestValueNumeric(String measurement)
    {
        try
        {
            List<EctMeasurementsDataBean> list = getMeasurementData(measurement);
            if(list != null && list.size() > 0)
            {
                return Optional.of(Double.parseDouble(list.get(0).getDataField()));
            }
        }
        catch(NumberFormatException e)
        {
            MiscUtils.getLogger().error("Error", e);
        }
        return Optional.empty();
    }

    public int getLastValueAsInt(String measurement)
    {
        int value = -1; //TODO-legacy not sure how to handle a non int value.
        List<EctMeasurementsDataBean> list = getMeasurementData(measurement);
        if(list != null && list.size() > 0)
        {
            EctMeasurementsDataBean mdata = list.get(0);
            try
            {
                value = Integer.parseInt(mdata.getDataField());
            }
            catch(NumberFormatException e)
            {
                MiscUtils.getLogger().error("Error", e);
            }
        }
        return value;
    }

    @Override
    public String getLatestValue(String measurement)
    {
        String value = null;
        List<EctMeasurementsDataBean> list = getMeasurementData(measurement);
        if(list != null && list.size() > 0)
        {
            EctMeasurementsDataBean mdata = list.get(0);
            value = mdata.getDataField();
        }
        return value;
    }

    public int isDataEqualToYes(String measurement){
        int v = 0;
        String str="";
        List<EctMeasurementsDataBean> list = getMeasurementData(measurement);
        if ( list != null && list.size() > 0){
            EctMeasurementsDataBean mdata = list.get(0);
            try{
            	str = mdata.getDataField();
            }catch (Exception e ){
               MiscUtils.getLogger().error("Error", e);
            }
        }        
        
        if(str.equalsIgnoreCase("yes")){
        	v=1;
        }
        
        return v;
    }
    
    /*
     * Called by Drools - not sure how to pass in LoggedInInfo
     */
    public boolean getGender(String sex){
    	if (sex==null) return false;
    	DemographicDao demographicDao = SpringUtils.getBean(DemographicDao.class);
    	Demographic d = demographicDao.getDemographic(demographicNo);
    	return (sex.trim().equals(d.getSex()));
    }

    @Override
    public String getGender()
    {
        DemographicDao demographicDao = SpringUtils.getBean(DemographicDao.class);
        Demographic d = demographicDao.getDemographic(demographicNo);
        return (StringUtils.trimToNull(d.getSex()));
    }
    
    /*
     *Called by Drools - not sure how to pass in LoggedInInfo
     */
    public int getAge(){
    	DemographicDao demographicDao = SpringUtils.getBean(DemographicDao.class);
    	Demographic d = demographicDao.getDemographic(demographicNo);
    	return d.getAgeInYears();
    }

    @Override
    public int getAgeInYears()
    {
        return getAge();
    }
    

 
    private int getNumMonths(Date dStart, Date dEnd) {
        int i = 0;
        log.debug("Getting the number of months between "+dStart.toString()+ " and "+dEnd.toString() );
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dStart);
        while (calendar.getTime().before(dEnd) || calendar.getTime().equals(dEnd)) {
            calendar.add(Calendar.MONTH, 1);
            i++;
        }
        i--;
        if (i < 0) { i = 0; }
        return i;
   }
}