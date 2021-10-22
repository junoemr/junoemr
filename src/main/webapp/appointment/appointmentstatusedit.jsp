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
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<%
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
%>
<security:oscarSec roleName="<%=roleName$%>"
	objectName="_admin,_admin.userAdmin,_admin.schedule" rights="r" reverse="<%=true%>">
	<%response.sendRedirect("../logout.jsp");%>
</security:oscarSec>

<html>
<head>
<title><bean:message key="admin.appt.status.mgr.title" /></title>
<link href="../css/jquery.ui.colorPicker.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />
<link rel="stylesheet" type="text/css" media="all" href="../css/font/junoIcons/stylesheet.css" />
<script src="../js/jquery-3.1.0.min.js" type="text/javascript"></script>
<script src="../js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>
<script src="../js/jquery.ui.colorPicker.min.js" type="text/javascript"></script>

	<style>
        tr {
            height: 40px;
        }

	    .juno-color-button {
		    width: 25px;
		    height: 25px;
		    border: 0;
		    border-radius: 2px;
	    }

        .submit {
            margin: 0 auto;
            display: block;
        }

        .margin-t {
            margin-top: 32px;
        }

        .margin-b {
            margin-bottom: 32px;
        }

        .text-center {
            text-align: center;
        }

        .header {
            width: 20%;
            font-weight: bold;
        }

        .right {
            text-align: right;
            padding-right: 8px;
        }

        .grey {
            color: #808080;
        }

        .preview {
            padding-left: 4px;
            max-height: 10px;
        }

        .w-10 {
            width: 10%;
        }

        .w-15 {
            width: 15%;
        }
	</style>
</head>
<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />
<body>
<script type="text/javascript">
	$(document).ready(function ()
	{
        var color = $('#apptColor').val();
        var junoColor = $('#apptJunoColor').val();

        var $apptColorPicker = $('#apptColor');
        var $classicPreview = $('#classic-preview');
		$apptColorPicker.colorPicker({
			format: 'hex',
			colorChange: function (e, ui)
			{
				$apptColorPicker.val(ui.color);
				$apptColorPicker.css("background-color", ui.color);
				$classicPreview.css('background-color', ui.color);
			}
		}).colorPicker('setColor', color);
		$apptColorPicker.css("background-color", color);
		$classicPreview.css('background-color', color);

		var $junoColorPicker = $('#apptJunoColor');
		var $junoPreview = $('#juno-preview');
		$junoColorPicker.colorPicker({
			format: 'hex',
			colorChange: function (e, ui)
			{
				$junoColorPicker.val(ui.color);
				$junoColorPicker.css("background-color", ui.color);
				$junoPreview.css('background-color', ui.color);
			}
		}).colorPicker('setColor', junoColor);
		$junoColorPicker.css("background-color", junoColor);
		$junoPreview.css('background-color', junoColor);

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

        $("#icon-picker").on("change", updateIconPreview);
		updateIconPreview();

        $("#form-submit").on("click", function(event)
        {
        	processForm(event);
        });
	});

	function updateIconPreview()
    {
    	var iconRegex = /(.+)\.gif/g;
    	var iconFile = $('#icon-picker').val();

	    var name = iconRegex.exec(iconFile)[1];

	    var $junoIcon = $('#juno-preview > i');
	    $junoIcon.attr("class", "preview icon-" + name);

	    var $classicIcon = $('#classic-preview > img');
	    $classicIcon.attr("src", "../images/" + iconFile);
    }

	function processForm(event)
    {
    	event.preventDefault();

    	var description = $('#appt-description').val();
    	if (!description || !description.trim())
        {
        	alert("Appointment Status must have a valid description");
        	return false;
        }
    	else
    	{
    		var endpoint = $('#statusForm').attr('action');
            var method = $('#action').val();

		    $('#statusForm').attr('action', endpoint + "?method=" + method);
    	    document.forms[0].submit();
        }
    }
</script>

<table border=0 cellspacing=0 cellpadding=0 width="100%">
	<tr bgcolor="#486ebd">
		<th align="CENTER" NOWRAP><font face="Helvetica" color="#FFFFFF">
            <c:if test = "${ action eq 'update'}">
                Update an Appointment Status
            </c:if>
            <c:if test = "${ action eq 'add'}">
                Create a New Appointment Status
            </c:if>
        </font></th>
	</tr>
</table>
<input type="hidden" id="action" value="<c:out value="${action}"/>">
<html:form action="/appointment/apptStatusSetting" styleId="statusForm">
    <div>
        <table class="margin-t">
            <tr>
                <td class="header"><bean:message key="admin.appt.status.mgr.label.desc" /></td>
                <c:if test = "${ canDisable eq 1}">
                <td><html:text property="description" styleId="appt-description" size="40"></html:text></td>
                </c:if>
                <c:if test = "${ canDisable eq 0}">
                <td><html:text property="description" readonly="true" title="System managed status descriptions cannot be changed" styleId="appt-description" size="40"></html:text></td>
                </c:if>
            </tr>
            <tr>
                <td class="header">Icon</td>
                <td>
                    <html:select property="icon" styleId="icon-picker">
                        <html:option value="starbill.gif">StarBill</html:option>
                        <html:option value="todo.gif">Todo</html:option>
                        <html:option value="here.gif">Here</html:option>
                        <html:option value="picked.gif">Picked</html:option>
                        <html:option value="noshow.gif">No Show</html:option>
                        <html:option value="cancel.gif">Cancel</html:option>
                        <html:option value="billed.gif">Billed</html:option>
                        <html:option value="empty.gif">Empty</html:option>
                        <html:option value="dot.gif">Dot</html:option>
                        <html:option value="1.gif">Custom 1</html:option>
                        <html:option value="2.gif">Custom 2</html:option>
                        <html:option value="3.gif">Custom 3</html:option>
                        <html:option value="4.gif">Custom 4</html:option>
                        <html:option value="5.gif">Custom 5</html:option>
                        <html:option value="6.gif">Custom 6</html:option>
                        <html:option value="7.gif">Custom 7</html:option>
                        <html:option value="8.gif">Custom 8</html:option>
                        <html:option value="9.gif">Custom 9</html:option>
                        <html:option value="10.gif">Custom 10</html:option>
                        <html:option value="11.gif">Custom 11</html:option>
                    </html:select>
                </td>
                <td class="w-15 right">Classic Preview</td>
                <td id="classic-preview" class="w-10"><img class="preview"/></td>
                <td class="w-15 right">Juno Preview</td>
                <td id="juno-preview" class="w-10"><i></i></td>
            </tr>

            <tr>
                <td class="header">Toggle</td>
                <c:if test = "${ canDisable eq 1}">
                <td>
                    <html:select property="active">
                        <html:option value="1">Enabled</html:option>
                        <html:option value="0">Disabled</html:option>
                    </html:select>
                </td>
                </c:if>
                <c:if test = "${ canDisable eq 0}">
                <td class="grey">Cannot be disabled</td>
                </c:if>
            </tr>
        </table>
        <hr class="margin-t margin-b">
        <table>
            <tr>
                <td class="header text-center">
                    Classic UI Color
                </td>
                <td style="width: 96px"></td>
                <td colspan="2" class="header text-center">
                    Juno UI Color
                </td>
            </tr>
            <tr>
                <td>
                    <html:text property="color" styleId="apptColor" size="20"></html:text>
                </td>
                <td></td>
                <td>
                    <html:text property="junoColor" styleId="apptJunoColor" size="20"></html:text>

                </td>
                <td><div id="junoColorPalette"></div></td>
                <td></td>
            </tr>
        </table>
        <hr class="margin-t margin-b">
        <div>
            <html:hidden property="id"/>
            <html:submit styleId="form-submit" styleClass="submit">
                <c:if test="${ action eq 'add'}">Create Status</c:if>
                <c:if test="${ action eq 'update'}">Update Status</c:if>
            </html:submit>
        </div>
    </div>
</html:form>
</body>
</html>
