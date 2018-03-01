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
	boolean faxAll = request.getParameter("faxAll") != null;
%>
<html>
<head>
	<script src="<%=request.getContextPath() %>/share/javascript/jquery/jquery-2.2.4.min.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath() %>/share/javascript/eforms/faxControl.js" type="text/javascript"></script>
	<script type="text/javascript">
		function submitForm ()
		{
			var parentForm = window.opener.document.querySelector('form');

			$("form").find("input:hidden").appendTo(parentForm);

			// let form set things up as it would for printing but set target to original window
			// (we don't want that window to persist after it's been polluted with faxing values)
			if (<%=faxAll%>)
			{
				window.opener.onPrintAll();
			}
			else
			{
				window.opener.onPrint();
			}
			parentForm.target = "_self";

			window.close();
			HTMLFormElement.prototype.submit.call(parentForm);
		}
	</script>
	<title>Fax Form</title>
</head>
<body>
<form action="#" onsubmit="submitForm();">
	<div id="faxControl"></div>
</form>
</body>
</html>
