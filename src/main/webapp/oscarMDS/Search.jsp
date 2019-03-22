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
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ page import="java.time.LocalDate" %>
<html>
<head>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/moment.min.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/util/date.js"></script>
	<script type="text/javascript"
			src="<%= request.getContextPath() %>/js/jquery-1.9.1.js"></script>
	<script type="text/javascript"
			src="<%= request.getContextPath() %>/js/jquery-ui-1.10.2.custom.min.js"></script>

	<script type="text/javascript">
		function clearDate(id)
		{
			document.getElementById(id).value = '';
		}
		function handleDateChange(elem)
		{
			if (!Oscar.Util.Date.cleanDateInput(elem))
			{
				elem.focus();
				alert("<bean:message key="oscarMDS.search.msgInvalidDate"/>");
				return false;
			}
			return true;
		}
		function onSubmitCheck()
		{
			if (!handleDateChange(document.searchFrm.startDate)
				|| !handleDateChange(document.searchFrm.endDate))
			{
				return false;
			}

			var url = "../dms/inboxManage.do?method=prepareForIndexPage&providerNo=<%=request.getParameter("providerNo")%>";
			if ($("#provfind").val().trim() != "")
			{
				url += "&searchProviderNo=" + $("#provfind").val().trim();
			}
			else
			{
				url += "&searchProviderNo=-1";
			}
			if ($("#lname").val().trim() != "")
			{
				url += "&lname=" + $("#lname").val().trim();
			}
			if ($("#fname").val().trim() != "")
			{
				url += "&fname=" + $("#fname").val().trim();
			}
			if ($("#hnum").val().trim() != "")
			{
				url += "&hnum=" + $("#hnum").val().trim();
			}
			if ($("#startDate").val().trim() != "")
			{
				url += "&startDate=" + $("#startDate").val().trim();
			}
			if ($("#endDate").val().trim() != "")
			{
				url += "&endDate=" + $("#endDate").val().trim();
			}
			if ($("input[name='searchProviderAll']").is(':checked'))
			{
				url += "&searchProviderAll=" + $("input[name='searchProviderAll']:checked").val();
			}
			if ($("input[name='status']").is(':checked'))
			{
				url += "&status=" + $("input[name='status']:checked").val();
			}

			$("#searchFrm").attr("action", url);

		}

		function clearProvider()
		{

			setTimeout(function()
			{
				radios = $("input[name=searchProviderAll]");
				for (var i = 0; i < radios.length; i++)
				{
					if (radios[i].checked)
					{
						$("#autocompleteprov").val(null);
						$("#provfind").val(null);
					}
				}
			});
		}


		$(function()
		{

			$("#autocompleteprov").autocomplete({
				source: "<%= request.getContextPath() %>/provider/SearchProvider.do?method=labSearch",
				minLength: 2,
				messages: {
					noResults: '',
					results: ''
				},
				focus: function(event, ui)
				{
					$("#autocompleteprov").val(ui.item.label);
					return false;
				},
				select: function(event, ui)
				{
					// Uncheck the radios when a doctor is selected
					setTimeout(function()
					{
						$("#autocompleteprov").val(ui.item.label);
						$("#provfind").val(ui.item.value);
						radios = $("input[name=searchProviderAll]");
						for (var i = 0; i < radios.length; i++)
						{
							radios[i].checked = false;
						}
					});

					return false;
				},
				close: function(event, uid)
				{
					$("#autocompleteprov").val(null);
					$("#provfind").val(null);
				}
			})
		});

	</script>

	<style type="text/css">

		.ui-autocomplete {
			background-color: #CEF6CE;
			border: 3px outset #2EFE2E;
			width: 300px;
		}

		.ui-menu-item:hover {
			background-color: #426FD9;
			color: #FFFFFF;
		}

	</style>

	<link rel="stylesheet" type="text/css" href="encounterStyles.css">
	<title><bean:message key="oscarMDS.search.title"/></title>
</head>

<body>
<form id="searchFrm" name="searchFrm" method="POST" action="" onSubmit="return onSubmitCheck();">
	<input type="hidden" name="method" value="prepareForIndexPage"/>
	<table width="100%" height="100%" border="0">
		<tr class="MainTableTopRow">
			<td class="MainTableTopRow" colspan="9" align="left">
				<table width="100%">
					<tr>
						<td align="left"><input type="button"
												value=" <bean:message key="global.btnClose"/> "
												onClick="window.close()"></td>
						<td align="right"><oscar:help keywords="document" key="app.top1"/> | <a
								href="javascript:popupStart(300,400,'About.jsp')"><bean:message
								key="global.about"/></a> | <a
								href="javascript:popupStart(300,400,'License.jsp')"><bean:message
								key="global.license"/></a></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td valign="middle">
				<center>
					<table border="0" cellpadding="5" cellspacing="5">
						<tr>
							<td><bean:message key="oscarMDS.search.formPatientLastName"/>:
							</td>
							<td><input type="text" id="lname" name="lname" size="20"></td>
						</tr>
						<tr>
							<td><bean:message key="oscarMDS.search.formPatientFirstName"/>:
							</td>
							<td><input type="text" id="fname" name="fname" size="20"></td>
						</tr>
						<tr>
							<td><bean:message key="oscarMDS.search.formPatientHealthNumber"/>:
							</td>
							<td><input type="text" id="hnum" name="hnum" size="15"></td>
						</tr>

						<tr>
							<td>
								<label for="startDate">Start Date:(yyyy-mm-dd)</label>
							</td>
							<td><input type="text" id="startDate" name="startDate" size="15"
									   value="<%= LocalDate.now().minusMonths(3) %>"
									   onchange="handleDateChange(this)">
								&nbsp;&nbsp;&nbsp;<button type="button" onclick="clearDate('startDate'); return false;">clear</button>
							</td>
						</tr>
						<tr>
							<td>
								<label for="endDate">End Date:(yyyy-mm-dd)</label>
							</td>
							<td><input type="text" id="endDate" name="endDate" size="15"
									   value="<%= LocalDate.now() %>"
									   onchange="handleDateChange(this)">
								&nbsp;&nbsp;&nbsp;<button type="button" onclick="clearDate('endDate'); return false;">clear</button>
							</td>
						</tr>


						<tr>
							<td valign="top"><bean:message
									key="oscarMDS.search.formPhysician"/>:
							</td>
							<td><input type="radio" name="searchProviderAll" value="-1"
									   onclick="clearProvider();"
									   ondblclick="this.checked = false;">&nbsp;<bean:message
									key="oscarMDS.search.formPhysicianAll"/>
								<input type="radio" name="searchProviderAll" value="0"
									   onclick="clearProvider();"
									   ondblclick="this.checked = false;">&nbsp;<bean:message
										key="oscarMDS.search.formPhysicianUnclaimed"/>
								<input type="hidden" name="providerNo"
									   value="<%= request.getParameter("providerNo") %>">
							</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>
								<input type="hidden" name="searchProviderNo" id="provfind"/>
								<input type="text" id="autocompleteprov" name="demographicKeyword"/>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<center><bean:message
										key="oscarMDS.search.formReportStatus"/>: <input
										type="radio"
										name="status" value=""><bean:message
										key="oscarMDS.search.formReportStatusAll"/> <input
										type="radio"
										name="status" value="N" checked><bean:message
										key="oscarMDS.search.formReportStatusNew"/> <input
										type="radio"
										name="status" value="A"><bean:message
										key="oscarMDS.search.formReportStatusAcknowledged"/> <input
										type="radio" name="status" value="F">Filed
								</center>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<center><input type="submit"
											   value=" <bean:message key="oscarMDS.search.btnSearch"/> ">
								</center>
							</td>
						</tr>
					</table>
				</center>
			</td>
		</tr>
	</table>
</form>
</body>
</html>
