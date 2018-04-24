<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>

<%@ page import="oscar.oscarBilling.ca.bc.data.BillingCodeData" %>
<%
	String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	boolean authed = true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin.billing,_admin" rights="w" reverse="<%=true%>">
	<%authed = false; %>
	<%response.sendRedirect("../../../../securityError.jsp?type=_admin&type=_admin.billing");%>
</security:oscarSec>
<%
	if(!authed)
	{
		return;
	}
%>

<%
	String serviceCodeId = request.getParameter("codeId");
	if(serviceCodeId != null)
	{
		BillingCodeData.deleteBillingCode(Integer.parseInt(serviceCodeId));
	}
	response.sendRedirect("billingPrivateCodeAdjust.jsp");
%>
