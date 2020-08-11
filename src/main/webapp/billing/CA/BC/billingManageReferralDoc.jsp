<!DOCTYPE html>
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

<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
      boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin.billing,_admin" rights="w" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../../../../securityError.jsp?type=_admin&type=_admin.billing");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@page import="org.oscarehr.common.dao.BillingreferralDao" %>
<%@page import="org.oscarehr.common.model.Billingreferral" %>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="java.util.List" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%
	BillingreferralDao billingReferralDao = (BillingreferralDao)SpringUtils.getBean("BillingreferralDAO");
%>
<html:html locale="true">

<head>
<title><bean:message key="admin.admin.ManageReferralDoc"/></title>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.9.1.js"></script>
<link href="<%=request.getContextPath() %>/css/bootstrap.min.css" rel="stylesheet">

<script type="text/javascript">

function isNumeric(strString){
    var validNums = "0123456789.";
    var strChar;
    var retval = true;

    for (i = 0; i < strString.length && retval == true; i++){
       strChar = strString.charAt(i);
       if (validNums.indexOf(strChar) == -1){
          retval = false;
       }
    }
     return retval;
}

function checkUnits(){
	if  (!isNumeric(document.BillingAddCodeForm.value.value)){
		alert("Price has to be a numeric value");
	        document.BillingAddCodeForm.value.focus();
		return false;
	}
	return true;
}
function registerFormSubmit(formId, divId) {
	$('#'+formId).submit(function() {
		if(!$('#'+formId).valid()){
			return false;
		}
		// gather the form data
		var data = $(this).serialize();
		// post data
		$.post($('#'+formId).attr('action'), data, function(returnData) {
			// insert returned html
			$('#'+divId).html(returnData)
		})

		return false; // stops browser from doing default submit process
	});
}

</script>

<style>
table td{font-size:10px;}
</style>

<body>
	<h3><bean:message key="admin.admin.ManageReferralDoc"/></h3>
	<div class="container-fluid">

		<%
			String limit = request.getParameter("limit");
			String offset = request.getParameter("offset");
			String lastname = request.getParameter("lastname");
			String escaped_lastname = StringEscapeUtils.escapeHtml(lastname);
		%>
		<form action="<%=request.getContextPath() %>/billing/CA/BC/billingManageReferralDoc.jsp" class="form-inline" name="referralDocform" id="referralDocform">
			Last Name: <input type="text" name="lastname" value="<%= (lastname == null)?"":escaped_lastname%>" /> 
			<select name="limit" class="span1" title="limit results">
				<option value="10" <%=selected(limit,"10")%>>10</option>
				<option value="50" <%=selected(limit,"50")%>>50</option>
				<option value="100" <%=selected(limit,"100")%>>100</option>
			</select>
			<input type="hidden" name="offset" value="0"/>
			<input class="btn btn-primary" type="submit" value="Search" /> 
			<a href="<%=request.getContextPath() %>/billing/CA/BC/billingAddReferralDoc.jsp" class="contentLink btn btn-info">Add Doctor</a>
		</form>

		<table class="table table-striped table-condensed table-hover">
		<thead>
			<tr>
				<!--th>id</th-->
				<th>Billing#</th>
				<th>Last Name</th>
				<th>First Name</th>
				<th>Specialty</th>
				<th>Address1</th>
				<th>Address2</th>
				<th>City</th>
				<th>Province</th>
				<th>Postal</th>
				<th>Phone</th>
				<th>Fax</th>
			</tr>
		</thead>

		<tbody>
			<%
			//ReferralBillingData rbd = new ReferralBillingData();
				if(limit == null) limit = "10";
				if(offset == null) offset = "0";
				if(lastname == null) lastname = "%";

				int limit_int = 10;
				int offset_int = 0;
				try {
					limit_int = Integer.parseInt(limit);
					offset_int = Integer.parseInt(offset);
				}
				catch(Exception e)
				{
					// Don't really want to do anything here
				}

				List<Billingreferral> alist = billingReferralDao.getBillingreferralByLastName(lastname, limit_int, offset_int);
				for(int i = 0; i < alist.size(); i++)
				{
					Billingreferral billingReferral = alist.get(i);
            %>
			<tr>
				<!--td><%=billingReferral.getBillingreferralNo()%></td-->
				<td><a href="<%=request.getContextPath() %>/billing/CA/BC/billingAddReferralDoc.jsp?id=<%=billingReferral.getBillingreferralNo()%>" class="contentLink"><%=billingReferral.getReferralNo()%></a></td>
				<td><%if(billingReferral.getLastName()!=null){out.print(billingReferral.getLastName());}%></td>
				<td><%if(billingReferral.getFirstName()!=null){out.print(billingReferral.getFirstName());}%></td>
				<td><%if(billingReferral.getSpecialty()!=null){out.print(billingReferral.getSpecialty());}%></td>
				<td><%if(billingReferral.getAddress1()!=null){out.print(billingReferral.getAddress1());}%></td>
				<td><%if(billingReferral.getAddress2()!=null){out.print(billingReferral.getAddress2());}%></td>
				<td><%if(billingReferral.getCity()!=null){out.print(billingReferral.getCity());}%></td>
				<td><%if(billingReferral.getProvince()!=null){out.print(billingReferral.getProvince());}%></td>
				<td><%if(billingReferral.getPostal()!=null){out.print(billingReferral.getPostal());}%></td>
				<td><%if(billingReferral.getPhone()!=null){out.print(billingReferral.getPhone());}%></td>
				<td><%if(billingReferral.getFax()!=null){out.print(billingReferral.getFax());}%></td>
			</tr>
			<%}%>
		</tbody>
		</table>
		<div style="margin-top: 20px;">
			<%
				if(alist != null && offset_int > 0) {
					int previous_page = 0;
					if( (offset_int - limit_int) > 0) {
						previous_page = offset_int - limit_int;
					}
			%>
			<a href="#" onclick="document.forms[0].offset.value=<%=previous_page%>;document.forms[0].submit();return false;">Previous</a>
			<%
				}
				if(alist != null && alist.size() == limit_int)
				{
			%>
			<a href="#" onclick="document.forms[0].offset.value=<%=offset_int+limit_int%>;document.forms[0].submit();return false;">Next</a>
			<%
				}
			%>
		</div>
	</div>
<script>
registerFormSubmit('referralDocform', 'dynamic-content');

$( document ).ready(function( $ )
{
	$("a.contentLink").on("click", function(e)
	{
		e.preventDefault();
		$(document.body).load
		(
			$(this).attr("href"),
			function(response, status, xhr)
			{
				if (status === "error")
				{
					var msg = "Sorry but there was an error: ";
					$(document.body).html(msg + xhr.status + " " + xhr.statusText);
				}
			}
		);
	});
});
</script>
</body>
</html:html>
<%!
 String selected(String var,String constant){
     if (var != null && var.equals(constant)){
         return "selected";
     }else{
         return "";
     }
 }
%>
