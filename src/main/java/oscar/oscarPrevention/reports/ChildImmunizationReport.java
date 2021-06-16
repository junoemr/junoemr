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
import org.oscarehr.common.model.Demographic;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Childhood Immunization Cumulative Preventative Care Bonus (April 2020)
 *
 * This bonus is based on the percentage of the target population who have received all of the
 * ministry supplied immunizations as recommended by the National Advisory Committee on
 * Immunization.  The target population consists of enrolled patients who are aged 30 to 42
 * months of age, inclusive as of March 31st of the fiscal year for which the bonus is being
 * claimed.  These patients must have received all applicable immunizations by 30 months of age
 *
 */
public class ChildImmunizationReport implements PreventionReport {

	private static DemographicManager demographicManager = SpringUtils.getBean(DemographicManager.class);
	
    public boolean displayNumShots()
    {
        return true;
    }

    //Sort class for preventions used to sort final list of dtap preventions
    class DtapComparator implements Comparator<Map<String, Object>> {

        public int compare(Map<String, Object> x, Map<String, Object> y) {
            return ((String)x.get("prevention_date")).compareTo(((String)y.get("prevention_date")));
        }
    }

    private static Logger log = MiscUtils.getLogger();

    /**
     * @param loggedInInfo LoggedInInfo
     * @param list List of demographics. Each demographic should take the form of a 3 member list  <List><List>{demoNo, lastName, firstName}</List></List>
     *             except that the contents (and order?) of that list are determined by the demographic query.
     *
     * @param asOfDate Date to use as "today" for the purposes of the calculation.  For this report to be accurate for bookkeeping and
     *                 billing, this system to be refactored such that the hard coded date is always March 31st (ie: YYYY-03-31) since
     *                 that is the end of the fiscal year, and all ages are relative to that day.)
     *
     * @return a hashtable of report parameters
     */
    public Hashtable<String,Object> runReport(LoggedInInfo loggedInInfo, ArrayList<ArrayList<String>> list, Date asOfDate)
    {
        List<PreventionReportDisplay> childhoodImmunizationReport = new ArrayList<>();
        int eligiblePatientCount = 0;
        int qualifiesForBonusCount  = 0;
        int ineligiblePatientCount = 0;

        List<ReportPatientInfo> patientInfoList = ReportPatientInfo.fromList(list);
        
        for (ReportPatientInfo patientInfo : patientInfoList)
        {
        	Demographic demographic = demographicManager.getDemographic(loggedInInfo, patientInfo.demographicNo);
            //  Each Map<String,Object> is a prevention item, with field names as keys... this is ridiculous.
            ArrayList<Map<String, Object>> preventions = PreventionData.getPreventionData(loggedInInfo, demographic.getDemographicNo());

            // These two fields track legacy behaviour.  I'm not sure at this moment if they are correct in terms of billing or bonus calculations
            boolean atLeastOneRefused = false;
            boolean atLeastOneIneligible = false;

            Date latestPrevention = null;
            
            Map<String, Integer> immunizationsRemaining = createChildhoodSchedule();
            for (Map<String, Object> prevention : preventions)
            {
             
            	String type = (String) prevention.get("type");
                switch (type)
                {
                    case "DTaP-IPV-Hib":
                    case "Pneu-C":
                    case "Rot":
                    case "MenC-C":
                    case "MMR":
                    {
                        boolean refused = String.valueOf(Prevention.REFUSED_STATUS_REFUSED).equals(prevention.get("refused"));
                        boolean ineligible = String.valueOf(Prevention.REFUSED_STATUS_INELIGIBLE).equals(prevention.get("refused"));

                        if (refused)
                        {
                            atLeastOneRefused = true;
                        }
	                    else if (ineligible)
                        {
                            atLeastOneIneligible = true;
                        }
	                    else
                        {
                            immunizationsRemaining.put(type, immunizationsRemaining.get(type) - 1);

                            Date preventionDate = (Date) prevention.get("prevention_date_asDate");
                            if (latestPrevention == null || preventionDate.after(latestPrevention))
                            {
                                latestPrevention = preventionDate;
                            }
                        }
                        break;
                    }
                    default:
                        break;
                }
            }

            
            int immunizationsCompleted = calculateScheduleCompletion(immunizationsRemaining);
	
	        // This entire part needs to be refactored, this is running DB queries in a loop for every single row in the table.
	        // It can be put into the ReportPatientInfo class, but nothing is actually guaranteed to be in there...
	        // Ideally this would use a hard coded sql query with the only parameter being the rostered provider.
	        
            PreventionReportDisplay entry = createReportEntry(demographic, immunizationsCompleted, latestPrevention, asOfDate, atLeastOneRefused, atLeastOneIneligible);
	        letterProcessing(entry,"CIMF",asOfDate);
         
	        childhoodImmunizationReport.add(entry);

            if (entry.state.equals("Ineligible"))
            {
            	// For some reason the "-------" state (ie: untargeted) doesn't count as ineligible.
                ineligiblePatientCount++;
            }
            else if (!entry.state.equals("------"))
            {
                eligiblePatientCount++;
            }

            if (entry.bonusStatus.equals("Y"))
            {
                qualifiesForBonusCount++;
            }
        }

        int percentCompliant = Math.round(((float)qualifiesForBonusCount / (float)eligiblePatientCount) * 100);
        
        String contactBillingCode = "Q004A";

        Hashtable<String,Object> reportParams = new Hashtable<>();

        reportParams.put("up2date", String.valueOf(qualifiesForBonusCount));
        reportParams.put("percent",  String.valueOf(percentCompliant));
        reportParams.put("returnReport", childhoodImmunizationReport);
        reportParams.put("inEligible", String.valueOf(ineligiblePatientCount));
        reportParams.put("eformSearch","CHI");
        reportParams.put("followUpType","CIMF");
        reportParams.put("BillCode", contactBillingCode);

        return reportParams;
    }
    
	public PreventionReportDisplay createReportEntry(Demographic patientInfo, int immunizationsCompleted, Date latestPrevention, Date asOfDate, boolean refused, boolean ineligible)
    {
    	// demoAge is relative to asOfDate;
	
	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    PreventionReportDisplay entry = new PreventionReportDisplay();
	    
	    entry.demographicNo = patientInfo.getDemographicNo();
	    entry.numShots = Integer.toString(immunizationsCompleted);
	    
	    if (latestPrevention != null)
	    {
		    entry.lastDate = dateFormat.format(latestPrevention);
		
		    int monthsSinceLastPrevention = UtilDateUtilities.getNumMonths(latestPrevention, asOfDate);
		    entry.numMonths = monthsSinceLastPrevention + " months";
	    }
	    else
	    {
	    	entry.lastDate = "------";
	    	entry.numMonths = "------";
	    }
	    
	    boolean eligibleForBonus = eligibleForBonus(patientInfo, immunizationsCompleted, asOfDate, latestPrevention);
	    if (eligibleForBonus && !refused && !ineligible)
	    {
		    // Legacy implementation had the billing and bonus status being set to "Y" at the same time.
		    // I don't know why (or what billStatus is), so I am leaving it that way until I know more.
	    	entry.bonusStatus = "Y";
	    	entry.billStatus = "Y";
	    }
	    else
	    {
	    	entry.bonusStatus = "N";
	    	entry.billStatus = "N";
	    }
	    
	    if (ineligible)
        {
            entry.rank = 5;
            entry.state = "Ineligible";
            entry.color = "grey";
        }
        else if (refused)
        {
            entry.rank = 3;
            entry.state = "Refused";
            entry.color = "orange";
        }
        else if (immunizationsCompleted == 0 && latestPrevention == null)
        {
            entry.rank = 1;
            entry.state = "No Info";
            entry.color = "magenta";
        }
        else if (immunizationsCompleted >= 5)
        {
        	// Note it is possible to be up to date AND NOT bonus eligible if
	        // the latest vaccination is given past 30mo or patient is not in age range.
            entry.rank = 4;
            entry.state = "Up to date";
            entry.color = "green";
        }
        else if (immunizationsCompleted < 5)
        {
            entry.rank = 2;
            entry.state = "due";
            entry.color = "yellow";
        }
        // TODO decide whether to leave this in...
        else
	    {
		    // If age is ever considered, this should be accessible for demographics which are missing 1 or more shots
		    // but have not aged into the age required for prevention targeting.  Left in for legacy purposes.
		    entry.state = "other";
		    entry.color = "white";
	    }
        return entry;
    }

    public Hashtable<String,Object> runReport(LoggedInInfo loggedInInfo, ArrayList<ArrayList<String>> list,Date asofDate, boolean foobar){
        int inList = 0;
        double done= 0;
        ArrayList<PreventionReportDisplay> returnReport = new ArrayList<PreventionReportDisplay>();

        int dontInclude = 0;
          for (int i = 0; i < list.size(); i ++){//for each  element in arraylist
             ArrayList<String> fieldList = list.get(i);
             log.debug("list "+list.size());
             Integer demo = Integer.parseInt(fieldList.get(0));
             log.debug("fieldList "+fieldList.size());

			// search prevention_date prevention_type deleted refused
			ArrayList<Map<String, Object>> prevs1 = PreventionData.getPreventionData(loggedInInfo, "DTap-IPV", demo);
			PreventionData.addRemotePreventions(loggedInInfo, prevs1, demo,"DTap-IPV",null);
			ArrayList<Map<String, Object>> prevsDtapIPVHIB = PreventionData.getPreventionData(loggedInInfo, "DTaP-IPV-Hib", demo);
			PreventionData.addRemotePreventions(loggedInInfo, prevsDtapIPVHIB, demo,"DTaP-IPV-Hib",null);
			ArrayList<Map<String, Object>> prevs2 = PreventionData.getPreventionData(loggedInInfo, "Hib", demo);
			PreventionData.addRemotePreventions(loggedInInfo, prevs2, demo,"Hib",null);
			ArrayList<Map<String, Object>> prevs4 = PreventionData.getPreventionData(loggedInInfo, "MMR",demo);
			PreventionData.addRemotePreventions(loggedInInfo, prevs4, demo,"MMR",null);
			prevs4.addAll(PreventionData.getPreventionData(loggedInInfo, "MMRV", demo));
			PreventionData.addRemotePreventions(loggedInInfo, prevs4, demo,"MMRV",null);

             //need to compile accurate dtap numbers
			 Map<String, Object> hDtapIpv, hDtapIpvHib;
             boolean add;

             for( int idx = 0; idx < prevsDtapIPVHIB.size(); ++idx ) {
                 hDtapIpvHib = prevsDtapIPVHIB.get(idx);
                 add = true;
                 for( int idx2 = 0; idx2 < prevs1.size(); ++idx2 ) {
                     hDtapIpv = prevs1.get(idx2);
                     if(((String)hDtapIpvHib.get("prevention_date")).equals((hDtapIpv.get("prevention_date")))) {
                         add = false;
                         break;
                     }
                 }

                 if( add ) {
                     prevs1.add(hDtapIpvHib);
                 }
             }


             Collections.sort(prevs1, new DtapComparator());

             int numDtap = prevs1.size();  //4
             int numHib  = prevs2.size();  //4
             int numMMR  = prevs4.size();  //1

             log.debug("prev1 "+prevs1.size()+ " prevs2 "+ prevs2.size() +" prev4 "+prevs4.size());

             DemographicData dd = new DemographicData();
             org.oscarehr.common.model.Demographic demoData = dd.getDemographic(loggedInInfo, demo.toString());
             // This a kludge to get by conformance testing in ontario -- needs to be done in a smarter way
             int totalImmunizations = numDtap + /*numHib +*/ numMMR ;
             int recommTotal = 5; //9;NOT SURE HOW HIB WORKS
             int ageInMonths = DemographicData.getAgeInMonthsAsOf(demoData,asofDate);
             PreventionReportDisplay prd = new PreventionReportDisplay();
             prd.demographicNo = demo;
             prd.bonusStatus = "N";
             prd.billStatus = "N";
             if (totalImmunizations == 0){// no info
                prd.rank = 1;
                prd.lastDate = "------";
                prd.state = "No Info";
                prd.numMonths = "------";
                prd.color = "Magenta";
             }else if(  (  prevs1.size()> 0 &&  ineligible(prevs1.get(prevs1.size()-1))) || (  prevs2.size()> 0 && ineligible(prevs2.get(prevs2.size()-1))) || (  prevs4.size()> 0 && ineligible(prevs4.get(prevs4.size()-1))) ){
                prd.rank = 5;
                prd.lastDate = "------";
                prd.state = "Ineligible";
                prd.numMonths = "------";
                prd.color = "grey";
                inList++;
            }else{

                boolean refused = false;
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                Date lastDate = null;
                String prevDateStr = "";

                if(prevs1.size() > 0){
                	Map<String, Object> hDtap = prevs1.get(prevs1.size()-1);
                   if ( hDtap.get("refused") != null && ((String) hDtap.get("refused")).equals("1")){
                      refused = true;
                   }
                   prevDateStr = (String) hDtap.get("prevention_date");
                   try{
                      lastDate = formatter.parse(prevDateStr);
                   }catch (Exception e){MiscUtils.getLogger().error("Error", e);}
                }

                if(prevs4.size() > 0){
                	Map<String, Object> hMMR  = prevs4.get(0);  //Changed to get first MMR value instead of last value
                   if ( hMMR.get("refused") != null && ((String) hMMR.get("refused")).equals("1")){
                      refused = true;
                   }

                   String mmrDateStr = (String) hMMR.get("prevention_date");
                   Date prevDate = null;
                   try{
                      prevDate = formatter.parse(mmrDateStr);
                      if (prevDate.after(lastDate)){
                         lastDate = prevDate;
                         prevDateStr = mmrDateStr;
                      }
                   }catch (Exception e){MiscUtils.getLogger().error("Error", e);}
                }

                String numMonths = "------";
                if ( lastDate != null){
                   int num = UtilDateUtilities.getNumMonths(lastDate,asofDate);
                   numMonths = ""+num+" months";
                }

                Date dob = dd.getDemographicDOB(loggedInInfo, demo.toString());
                Calendar cal = Calendar.getInstance();
                cal.setTime(dob);
                cal.add(Calendar.MONTH, 30);
                Date twoYearsAfterDOB = cal.getTime();
                if( lastDate != null ) {
                    log.debug("twoYearsAfterDOB date "+twoYearsAfterDOB+ " "+lastDate.before(twoYearsAfterDOB) );
                    if (!refused && (totalImmunizations >= recommTotal  ) && lastDate.before(twoYearsAfterDOB) && ( ageInMonths >= 18 )){//&& endOfYear.after(prevDate)){
                       prd.bonusStatus = "Y";
                       prd.billStatus = "Y";
                       done++;
                    }
                }
                //outcomes
                if (!refused && totalImmunizations < recommTotal && ageInMonths >= 18 && ageInMonths <= 23){ // less < 9
                   prd.rank = 2;
                   prd.lastDate = prevDateStr;
                   prd.state = "due";
                   prd.numMonths = numMonths;
                   prd.numShots = ""+totalImmunizations;
                   prd.color = "yellow"; //FF00FF

                } else if (!refused && totalImmunizations < recommTotal && ageInMonths > 23 ){ // overdue
                   prd.rank = 2;
                   prd.lastDate = prevDateStr;
                   prd.state = "Overdue";
                   prd.numMonths = numMonths;
                   prd.numShots = ""+totalImmunizations;
                   prd.color = "red"; //FF00FF

                } else if (refused){  // recorded and refused
                   prd.rank = 3;
                   prd.lastDate = "-----";
                   prd.state = "Refused";
                   prd.numMonths = numMonths;
                   prd.numShots = ""+totalImmunizations;
                   prd.color = "orange"; //FF9933
                } else if (totalImmunizations >= recommTotal  ){  // recorded done
                   prd.rank = 4;
                   prd.lastDate = prevDateStr;
                   prd.state = "Up to date";
                   prd.numMonths = numMonths;
                   prd.numShots = ""+totalImmunizations;
                   prd.color = "green";
                   //done++;
                }else{
                   prd.state = "------";
                   prd.lastDate = prevDateStr;
                   prd.numMonths = numMonths;
                   prd.numShots = ""+totalImmunizations;
                   prd.color = "white";
                   dontInclude++;
                }


             }

             letterProcessing( prd,"CIMF",asofDate);
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

          Hashtable<String,Object> h = new Hashtable<String,Object>();

          h.put("up2date",""+Math.round(done));
          h.put("percent",percentStr);
          h.put("returnReport",returnReport);
          h.put("inEligible", ""+inList);
          h.put("eformSearch","CHI");
          h.put("followUpType","CIMF");
          h.put("BillCode", "Q004A");
          log.debug("set returnReport "+returnReport);
          return h;
    }

    boolean ineligible(Map<String, Object> h){
       boolean ret =false;
       if ( h.get("refused") != null && ((String) h.get("refused")).equals("2")){
          ret = true;
       }
       return ret;
   }




   //TODO: THIS MAY NEED TO BE REFACTORED AT SOME POINT IF MAM and PAP are exactly the same

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

              Collection followupData = measurementDataHandler.getMeasurementsDataVector();
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

                  /*if ( measurementData.getDateObservedAsDate().before(onemon)){
                	  
                      if (prd.lastFollupProcedure.equals(this.LETTER1)){
                                    prd.nextSuggestedProcedure = this.LETTER2;
                                    return this.LETTER2;
                      //is last measurementData within 3 months
                      }else if( measurementData.getDateObservedAsDate().before(threemon)){
                                  prd.nextSuggestedProcedure = "----";
                                  return "----";
                      }else if(prd.lastFollupProcedure.equals(this.LETTER2)){
                                    prd.nextSuggestedProcedure = this.PHONE1;
                                    return this.PHONE1;
                      }else{
                                  prd.nextSuggestedProcedure = "----";
                                  return "----";
                      }

                  }else if(prd.lastFollupProcedure.equals(this.LETTER2)){
                      prd.nextSuggestedProcedure = this.PHONE1;
                      return this.PHONE1;
                  }else{
                      prd.nextSuggestedProcedure = "----";
                      return "----";
                  }*/
              }
          }else if (prd.state.equals("Refused") ){  //Not sure what to do about refused
                //prd.lastDate = "-----";

              EctMeasurementsDataBeanHandler measurementDataHandler = new EctMeasurementsDataBeanHandler(prd.demographicNo,measurementType);
              log.debug("2getting followup data for "+prd.demographicNo);
              Collection followupData = measurementDataHandler.getMeasurementsDataVector();

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
	
	/**
	 * Create a Map<type, # vaccines on schedule>
	 * @return A map of vaccine types, and the number of times each one should be administered.
	 */
	private Map<String, Integer> createChildhoodSchedule()
   {
   	   // Format is <type, # of required shots>
	   Map<String, Integer> requiredChildHoodImmunizations = new HashMap<>();
       requiredChildHoodImmunizations.put("DTaP-IPV-Hib", 4);
       requiredChildHoodImmunizations.put("Pneu-C", 3);
       requiredChildHoodImmunizations.put("Rot", 2);
       requiredChildHoodImmunizations.put("MenC-C", 1);
       requiredChildHoodImmunizations.put("MMR", 1);
       
       return requiredChildHoodImmunizations;
   }
	
	/**
	 * Calculate how many of the five childhood vaccinations have had their full schedules completed.
	 * @param immunizations Map<type, # remaining> vaccines remaining
	 *
	 * @return # of vaccines fully completed
	 */
	private int calculateScheduleCompletion(Map<String, Integer> immunizations)
   {
       int completed = (int) immunizations.entrySet()
                                          .stream()
                                          .filter(immunization -> immunization.getValue() <= 0)
                                          .count();

       return completed;
   }
	
	/**
	 * Determine if a demographic is eligible for the bonus code.
	 * A demographic is eligible for the bonus if they meet the following criteria:
	 *
	 * 1) They have had full schedules of all 5 childhood immunizations
	 * 2) They are between 30 and 42 months old, relative to the asOfDate
	 * 3) Their last childhood immunization was given prior to 30 months old.
	 *
	 * @param demographic demographic
	 * @param asOfDate calculations are relative to this date.
	 * @param latestPrevention date of the last childhood immunization
	 * @return true if bonus conditions met
	 */
   private boolean eligibleForBonus(Demographic demographic, int numberOfShots, Date asOfDate, Date latestPrevention)
   {
   	    int relativeAgeAsOf = DemographicData.getAgeInMonthsAsOf(demographic, asOfDate);
   	    
   	    boolean inAgeRange = relativeAgeAsOf >= 30 && relativeAgeAsOf <= 42;
   	    boolean allShots = numberOfShots >= 5;
   	    boolean thirtyMonthsAtLastPrevention = latestPrevention != null && DemographicData.getAgeInMonthsAsOf(demographic, latestPrevention) <= 30;

   	    return inAgeRange && allShots && thirtyMonthsAtLastPrevention;
   }
}
