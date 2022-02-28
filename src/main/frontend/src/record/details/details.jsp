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

<juno-security-check show-placeholder="true" permissions="$ctrl.SecurityPermissions.DemographicRead">
	<div class="col-xs-12" id="patient-details-page" ng-class="$ctrl.pageClasses()">
		<div class="row">
				<div class="col-md-10 col-lg-8">
					<div class="row">
						<div class="col-md-8 pull-left">
							<div class="btn-group sub-nav">
								<div class="btn-group">
									<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
										<bean:message key="demographic.demographicprintdemographic.btnPrint"/> <span class="caret"></span>
									</button>
									<ul class="dropdown-menu">
										<li><a href="../report/GenerateEnvelopes.do?demos={{$ctrl.page.demo.id}}">PDF Envelope</a></li>
										<li><a class="hand-hover" ng-click="$ctrl.printLabel('PDFLabel')"><bean:message key="demographic.demographiceditdemographic.btnCreatePDFLabel"/></a></li>
										<li><a class="hand-hover" ng-click="$ctrl.printLabel('PDFAddress')"><bean:message key="demographic.demographiceditdemographic.btnCreatePDFAddressLabel"/></a></li>
										<li><a class="hand-hover" ng-click="$ctrl.printLabel('PDFChart')"><bean:message key="demographic.demographiceditdemographic.btnCreatePDFChartLabel"/></a></li>
										<li><a class="hand-hover" ng-click="$ctrl.printLabel('PrintLabel')"><bean:message key="demographic.demographiceditdemographic.btnPrintLabel"/></a></li>
										<li><a class="hand-hover" ng-click="$ctrl.printLabel('ClientLab')"><bean:message key="demographic.demographiceditdemographic.btnClientLabLabel"/></a></li>
									</ul>
								</div>
								<div class="btn-group">
									<button type="button" class="btn btn-primary dropdown-toggle"
											data-toggle="dropdown"
											ng-show="$ctrl.page.integratorEnabled"
											style="color:{{$ctrl.page.integratorStatusColor}}"
											title="{{$ctrl.page.integratorStatusMsg}}">
										<bean:message key="web.record.details.integrator"/> <span class="caret"></span>
									</button>
									<ul class="dropdown-menu">
										<li ng-show="$ctrl.page.integratorOffline"><a style="color:#FF5500">{{$ctrl.page.integratorStatusMsg}}</a></li>
										<li ng-hide="$ctrl.page.integratorOffline" title="{{$ctrl.page.integratorStatusMsg}}">
											<a style="color:{{$ctrl.page.integratorStatusColor}}"
											   ng-click="$ctrl.integratorDo('ViewCommunity')">
												<bean:message key="web.record.details.viewIntegratedCommunity"/>
											</a>
										</li>
										<li><a ng-click="$ctrl.integratorDo('Linking')"><bean:message key="web.record.details.manageLinkedClients"/></a></li>
										<div ng-show="!$ctrl.page.integratorOffline">
											<li><a ng-click="$ctrl.integratorDo('Compare')"><bean:message key="web.record.details.compareWithIntegrator"/></a></li>
											<li><a ng-click="$ctrl.integratorDo('Update')"><bean:message key="web.record.details.updateFromIntegrator"/></a></li>
											<li><a ng-click="$ctrl.integratorDo('SendNote')"><bean:message key="web.record.details.sendNoteIntegrator"/></a></li>
										</div>
									</ul>
								</div>
								<div class="btn-group">
									<button type="button"
									        class="btn btn-default dropdown-toggle"
									        ng-disabled="!$ctrl.canAccessAppointments()"
									        data-toggle="dropdown">
										<bean:message key="demographic.demographiceditdemographic.msgAppt"/> <span class="caret"></span>
									</button>
									<ul class="dropdown-menu">
										<li><a class="hand-hover" ng-click="$ctrl.appointmentDo('ApptHistory')"><bean:message key="demographic.demographiceditdemographic.btnApptHist"/></a></li>
										<li><a class="hand-hover" ng-click="$ctrl.appointmentDo('WaitingList')"><bean:message key="demographic.demographiceditdemographic.msgWaitList"/></a></li>
									</ul>
								</div>
								<div class="btn-group">
									<button type="button"
									        class="btn btn-default dropdown-toggle"
									        ng-disabled="!$ctrl.canAccessBilling()"
									        data-toggle="dropdown">
										<bean:message key="admin.admin.billing" /> <span class="caret"></span>
									</button>
									<ul class="dropdown-menu">
										<li><a class="hand-hover" ng-click="$ctrl.billingDo('BillingHistory')">{{$ctrl.page.billingHistoryLabel}}</a></li>
										<li><a class="hand-hover" ng-click="$ctrl.billingDo('CreateInvoice')"><bean:message key="demographic.demographiceditdemographic.msgCreateInvoice"/></a></li>
										<li ng-hide="$ctrl.isClinicaidBilling()"><a class="hand-hover" ng-click="$ctrl.billingDo('FluBilling')"><bean:message key="demographic.demographiceditdemographic.msgFluBilling"/></a></li>
										<li ng-hide="$ctrl.isClinicaidBilling()"><a class="hand-hover" ng-click="$ctrl.billingDo('HospitalBilling')"><bean:message key="demographic.demographiceditdemographic.msgHospitalBilling"/></a></li>
										<li ng-hide="$ctrl.isClinicaidBilling()"><a class="hand-hover" ng-click="$ctrl.billingDo('AddBatchBilling')"><bean:message key="demographic.demographiceditdemographic.msgAddBatchBilling"/></a></li>
										<li ng-hide="$ctrl.isClinicaidBilling()"><a class="hand-hover" ng-click="$ctrl.billingDo('AddINR')"><bean:message key="demographic.demographiceditdemographic.msgAddINR"/></a></li>
										<li ng-hide="$ctrl.isClinicaidBilling()"><a class="hand-hover" ng-click="$ctrl.billingDo('BillINR')"><bean:message key="demographic.demographiceditdemographic.msgINRBill"/></a></li>
									</ul>
								</div>
								<div class="btn-group" ng-show="$ctrl.page.macPHRIdsSet">
									<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
										<bean:message key="global.personalHealthRecord"/> <span class="caret"></span>
									</button>
									<ul class="dropdown-menu">
										<li><a class="hand-hover" ng-click="$ctrl.macPHRDo('SendMessage')"><bean:message key="demographic.demographiceditdemographic.msgSendMsgPHR"/></a></li>
										<li><a class="hand-hover" ng-click="$ctrl.macPHRDo('ViewRecord')"><bean:message key="web.record.details.viewPhrRecord"/></a></li>
									</ul>
								</div>
								<div class="btn-group">
									<button type="button"
									        class="btn btn-default"
									        ng-disabled="!$ctrl.canAccessExport()"
									        ng-click="$ctrl.exportDemographic()">
										<bean:message key="export"/>
									</button>
								</div>

							</div>
							<button class="btn btn-primary" ng-if="$ctrl.showEligibility" uib-popover-html="$ctrl.eligibilityMsg" popover-placement="bottom" popover-trigger="'outsideClick'" ng-click="$ctrl.checkEligibility()">
								Check Eligibility
							</button>
							<div class="btn-group">
								<juno-button
										id="swipe-health-card-button"
										button-color="JUNO_BUTTON_COLOR.PRIMARY"
										button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.COLORED"
										click="$ctrl.openSwipecardModal()"
										disabled="!$ctrl.canEdit()">
									Swipe Health Card
								</juno-button>
							</div>
						</div>
						<div class=" col-sm-2 col-xs-12 pull-right">
							<button id="save-button-top"
									type="button"
									class="btn btn-success"
									ng-click="$ctrl.validateHCSave(true)"
									ng-disabled="!$ctrl.canEdit() || $ctrl.page.dataChanged === false">
								Save
							</button>
						</div>
					</div>
				</div>
		</div>
		<div id="profile-fields" class="flex-row flex-wrap justify-content-start m-t-8">
			<div class="flex-item-grow-2" ng-if="$ctrl.page.demo">
				<div id="pd1" ng-click="$ctrl.checkAction($event)" ng-keypress="$ctrl.checkAction($event)">

					<demographic-section ng-model="$ctrl.page.demo"
					                     validations="$ctrl.validations"
					                     component-style="pageStyle">
					</demographic-section>
					<juno-divider component-style="pageStyle"></juno-divider>

					<contact-section ng-model="$ctrl.page.demo"
					                 validations="$ctrl.validations"
					                 component-style="pageStyle">
					</contact-section>
					<juno-divider component-style="pageStyle"></juno-divider>

					<health-insurance-section ng-model="$ctrl.page.demo"
					                          validations="$ctrl.validations"
					                          component-style="pageStyle">
					</health-insurance-section>
					<juno-divider component-style="pageStyle"></juno-divider>

					<care-team-section ng-model="$ctrl.page.demo"
					                   validations="$ctrl.validations"
					                   component-style="pageStyle">
					</care-team-section>
					<juno-divider component-style="pageStyle"></juno-divider>

					<roster-display-section ng-if="$ctrl.rosteringModuleEnabled"
					                        ng-model="$ctrl.page.demo"
					                        validations="$ctrl.validations"
					                        component-style="pageStyle">
					</roster-display-section>
					<juno-divider ng-if="$ctrl.rosteringModuleEnabled" component-style="pageStyle">
					</juno-divider>
					<additional-information-section ng-model="$ctrl.page.demo"
													validations="$ctrl.validations"
													component-style="pageStyle">
					</additional-information-section>
					<juno-divider component-style="pageStyle"></juno-divider>
				</div>
			</div>
		<br/>
			<div class="flex-item-grow flex-col flex-wrap">
				<div class="clearfix col-md-12">
					<img class="pull-left" id="photo"
						 title="Click to upload photo"
						 ng-click="$ctrl.launchPhoto()"
						 ng-src="../imageRenderingServlet?source=local_client&clientId={{$ctrl.page.demo.id}}"
					/>
				</div>
				<div id="pd2" ng-click="$ctrl.checkAction($event)" ng-keypress="$ctrl.checkAction($event)">
					<div class="col-md-12">
						<h3 class="form-heading"><bean:message key="demographic.demographiceditdemographic.formAlert"/></h3>
						<textarea id="alert-text-box" ng-model="$ctrl.page.demo.patientAlert" class="form-control form-control-details"></textarea>
					</div>
					<br/>
					<div class="col-md-12">
						<h3 class="form-heading"><bean:message key="demographic.demographiceditdemographic.formNotes"/></h3>
						<textarea id="notes-text-box" ng-model="$ctrl.page.demo.patientNote" class="form-control form-control-details"></textarea>
					</div>
				</div>
				<hr>
				<div class="col-md-12">
					<h3 class="form-heading">
						<bean:message key="web.record.details.proContacts"/>
							<button type="button" class="btn btn-primary pull-right manage-button-padding"  ng-click="$ctrl.manageContacts()">
								<bean:message key="web.record.details.manage"/>
							</button>
					</h3>
					<div class="form-group" ng-repeat="dc in $ctrl.page.demoContactPros">
						<div class="col-md-12 special-indent" style="font-weight:bold">{{dc.role}}</div>
						<div class="col-md-7 special-indent-extra" style="white-space:nowrap">{{dc.lastName}}, {{dc.firstName}}</div>
						<div class="col-md-7 special-indent-extra2">Work Phone: {{dc.workPhone}}</div>
					</div>
				</div>

				<div class="col-md-12">
					<demographic-contacts-section
							ng-model="$ctrl.page.demoContacts"
							component-style="pageStyle">
					</demographic-contacts-section>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-lg-8 col-md-10">
				<div class="alert alert-success" ng-show="$ctrl.page.saving">
					<bean:message key="web.record.details.saving"/>
				</div>

				<button id="save-button" type="button"
					class="btn btn-success"
					ng-click="$ctrl.validateHCSave(true)"
					ng-disabled="!$ctrl.canEdit() || $ctrl.page.dataChanged === false">
					Save
				</button>
			</div>
		</div>
	</div>
</juno-security-check>