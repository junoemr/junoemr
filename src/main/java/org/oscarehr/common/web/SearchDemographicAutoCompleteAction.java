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


package org.oscarehr.common.web;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.demographic.dao.DemographicCustDao;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographic.entity.DemographicCust;
import org.oscarehr.demographic.search.DemographicCriteriaSearch;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.util.AppointmentUtil;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.OscarProperties;
import oscar.oscarRx.data.RxProviderData;
import oscar.oscarRx.data.RxProviderData.Provider;

/**
 *
 * @author jaygallagher
 */
public class SearchDemographicAutoCompleteAction extends Action {

    private final int MAX_SEARCH_RESULTS = 100;

    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        String searchStr = request.getParameter("demographicKeyword");

        DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographic.dao.DemographicDao");

        if (searchStr == null)
        {
            searchStr = request.getParameter("query");
        }
        
        if (searchStr == null)
        {
            searchStr = request.getParameter("name");
        }
        
        if (searchStr == null)
        {
            searchStr = request.getParameter("term");
        }
        
        boolean activeOnly = "true".equalsIgnoreCase(request.getParameter("activeOnly"));
        boolean jqueryJSON = "true".equalsIgnoreCase(request.getParameter("jqueryJSON"));
        RxProviderData rx = new RxProviderData();

        DemographicService demoService = (DemographicService) SpringUtils.getBean("demographic.service.DemographicService");
        DemographicCriteriaSearch.SORT_MODE sortMode = DemographicCriteriaSearch.SORT_MODE.DemographicName;

        List<Demographic> list;
        DemographicService.SEARCH_MODE searchMode;
        DemographicService.STATUS_MODE statusMode;

        // search by birth date (format has to be yyyyMMdd)
        if (searchStr.length() == 8 && searchStr.matches("([0-9]*)"))
        {
            searchStr = searchStr.substring(0,4) + "-" + searchStr.substring(4,6) + "-" + searchStr.substring(6, 8);
            searchMode = DemographicService.SEARCH_MODE.dob;
            statusMode = DemographicService.STATUS_MODE.all;
        } 
        else if (activeOnly)
        {
            searchMode = DemographicService.SEARCH_MODE.name;
            statusMode = DemographicService.STATUS_MODE.active;
        }
        else
        {
            searchMode = DemographicService.SEARCH_MODE.name;
            statusMode = DemographicService.STATUS_MODE.all;
        }

        DemographicCriteriaSearch demoCriteriaSearch = demoService.buildDemographicSearch(searchStr, searchMode, statusMode, sortMode);

        demoCriteriaSearch.setLimit(MAX_SEARCH_RESULTS);
        list = demographicDao.criteriaSearch(demoCriteriaSearch);

        if (list.size() == demoCriteriaSearch.getLimit())
        {
            MiscUtils.getLogger().warn("More results exists than returned");
        }
        
        List<HashMap<String, String>> secondList= new ArrayList<>();
        for(Demographic demo :list)
        {
            int demoId = demo.getDemographicId();
            HashMap<String,String> hashMap = new HashMap<>();
            LocalDate dob = demo.getDateOfBirth();
            if (dob != null)
            {
                hashMap.put("formattedDob", dob.toString());
            }
            hashMap.put("formattedName",StringEscapeUtils.escapeJava(demo.getFormattedName().replaceAll("\"", "\\\"")));
            hashMap.put("demographicNo",String.valueOf(demoId));
            hashMap.put("status",demo.getPatientStatus());
             

            Provider provider = rx.getProvider(demo.getProviderNo());
            if (demo.getProviderNo() != null)
            {
                hashMap.put("providerNo", demo.getProviderNo());
            }
            if (provider.getSurname() != null && provider.getFirstName() != null)
            {
                hashMap.put("providerName", provider.getSurname() + ", " + provider.getFirstName());
            }
            
            if (OscarProperties.getInstance().isPropertyActive("workflow_enhance"))
            {
                 hashMap.put("nextAppointment", AppointmentUtil.getNextAppointment(demoId + ""));
                 DemographicCustDao demographicCustDao = (DemographicCustDao)SpringUtils.getBean("demographicCustDao");
                 DemographicCust demographicCust = demographicCustDao.find(demoId);

                 if (demographicCust != null)
                 {
                     String cust1 = StringUtils.trimToNull(demographicCust.getNurse());
                     String cust2 = StringUtils.trimToNull(demographicCust.getResident());
                     String cust4 = StringUtils.trimToNull(demographicCust.getMidwife());

                     if (cust1 != null)
                     {
                        hashMap.put("cust1", cust1);
                        provider = rx.getProvider(cust1);
                        hashMap.put("cust1Name", provider.getSurname() + ", " + provider.getFirstName());
                     }

                     if (cust2 != null)
                     {
                        hashMap.put("cust2", cust2);
                        provider = rx.getProvider(cust2);
                        hashMap.put("cust2Name", provider.getSurname() + ", " + provider.getFirstName());
                     }

                     if (cust4 != null)
                     {
                        hashMap.put("cust4", cust4);
                        provider = rx.getProvider(cust4);
                        hashMap.put("cust4Name", provider.getSurname() + ", " + provider.getFirstName());
                     }
                 }
            }

            secondList.add(hashMap);
        }

        HashMap<String,List<HashMap<String, String>>> demoMap = new HashMap<String,List<HashMap<String, String>>>();
        demoMap.put("results",secondList);
        response.setContentType("text/x-json");
        if (jqueryJSON)
        {
            response.getWriter().print(formatJSON(secondList));
            response.getWriter().flush();
        }
        else
        {
            JSONObject jsonArray = (JSONObject) JSONSerializer.toJSON( demoMap );
            jsonArray.write(response.getWriter());
        }
        return null;

    }
    
    private String formatJSON(List<HashMap<String, String>>info) {
        StringBuilder json = new StringBuilder("[");

        HashMap<String, String>record;
        int size = info.size();
        for(int idx = 0; idx < size; ++idx)
        {
            record = info.get(idx);
            json.append("{\"label\":\"" + record.get("formattedName") + " " + record.get("formattedDob") + " (" + record.get("status") + ")\",\"value\":\"" + record.get("demographicNo") + "\"");
            json.append(",\"providerNo\":\"" + record.get("providerNo") + "\",\"provider\":\"" + record.get("providerName") + "\",\"nextAppt\":\"" + record.get("nextAppointment")+"\",");
            json.append("\"formattedName\":\"" + record.get("formattedName") + "\"}");

            if(idx < size-1)
            {
                json.append(",");
            }
        }
        json.append("]");

        return json.toString();
    }

}
