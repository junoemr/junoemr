<%--

    Copyright (c) 2005-2012. OscarHost Inc. All Rights Reserved.
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

    This software was written for
    OscarHost, a Division of Cloud Practice Inc.

--%>

<%
  if (session.getValue("user") == null)
    response.sendRedirect("../../../logout.jsp");

	String protocol = "http://";
	if(request.isSecure()){
		protocol = "https://";
	}
%>

<%@page language="java" contentType="text/html"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@taglib uri="/WEB-INF/rewrite-tag.tld" prefix="rewrite"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>
<%@ page import="org.springframework.web.context.WebApplicationContext"%>
<%@ page import="oscar.OscarProperties"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="oscar.oscarDemographic.data.*"%>
<%@page import="java.net.*, java.sql.*,java.text.*, java.util.*, oscar.util.*, oscar.oscarBilling.ca.bc.data.*,oscar.oscarBilling.ca.bc.pageUtil.*,oscar.*,oscar.entities.*"%>
<%@page import="org.apache.commons.codec.binary.Base64"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.OutputStreamWriter"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.util.regex.Matcher"%>
<%@page import="java.net.Authenticator"%>
<jsp:useBean id="apptMainBean" class="oscar.AppointmentMainBean"
	scope="session" />
<%@include file="dbBilling.jspf" %>
<%!

public String getNonce(String identifier, String first_name, String last_name)
{
	OscarProperties oscarProps = OscarProperties.getInstance();

	// Create the URL
	final String clinicaid_domain = oscarProps.getProperty("clinicaid_domain");
	final String instance_name = oscarProps.getProperty("clinicaid_instance_name");
	final String api_key = oscarProps.getProperty("clinicaid_api_key");
	
	String url_string = clinicaid_domain + "/auth/pushed_login/";
	URL nonce_url;
	try
	{
		nonce_url = new URL(url_string);
	}
	catch(Exception e)
	{
		return e.getMessage();
	}
	
	String post_data = "{\"identifier\":\"" + identifier + "\",\"first_name\":\"" + first_name + "\",\"last_name\":\"" + last_name + "\"}";

	String userpass = instance_name + ":" + api_key;
	String userpass_base64_string = new String(new Base64().encode(userpass.getBytes()));
	userpass_base64_string = userpass_base64_string.replaceAll("\n", "").replaceAll("\r", "");
	String basicauth = "Basic " + userpass_base64_string;

	String output = "";
	try
	{
		HttpURLConnection uc = (HttpURLConnection) nonce_url.openConnection();
		uc.setRequestMethod("POST");

		// Auth
		uc.setRequestProperty("Authorization", basicauth);

		// POST data
		uc.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(uc.getOutputStream());
		wr.write(post_data);
		wr.flush();

		// Read the result
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) 
		{
			output += inputLine;
		}
		in.close();

		Pattern p = Pattern.compile(".*\"nonce\":\"([a-zA-Z0-9-]*)\".*");
		Matcher m = p.matcher(output);

		if(!m.matches())
		{
			// TODO: handle error
			return "dint match " + output;
		}

		return m.group(1);
	}
	catch(Exception e)
	{
		// Catch invalid url
		// Catch 401 (unauthorized)

		//return "auth: " + userpass + ",error: " + e.toString()+" -- "+url_string;
		return output + e.toString();
	}

	//return URLEncoder.encode(basicauth);
}

%>	
<%
oscar.oscarDemographic.data.DemographicData demoData = new oscar.oscarDemographic.data.DemographicData();
OscarProperties oscarProps = OscarProperties.getInstance();

String clinicaid_domain = oscarProps.getProperty("clinicaid_domain");
String clinicaid_link = "";
String nonce = "";

String action = request.getParameter("billing_action");
BillingFormData billform = new BillingFormData();

String user_no = (String)session.getAttribute("user");
String user_first_name = (String)session.getAttribute("userfirstname");
String user_last_name = (String)session.getAttribute("userlastname");

nonce = getNonce(user_no, user_first_name, user_last_name);

if(action.equals("create_invoice")){
	String service_recipient_oscar_number = request.getParameter("demographic_no");
	org.oscarehr.common.model.Demographic demo = demoData.getDemographic(service_recipient_oscar_number);

	String provider_no = "", provider_uli = "", provider_first_name = "", provider_last_name = "";
	
	// Get the appointment provider first
	String appointment_provider_no = request.getParameter("appointment_provider_no");
	
	if(appointment_provider_no != null){
		provider_no = URLEncoder.encode(appointment_provider_no, "UTF-8");
		provider_uli = billform.getPracNo(appointment_provider_no);

		ResultSet rslocal = apptMainBean.queryResults(appointment_provider_no, "search_provider_name");
		while(rslocal.next()){
			provider_first_name = rslocal.getString("first_name");
			provider_last_name = rslocal.getString("last_name");
		}
	}

	//If there's no appointment provider, get the doctor assigned to the patient
	if(provider_first_name.length() == 0){
		provider_no = URLEncoder.encode(demo.getProviderNo(), "UTF-8");
		provider_uli = billform.getPracNo(demo.getProviderNo());

		ResultSet rslocal2 = apptMainBean.queryResults(demo.getProviderNo(), "search_provider_name");
		while(rslocal2.next()){
			provider_first_name = rslocal2.getString("first_name");
			provider_last_name = rslocal2.getString("last_name");
		}
	}
	
	//If there's no doctor assigned to this patient
	if(provider_first_name.length() == 0){
		provider_no = user_no;
		provider_first_name = user_first_name;
		provider_last_name = user_last_name;
		provider_uli = billform.getPracNo(provider_no);
		
	}
	if(provider_uli == null){
		provider_uli = "";
	}
	provider_uli = provider_uli;
	
	String appointment_number = request.getParameter("appointment_no");
	
	String patient_dob = demo.getYearOfBirth()+"-"+demo.getMonthOfBirth()+"-"+demo.getDateOfBirth();
	String encoded_patient_gender = demo.getSex();
	String first_name = UtilMisc.toUpperLowerCase(demo.getFirstName());
	String last_name = UtilMisc.toUpperLowerCase(demo.getLastName());
	String status = demo.getPatientStatus();
	String hin = demo.getHin();
	String ver = demo.getVer();
	String age = demo.getAge();
	String encoded_province = demo.getProvince();
	String encoded_city = demo.getCity();
	String encoded_address = demo.getAddress();
	String encoded_postal_code = demo.getPostal();

	String appointment_start_time = request.getParameter("appointment_start_time");
	String chart_no=request.getParameter("chart_no");
	String service_start_date = request.getParameter("service_start_date");

	String encoded_referral_number = demo.getFamilyDoctorNumber();
	String encoded_referral_first_name =
		demo.getFamilyDoctorFirstName();
	String encoded_referral_last_name =
		demo.getFamilyDoctorLastName();
	
	//String billing_form_type = URLEncoder.encode(request.getParameter("billing_service_type"), "UTF-8");
	clinicaid_link = clinicaid_domain+"/?nonce="+ nonce +"#/invoice/add?service_recipient_first_name="+first_name+
						"&service_recipient_uli="+hin+
						"&service_recipient_ver="+ver+
						"&service_recipient_last_name="+last_name+
						"&service_recipient_oscar_number="+service_recipient_oscar_number+
						"&service_recipient_status="+status+
						"&service_recipient_age="+age+
						"&service_recipient_gender="+encoded_patient_gender+
						"&service_provider_oscar_number="+provider_no+
						"&service_provider_first_name="+provider_first_name+
						"&service_provider_last_name="+provider_last_name+
						"&service_provider_uli="+provider_uli+
						"&province="+encoded_province+
						"&city="+encoded_city+
						"&postal_code="+encoded_postal_code+
						"&service_start_date="+service_start_date+
						"&chart_number="+chart_no+
						"&service_recipient_birth_date="+patient_dob+
						"&appointment_number="+appointment_number+
						"&appointment_start_time="+appointment_start_time +
	    	            "&referral_number=" + encoded_referral_number +
	    	            "&referral_first_name=" + encoded_referral_first_name +
						"&referral_last_name=" + encoded_referral_last_name +
						"&address=" + encoded_address;
						//"&billForm="+billing_form_type;
}else if(action.equals("invoice_reports")){
	clinicaid_link = clinicaid_domain+"/?nonce="+nonce+"#/reports";
}
			
%>
<script type="text/javascript">
	window.location="<%=clinicaid_link%>";
</script>
