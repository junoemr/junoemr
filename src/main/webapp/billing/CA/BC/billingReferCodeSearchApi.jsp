<%--

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

--%>

<%
response.setContentType("application/json");
 
if(session.getValue("user") == null)
	response.sendRedirect("../../../logout.jsp");

String user_no;
user_no = (String) session.getAttribute("user");

%>


<%@page import="java.util.*"%>
<%@page import="java.sql.*"%>
<%@page import="oscar.*"%>
<%@page import="java.net.*"%>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="org.oscarehr.common.model.Billingreferral" %>
<%@page import="org.oscarehr.common.dao.BillingreferralDao" %>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.JSONObject"%>

<jsp:useBean id="apptMainBean" class="oscar.AppointmentMainBean" scope="session" />

<%


BillingreferralDao billingReferralDao = (BillingreferralDao)SpringUtils.getBean("BillingreferralDAO");

String search = "",search2 = "";
search = request.getParameter("search"); 
if (search.compareTo("") == 0){
	search = "search_referral_code";
}

 
String codeName= "",codeName1 = "", codeName2 = "";
String xcodeName= "",xcodeName1 = "",xcodeName2 = "";
String outputType = "", valueType = "";
codeName = request.getParameter("name");

if(codeName.trim().length() == 0){
	codeName = request.getParameter("term");
}

codeName1= request.getParameter("name1");
codeName2 = request.getParameter("name2");
xcodeName = request.getParameter("name");
xcodeName1= request.getParameter("name1");
xcodeName2 = request.getParameter("name2");
outputType = request.getParameter("outputType");
valueType = request.getParameter("valueType");

String desc = "", desc1 = "", desc2 = "";
String fdesc = "", fdesc1 = "", fdesc2 = "";
  
   
   
if (codeName == null || codeName.compareTo("") == 0 ){
	codeName = " ";
	desc = " ";
}
else{
	codeName = codeName + "%";
	if (codeName.indexOf(",") != -1)
	{
		desc = codeName.substring(0,codeName.indexOf(",")) + "%";
		fdesc =codeName.substring(codeName.indexOf(",")+1, codeName.length()-1).trim() + "%";
	}
	else{
		desc =  codeName + "%";
		fdesc = "%";
	}
}
if (codeName1.compareTo("") == 0 || codeName1 == null){
	codeName1 = " ";
	desc1 = " ";
}
else{
	codeName1 = codeName1 + "%";
	if (codeName1.indexOf(",") != -1)
	{
		desc1 =  codeName1.substring(0,codeName1.indexOf(",")) + "%";
		fdesc1 = codeName1.substring(codeName1.indexOf(",")+1, codeName1.length()-1).trim() + "%" ;
	}
	else{
		desc1 =  codeName1 + "%";
		fdesc1 = "%";
	}

}
if (codeName2.compareTo("") == 0 || codeName2 == null){
	codeName2 = " ";
	desc2 = " ";
}
else{
	codeName2 = codeName2 + "%";


	if (codeName2.indexOf(",") != -1)
	{
		desc2 =  codeName2.substring(0,codeName2.indexOf(",")) + "%";
		fdesc2 = codeName2.substring(codeName2.indexOf(",")+1);
	}
	else{
		desc2 =  codeName2 + "%";
		fdesc2 = "%";
	}
}

String[] param =new String[9];
param[0] = codeName;
param[1] = codeName1;
param[2] = codeName2;
param[3] = desc;
param[4] = fdesc;
param[5] = desc1;
param[6] = fdesc1;
param[7] = desc2;
param[8] = fdesc2;

    
    
String color="";
int Count = 0;
int intCount = 0;
String numCode="";
String textCode="";
String searchType="";
 

// Retrieving Provider
List<Map<String,String>> result_doctors = new ArrayList<Map<String,String>>();
List<Billingreferral> billingReferrals = billingReferralDao.searchReferralCode(param[0], param[1], param[2], param[3], param[4], param[5], param[6], param[7], param[8]);

JSONObject json = new JSONObject();
JSONArray doctors = new JSONArray();

if(valueType == null){
	valueType = "";
}

//Grab all the doctors, put them in an arraylist of hashmaps.
//Limit the search to 100 since it might crash the browser
for(Billingreferral billingReferral:billingReferrals) {
	Map<String,String> result_doctor = new HashMap<String,String>();
	
	result_doctor.put("referral_no", new String(billingReferral.getReferralNo()));
	result_doctor.put("namedesc", new String(billingReferral.getLastName()+", "+billingReferral.getFirstName()));
	String city = "", phone = "";
	if(billingReferral.getCity() != null){
		city = billingReferral.getCity();
	}
	if(billingReferral.getPhone() != null){
		phone = billingReferral.getPhone();
	}
	result_doctor.put("city", city);
	result_doctor.put("phone", phone);
	
	result_doctors.add(result_doctor);
	
	intCount++;

	if(intCount >= 100){
		break;
	}
}

//Now that we have the doctors stored in an arraylist, we can choose how to display them
//This code currently only supports JSON for now
if(outputType.equals("json")){
	for (Map<String, String> result_doctor : result_doctors) {
		
		JSONObject doctor;
		
		doctor = new JSONObject();
		// Use referral_no as default value
		if(valueType.equals("name")){
			doctor.put("value", result_doctor.get("namedesc"));
			doctor.put("referral_no", result_doctor.get("referral_no"));
		}else{
			doctor.put("value", result_doctor.get("referral_no"));
			doctor.put("namedesc", result_doctor.get("namedesc"));
		}
		doctor.put("label", result_doctor.get("referral_no") + " " + result_doctor.get("namedesc"));
		doctor.put("desc", result_doctor.get("city")+ " (" +result_doctor.get("phone") + ")");
			
		doctors.add(doctor);
	}
	response.getWriter().write(doctors.toString());
}
 %>
