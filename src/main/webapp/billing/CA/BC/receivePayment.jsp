<%@page import="java.sql.*" errorPage=""%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title><bean:message key="oscar.billing.CA.BC.title" /></title>
<script type="javascript">
function refreshParent(){
	opener.window.location.href = opener.window.location.href;
}
</script>
<!-- calendar stylesheet -->
<link rel="stylesheet" type="text/css" media="all"
    href="../../../share/calendar/calendar.css" title="win2k-cold-1" />
<!-- main calendar program -->
<script type="text/javascript" src="../../../share/calendar/calendar.js"></script>

<!-- language for the calendar -->
<script type="text/javascript"
    src="../../../share/calendar/lang/<bean:message key="global.javascript.calendar"/>"></script>

<!-- the following script defines the Calendar.setup helper function, which makes
       adding a calendar a matter of 1 or 2 lines of code. -->
<script type="text/javascript" src="../../../share/calendar/calendar-setup.js"></script>
<link rel="stylesheet" type="text/css" media="all" href="../../../share/css/extractedFromPages.css"  />
</head>
<body>
<logic:equal name="receivePaymentActionForm" property="paymentReceived"
	value="true">
	<fieldset><legend><bean:message
		key="oscar.billing.CA.BC.received" /></legend>
	<div class="msgDisplay">
	<%
oscar.oscarBilling.ca.bc.pageUtil.ReceivePaymentActionForm frm = (oscar.oscarBilling.ca.bc.pageUtil.ReceivePaymentActionForm)request.getAttribute("receivePaymentActionForm");
	%> <%=java.text.NumberFormat.getCurrencyInstance().format(new Double(frm.getAmountReceived()))%>
	<bean:message key="oscar.billing.CA.BC.credit" /> &nbsp; <bean:message
		key="oscar.billing.CA.BC.invoice" /> <bean:write
		name="receivePaymentActionForm" property="billNo" /> &nbsp; <bean:message
		key="oscar.billing.CA.BC.lineNo" /> <bean:write
		name="receivePaymentActionForm" property="billingmasterNo" /></div>
	<div align="center">
	<button
		onclick="opener.window.location.reload();self.close();return false;">Close</button>
	</div>
	</fieldset>
</logic:equal>
<logic:notEqual name="receivePaymentActionForm"
	property="paymentReceived" value="true">
	<html:form action="/billing/CA/BC/receivePaymentAction">
		<html:hidden property="billingmasterNo" />
		<html:hidden property="billNo" />

		<fieldset><legend> <bean:message
			key="oscar.billing.CA.BC.title" /> </legend>
		<div class="msgDisplay">
		<p><bean:message key="oscar.billing.CA.BC.invoice" /> <bean:write
			name="receivePaymentActionForm" property="billNo" /></p>
		<p><bean:message key="oscar.billing.CA.BC.lineNo" /> <bean:write
			name="receivePaymentActionForm" property="billingmasterNo" /></p>
		</div>
		<p><label> <bean:message key="oscar.billing.CA.BC.amount" />
		<html:text maxlength="6" property="amountReceived" /><!--&nbsp;<html:checkbox property="isRefund" value="true"/>-->
		</label></p>
		<p><label> <bean:message key="oscar.billing.CA.BC.method" />
		<bean:define id="paymentMethodList" scope="request"
			name="receivePaymentActionForm" property="paymentMethodList" /> <html:select
			property="paymentMethod">
			<html:options name="receivePaymentActionForm"
				collection="paymentMethodList" property="id"
				labelProperty="paymentType" />
		</html:select> </label></p>
		<p>
		  <bean:message key="oscar.billing.CA.BC.paymentdate" /> 
		  <html:text maxlength="10" property="paymentDate" styleId="payment_date" readonly="true"/>
          <img src="../../../images/cal.gif" id="paymentdate_cal">
          <a href="#" onclick="document.forms[0].payment_date.value = ''">clear</a>
        </p>
		<p><input type="submit" value="Submit" /></p>
		</fieldset>
	</html:form>
	<script type="text/javascript">
	Calendar.setup({ inputField : "payment_date", ifFormat : "%Y-%m-%d", showsTime :false, button : "paymentdate_cal", singleClick : true, step : 1 });
	</script>
</logic:notEqual>


</body>
</html>
