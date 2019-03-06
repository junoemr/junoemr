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


package oscar.oscarBilling.ca.bc.MSP;

import java.util.ArrayList;
import java.util.List;

import org.oscarehr.ticklers.dao.CDMTicklerDao;
import org.oscarehr.ticklers.model.CDMTicklerInfo;
import org.oscarehr.util.LoggedInInfo;

import org.oscarehr.util.SpringUtils;
import oscar.oscarTickler.TicklerCreator;
import oscar.util.SqlUtils;

public class CDMReminderHlp {

  public CDMReminderHlp() {
  }

  private List<Integer> getCDMDxCodes(List<String[]> codes) {
    List<Integer> toReturn = new ArrayList<>();

    for (String[] code : codes)
    {
        toReturn.add(Integer.parseInt(code[0]));
    }

    return toReturn;
  }

 
  public void manageCDMTicklers(LoggedInInfo loggedInInfo,String providerNo, String[] alertCodes) throws Exception {

    // DELETE should come first... I think there's a reason why...


    //get all demographics with a problem that falls within CDM category
    TicklerCreator crt = new TicklerCreator();
    ServiceCodeValidationLogic lgc = new ServiceCodeValidationLogic();
    List<String[]> cdmServiceCodes = lgc.getCDMCodes();

    List<Integer> cdmDxCodes = getCDMDxCodes(cdmServiceCodes);

    List<String[]> cdmPatients = this.getCDMPatients(alertCodes);
    CDMTicklerDao cdmTicklerDao = SpringUtils.getBean(CDMTicklerDao.class);

    List<CDMTicklerInfo> cdmPatientsToUpdate = cdmTicklerDao.getCDMTicklerCreationInfo(cdmDxCodes);
    // For these people, create a tickler

    List<CDMTicklerInfo> cdmTicklersToDelete = cdmTicklerDao.getCDMTicklerDeleteInfo(cdmDxCodes);
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
