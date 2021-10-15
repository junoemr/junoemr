

<%@ page import="oscar.oscarReport.data.DoctorList"%>
<%@ page import="oscar.oscarProvider.bean.ProviderNameBean"%>
<%@ page import="java.util.ArrayList"%>

<%@ include file="/taglibs.jsp"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"
	scope="request" />

<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
      boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_report,_admin.reporting" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_report&type=_admin.reporting");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>

<div class="page-header">
	<h4>Patient List</h4>
</div>

<form id="plForm" action="${ctx}/patientlistbyappt" class="well form-horizontal">

	<fieldset>
		<h4>
			<bean:message key="admin.admin.exportPatientbyAppt"/> <br> <small>Please select
				the provider and appointment date from &amp; to.</small>
		</h4>
		<div class="row-fluid">
			<div class="control-group">
				<label class="control-label">Doctor</label>
				<div class="controls">
					<select name="provider_no" class="span3">
						<option value="all">All Doctors</option>
						<%
							ArrayList<ProviderNameBean> dnl = new DoctorList().getDoctorNameList();
							for (int i = 0; i < dnl.size(); i++) {
								ProviderNameBean pb = (ProviderNameBean) dnl.get(i);
						%>
						<option value="<%=pb.getProviderID()%>"><%=pb.getProviderName()%></option>
						<%
							}
						%>
					</select>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Date From</label>
				<div class="controls">
					<input id="date_from" name="date_from" size="10"
						type="text" />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Date To</label>
				<div class="controls">
					<input id="date_to" name="date_to" size="10"
						type="text" />
				</div>
			</div>
			<div class="control-group">
				<div class="controls">
					<button type="submit" class="btn btn-primary">
						<i class="icon-download-alt icon-white"></i>Export
					</button>
				</div>
			</div>
		</div>
	</fieldset>
</form>

<script>
	var startDt = $("#date_from").datepicker({
		format : "yyyy-mm-dd"
	});

	var endDt = $("#date_to").datepicker({
		format : "yyyy-mm-dd"
	});

	$(document).ready(function() {
		$('#plForm').validate({
			rules : {
				date_from : {
					required : true,
					oscarDate : true
				},
				date_to : {
					required : true,
					oscarDate : true
				}
			}
		});
	});

	$('#plForm').submit(function () {
		var $form = $('#plForm');

		if ($form.valid())
		{
			var $path = $form.attr('action');
			var $provider_no = $('#provider_no').val();
			var $date_from = $('#date_from').val();
			var $date_to = $('#date_to').val();
			var userAgentString = navigator.userAgent;
			var isChrome = userAgentString.indexOf("Chrome") > -1;
			var new_window;

			$(document).find(':button[type=submit]').prop('disabled', true);

			if (isChrome)
			{
				window.alert("Please wait for the file to be downloaded, then close the download window to re-enable the Export button.");
				new_window = window.open($path + "?" + $form.serialize(), "Downloading", "length=100,width=100");
				new_window.document.title = "File Download";
			}
			else
			{
				new_window = window.open($path + "?" + $form.serialize())
			}

			new_window.onbeforeunload = (function() {
				$(document).find(':button[type=submit]').prop('disabled', false);
			});
		}
	});
</script>