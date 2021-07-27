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


package oscar.oscarPrevention.reports;

import org.apache.log4j.Logger;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import oscar.oscarDemographic.data.DemographicData;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBean;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;
import oscar.oscarPrevention.PreventionData;
import oscar.oscarPrevention.pageUtil.PreventionReportDisplay;
import oscar.util.UtilDateUtilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 *
 * @author jay
 */
public class FluReport extends PreventionsReport {
    private static Logger log = MiscUtils.getLogger();
    /** Creates a new instance of FluReport */
    public FluReport() {
    }

    public boolean displayNumShots()
    {
        return false;
    }

    public Hashtable<String,Object> runReport(LoggedInInfo loggedInInfo, ArrayList<ArrayList<String>> list,Date asofDate){
        int inList = 0;
        double done= 0;
        ArrayList<PreventionReportDisplay> returnReport = new ArrayList<PreventionReportDisplay>();

        for (int i = 0; i < list.size(); i ++){//for each  element in arraylist
             ArrayList<String> fieldList = list.get(i);
             Integer demo = Integer.valueOf(fieldList.get(0));

             log.debug("processing patient : "+demo);

             //search   prevention_date prevention_type  deleted   refused

             ArrayList<Map<String,Object>>  prevs = PreventionData.getPreventionData(loggedInInfo, "Flu",demo);
             PreventionData.addRemotePreventions(loggedInInfo, prevs, demo,"Flu",null);

             if (loggedInInfo.getCurrentFacility().isIntegratorEnabled()) {
            	 try {
	                ArrayList<HashMap<String,Object>> remotePreventions=PreventionData.getLinkedRemotePreventionData(loggedInInfo, "Flu", demo);
	                prevs.addAll(remotePreventions);

	                Collections.sort(prevs, new PreventionData.PreventionsComparator());
                } catch (Exception e) {
                	log.error("Error getting remote preventions.", e);
                }
             }

             ArrayList<Map<String,Object>> noFutureItems =  removeFutureItems(prevs, asofDate);
             PreventionReportDisplay prd = new PreventionReportDisplay();
             prd.demographicNo = demo;
             prd.bonusStatus = "N";
             prd.billStatus = "N";

             Date[] begendDates =  getStartEndDate(asofDate);
             Date beginingOfYear  =begendDates[0];
             Date endOfYear = begendDates[1] ;

             Calendar cal = Calendar.getInstance();
                cal.setTime(asofDate);
                cal.add(Calendar.MONTH, -6);
                Date dueDate = cal.getTime();
                //cal.add(Calendar.MONTH,-6);
                Date cutoffDate =  dueDate;//asofDate ; //cal.getTime();
             if(!isOfAge(loggedInInfo, demo.toString(),asofDate)){
                prd.rank = 5;
                prd.lastDate = "------";
                prd.state = "Ineligible";
                prd.numMonths = "------";
                prd.color = "grey";
                inList++;
             }else if (noFutureItems.size() == 0){// no info
                prd.rank = 1;
                prd.lastDate = "------";
                prd.state = "No Info";
                prd.numMonths = "------";
                prd.color = "Magenta";
             } else{
                Map<String,Object> h =  noFutureItems.get(noFutureItems.size()-1);
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                boolean refused = false;


                //This was commented out but it's need for spec 4
                if ( h.get("refused") != null && ((String) h.get("refused")).equals("1")){
                   refused = true;
                }

                String prevDateStr = (String) h.get("prevention_date");

                if (refused && noFutureItems.size() > 1){
                    log.debug("REFUSED AND PREV IS greater than one for demo "+demo);
                    for (int pr = (noFutureItems.size() -2) ; pr > -1; pr--){
                        log.debug("pr #"+pr);
                        Map<String,Object> h2 = noFutureItems.get(pr);
                        log.debug("pr #"+pr+ "  "+((String) h2.get("refused")));
                        if ( h2.get("refused") != null && ((String) h2.get("refused")).equals("0")){
                            prevDateStr = (String) h2.get("prevention_date");
                            log.debug("REFUSED prevDateStr "+prevDateStr);
                            pr = 0;
                        }
                    }
                }


                Date prevDate = null;
                try{
                   prevDate = formatter.parse(prevDateStr);
                }catch (Exception e){
                	//empty
                }

                String numMonths = "------";
                if ( prevDate != null){
                   int num = UtilDateUtilities.getNumMonths(prevDate,asofDate);
                   numMonths = ""+num+" months";
                }

                log.debug("\n\n\n prevDate "+prevDate+" "+demo);
                log.debug("bonusEl start date "+beginingOfYear+ " "+beginingOfYear.before(prevDate));
                log.debug("bonusEl end date "+endOfYear+ " "+endOfYear.after(prevDate));
                log.debug("ASOFDATE "+asofDate);
                if (beginingOfYear.before(prevDate) && endOfYear.after(prevDate) && isOfAge(loggedInInfo, demo.toString(),asofDate)){
                    if( refused ) {
                        prd.billStatus = "Y";
                    }
                    else {
                        prd.bonusStatus = "Y";
                        prd.billStatus = "Y";
                        done++;
                    }
                }
                //outcomes
                log.debug("due Date "+dueDate.toString()+" cutoffDate "+cutoffDate.toString()+" prevDate "+prevDate.toString());
                log.debug("due Date  ("+dueDate.toString()+" ) After Prev ("+prevDate.toString() +" ) "+dueDate.after(prevDate));
                log.debug("cutoff Date  ("+cutoffDate.toString()+" ) before Prev ("+prevDate.toString() +" ) "+cutoffDate.before(prevDate));
                if (!refused && dueDate.after(prevDate) && cutoffDate.before(prevDate)){ // overdue
                   prd.rank = 2;
                   prd.lastDate = prevDateStr;
                   prd.state = "due";
                   prd.numMonths = numMonths;
                   prd.color = "yellow"; //FF00FF

                } else if (!refused && cutoffDate.after(prevDate)){ // overdue
                   prd.rank = 2;
                   prd.lastDate = prevDateStr;
                   prd.state = "Overdue";
                   prd.numMonths = numMonths;
                   prd.color = "red"; //FF00FF

                } else if (refused){  // recorded and refused
                   prd.rank = 3;
                   prd.lastDate = "------";
                   prd.state = "Refused";
                   prd.numMonths = numMonths;
                   prd.color = "orange"; //FF9933
                } else if (dueDate.before(prevDate)  ){  // recorded done
                   prd.rank = 4;
                   prd.lastDate = prevDateStr;
                   prd.state = "Up to date";
                   prd.numMonths = numMonths;
                   prd.color = "green";
                   //done++;
                }
                else
                {
                	log.error("Mised case : refused="+refused+", prevDate="+prevDate+", dueDate="+dueDate+", cutoffDate="+cutoffDate);
                }
             }

             if (asofDate.before(endOfYear) && asofDate.after(beginingOfYear)){

             }else{
                EctMeasurementsDataBeanHandler measurementData = new EctMeasurementsDataBeanHandler(prd.demographicNo,"FLUF");
                log.debug("getting FLUF data for "+prd.demographicNo);
                Collection<EctMeasurementsDataBean> fluFollowupData = measurementData.getMeasurementsData();

                if ( fluFollowupData.size() > 0 ){
                      EctMeasurementsDataBean fluData = fluFollowupData.iterator().next();
                      log.debug("fluData "+fluData.getDataField());
                      log.debug("lastFollowup "+fluData.getDateObservedAsDate()+ " last procedure "+fluData.getDateObservedAsDate());

                      log.debug("toString: "+fluData.toString());
                      prd.lastFollowup = fluData.getDateObservedAsDate();
                      prd.lastFollupProcedure = fluData.getDataField();
                }
                prd.nextSuggestedProcedure = "------";
             }
             prd.nextSuggestedProcedure = letterProcessing(prd, cutoffDate);
             returnReport.add(prd);
          }
          String percentStr = "0";
          double eligible = list.size() - inList;
          log.debug("eligible "+eligible+" done "+done);
          if (eligible != 0){
             double percentage = ( done / eligible ) * 100;
             log.debug("in percentage  "+percentage   +" "+( done / eligible));
             percentStr = ""+Math.round(percentage);
          }

          Collections.sort(returnReport);

          Hashtable<String,Object> h = new Hashtable<String,Object>();

          h.put("up2date",""+Math.round(done));
          h.put("percent",percentStr);
          h.put("returnReport",returnReport);
          h.put("inEligible", ""+inList);
          h.put("eformSearch","Flu");
          h.put("followUpType","FLUF");
          h.put("BillCode", "Q003A");

          log.debug("set returnReport "+returnReport);
          return h;
    }

    boolean ineligible(Hashtable<String,String> h){
       boolean ret =false;
       if ( h.get("refused") != null && ( h.get("refused")).equals("2")){
          ret = true;
       }
       return ret;
   }

   boolean ineligible(ArrayList<Hashtable<String,String>> list){
       for (int i =0; i < list.size(); i ++){
           Hashtable<String,String> h = list.get(i);
           if (ineligible(h)){
               return true;
           }
       }
       return false;
   }

   boolean isOfAge(LoggedInInfo loggedInInfo, String d,Date asofDate){
        boolean isAge = true;
        DemographicData demoData= new DemographicData();
        org.oscarehr.common.model.Demographic demo = demoData.getDemographic(loggedInInfo, d);
        Date demoDOB = DemographicData.getDOBObj(demo);

        Calendar bonusEl = Calendar.getInstance();
        bonusEl.setTime(asofDate);
        int year = bonusEl.get(Calendar.YEAR);
        Calendar cal = new GregorianCalendar(year, Calendar.DECEMBER, 31);
        cal.add(Calendar.YEAR,-65);
        Date cutoff = cal.getTime();
        log.debug("FLU CUT OFFDOB YEAR IS "+cutoff);

        if (demoDOB.after(cutoff)){
            isAge = false;
        }
        return isAge;
   }

   Date[] getStartEndDate(Date asofDate){
       // if prevDate is in the previous year
                Calendar bonusEl = Calendar.getInstance();
                bonusEl.setTime(asofDate);
                Date[] retDates = new Date[2];
                Date endOfYear = null;
                Date beginingOfYear  = null;
                int year = bonusEl.get(Calendar.YEAR);
                log.debug("YEAR FOR FLU BONUS "+year);

                Calendar cal;
                if( bonusEl.get(Calendar.MONTH) == Calendar.JANUARY) {
                   cal = new GregorianCalendar(year, Calendar.FEBRUARY, 1);
                   endOfYear = cal.getTime();
                   cal = new GregorianCalendar(year-1, Calendar.AUGUST, 31);
                   beginingOfYear = cal.getTime();
                }
                else {
                	cal = new GregorianCalendar(year+1, Calendar.FEBRUARY, 1);
                    endOfYear = cal.getTime();
                    cal = new GregorianCalendar(year, Calendar.AUGUST, 31);
                    beginingOfYear = cal.getTime();
                }
               // }
                log.debug("FLU REPORT FOR start: " +beginingOfYear+ " end: "+endOfYear);
                retDates[0] = beginingOfYear;
                retDates[1] = endOfYear;
                return retDates;
   }

   private ArrayList<Map<String,Object>> removeFutureItems(ArrayList<Map<String,Object>> list,Date asOfDate){
       ArrayList<Map<String,Object>> noFutureItems = new ArrayList<Map<String,Object>>();
       DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
       for (int i =0; i < list.size(); i ++){
            Map<String,Object> map = list.get(i);
            String prevDateStr = (String) map.get("prevention_date");
            Date prevDate = null;
            try{
                prevDate = formatter.parse(prevDateStr);
            }catch (Exception e){
            	//empty
            }

            if (prevDate != null && prevDate.before(asOfDate)){
               noFutureItems.add(map);
            }
       }
       return noFutureItems;
   }

   //FLu is different then the others IT only has one letter and a phone call
    @Override
    protected String letterProcessing(PreventionReportDisplay prd, Date cuttoffDate)
    {
       if (prd != null)
       {
          if (PreventionReport.NO_INFO.equals(prd.state) || PreventionReport.DUE.equals(prd.state) || PreventionReport.OVERDUE.equals(prd.state))
          {
              // Get LAST contact method
              EctMeasurementsDataBeanHandler measurementData = new EctMeasurementsDataBeanHandler(prd.demographicNo,"FLUF");
              Collection<EctMeasurementsDataBean> fluFollowupData = measurementData.getMeasurementsData();

              //NO Contact
              if ( fluFollowupData.size() == 0 )
              {
                  prd.nextSuggestedProcedure = PreventionReport.FIRST_LETTER;
              }
              else
              {
                  //There has been contact
                  EctMeasurementsDataBean fluData = fluFollowupData.iterator().next();
                  prd.lastFollowup = fluData.getDateObservedAsDate();
                  prd.lastFollupProcedure = fluData.getDataField();
                  if (prd.lastFollupProcedure.equals(PreventionReport.FIRST_LETTER))
                  {
                      prd.nextSuggestedProcedure = PreventionReport.PHONE_CALL;
                  }
                  else
                  {
                      prd.nextSuggestedProcedure = PreventionReport.NO_FOLLOWUP;
                  }
              }
          }
          else if (PreventionReport.REFUSED.equals(prd.state))
          {  //Not sure what to do about refused
              EctMeasurementsDataBeanHandler measurementData = new EctMeasurementsDataBeanHandler(prd.demographicNo,"FLUF");
              log.debug("getting FLUF data for "+prd.demographicNo);
              Collection<EctMeasurementsDataBean> fluFollowupData = measurementData.getMeasurementsData();

              if ( fluFollowupData.size() > 0 )
              {
                  EctMeasurementsDataBean fluData = fluFollowupData.iterator().next();
                  log.debug("fluData "+fluData.getDataField());
                  log.debug("lastFollowup "+fluData.getDateObservedAsDate()+ " last procedure "+fluData.getDateObservedAsDate());
                  log.debug("CUTTOFF DATE : "+cuttoffDate);
                  log.debug("toString: "+fluData.toString());
                  prd.lastFollowup = fluData.getDateObservedAsDate();
                  prd.lastFollupProcedure = fluData.getDataField();
              }
          }else if(PreventionReport.INELIGIBLE.equals(prd.state))
          {
                // Do nothing
          }
          else if(PreventionReport.UP_TO_DATE.equals(prd.state))
          {
                //Do nothing
              EctMeasurementsDataBeanHandler measurementDataHandler = new EctMeasurementsDataBeanHandler(prd.demographicNo,"FLUF");
              log.debug("getting followup data for "+prd.demographicNo);
              Collection<EctMeasurementsDataBean> followupData = measurementDataHandler.getMeasurementsData();

              if ( followupData.size() > 0 )
              {
                  EctMeasurementsDataBean measurementData = followupData.iterator().next();
                  prd.lastFollowup = measurementData.getDateObservedAsDate();
                  prd.lastFollupProcedure = measurementData.getDataField();
              }
          }
          else
          {
              log.warn("NOT SURE WHAT HAPPEND IN THE LETTER PROCESSING");
        	  log.error("prd.state appears to be null or a missed case : "+prd.state);
          }
          return prd.nextSuggestedProcedure;
       }
       return null;
   }
}

