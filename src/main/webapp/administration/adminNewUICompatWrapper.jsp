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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="bean" uri="http://jakarta.apache.org/struts/tags-bean" %>

<!-- Compatibility page to allow old admin pages to function in the new admin, until we convert all of them -->
<html>
<head>
	<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.9.1.js"></script>

	<script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap-datepicker.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.validate.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath() %>/js/DT_bootstrap.js"></script>

	<link href="<%=request.getContextPath() %>/css/bootstrap.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath() %>/css/datepicker.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath() %>/css/DT_bootstrap.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath() %>/css/bootstrap-responsive.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome.min.css">
	<link rel="stylesheet" href="../css/helpdetails.css" type="text/css">

	<style rel="stylesheet">
		input[type="text"] {
			height: 30px;
		}
	</style>

	<script type="text/javascript">
		//////////// methods use by injected admin pages ////////////////

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

		function submitForm(formId, divId){
			// gather the form data
			var data = $(this).serialize();
			// post data
			$.post($('#'+formId).attr('action'), data, function(returnData) {
				// insert returned html
				$('#'+divId).html(returnData)
			})
		}

		function parseDate(date, format, separator) {
			if(!date){
				date = '';
			}
			var parts = date.split(separator), formatParts = format.split(separator),
				date1 = new Date(),
				val;
			date1.setHours(0);
			date1.setMinutes(0);
			date1.setSeconds(0);
			date1.setMilliseconds(0);
			if (parts.length === formatParts.length) {
				var year = date1.getFullYear(), day = date1.getDate(), month = date1.getMonth();
				for (var i=0, cnt = formatParts.length; i < cnt; i++) {
					val = parseInt(parts[i], 10)||1;
					switch(formatParts[i]) {
						case 'dd':
						case 'd':
							day = val;
							date1.setDate(val);
							break;
						case 'mm':
						case 'm':
							month = val - 1;
							date1.setMonth(val - 1);
							break;
						case 'yy':
							year = 2000 + val;
							date1.setFullYear(2000 + val);
							break;
						case 'yyyy':
							year = val;
							date1.setFullYear(val);
							break;
						default:
							if(!val)
								return null;
					}
				}
				date1 = new Date(year, month, day, 0 ,0 ,0);
				return date1;
			}
			return null;
		}

		function validDate(value, format, separator){
			try{
				var d = parseDate(value, format, separator);

				return d!=null;
			} catch(e){
				return false;
			}
		}

		function resizeIframe(newHgt)
		{
			$('#myFrame').height((parseInt(newHgt)+75)+'px');
			$("html, body").animate({ scrollTop: 0 }, "slow");
		}

		$(document).ready(function() {

			// set validation defaults
			jQuery.validator.setDefaults({
				debug: true,
				highlight: function(element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
				},
				success: function(element) {
					element.closest('.control-group').removeClass('error').addClass('success');
				}
			});


			jQuery.validator.addMethod("oscarDate", function(value, element) {
					return validDate(value, "yyyy-mm-dd", "-");
				},
				"Date format should be yyyy-mm-dd.");

			jQuery.validator.addMethod("oscarMonth", function(value, element) {
					return validDate(value, "mm/yyyy", "/");
				},
				"Date format should be mm/yyyy.");


			// initialiaze toolstips
			$('[rel=tooltip]').tooltip();
		});

		function popupPage(vheight, vwidth, varpage) {
			var page = "" + varpage;
			windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=50,screenY=50,top=0,left=0";
			var popup=window.open(page, "<bean:message key="provider.appointmentProviderAdminDay.apptProvider"/>", windowprops);
			if (popup != null) {
				if (popup.opener == null) {
					popup.opener = self;
				}
				popup.focus();
			}
		}

		$(document).ready(function() {
			$("#dynamic-content").load('<%=request.getParameter("frameUrl")%>');
		});
	</script>
</head>
<body>
	<div id="dynamic-content">
		<!-- dynamic content -->
	</div>
</body>
</html>
