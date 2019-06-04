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

<!-- make div layout more fluid see medical history as an example -->
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
	<div class="modal-content">

		<div class="modal-header"  > 
		<button type="button" class="close" data-dismiss="modal" aria-label="Close" ng-click="recordPrintCtrl.cancelPrint()"><span aria-hidden="true" >&times;</span></button>
			<h3 class="modal-title"><bean:message key="oscarEncounter.Index.PrintDialog" /></h3>
		</div>

		<div class="modal-body">			
	        	<div class="row">
		        	<div class="alert alert-danger" ng-show="recordPrintCtrl.page.selectedWarning">
	  					<strong><bean:message key="global.warning" /></strong> 
						<bean:message key="oscarEncounter.nothingToPrint.msg" />
					</div>
	        	
	        		<div class="col-xs-10 col-xs-offset-1">
						<form>
							<div class="form-group col-sm-6">
								<label></label>
								<div class="controls">
									<label class="radio-inline">
										<input type="radio" ng-model="recordPrintCtrl.pageOptions.printType" id="printopSelected" value="selected">
										<bean:message key="oscarEncounter.Index.PrintSelect" />
									</label>
									<label class="radio-inline">
										<input type="radio" ng-model="recordPrintCtrl.pageOptions.printType" id="printopAll" value="all">
										<bean:message key="oscarEncounter.Index.PrintAll" />
									</label>
									<label class="radio-inline">
										<input type="radio" ng-model="recordPrintCtrl.pageOptions.printType" id="printopDates" value="dates">
										<bean:message key="oscarEncounter.Index.PrintDates" />&nbsp;
										<a ng-click="recordPrintCtrl.printToday()" ><bean:message key="oscarEncounter.Index.PrintToday" /></a><br>
									</label>
								</div>
							</div>
							<div class="form-group col-sm-6">
								<label></label>
								<div class="controls">
									<label class="checkbox-inline">
										<input type="checkbox" value="true" ng-model="recordPrintCtrl.pageOptions.cpp" alt="<bean:message key="oscarEncounter.togglePrintCPP.title"/>" id="imgPrintCPP"  >
										<bean:message key="oscarEncounter.cpp.title" />
									</label>
									<label class="checkbox-inline">
										<input type="checkbox" value="true" ng-model="recordPrintCtrl.pageOptions.rx" alt="<bean:message key="oscarEncounter.togglePrintRx.title"/>" id="imgPrintRx"  >
										<bean:message key="oscarEncounter.Rx.title" />
									</label>
									<label class="checkbox-inline">
										<input type="checkbox" value="true" ng-model="recordPrintCtrl.pageOptions.labs" alt="<bean:message key="oscarEncounter.togglePrintLabs.title"/>" id="imgPrintLabs"  >
										<bean:message key="oscarEncounter.Labs.title" />
									</label>
								</div>
							</div>		
							<div class="form-group col-sm-6" ng-show="recordPrintCtrl.pageOptions.printType === 'dates'">
								<label for="exampleInputEmail1"><bean:message key="oscarEncounter.startdate.title" /></label>
								<juno-datepicker-popup  juno-model="recordPrintCtrl.pageOptions.dates.start" 
									placeholder="Start Date"
									show-icon="true"
									type="Input"> 
								</juno-datepicker-popup>
							</div>
							<div class="form-group col-sm-6" ng-show="recordPrintCtrl.pageOptions.printType === 'dates'">
								<label for="exampleInputEmail1"><bean:message key="global.enddate" /></label>
								<juno-datepicker-popup  juno-model="recordPrintCtrl.pageOptions.dates.end" 
									placeholder="End Date"
									show-icon="true"
									type="Input"> 
								</juno-datepicker-popup>
							</div>		
						</form>
	        		</div>
	        	</div>
	        	
		</div><!-- modal-body -->		
		<div class="modal-footer">		
			<input type="button" class="btn btn-default" ng-click="recordPrintCtrl.cancelPrint()" value="<bean:message key="global.btnCancel"/>"> 
			<input type="button" class="btn btn-default" ng-click="recordPrintCtrl.sendToPhr();" value="<bean:message key="global.btnSendToPHR"/>">
			<input type="button" class="btn btn-default" ng-click="recordPrintCtrl.print();" value="<bean:message key="global.btnPrint"/>" >
		</div>
	</div>
