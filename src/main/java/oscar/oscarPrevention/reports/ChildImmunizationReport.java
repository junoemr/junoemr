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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;

import org.oscarehr.util.SpringUtils;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBean;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;
import oscar.oscarPrevention.PreventionData;
import oscar.oscarPrevention.pageUtil.PreventionReportDisplay;
import oscar.util.ConversionUtils;
import oscar.util.UtilDateUtilities;

/**
 *
 * @author jay
 */
public class ChildImmunizationReport implements PreventionReport{

    public static final String PREVENTION_DTAP_IPV = "DTaP-IPV";
    public static final String PREVENTION_DTAP_IPV_HIB = "DTaP-IPV-Hib";
    public static final String PREVENTION_HIB = "Hib";
    public static final String PREVENTION_MMR = "MMR";
    public static final String PREVENTION_MMRV = "MMRV";
    private static final Logger log = MiscUtils.getLogger();

    //Sort class for preventions used to sort final list of dtap preventions
    class DtapComparator implements Comparator<Map<String, Object>> {

        public int compare(Map<String, Object> x, Map<String, Object> y) {
            return ((String)x.get("prevention_date")).compareTo(((String)y.get("prevention_date")));
        }
    }

    /** Creates a new instance of ChildImmunizationReport */
    public ChildImmunizationReport()
    {
    }

    public boolean displayNumShots()
    {
        return true;
    }

    public Hashtable<String,Object> runReport(LoggedInInfo loggedInInfo, ArrayList<ArrayList<String>> list,Date asofDate)
    {
        int inList = 0;
        double done = 0;
        List<PreventionReportDisplay> returnReport = new ArrayList<>();
        DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographic.dao.DemographicDao");

        int dontInclude = 0;
        for (List<String> fieldList : list)
        {
            Integer demo = Integer.parseInt(fieldList.get(0));
            Demographic demoData = demographicDao.find(demo);

            // search prevention_date prevention_type deleted refused
            ArrayList<Map<String, Object>> preventionsDTaPIPV = PreventionData.getPreventionData(loggedInInfo, PREVENTION_DTAP_IPV, demo);
            PreventionData.addRemotePreventions(loggedInInfo, preventionsDTaPIPV, demo, PREVENTION_DTAP_IPV, null);

            ArrayList<Map<String, Object>> prevsDtapIPVHIB = PreventionData.getPreventionData(loggedInInfo, PREVENTION_DTAP_IPV_HIB, demo);
            PreventionData.addRemotePreventions(loggedInInfo, prevsDtapIPVHIB, demo, PREVENTION_DTAP_IPV_HIB, null);

            ArrayList<Map<String, Object>> preventionsHiB = PreventionData.getPreventionData(loggedInInfo, PREVENTION_HIB, demo);
            PreventionData.addRemotePreventions(loggedInInfo, preventionsHiB, demo, PREVENTION_HIB, null);

            ArrayList<Map<String, Object>> preventionsMMR = PreventionData.getPreventionData(loggedInInfo, PREVENTION_MMR, demo);
            PreventionData.addRemotePreventions(loggedInInfo, preventionsMMR, demo, PREVENTION_MMR, null);
            preventionsMMR.addAll(PreventionData.getPreventionData(loggedInInfo, PREVENTION_MMRV, demo));
            PreventionData.addRemotePreventions(loggedInInfo, preventionsMMR, demo, PREVENTION_MMRV, null);

            //need to compile accurate dtap numbers
            Map<String, Object> hDtapIpv;
            Map<String, Object> hDtapIpvHib;
            boolean add;

            for (Map<String, Object> stringObjectMap : prevsDtapIPVHIB)
            {
                hDtapIpvHib = stringObjectMap;
                add = true;
                for (Map<String, Object> objectMap : preventionsDTaPIPV)
                {
                    hDtapIpv = objectMap;
                    if (((String) hDtapIpvHib.get("prevention_date")).equals((hDtapIpv.get("prevention_date"))))
                    {
                        add = false;
                        break;
                    }
                }

                if (add)
                {
                    preventionsDTaPIPV.add(hDtapIpvHib);
                }
            }

            preventionsDTaPIPV.sort(new DtapComparator());

            int numDtap = preventionsDTaPIPV.size();  //4
            int numHib = preventionsHiB.size();  //4
            int numMMR = preventionsMMR.size();  //1

            log.debug("prev1 " + preventionsDTaPIPV.size() + " prevs2 " + preventionsHiB.size() + " prev4 " + preventionsMMR.size());

            // This a kludge to get by conformance testing in ontario -- needs to be done in a smarter way
            int totalImmunizations = numDtap + numHib + numMMR;
            int recommTotal = 5; //9;NOT SURE HOW HIB WORKS
            long ageInMonths = ChronoUnit.MONTHS.between(
                    demoData.getDateOfBirth(),
                    ConversionUtils.toLocalDate(ConversionUtils.toDateString(asofDate))
            );
            PreventionReportDisplay prd = new PreventionReportDisplay();
            prd.demographicNo = demo;
            prd.bonusStatus = "N";
            prd.billStatus = "N";
            prd.numShots = "0";
            if (totalImmunizations == 0)
            {// no info
                prd.rank = 1;
                prd.lastDate = "------";
                prd.state = "No Info";
                prd.numMonths = "------";
                prd.color = "Magenta";
            }
            else if ((preventionsDTaPIPV.size() > 0 && ineligible(preventionsDTaPIPV.get(preventionsDTaPIPV.size() - 1)))
                    || (preventionsHiB.size() > 0 && ineligible(preventionsHiB.get(preventionsHiB.size() - 1)))
                    || (preventionsMMR.size() > 0 && ineligible(preventionsMMR.get(preventionsMMR.size() - 1))))
            {
                prd.rank = 5;
                prd.lastDate = "------";
                prd.state = "Ineligible";
                prd.numMonths = "------";
                prd.color = "grey";
                inList++;
            }
            else
            {

                boolean refused = false;
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                Date lastDate = null;
                String prevDateStr = "";

                if (preventionsDTaPIPV.size() > 0)
                {
                    Map<String, Object> hDtap = preventionsDTaPIPV.get(preventionsDTaPIPV.size() - 1);
                    if (hDtap.get("refused") != null && ((String) hDtap.get("refused")).equals("1"))
                    {
                        refused = true;
                    }
                    prevDateStr = (String) hDtap.get("prevention_date");
                    try
                    {
                        lastDate = formatter.parse(prevDateStr);
                    }
                    catch (Exception e)
                    {
                        MiscUtils.getLogger().error("Error", e);
                    }
                }

                if (preventionsMMR.size() > 0)
                {
                    Map<String, Object> hMMR = preventionsMMR.get(0);  //Changed to get first MMR value instead of last value
                    if (hMMR.get("refused") != null && ((String) hMMR.get("refused")).equals("1"))
                    {
                        refused = true;
                    }

                    String mmrDateStr = (String) hMMR.get("prevention_date");
                    Date prevDate = null;
                    try
                    {
                        prevDate = formatter.parse(mmrDateStr);
                        if (prevDate.after(lastDate))
                        {
                            lastDate = prevDate;
                            prevDateStr = mmrDateStr;
                        }
                    }
                    catch (Exception e)
                    {
                        MiscUtils.getLogger().error("Error", e);
                    }
                }

                String numMonths = "------";
                if (lastDate != null)
                {
                    int num = UtilDateUtilities.getNumMonths(lastDate, asofDate);
                    numMonths = "" + num + " months";
                }

                // Converting to date simply to get this code working w/ new demographic model
                Date dob = ConversionUtils.toLegacyDate(demoData.getDateOfBirth());
                Calendar cal = Calendar.getInstance();
                cal.setTime(dob);
                cal.add(Calendar.MONTH, 30);
                Date twoYearsAfterDOB = cal.getTime();
                if (lastDate != null)
                {
                    log.debug("twoYearsAfterDOB date " + twoYearsAfterDOB + " " + lastDate.before(twoYearsAfterDOB));
                    if (!refused && (totalImmunizations >= recommTotal) && lastDate.before(twoYearsAfterDOB) && (ageInMonths >= 18))
                    {
                        prd.bonusStatus = "Y";
                        prd.billStatus = "Y";
                        done++;
                    }
                }
                //outcomes
                if (!refused && totalImmunizations < recommTotal && ageInMonths >= 18 && ageInMonths <= 23)
                { // less < 9
                    prd.rank = 2;
                    prd.lastDate = prevDateStr;
                    prd.state = "due";
                    prd.numMonths = numMonths;
                    prd.numShots = "" + totalImmunizations;
                    prd.color = "yellow"; //FF00FF

                }
                else if (!refused && totalImmunizations < recommTotal && ageInMonths > 23)
                { // overdue
                    prd.rank = 2;
                    prd.lastDate = prevDateStr;
                    prd.state = "Overdue";
                    prd.numMonths = numMonths;
                    prd.numShots = "" + totalImmunizations;
                    prd.color = "red"; //FF00FF

                }
                else if (refused)
                {  // recorded and refused
                    prd.rank = 3;
                    prd.lastDate = "-----";
                    prd.state = "Refused";
                    prd.numMonths = numMonths;
                    prd.numShots = "" + totalImmunizations;
                    prd.color = "orange"; //FF9933
                }
                else if (totalImmunizations >= recommTotal)
                {  // recorded done
                    prd.rank = 4;
                    prd.lastDate = prevDateStr;
                    prd.state = "Up to date";
                    prd.numMonths = numMonths;
                    prd.numShots = "" + totalImmunizations;
                    prd.color = "green";
                    //done++;
                }
                else
                {
                    prd.state = "------";
                    prd.lastDate = prevDateStr;
                    prd.numMonths = numMonths;
                    prd.numShots = "" + totalImmunizations;
                    prd.color = "white";
                    dontInclude++;
                }


            }

            letterProcessing(prd, "CIMF", asofDate);
            returnReport.add(prd);

        }
          String percentStr = "0";
          double eligible = list.size() - inList - dontInclude;
          log.debug("eligible "+eligible+" done "+done);
          if (eligible != 0){
             double percentage = ( done / eligible ) * 100;
             log.debug("in percentage  "+percentage   +" "+( done / eligible));
             percentStr = ""+Math.round(percentage);
          }


            Collections.sort(returnReport);

          Hashtable<String,Object> returnHash = new Hashtable<String,Object>();

          returnHash.put("up2date",""+Math.round(done));
          returnHash.put("percent",percentStr);
          returnHash.put("returnReport",returnReport);
          returnHash.put("inEligible", ""+inList);
          returnHash.put("eformSearch","CHI");
          returnHash.put("followUpType","CIMF");
          returnHash.put("BillCode", "Q004A");
          log.debug("set returnReport "+returnReport);
          return returnHash;
    }

    boolean ineligible(Map<String, Object> h){
       boolean ret =false;
       if ( h.get("refused") != null && ((String) h.get("refused")).equals("2")){
          ret = true;
       }
       return ret;
   }




   //TODO-legacy: THIS MAY NEED TO BE REFACTORED AT SOME POINT IF MAM and PAP are exactly the same

                //Get last contact method?
                    //NO contact
                        //Send letter
                    //Was it atleast 3months ago?
                        //WAS is L1
                            //SEnd L2
                        //Was is L2
                            //P1

   //Measurement Type will be 1 per Prevention report, with the dataField holding method ie L1, L2, P1 (letter 1 , letter 2, phone call 1)
   String LETTER1 = "L1";
   String LETTER2 = "L2";
   String PHONE1 = "P1";

   private String letterProcessing(PreventionReportDisplay prd,String measurementType,Date asofDate){
       if (prd != null){
          if (prd.state.equals("No Info") || prd.state.equals("due") || prd.state.equals("Overdue") ){
              // Get LAST contact method
              EctMeasurementsDataBeanHandler measurementDataHandler = new EctMeasurementsDataBeanHandler(prd.demographicNo,measurementType);
              log.debug("getting followup data for "+prd.demographicNo);

              Collection followupData = measurementDataHandler.getMeasurementsData();
              //NO Contact
              if ( followupData.size() == 0 ){
                  prd.nextSuggestedProcedure = this.LETTER1;
                  return this.LETTER1;
              }else{ //There has been contact
            	  
                  Calendar threemonth = Calendar.getInstance();
                  threemonth.setTime(asofDate);
                  threemonth.add(Calendar.MONTH,-1);
                  Date onemon = threemonth.getTime();
                  threemonth.add(Calendar.MONTH,-2);
                  Date threemon = threemonth.getTime();               
                  Date observationDate = null;
                  int count = 0;
                  int index = 0;
                  EctMeasurementsDataBean measurementData = null;
                  
                  @SuppressWarnings("unchecked")
            	  Iterator<EctMeasurementsDataBean>iterator = followupData.iterator();                                    
                  
                  while(iterator.hasNext()) {
                	  measurementData =  iterator.next();
                	  observationDate = measurementData.getDateObservedAsDate();
                	  
                	  if( index == 0 ) {
                          log.debug("fluData "+measurementData.getDataField());
                          log.debug("lastFollowup "+measurementData.getDateObservedAsDate()+ " last procedure "+measurementData.getDateObservedAsDate());
                          log.debug("toString: "+measurementData.toString());
                          prd.lastFollowup = observationDate;
                          prd.lastFollupProcedure = measurementData.getDataField();

                          if( prd.lastFollupProcedure.equals(this.PHONE1)) {
                        	  prd.nextSuggestedProcedure = "----";
                        	  return "----";
                          }

                	  }
                	  
                	  
                	  log.debug(prd.demographicNo + " obs" + observationDate + String.valueOf(observationDate.before(onemon)) + " threeMth " + threemon + " " + String.valueOf(observationDate.after(threemon)));
                	  if( observationDate.before(onemon) && observationDate.after(threemon)) {                		  
                		  ++count;
                	  }
                	  
                	  ++index;

                  }
                  
                  switch (count) {
                  case 0: 
                   	  prd.nextSuggestedProcedure = this.LETTER1;
                	  break;
                  case 1:
                	  prd.nextSuggestedProcedure = this.LETTER2;
                	  break;
                  case 2:
                	  prd.nextSuggestedProcedure = this.PHONE1;
                	  break;
                  default:
                	  prd.nextSuggestedProcedure = "----";
                  }
                  
                  return prd.nextSuggestedProcedure;

              }
          }else if (prd.state.equals("Refused") ){  //Not sure what to do about refused
                //prd.lastDate = "-----";

              EctMeasurementsDataBeanHandler measurementDataHandler = new EctMeasurementsDataBeanHandler(prd.demographicNo,measurementType);
              log.debug("2getting followup data for "+prd.demographicNo);
              Collection followupData = measurementDataHandler.getMeasurementsData();

              if ( followupData.size() > 0 ){
                  EctMeasurementsDataBean measurementData = (EctMeasurementsDataBean) followupData.iterator().next();
                  prd.lastFollowup = measurementData.getDateObservedAsDate();
                  prd.lastFollupProcedure = measurementData.getDataField();
              }

              prd.nextSuggestedProcedure = "----";
                //prd.numMonths ;
          }else if(prd.state.equals("Ineligible")){
                // Do nothing
                prd.nextSuggestedProcedure = "----";
          }else if(prd.state.equals("Up to date")){
                //Do nothing
              prd.nextSuggestedProcedure = "----";
          }else{
               log.debug("NOT SURE WHAT HAPPEND IN THE LETTER PROCESSING");
          }
       }
       return null;
   }


}
