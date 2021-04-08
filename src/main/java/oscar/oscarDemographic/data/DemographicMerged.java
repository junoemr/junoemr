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


/*
 * DemographicMergedDAO.java
 *
 * Created on September 14, 2007, 1:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oscar.oscarDemographic.data;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.RecycleBinDao;
import org.oscarehr.common.model.RecycleBin;
import org.oscarehr.demographic.dao.DemographicMergedDao;
import org.oscarehr.security.dao.SecObjPrivilegeDao;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.log.LogAction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author wrighd
 * NOTE: please don't use this class. It's attempting to be a DAO without the underlying class structure.
 * Use DemographicMergedDao instead.
 */
@Deprecated
public class DemographicMerged {

    Logger logger = MiscUtils.getLogger();
    private DemographicMergedDao dao = SpringUtils.getBean(DemographicMergedDao.class);
    private SecObjPrivilegeDao secObjPrivilegeDao = SpringUtils.getBean(SecObjPrivilegeDao.class);
    private RecycleBinDao recycleBinDao = SpringUtils.getBean(RecycleBinDao.class);
    
    public DemographicMerged() {
    }

    public void Merge(LoggedInInfo loggedInInfo, String demographic_no, String head) {

    	org.oscarehr.demographic.model.DemographicMerged dm = new org.oscarehr.demographic.model.DemographicMerged();
    	
    	 // always merge the head of records that have already been merged to the new head
        String record_head = getHead(demographic_no);
        if (record_head == null)
            dm.setDemographicNo(Integer.parseInt( demographic_no ));
        else
            dm.setDemographicNo(Integer.parseInt(record_head));

        dm.setMergedTo(Integer.parseInt( head ));
       
        dm.setLastUpdateUser(loggedInInfo.getLoggedInProviderNo());
        dm.setLastUpdateDate(new Date());
        dao.persist(dm);
        
        LogAction.addLogSynchronous(loggedInInfo, "DemographicMerged.Merge", "demographic_no="+demographic_no);

  
    }

    public void UnMerge(LoggedInInfo loggedInInfo, String demographic_no, String curUser_no) {

    	List<org.oscarehr.demographic.model.DemographicMerged> dms = dao.findByDemographicNo(Integer.parseInt(demographic_no));
    	for(org.oscarehr.demographic.model.DemographicMerged dm:dms) {
    		dm.setLastUpdateUser(loggedInInfo.getLoggedInProviderNo());
            dm.setLastUpdateDate(new Date());
			dm.delete();
			dao.merge(dm);
    	}
    	
    	 String privilege = "";
         String priority = "";
         String provider_no = "";
    	
    	RecycleBin rb = new RecycleBin();
    	rb.setProviderNo(curUser_no);
    	rb.setUpdateDateTime(new Date());
    	rb.setTableName("secObjPrivilege");
    	rb.setKeyword("_all|_eChart$"+ demographic_no);
    	rb.setTableContent("<roleUserGroup>_all</roleUserGroup>" + "<objectName>_eChart$" + demographic_no + "</objectName><privilege>" + privilege + "</privilege>" + "<priority>" + priority + "</priority><provider_no>" + provider_no + "</provider_no>");
    	recycleBinDao.persist(rb);

    	 LogAction.addLogSynchronous(loggedInInfo, "DemographicMerged.UnMerge", "demographic_no="+demographic_no);

    }
    
    public String getHead(String demographic_no) {
    	Integer result = getHead(Integer.parseInt(demographic_no));
    	if(result != null) {
    		return result.toString();
    	}
    	return null;
    }


    public Integer getHead(Integer demographic_no)  {
    	Integer head = null;

		org.oscarehr.demographic.model.DemographicMerged dm = dao.getCurrentHead(demographic_no);

		if (dm != null)
		{
			head = getHead(dm.getMergedTo());
		}

        return head;
    }

    public ArrayList<String> getTail(String demographic_no) {
    	ArrayList<String> tailArray = new ArrayList<String>();

    	List<org.oscarehr.demographic.model.DemographicMerged> dms = dao.findCurrentByMergedTo(Integer.parseInt(demographic_no));
    	for(org.oscarehr.demographic.model.DemographicMerged dm:dms) {
    		tailArray.add(String.valueOf(dm.getDemographicNo()));
    	}
    	
        int size = tailArray.size();
        for (int i=0; i < size; i++){
            tailArray.addAll(getTail(  tailArray.get(i) ));
        }

        return tailArray;

    }
}
