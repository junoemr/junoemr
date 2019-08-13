
package oscar.oscarBilling.ca.bc.MSP;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtilsOld;

import oscar.oscarTickler.TicklerCreator;
import oscar.util.SqlUtils;

public class CDMReminderHlp {

  public CDMReminderHlp() {
  }

  private String[] createCDMCodeArray(List<String[]> codes) {
    String[] ret = new String[codes.size()];
    for (int i = 0; i < codes.size(); i++) {
      String[] row = codes.get(i);
      ret[i] = row[0];
    }
    return ret;
  }

 
  public void manageCDMTicklers(LoggedInInfo loggedInInfo,String providerNo, String[] alertCodes) throws Exception {
    //get all demographics with a problem that falls within CDM category
    TicklerCreator crt = new TicklerCreator();
    ServiceCodeValidationLogic lgc = new ServiceCodeValidationLogic();
    List<String[]> cdmServiceCodes = lgc.getCDMCodes();
    alertCodes = createCDMCodeArray(cdmServiceCodes);

    final String remString = "SERVICE CODE";
    List<String[]> cdmPatients = this.getCDMPatients(alertCodes);
    List<String> cdmPatientNos = extractPatientNos(cdmPatients);
    crt.resolveTicklers(loggedInInfo, providerNo, cdmPatientNos, remString);

    for (String[] cdmPatientInfo : cdmPatients) {

      MiscUtilsOld.checkShutdownSignaled();

      String demoNo = cdmPatientInfo[0];
      String provNo = cdmPatientInfo[1];
      String dxcode = cdmPatientInfo[2];

      for (String[] cdmRecord : cdmServiceCodes) {

        String cdmCode = cdmRecord[0]; //A declared cdm code
        String cdmServiceCode = cdmRecord[1]; //The associated service code for the specified cdm
        //if the specified patient has one one the specified chronic diseases
        if (cdmCode.equals(dxcode)) {
          /*
          check if a conflicting code has already been billed
           */
          boolean conflict = false;
          List<String> conflictServiceCodes = getConflictingServiceCodes(cdmServiceCode);
          for (String conflictCode : conflictServiceCodes)
          {
            if (lgc.daysSinceCodeLastBilled(demoNo, conflictCode) <= 365 && lgc.daysSinceCodeLastBilled(demoNo, conflictCode) != -1)
            {
              conflict = true;
              break;
            }
          }

          if (!conflict)
          {
            /*
             * Check If the associated service code was billed in the past calendar year
             */
            int daysPast = lgc.daysSinceCodeLastBilled(demoNo, cdmServiceCode);
            if (daysPast > 365)
            {
              GregorianCalendar cal = new GregorianCalendar();
              cal.add(Calendar.DAY_OF_YEAR, -daysPast);
              java.util.Date dateLastBilled = cal.getTime();
              SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
              String newfmt = formatter.format(dateLastBilled);
              String message = remString + " " + cdmServiceCode + " - Last Billed On: " + newfmt;
              crt.createTickler(loggedInInfo, demoNo, provNo, message);
            } else if (daysPast < 0)
            {
              String message =
                      remString + " " + cdmServiceCode + " - Never billed for this patient";
              crt.createTickler(loggedInInfo, demoNo, provNo, message);
            }
          }
        }
      }
    }
  }

  /**
   * get a list of service codes that conflict with the given service code, i.e. you cannot bill both.
   * @param serviceCode - the service code you whish to get conflicts for.
   * @return - list of conflicting service codes
   */
  private List<String> getConflictingServiceCodes(String serviceCode)
  {
    ArrayList<String> conflicts = new ArrayList<>();
    String cdmRulesQry =
            "SELECT serviceCode,conditionCode FROM billing_service_code_conditions";
    List<String[]> cdmRules = SqlUtils.getQueryResultsList(cdmRulesQry);
    for (String[] cdmRule : cdmRules)
    {
      if (cdmRule[0].equals(serviceCode))
      {
        conflicts.add(cdmRule[1]);
      }
    }
    return conflicts;
  }

  private List<String> extractPatientNos(List<String[]> cdmPatients) {
    ArrayList<String> cdmPatientNos = new ArrayList<>();

    for (String[] patientInfo : cdmPatients) {
      cdmPatientNos.add(patientInfo[0]);
    }

    return cdmPatientNos;
  }

  /**
   * Returns a String list of demographic numbers for patients that are associated with the
   * specified provider number and who have been diagnosed with a chronic disease
   *
   * @return ArrayList
   */
  private List<String[]> getCDMPatients(String[] codes) {

    String qry = "SELECT de.demographic_no,de.provider_no,dxresearch_code FROM dxresearch d, demographic de WHERE de.demographic_no=d.demographic_no " +
        " and d.dxresearch_code ";
    qry += SqlUtils.constructInClauseString(codes, true);
    qry +=
        " and status = 'A' and patient_status = 'AC' order by de.demographic_no";
    List<String[]> lst = SqlUtils.getQueryResultsList(qry);
    return lst == null ? new ArrayList<String[]>() : lst;
  }
}
