
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
	String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	boolean authed = true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_report,_admin.reporting,_admin" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../../../securityError.jsp?type=_report&type=_admin.reporting&type=_admin");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<%@ page import="org.oscarehr.common.dao.ReportProviderDao"%>
<%@ page import="org.oscarehr.common.model.Provider"%>
<%@ page import="org.oscarehr.util.SpringUtils"%>
<%@ page import="oscar.util.DateUtils"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>


<%
	ReportProviderDao reportProviderDao = SpringUtils.getBean(ReportProviderDao.class);
	String xml_vdate = request.getParameter("xml_vdate") == null ? "" : request.getParameter("xml_vdate");
	String xml_appointment_date = request.getParameter("xml_appointment_date") == null ? "" : request.getParameter("xml_appointment_date");

	List<Provider> providers = new ArrayList<Provider>();

	for (Object[] result : reportProviderDao.search_reportprovider("billingreport")) {
		// result is an array consisting of {ReportProvider, Provider} objects
		providers.add((Provider)result[1]);
	}
%>
<html>
<head>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
	<html:base />
	<title>Billing Report</title>
	<link rel="stylesheet" type="text/css" media="all"
		  href="../../../share/calendar/calendar.css" title="win2k-cold-1" />
	<script type="text/javascript" src="../../../share/calendar/calendar.js"></script>
	<script type="text/javascript"
			src="../../../share/calendar/lang/<bean:message key="global.javascript.calendar"/>"></script>
	<script type="text/javascript"
			src="../../../share/calendar/calendar-setup.js"></script>
	<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />
	<script language="JavaScript">
        function popupPage(vheight, vwidth, varpage, pagename) {
            var page = "" + varpage,
                windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes",
                popup=window.open(page, pagename, windowprops);

            if (popup && popup.opener == null) {
                popup.opener = self;
            }
            popup.focus();
        }

        function setUIState() {
            var frm = document.forms[0],
                repType = frm.repType.value;

            if (repType === "REP_INVOICE" || repType === "REP_ACCOUNT_REC" || repType === "REP_REJ") {
                // Forms won't submit disabled elements, this is a purely cosmetic indicator for the user.
                frm.selProv.value = 'ALL';
                frm.selPayee.value = 'ALL';

                frm.selProv.disabled = true;
                frm.selPayee.disabled = true;
            } else {
            	frm.selProv.disabled = false;
            	frm.selPayee.disabled = false;
            }
        }

        function clearField(field){
            if (field === "xml_appointment_date") {
                document.forms[0].xml_appointment_date.value = ""
            }
            else if (field === "xml_vdate") {
                document.forms[0].xml_vdate.value = ""
            }
        }
	</script>

	<style type="text/css">
		.rowLabel {
			font-weight: 700;
			color: #333333;
			font-family: Verdana, Arial, Helvetica, sans-serif;
			font-size: 0.8em;
			padding-left: 15px;
		}

		.pageTitle {
			font-weight: 700;
			font-family: Verdana, Arial, Helvetica, sans-serif;
			font-size: 1.6em;
			padding: 5px 15px 5px 15px;
			color: lightgrey;
			white-space: nowrap;
		}

		.dateLabel {
			font-family: Verdana, Arial, Helvetica, sans-serif;
			font-size: 0.8em;
		}

		.rowText {
			font-family: Verdana, Arial, Helvetica, sans-serif;
			font-size: 1.0em;
		}

	</style>

</head>
<body bgcolor="#FFFFFF" text="#000000" onLoad="setUIState()">
<table width="100%" border="1" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<table width="100%" border="1" cellspacing="0" cellpadding="0">
				<tr bgcolor="#FFFFFF">
					<td>
						<div align="right">
							<a href="javascript: return false;" class="rowText"
							   onClick="popupPage(700,720,'../../../oscarReport/manageProvider.jsp?action=billingreport', 'attachment')">
								Manage Provider List
							</a>
						</div>
					</td>
				</tr>
			</table>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr bgcolor="#000000">
					<td class="pageTitle" align="left">Billing Reports</td>
					<td class="pageTitle" align="right"><%=DateUtils.sumDate("yyyy-M-d","0")%></td>
				</tr>
			</table>
			<form action="createBillingReportAction.do">
				<table width="100%" border="0" bgcolor="#EEEEFF">
					<tr>
						<td width="34%" class="rowLabel">Select Payee</td>
						<td width="66%" class="bCellData">
							<select name="selPayee" size="1" id="selPayee">
								<option value="ALL">All Payees</option>
								<% for (Provider payee : providers) {	// For this option, we want the value to be the OHIP number.%>
								<option value="<%=payee.getOhipNo()%>"><%=payee.getLastName()%>, <%=payee.getFirstName()%></option>
								<% } %>
							</select>
						</td>
					</tr>
					<tr>
						<td class="rowLabel">Select Provider</td>
						<td class="bCellData">
							<select name="selProv" id="select">
								<option value="ALL">All Providers</option>
								<% for (Provider provider : providers) { %>
								<option value="<%=provider.getProviderNo()%>"><%=provider.getLastName()%>, <%=provider.getFirstName()%></option>
								<% } %>
							</select>
						</td>
					</tr>
					<tr>
						<td class="rowLabel">Select Account</td>
						<td class="bCellData">
							<select name="selAccount" id="select2">
								<option value="ALL">All Accounts</option>
								<% for (Provider account : providers) { %>
								<option value="<%=account.getProviderNo()%>"><%=account.getLastName()%>, <%=account.getFirstName()%></option>
								<% } %>
							</select>
						</td>
					</tr>
					<tr>
						<td class="rowLabel">Report Type</td>
						<td class="bCellData">
							<select name="repType" onChange="setUIState()">
								<option value="REP_INVOICE">Invoice</option>
								<option value="REP_REJ">Rejection</option>
								<option value="REP_ACCOUNT_REC">Accounts Receivable</option>
								<option value="REP_WO">Write-Off</option>
								<option value="REP_PAYREF">Payments and Refunds(Cash)</option>
								<option value="REP_PAYREF_SUM">Payments and Refunds Summary</option>
							</select>
						</td>
					</tr>
					<tr>
						<td class="rowLabel">Date Range</td>
						<td class="bCellData">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<a href="javascript: return false;" class="dateLabel" id="hlSDate">Start Date :</a>
									</td>
									<td>
										<input type="text" name="xml_vdate" id="xml_vdate" value="<%=xml_vdate%>" readonly="true">
										<a href="javascript: clearField('xml_vdate')" class="dateLabel">clear</a>
									</td>
								</tr>
								<tr>
									<td>
										<a href="javascript: return false;" class="dateLabel" id="hlADate">End Date :</a>
									</td>
									<td>
										<input type="text" name="xml_appointment_date" id="xml_appointment_date" value="<%=xml_appointment_date%>" readonly="true">
										<a href="javascript: clearField('xml_appointment_date')" class="dateLabel">clear</a>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td class="rowLabel">Document Format</td>
						<td class="bCellData">
							<select name="docFormat" id="select3">
								<option value="pdf">PDF</option>
								<option value="csv">Spread Sheet</option>
							</select>
						</td>
					</tr>
					<tr>
						<td class="rowLabel">Insurer</td>
						<td class="bCellData">
							<table width="100%">
								<tr>
									<td class="bCellData">
										<label for="showMSP" class="rowText">MSP</label>
										<input name="showMSP" id="showMSP" type="checkbox" value="true" checked/>
									</td>
									<td class="bCellData">
										<label for="showWCB" class="rowText">WCB</label>
										<input name="showWCB" id="showWCB" type="checkbox" value="true" checked/>
									</td>
									<td class="bCellData">
										<label for="showPRIV" class="rowText">Private</label>
										<input name="showPRIV" id="showPRIV" type="checkbox" value="true" checked/>
									</td>
									<td class="bCellData">
										<label for="showICBC" class="rowText">ICBC</label>
										<input name="showICBC" id="showICBC" type="checkbox" value="true" checked/>
									</td>
								</tr>
							</table>
					</tr>
					<tr>
						<td colspan="2">
							<div align="center" style="margin-top:20px">
								<input type="hidden" name="verCode" value="V03" />
								<input type="submit" name="Submit" value="Create Report">
							</div>
						</td>
					</tr>
				</table>
			</form>

			<script language='javascript'>
                Calendar.setup ({
                    inputField:"xml_vdate",
                    ifFormat:"%Y-%m-%d",
                    showsTime:false,
                    button:"hlSDate",
                    singleClick:true,
					step:1
                });
                Calendar.setup ({
                    inputField:"xml_appointment_date",
                    ifFormat:"%Y-%m-%d",
                    showsTime:false,
                    button:"hlADate",
                    singleClick:true,
                    step:1
                });
			</script>
		</td>
	</tr>
</table>
</body>
</html>
