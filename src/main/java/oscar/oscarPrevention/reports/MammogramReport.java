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
import oscar.oscarPrevention.PreventionData;
import oscar.oscarPrevention.pageUtil.PreventionReportDisplay;
import oscar.util.ConversionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;

/**
 *
 * @author jay
 */
public class MammogramReport extends PreventionsReport{
    private static Logger log = MiscUtils.getLogger();
    /** Creates a new instance of MammogramReport */
    public MammogramReport() {
    }

    public boolean displayNumShots()
    {
        return false;
    }

    public Hashtable runReport(LoggedInInfo loggedInInfo,ArrayList<ArrayList<String>> list,Date asofDate){
        int inList = 0;
        double done= 0,doneWithGrace = 0;
        ArrayList<PreventionReportDisplay> returnReport = new ArrayList<PreventionReportDisplay>();

        for (int i = 0; i < list.size(); i ++){//for each  element in arraylist
             ArrayList<String> fieldList = list.get(i);
             Integer demo =Integer.valueOf(fieldList.get(0));

             //search   prevention_date prevention_type  deleted   refused
             ArrayList<Map<String,Object>>  prevs = PreventionData.getPreventionData(loggedInInfo, "MAM",demo);
             PreventionData.addRemotePreventions(loggedInInfo, prevs, demo,"MAM",null);
             ArrayList<Map<String,Object>> noFutureItems =  removeFutureItems(prevs, asofDate);
             PreventionReportDisplay prd = new PreventionReportDisplay();
             prd.demographicNo = demo;
             prd.bonusStatus = "N";
             prd.billStatus = "N";
             Date prevDate = null;
             if(ineligible(prevs)){
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
                String prevDateStr = (String) h.get("prevention_date");

                try{
                   prevDate = formatter.parse(prevDateStr);
                }catch (Exception e){
                	//empty
                }
                boolean refused = false;
                if ( h.get("refused") != null && ((String) h.get("refused")).equals("1")){
                   refused = true;
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(asofDate);
                cal.add(Calendar.YEAR, -2);
                Date dueDate = cal.getTime();
                cal.add(Calendar.MONTH,-6);
                Date cutoffDate = cal.getTime();

                Calendar cal2 = GregorianCalendar.getInstance();
                cal.setTime(asofDate);
                cal2.add(Calendar.YEAR, -2);
                cal2.add(Calendar.MONTH,-6);
                Date cutoffDate2 = cal2.getTime();

                log.debug("cut 1 "+cutoffDate.toString()+ " cut 2 "+cutoffDate2.toString());

                // if prevDate is less than as of date and greater than 2 years prior
                Calendar bonusEl = Calendar.getInstance();
                bonusEl.setTime(asofDate);
                bonusEl.add(Calendar.MONTH,-30);
                Date bonusStartDate = bonusEl.getTime();

                log.debug("\n\n\n prevDate "+prevDate);
                log.debug("bonusEl date "+bonusStartDate+ " "+bonusEl.after(prevDate));
                log.debug("asofDate date"+asofDate+" "+asofDate.after(prevDate));
                String result = PreventionData.getExtValue((String)h.get("id"), "result");

                if (!refused && bonusStartDate.before(prevDate) && asofDate.after(prevDate) && !result.equalsIgnoreCase("pending")){
                   prd.bonusStatus = "Y";
                   prd.billStatus = "Y";
                   done++;
                }

                //Calendar today = Calendar.getInstance();
                //change as of date to run the report for a different year

                String numMonths = "------";
                if (prevDate != null){
                   long num = ChronoUnit.MONTHS.between(ConversionUtils.toLocalDate(ConversionUtils.toDateString(prevDate)),
                                ConversionUtils.toLocalDate(ConversionUtils.toDateString(asofDate)));
                   numMonths = ""+num+" months";
                }


                //outcomes
                log.debug("due Date "+dueDate.toString()+" cutoffDate "+cutoffDate.toString()+" prevDate "+prevDate.toString());
                log.debug("due Date  ("+dueDate.toString()+" ) After Prev ("+prevDate.toString() +" ) "+dueDate.after(prevDate));
                log.debug("cutoff Date  ("+cutoffDate.toString()+" ) before Prev ("+prevDate.toString() +" ) "+cutoffDate.before(prevDate));

                 // due
                if (!refused && dueDate.after(prevDate) && cutoffDate.before(prevDate))
                {
                   prd.rank = 2;
                   prd.lastDate = prevDateStr;
                   prd.state = "due";
                   prd.numMonths = numMonths;
                   prd.color = "yellow"; //FF00FF
                   if(!prd.bonusStatus.equals("Y"))
                   {
                       prd.billStatus = "Y";
                       doneWithGrace++;
                   }
                }
                // overdue
                else if (!refused && cutoffDate.after(prevDate))
                {
                   prd.rank = 2;
                   prd.lastDate = prevDateStr;
                   prd.state = "Overdue";
                   prd.numMonths = numMonths;
                   prd.color = "red"; //FF00FF
                }

                // recorded and refused
                else if (refused)
                {
                   prd.rank = 3;
                   prd.lastDate = "------";
                   prd.state = "Refused";
                   prd.numMonths = numMonths;
                   prd.color = "orange"; //FF9933

                }

                //pending
                else if( dueDate.before(prevDate) && result.equalsIgnoreCase("pending") )
                {
                    prd.rank = 4;
                    prd.lastDate = prevDateStr;
                    prd.state = "Pending";
                    prd.numMonths = numMonths;
                    prd.color = "pink";
                }

                // recorded done
                else if (dueDate.before(prevDate))
                {
                   prd.rank = 4;
                   prd.lastDate = prevDateStr;
                   prd.state = "Up to date";
                   prd.numMonths = numMonths;
                   prd.color = "green";
                   //done++;
                }
             }
             prd.nextSuggestedProcedure = letterProcessing( prd,"MAMF", asofDate, prevDate);
             returnReport.add(prd);
          }

          String percentStr = "0";
          String percentWithGraceStr = "0";
          double eligible = list.size() - inList;
          log.debug("eligible "+eligible+" done "+done);
          if (eligible != 0){
             double percentage = ( done / eligible ) * 100;
             double percentageWithGrace =  (done+doneWithGrace) / eligible  * 100 ;
             log.debug("in percentage  "+percentage   +" "+( done / eligible));
             percentStr = ""+Math.round(percentage);
             percentWithGraceStr = ""+Math.round(percentageWithGrace);
          }

          Collections.sort(returnReport);

          Hashtable<String,Object> h = new Hashtable<String,Object>();

          h.put("up2date",""+Math.round(done));
          h.put("percent",percentStr);
          h.put("percentWithGrace",percentWithGraceStr);
          h.put("returnReport",returnReport);
          h.put("inEligible", ""+inList);
          h.put("eformSearch","Mam");
          h.put("followUpType","MAMF");
          h.put("BillCode", "Q002A");
          log.debug("set returnReport "+returnReport);
          return h;
    }

    boolean ineligible(Map<String,Object> h){
       boolean ret =false;
       if ( h.get("refused") != null && ((String) h.get("refused")).equals("2")){
          ret = true;
       }
       return ret;
   }

    boolean ineligible(ArrayList<Map<String,Object>> list){
       for (int i =0; i < list.size(); i ++){
    	   Map<String,Object> h = list.get(i);
           if (ineligible(h)){
               return true;
           }
       }
       return false;
   }

   private ArrayList<Map<String,Object>> removeFutureItems(ArrayList<Map<String,Object>> list,Date asOfDate){
       ArrayList<Map<String,Object>> noFutureItems = new ArrayList<Map<String,Object>>();
       DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
       for (int i =0; i < list.size(); i ++){
    	   Map<String,Object> h = list.get(i);
            String prevDateStr = (String) h.get("prevention_date");
            Date prevDate = null;
            try{
                prevDate = formatter.parse(prevDateStr);
            }catch (Exception e){
            	//empty
            }

            if (prevDate != null && prevDate.before(asOfDate)){
               noFutureItems.add(h);
            }
       }
       return noFutureItems;
   }

    @Override
    protected String letterProcessing(PreventionReportDisplay prd, Date cuttoffDate)
    {
        return null;
    }
}
