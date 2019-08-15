<%--

    Copyright (c) 2006-. OSCARservice, OpenSoft System. All Rights Reserved.
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

--%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>

<%
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
%>
<security:oscarSec roleName="<%=roleName$%>"
	objectName="_admin,_admin.userAdmin,_admin.schedule" rights="r" reverse="<%=true%>">
	<%response.sendRedirect("../logout.jsp");%>
</security:oscarSec>

<html>
<head>
<%--<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>--%>
<title><bean:message key="admin.appt.status.mgr.title" /></title>
<link href="../css/jquery.ui.colorPicker.css" rel="stylesheet" type="text/css" />
<%--<script src="../js/jquery-1.7.1.min.js" type="text/javascript"></script>--%>
	<script src="../js/jquery-3.1.0.min.js" type="text/javascript"></script>

	<script src="../js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>
<script src="../js/jquery.ui.colorPicker.min.js" type="text/javascript"></script>
<oscar:customInterface section="apptStatusEdit"/>

	<style>
		.juno-color-button {
			width: 25px;
			height: 25px;
			border: 0;
			border-radius: 2px;
		}
	</style>
</head>
<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />
<body>
<script type="text/javascript">
	$(document).ready(function ()
	{
		// init colour pickers
		$('#apptColor').colorPicker({
			format: 'hex',
			colorChange: function (e, ui)
			{
				$('#apptColor').val(ui.color);
			}
		}).colorPicker('setColor', $('#old_color').val());

		var $junoColorPicker = $('#apptJunoColor');
		$junoColorPicker.colorPicker({
			format: 'hex',
			colorChange: function (e, ui)
			{
				$('#apptJunoColor').val(ui.color);
			}
		}).colorPicker('setColor', $('#old_juno_color').val());

		var colorCodeArray = [
			//darkest, darker,  dark,   base,    light,   lighter,  lightest
			['401616','661a1a','991f1f','cc2929','e65c5c','e68a8a','ffcccc'], //red
			['4d2a08','80460d','b36212','e67e17','e69545','f2b679','ffd9b3'], //orange
			['4d420f','806e19','b39b24','e6c72e','e6cf5c','f2e291','fff7cc'], //yellow
			['243811','406619','609926','80cc33','a6e667','bde695','e6ffcc'], //lime
			['0d3313','1a6626','238c35','30bf48','62d975','95e6a3','ccffd4'], //green
			['0d3326','165943','238c69','30bf8f','62d9b1','95e6cb','ccffee'], //teal
			['0d3333','165959','238c8c','30bfbf','62d9d9','95e6e6','ccffff'], //cyan
			['102f40','164259','23678c','308dbf','62afd9','8ac5e6','ccedff'], //blue
			['1a2040','1f2b66','263999','334dcc','5c73e6','8a99e6','ccd5ff'], //indigo
			['231b4d','302080','432db3','5639e6','7c67e6','ac9df2','d4ccff'], //violet
			['301640','4b1f66','6d2699','9133cc','a15ccc','c795e6','ebccff'], //grape
			['401624','661933','99264d','cc3366','e66791','e6a1b8','ffccdd'], //pink
		];

		// init juno pre-picked colour palette
		var $junoColorPalette = $("#junoColorPalette");
		var $table = $("<table>");
		for(var i=0; i < colorCodeArray.length; i++)
		{
			var $row = $("<tr>");
			for(var j=0; j < colorCodeArray[i].length; j++)
			{
				var colorCode = colorCodeArray[i][j];

				var $button = $("<td>").append($("<button>", {
					style: "background-color: #" + colorCode + ";",
					class: "juno-color-button",
				}));
				$row.append($button);
			}
			$table.append($row);
		}
		$junoColorPalette.append($table);

		$(".juno-color-button").on("click", function(event)
		{
			var colorCode = $(this).css('background-color');
			$junoColorPicker.colorPicker('setColor', colorCode);
			event.preventDefault();
		})
	});
</script>

<table border=0 cellspacing=0 cellpadding=0 width="100%">
	<tr bgcolor="#486ebd">
		<th align="CENTER" NOWRAP><font face="Helvetica" color="#FFFFFF"><bean:message
			key="admin.appt.status.mgr.title" /></font></th>
	</tr>
</table>


<html:form action="/appointment/apptStatusSetting">
	<table>
		<tr>
			<td class="tdLabel"><bean:message
				key="admin.appt.status.mgr.label.status" />:</td>
			<td colspan="3"><html:text readonly="true" property="apptStatus" size="40" /></td>
		</tr>
		<tr>
			<td class="tdLabel"><bean:message
				key="admin.appt.status.mgr.label.desc" />:</td>
			<td colspan="3"><html:text property="apptDesc" size="40" /></td>
		</tr>

		<tr>
			<td class="tdLabel"><bean:message
					key="admin.appt.status.mgr.label.oldcolor" />:</td>
			<td><html:text readonly="true" styleId="old_color" property="apptOldColor" size="20" />
			</td>
		</tr>
		<tr>
			<td class="tdLabel"><bean:message
					key="admin.appt.status.mgr.label.newcolor" />:</td>
			<td>
				<input id="apptColor" name="apptColor" value="" size="20" />
			</td>
		</tr>
		<tr>
			<td class="tdLabel"><bean:message
					key="admin.appt.status.mgr.label.oldJunoColor" />:</td>
			<td><html:text readonly="false" styleId="old_juno_color" property="apptOldJunoColor" size="20" />
			</td>
		</tr>
		<tr>
			<td class="tdLabel"><bean:message
					key="admin.appt.status.mgr.label.newJunoColor" />:</td>
			<td>
				<input id="apptJunoColor" name="apptJunoColor" value="#000000" size="20" />
			</td>

			<td id="junoColorPalette">
			</td>
		</tr>

		<div id="list_entries"></div>
		<tr>
			<td colspan="2"><html:hidden property="ID" /> <input
				type="hidden" name="dispatch" value="update" /> <br />
			<input type="submit"
				value="<bean:message key="oscar.appt.status.mgr.label.submit"/>" />
			</td>
		</tr>
	</table>
</html:form>
</body>
</html>
