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
 * RxManagePharmacyAction.java
 *
 * Created on September 29, 2004, 3:20 PM
 */

package oscar.oscarRx.pageUtil;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.oscarehr.common.model.PharmacyInfo;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;

import oscar.oscarRx.data.RxPharmacyData;

/**
 *
 * @author  Jay Gallagher &amp; Jackson Bi
 */
public final class RxManagePharmacyAction extends DispatchAction {

	public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		RxManagePharmacyForm frm = (RxManagePharmacyForm) form;

		String actionType = frm.getPharmacyAction();
		RxPharmacyData pharmacy = new RxPharmacyData();
		PharmacyInfo pharmacyInfo = new PharmacyInfo();
		if (frm.getID() != null && !frm.getID().isEmpty())
		{
			pharmacyInfo.setId(Integer.parseInt(frm.getID()));
		}
		pharmacyInfo.setName(frm.getName());
		pharmacyInfo.setAddress(frm.getAddress());
		pharmacyInfo.setCity(frm.getCity());
		pharmacyInfo.setProvince(frm.getProvince());
		pharmacyInfo.setPostalCode(frm.getPostalCode());
		pharmacyInfo.setPhone1(frm.getPhone1());
		pharmacyInfo.setPhone2(frm.getPhone2());
		pharmacyInfo.setFax(frm.getFax());
		pharmacyInfo.setEmail(frm.getEmail());
		pharmacyInfo.setServiceLocationIdentifier(frm.getServiceLocationIdentifier());
		pharmacyInfo.setNotes(frm.getNotes());
		pharmacyInfo.setAddDate(new Date());
		pharmacyInfo.setStatus(PharmacyInfo.ACTIVE);

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

		if(actionType.equals("Add"))
		{
			pharmacy.addPharmacy(pharmacyInfo, loggedInInfo);
		}
		else if(actionType.equals("Edit"))
		{
			pharmacy.updatePharmacy(pharmacyInfo, loggedInInfo);
		}
		else if(actionType.equals("Delete"))
		{
			Integer id = Integer.parseInt(frm.getID());
			pharmacy.deletePharmacy(id, loggedInInfo);
		}

	   return mapping.findForward("success");
	}

 public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException {
    	
	 
	String retVal = "{\"success\":true}";
    try {
    	String pharmacyId = request.getParameter("pharmacyId");
		Integer id = Integer.parseInt(pharmacyId);
		RxPharmacyData pharmacy = new RxPharmacyData();
		LoggedInInfo loggedInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

		pharmacy.deletePharmacy(id, loggedInfo);
	}
    catch( Exception e) {
    	MiscUtils.getLogger().error("CANNOT DELETE PHARMACY ",e);
    	retVal = "{\"success\":false}";
    }
    
	response.setContentType("application/json");
	JSONObject jsonObject = JSONObject.fromObject(retVal);
    jsonObject.write(response.getWriter());
    
    return null;
 }
    
    public ActionForward unlink(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    	
    	try {
    		String data = request.getParameter("preferedPharmacy");
			Integer demographicNo = Integer.parseInt(request.getParameter("demographicNo"));

    		ObjectMapper mapper = new ObjectMapper();       		
    		PharmacyInfo pharmacyInfo =  mapper.readValue(data, PharmacyInfo.class);
			LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
			RxPharmacyData pharmacy = new RxPharmacyData();

			pharmacy.unlinkPharmacy(pharmacyInfo.getId(), demographicNo, loggedInInfo);

			response.setContentType("application/json");
			String retVal = "{\"id\":\"" + pharmacyInfo.getId() + "\"}";
    		JSONObject jsonObject = JSONObject.fromObject(retVal);
    		jsonObject.write(response.getWriter());
    	}
    	catch( Exception e ) {
    		MiscUtils.getLogger().error("CANNOT UNLINK PHARMACY",e);
    	}
    	
    	return null;
    }
    
    public ActionForward getPharmacyFromDemographic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException {
    	
    	String demographicNo = request.getParameter("demographicNo");
    	
    	RxPharmacyData pharmacyData = new RxPharmacyData();
        List<PharmacyInfo> pharmacyList;
        pharmacyList = pharmacyData.getPharmacyFromDemographic(demographicNo);
        
		response.setContentType("application/json");
		ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), pharmacyList);
        
    	return null;
    }
    
    public ActionForward setPreferred(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    	RxPharmacyData pharmacy = new RxPharmacyData();
    	
    	try {
			LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
			PharmacyInfo pharmacyInfo = pharmacy.addPharmacyToDemographic(request.getParameter("pharmacyId"), request.getParameter("demographicNo"), request.getParameter("preferredOrder"), loggedInInfo);
			ObjectMapper mapper = new ObjectMapper();
			response.setContentType("application/json");
			mapper.writeValue(response.getWriter(), pharmacyInfo);
    	}
    	catch( Exception e ) {
    		MiscUtils.getLogger().error("ERROR SETTING PREFERRED ORDER", e);
    	}
    	
    	return null;
    }
    
    public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    	RxPharmacyData pharmacy = new RxPharmacyData();
		PharmacyInfo pharmacyInfo = new PharmacyInfo();
		pharmacyInfo.setName(request.getParameter("pharmacyName"));
		pharmacyInfo.setAddress(request.getParameter("pharmacyAddress"));
		pharmacyInfo.setCity(request.getParameter("pharmacyCity"));
		pharmacyInfo.setProvince(request.getParameter("pharmacyProvince"));
		pharmacyInfo.setPostalCode(request.getParameter("pharmacyPostalCode"));
		pharmacyInfo.setPhone1(request.getParameter("pharmacyPhone1"));
		pharmacyInfo.setPhone2(request.getParameter("pharmacyPhone2"));
		pharmacyInfo.setFax(request.getParameter("pharmacyFax"));
		pharmacyInfo.setEmail(request.getParameter("pharmacyEmail"));
		pharmacyInfo.setServiceLocationIdentifier(request.getParameter("pharmacyServiceLocationId"));
		pharmacyInfo.setNotes(request.getParameter("pharmacyNotes"));
		pharmacyInfo.setAddDate(new Date());
		pharmacyInfo.setStatus(PharmacyInfo.ACTIVE);

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

		String status = "{\"success\":true}";
    	
    	try {
			pharmacy.addPharmacy(pharmacyInfo, loggedInInfo);
		}
    	catch( Exception e ) {
    		MiscUtils.getLogger().error("Error Updating Pharmacy " + request.getParameter("pharmacyId"), e);
    		status = "{\"success\":false}";    		
    	}
    	
    	JSONObject jsonObject = JSONObject.fromObject(status);
    	
    	try {
			response.setContentType("application/json");
			jsonObject.write(response.getWriter());
    	}
    	catch( IOException e ) {
    		MiscUtils.getLogger().error("Cannot write response", e);    		
    	}
    	
    	return null;
    }
    
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		RxPharmacyData pharmacy = new RxPharmacyData();
		PharmacyInfo pharmacyInfo = new PharmacyInfo();

		try
		{
			pharmacyInfo.setId(Integer.parseInt(request.getParameter("pharmacyId")));
		}
		catch (NumberFormatException ignored) {}

    	pharmacyInfo.setName(request.getParameter("pharmacyName"));
    	pharmacyInfo.setAddress(request.getParameter("pharmacyAddress"));
    	pharmacyInfo.setCity(request.getParameter("pharmacyCity"));
    	pharmacyInfo.setProvince(request.getParameter("pharmacyProvince"));
    	pharmacyInfo.setPostalCode(request.getParameter("pharmacyPostalCode"));
    	pharmacyInfo.setPhone1(request.getParameter("pharmacyPhone1"));
    	pharmacyInfo.setPhone2(request.getParameter("pharmacyPhone2"));
    	pharmacyInfo.setFax(request.getParameter("pharmacyFax"));
    	pharmacyInfo.setEmail(request.getParameter("pharmacyEmail"));
    	pharmacyInfo.setServiceLocationIdentifier(request.getParameter("pharmacyServiceLocationId"));
    	pharmacyInfo.setNotes(request.getParameter("pharmacyNotes"));
		pharmacyInfo.setAddDate(new Date());
		pharmacyInfo.setStatus(PharmacyInfo.ACTIVE);

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

		try
		{
			if (pharmacyInfo.getId() == null)
			{
				pharmacy.addPharmacy(pharmacyInfo, loggedInInfo);
			}
			else
			{
				pharmacy.updatePharmacy(pharmacyInfo, loggedInInfo);
			}
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("Error Updating Pharmacy " + request.getParameter("pharmacyId"), e);
    		return null;
    	}
    	
		try
		{
			response.setContentType("application/json");
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(response.getWriter(), pharmacyInfo);
		}
		catch (IOException e)
		{
			MiscUtils.getLogger().error("Error writing response", e);
		}
    	
    	return null;
    }
    
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    	
    	String searchStr = request.getParameter("term");    	
    	
    	RxPharmacyData pharmacy = new RxPharmacyData();
    	
    	List<PharmacyInfo>pharmacyList = pharmacy.searchPharmacy(searchStr);
    	
		response.setContentType("application/json");
		ObjectMapper mapper = new ObjectMapper();
    	
    	try {
    		mapper.writeValue(response.getWriter(), pharmacyList);
    	}
    	catch( IOException e ) {
    		MiscUtils.getLogger().error("ERROR WRITING RESPONSE ",e);
    	}
    	
    	return null;
    	
    }
    
    public ActionForward searchCity(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    	
    	String searchStr = request.getParameter("term");    	
    	
    	RxPharmacyData pharmacy = new RxPharmacyData();
    	
		response.setContentType("application/json");
		ObjectMapper mapper = new ObjectMapper();
    	
    	List<String> cityList = pharmacy.searchPharmacyCity(searchStr);
    	
    	try {    		
    		mapper.writeValue(response.getWriter(), cityList);
    	}
    	catch( IOException e ) {
    		MiscUtils.getLogger().error("ERROR WRITING RESPONSE ",e);
    	}
    	
    	return null;
    }

    public ActionForward getPharmacyInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pharmacyId=request.getParameter("pharmacyId");
        MiscUtils.getLogger().debug("pharmacyId="+pharmacyId);
        if(pharmacyId==null) return null;
        RxPharmacyData pharmacyData = new RxPharmacyData();
        PharmacyInfo pharmacy=pharmacyData.getPharmacy(pharmacyId);
        HashMap<String,String> hm=new HashMap<String,String>();
       if(pharmacy!=null){
           hm.put("address", pharmacy.getAddress());
            hm.put("city", pharmacy.getCity());
            hm.put("email", pharmacy.getEmail());
            hm.put("fax", pharmacy.getFax());
            hm.put("name", pharmacy.getName());
            hm.put("phone1", pharmacy.getPhone1());
            hm.put("phone2", pharmacy.getPhone2());
            hm.put("postalCode", pharmacy.getPostalCode());
            hm.put("province", pharmacy.getProvince());
            hm.put("serviceLocationIdentifier", pharmacy.getServiceLocationIdentifier());
            hm.put("notes", pharmacy.getNotes());
            JSONObject jsonObject = JSONObject.fromObject(hm);
            response.getOutputStream().write(jsonObject.toString().getBytes());
       }
        return null;
    }
   /** Creates a new instance of RxManagePharmacyAction */
   public RxManagePharmacyAction() {
   }

}
