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

<div class="col-lg-12" ng-show="detailsCtrl.page.canRead === false">
	<bean:message key="demographic.demographiceditdemographic.accessDenied"/>
</div>
<div class="col-xs-12" ng-show="detailsCtrl.page.canRead === true" id="patient-details-page">
	<div class="row">
			<div class="col-md-10 col-lg-8">
				<div class="row">
					<div class="col-md-7 pull-left">
						<div class="btn-group sub-nav">
							<div class="btn-group">
								<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
									<bean:message key="demographic.demographicprintdemographic.btnPrint"/> <span class="caret"></span>
								</button>
								<ul class="dropdown-menu">
									<li><a href="../report/GenerateEnvelopes.do?demos={{detailsCtrl.page.demo.demographicNo}}">PDF Envelope</a></li>
									<li><a class="hand-hover" ng-click="detailsCtrl.printLabel('PDFLabel')"><bean:message key="demographic.demographiceditdemographic.btnCreatePDFLabel"/></a></li>
									<li><a class="hand-hover" ng-click="detailsCtrl.printLabel('PDFAddress')"><bean:message key="demographic.demographiceditdemographic.btnCreatePDFAddressLabel"/></a></li>
									<li><a class="hand-hover" ng-click="detailsCtrl.printLabel('PDFChart')"><bean:message key="demographic.demographiceditdemographic.btnCreatePDFChartLabel"/></a></li>
									<li><a class="hand-hover" ng-click="detailsCtrl.printLabel('PrintLabel')"><bean:message key="demographic.demographiceditdemographic.btnPrintLabel"/></a></li>
									<li><a class="hand-hover" ng-click="detailsCtrl.printLabel('ClientLab')"><bean:message key="demographic.demographiceditdemographic.btnClientLabLabel"/></a></li>
								</ul>
							</div>
							<div class="btn-group">
								<button type="button" class="btn btn-primary dropdown-toggle"
										data-toggle="dropdown"
										ng-show="detailsCtrl.page.integratorEnabled"
										style="color:{{detailsCtrl.page.integratorStatusColor}}"
										title="{{detailsCtrl.page.integratorStatusMsg}}">
									<bean:message key="web.record.details.integrator"/> <span class="caret"></span>
								</button>
								<ul class="dropdown-menu">
									<li ng-show="detailsCtrl.page.integratorOffline"><a style="color:#FF5500">{{detailsCtrl.page.integratorStatusMsg}}</a></li>
									<li ng-hide="detailsCtrl.page.integratorOffline" title="{{detailsCtrl.page.integratorStatusMsg}}">
										<a style="color:{{detailsCtrl.page.integratorStatusColor}}"
										   ng-click="detailsCtrl.integratorDo('ViewCommunity')">
											<bean:message key="web.record.details.viewIntegratedCommunity"/>
										</a>
									</li>
									<li><a ng-click="detailsCtrl.integratorDo('Linking')"><bean:message key="web.record.details.manageLinkedClients"/></a></li>
									<div ng-show="detailsCtrl.page.conformanceFeaturesEnabled && !detailsCtrl.page.integratorOffline">
										<li><a ng-click="detailsCtrl.integratorDo('Compare')"><bean:message key="web.record.details.compareWithIntegrator"/></a></li>
										<li><a ng-click="detailsCtrl.integratorDo('Update')"><bean:message key="web.record.details.updateFromIntegrator"/></a></li>
										<li><a ng-click="detailsCtrl.integratorDo('SendNote')"><bean:message key="web.record.details.sendNoteIntegrator"/></a></li>
									</div>
								</ul>
							</div>
							<div class="btn-group">
								<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
									<bean:message key="demographic.demographiceditdemographic.msgAppt"/> <span class="caret"></span>
								</button>
								<ul class="dropdown-menu">
									<li><a class="hand-hover" ng-click="detailsCtrl.appointmentDo('ApptHistory')"><bean:message key="demographic.demographiceditdemographic.btnApptHist"/></a></li>
									<li><a class="hand-hover" ng-click="detailsCtrl.appointmentDo('WaitingList')"><bean:message key="demographic.demographiceditdemographic.msgWaitList"/></a></li>
								</ul>
							</div>
							<div class="btn-group">
								<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
									<bean:message key="admin.admin.billing" /> <span class="caret"></span>
								</button>
								<ul class="dropdown-menu">
									<li><a class="hand-hover" ng-click="detailsCtrl.billingDo('BillingHistory')">{{detailsCtrl.page.billingHistoryLabel}}</a></li>
									<li><a class="hand-hover" ng-click="detailsCtrl.billingDo('CreateInvoice')"><bean:message key="demographic.demographiceditdemographic.msgCreateInvoice"/></a></li>
									<li ng-hide="detailsCtrl.isClinicaidBilling()"><a class="hand-hover" ng-click="detailsCtrl.billingDo('FluBilling')"><bean:message key="demographic.demographiceditdemographic.msgFluBilling"/></a></li>
									<li ng-hide="detailsCtrl.isClinicaidBilling()"><a class="hand-hover" ng-click="detailsCtrl.billingDo('HospitalBilling')"><bean:message key="demographic.demographiceditdemographic.msgHospitalBilling"/></a></li>
									<li ng-hide="detailsCtrl.isClinicaidBilling()"><a class="hand-hover" ng-click="detailsCtrl.billingDo('AddBatchBilling')"><bean:message key="demographic.demographiceditdemographic.msgAddBatchBilling"/></a></li>
									<li ng-hide="detailsCtrl.isClinicaidBilling()"><a class="hand-hover" ng-click="detailsCtrl.billingDo('AddINR')"><bean:message key="demographic.demographiceditdemographic.msgAddINR"/></a></li>
									<li ng-hide="detailsCtrl.isClinicaidBilling()"><a class="hand-hover" ng-click="detailsCtrl.billingDo('BillINR')"><bean:message key="demographic.demographiceditdemographic.msgINRBill"/></a></li>
								</ul>
							</div>
							<div class="btn-group" ng-show="detailsCtrl.page.macPHRIdsSet">
								<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
									<bean:message key="global.personalHealthRecord"/> <span class="caret"></span>
								</button>
								<ul class="dropdown-menu">
									<li><a class="hand-hover" ng-click="detailsCtrl.macPHRDo('SendMessage')"><bean:message key="demographic.demographiceditdemographic.msgSendMsgPHR"/></a></li>
									<li><a class="hand-hover" ng-click="detailsCtrl.macPHRDo('ViewRecord')"><bean:message key="web.record.details.viewPhrRecord"/></a></li>
								</ul>
							</div>
							<div class="btn-group">
								<button type="button" class="btn btn-default" ng-click="detailsCtrl.exportDemographic()"><bean:message key="export"/></button>
							</div>

						</div>
						<button class="btn btn-primary" ng-if="detailsCtrl.showEligibility" uib-popover-html="detailsCtrl.eligibilityMsg" popover-placement="bottom" popover-trigger="'outsideClick'" ng-click="detailsCtrl.checkEligibility()">
							Check Eligibility
						</button>
						<div class="btn-group">
							<button type="button" class="btn btn-success"
							        ng-click="detailsCtrl.openSwipecardModal()">
								Swipe Health Card</button>
						</div>
					</div>
					<div class=" col-sm-2 col-xs-12 pull-right">
						<button id="save-button-top"
								type="button"
								class="btn btn-success"
								ng-click="detailsCtrl.validateHCSave(true)"
								ng-disabled="detailsCtrl.page.dataChanged === false">
							Save
						</button>
					</div>
				</div>
			</div>
	</div>
	<div class="row">
		<div class="col-md-10 col-lg-8 ">
			<div id="pd1" ng-click="detailsCtrl.checkAction($event)" ng-keypress="detailsCtrl.checkAction($event)">
				
			<div class="form-horizontal">
				<div class="row">
					<div class="col-md-12">
						<h3 class="form-heading"><bean:message key="demographic.demographiceditdemographic.msgDemographic"/></h3>
					</div>
				</div>
				
				<div class="form-group">
					<label class="col-md-2 control-label required" title="Required Field">
						<bean:message key="demographic.demographiceditdemographic.formLastName"/> 
					</label>
					<div class="col-md-4"
					     ng-class="{'has-warning': detailsCtrl.displayMessages.field_warnings()['lastName']}">
						<input type="text" class="form-control form-control-details" 
							placeholder="Family Name" title="Family Name" 
							ng-model="detailsCtrl.page.demo.lastName" 
							ng-change="detailsCtrl.formatLastName()"
						/>
					</div>
					<label class="col-md-2 control-label required" title="Required Field">
						<bean:message key="demographic.demographiceditdemographic.formFirstName"/> 
					</label>
					<div class="col-md-4"
					     ng-class="{'has-warning': detailsCtrl.displayMessages.field_warnings()['firstName']}">
						<input type="text" class="form-control form-control-details" 
							placeholder="First Name" title="First Name" 
							ng-model="detailsCtrl.page.demo.firstName" 
							ng-change="detailsCtrl.formatFirstName()"
						/>
					</div>
				</div>

				<div class="form-group">
					<label class="col-md-2 control-label required" title="Required Field">
						<bean:message key="web.record.details.dateOfBirth" /> 	
					</label>
					<div class="col-md-4"
					     ng-class="{'has-warning': detailsCtrl.displayMessages.field_warnings()['dob']}">
						<div class="input-group vertical-align">
							<input id="details-dob-year" type="text" placeholder="YYYY"
								   title="Birthday Year" class="form-control form-control-details"
								   ng-model="detailsCtrl.page.demo.dobYear"
								   ng-change="detailsCtrl.checkDate('DobY')"
								   ng-blur="detailsCtrl.formatDate('DobY')"
							/>
							<input id="details-dob-month" type="text" placeholder="MM"
								title="Birthday Month" class="form-control form-control-details"
								ng-model="detailsCtrl.page.demo.dobMonth"
								ng-change="detailsCtrl.checkDate('DobM')"
								ng-blur="detailsCtrl.formatDate('DobM')"
							    <%--ng-class="{'has-warning': detailsCtrl.displayMessages.field_warnings()['dob']}"--%>
							/>
							<input id="details-dob-day" type="text" placeholder="DD"
								title="Birthday Day"
								class="form-control form-control-details"
								ng-model="detailsCtrl.page.demo.dobDay"
								ng-change="detailsCtrl.checkDate('DobD')"
								ng-blur="detailsCtrl.formatDate('DobD')"
							    <%--ng-class="{'has-warning': detailsCtrl.displayMessages.field_warnings()['dob']}"--%>
							/>
							<div>&nbsp;
								(<span ng-if="!detailsCtrl.isNaN(detailsCtrl.page.demo.age)">{{detailsCtrl.page.demo.age}}y</span>)
							</div>
						</div>
					</div>
					
					
					<label class="col-md-2 control-label required" title="Required Field">
						<bean:message key="demographic.demographiceditdemographic.formSex"/> 
					</label>
					<div class="col-md-4"
					     ng-class="{'has-warning': detailsCtrl.displayMessages.field_warnings()['sex']}"
					>
						<select class="form-control form-control-details" title="Sex"
								ng-model="detailsCtrl.page.demo.sex"
								ng-options="sexes.value as sexes.label for sexes in detailsCtrl.page.genders"
						></select>
					</div>
				</div>
				
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.msgDemoTitle"/></label>
					<div class="col-md-4">
						<select class="form-control form-control-details" title="Title" 
								ng-model="detailsCtrl.page.demo.title" 
								ng-options="tt.value as tt.label for tt in detailsCtrl.page.titles">
							<option value="">--</option>
						</select>
					</div>
					<label class="col-md-2 control-label"><bean:message key="web.record.details.sin"/></label>
					<div class="col-md-4">
						<input type="text" class="form-control form-control-details" 
							placeholder="<bean:message key="web.record.details.sin"/>" 
							title="<bean:message key="web.record.details.sin"/>" 
							ng-model="detailsCtrl.page.demo.sin" 
							ng-change="detailsCtrl.checkSin()" 
							ng-blur="detailsCtrl.validateSin()"
						/>
					</div>
				</div>
			
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.msgDemoLanguage"/></label>
					<div class="col-md-4">
						<select class="form-control form-control-details" title="Language" 
								ng-model="detailsCtrl.page.demo.officialLanguage" 
								ng-options="ef.value as ef.label for ef in detailsCtrl.page.engFre">
							<option value="">--</option>
						</select>
					</div>
					<label class="col-md-2 control-label">Spoken</label>
					<div class="col-md-4">
						<select class="form-control form-control-details" title="Spoken Language" 
								ng-model="detailsCtrl.page.demo.spokenLanguage" 
								ng-options="sl.value as sl.label for sl in detailsCtrl.page.spokenlangs">
							<option value="">--</option>
						</select>
					</div>
				</div>

				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.aboriginal"/></label>
					<div class="col-md-4">
						<select class="form-control form-control-details" title="Aboriginal" ng-model="detailsCtrl.page.demo.scrAboriginal">
							<option value="">--</option>
							<option value="Yes">Yes</option>
							<option value="No">No</option>
						</select>
					</div>

					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.alias"/></label>
					<div class="col-md-4">
						<input type="text" class="form-control form-control-details" 
							placeholder="<bean:message key="demographic.demographiceditdemographic.alias"/>" 
							title="<bean:message key="demographic.demographiceditdemographic.formPHRUserName"/>" 
							ng-model="detailsCtrl.page.demo.alias"
						/>
					</div>
				</div>
			
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formPHRUserName"/></label>
					<div class="col-md-4">
						<div class="input-group">
							<input type="text" class="form-control form-control-details" 
								placeholder="<bean:message key="demographic.demographiceditdemographic.formPHRUserName"/>" 
								title="<bean:message key="demographic.demographiceditdemographic.formPHRUserName"/>" 
								ng-model="detailsCtrl.page.demo.myOscarUserName"
							/>
							<span class="input-group-btn">
								<button type="button" class="btn btn-primary" ng-click="detailsCtrl.macPHRDo('Register')" 
										ng-show="detailsCtrl.page.demo.myOscarUserName==null || detailsCtrl.page.demo.myOscarUserName==''">
									<span class="glyphicon glyphicon-pencil"></span><%--<bean:message key="demographic.demographiceditdemographic.msgRegisterPHR"/>--%>
								</button>
							</span>
						</div>
					</div>
				</div>
				
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.msgCountryOfOrigin"/></label>
					<div class="col-md-10">
						<select class="form-control form-control-details" title="<bean:message key="demographic.demographiceditdemographic.msgCountryOfOrigin"/>" 
								ng-model="detailsCtrl.page.demo.countryOfOrigin" 
								ng-options="cnty.value as cnty.label for cnty in detailsCtrl.page.countries">
							<option value="">--</option>
						</select>
					</div>
				</div>
			</div>

			<hr class="form-divider">
					
			<div class="form-horizontal">
				<div class="row">
					<div class="col-md-12">
						<h3 class="form-heading"><bean:message key="demographic.demographiceditdemographic.msgContactInfo"/></h3>
					</div>
				</div>

				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formAddr"/></label>
					<div class="col-md-4"
					     ng-class="{'has-warning': detailsCtrl.displayMessages.field_warnings()['address']}"
					>
						<input type="text" placeholder="<bean:message key="demographic.demographiceditdemographic.formAddr"/>" 
							title="<bean:message key="demographic.demographiceditdemographic.formAddr"/>" 
							class="form-control form-control-details" 
							ng-model="detailsCtrl.page.demo.address.address"
						/>
					</div>

					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formCity"/></label>
					<div class="col-md-4"
					     ng-class="{'has-warning': detailsCtrl.displayMessages.field_warnings()['city']}"
					>
						<input type="text" class="form-control form-control-details" 
							placeholder="<bean:message key="demographic.demographiceditdemographic.formCity"/>" 
							title="<bean:message key="demographic.demographiceditdemographic.formCity"/>" 
							ng-model="detailsCtrl.page.demo.address.city"
						/>
					</div>
				</div>

				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formProcvince"/></label>
					<div class="col-md-4"
					     ng-class="{'has-warning': detailsCtrl.displayMessages.field_warnings()['province']}"
					>
						<select class="form-control form-control-details" 
								title="<bean:message key="demographic.demographiceditdemographic.formProcvince"/>" 
								ng-model="detailsCtrl.page.demo.address.province" 
								ng-options="pv.value as pv.label for pv in detailsCtrl.page.provinces"
						>
							<option value="">--</option>
						</select>
					</div>
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formPostal"/></label>
					<div class="col-md-4"
					     ng-class="{'has-warning': detailsCtrl.displayMessages.field_warnings()['postal']}"
					>
						<input type="text" class="form-control form-control-details" 
							placeholder="<bean:message key="demographic.demographiceditdemographic.formPostal"/>" 
							title="<bean:message key="demographic.demographiceditdemographic.formPostal"/>" 
							ng-model="detailsCtrl.page.demo.address.postal"
						/>
					</div>
				</div>

				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formEmail"/></label>
					<div class="col-md-4">
						<input type="text" class="form-control form-control-details" 
							placeholder="<bean:message key="demographic.demographiceditdemographic.formEmail"/>" 
							title="<bean:message key="demographic.demographiceditdemographic.formEmail"/>" 
							ng-model="detailsCtrl.page.demo.email" 
							ng-blur="detailsCtrl.checkEmail()"
						/>
					</div>

					<label class="col-md-2 control-label" title="{{detailsCtrl.page.cellPhonePreferredMsg}}">
						<bean:message key="demographic.demographiceditdemographic.formPhoneC"/>
						<input type="checkbox" ng-model="detailsCtrl.page.demo.scrPreferredPhone" ng-change="detailsCtrl.setPreferredPhone()" ng-true-value="'C'" ng-disabled="isPhoneVoid(page.demo.scrCellPhone)"/>
					</label>
					<div class="col-md-4">
						<input type="text" class="form-control form-control-details" 
							placeholder="<bean:message key="demographic.demographiceditdemographic.formPhoneC"/>" 
							title="<bean:message key="demographic.demographiceditdemographic.formPhoneC"/>" 
							ng-model="detailsCtrl.page.demo.scrCellPhone" 
							ng-change="detailsCtrl.checkPhone('C')"
						/>
					</div>
				</div>

				<div class="form-group">
					<label class="col-md-2 control-label" title="{{detailsCtrl.page.homePhonePreferredMsg}}">
							<bean:message key="demographic.demographiceditdemographic.formPhoneH"/>
							<input type="checkbox" ng-model="detailsCtrl.page.demo.scrPreferredPhone" 
								ng-change="detailsCtrl.setPreferredPhone()" 
								ng-true-value="'H'" 
								ng-disabled="detailsCtrl.isPhoneVoid(detailsCtrl.page.demo.scrHomePhone)"
							/>
					</label>
					<div class="col-md-4">
						<div class="input-group">
							<input type="text" class="form-control form-control-details phone-num" 
								placeholder="<bean:message key="demographic.demographiceditdemographic.formPhoneH"/>" 
								title="<bean:message key="demographic.demographiceditdemographic.formPhoneH"/>" 
								ng-model="detailsCtrl.page.demo.scrHomePhone" 
								ng-change="detailsCtrl.checkPhone('H')"
							/>
							<input type="text" class="form-control form-control-details phone-ext" 
								placeholder="<bean:message key="demographic.demographiceditdemographic.msgExt"/>" 
								title="Home Phone Extension" 
								ng-model="detailsCtrl.page.demo.scrHPhoneExt" 
								ng-change="detailsCtrl.checkPhone('HX')"
							/>
						</div>
					</div>

					<label class="col-md-2 control-label" title="{{detailsCtrl.page.workPhonePreferredMsg}}">
							<bean:message key="demographic.demographiceditdemographic.formPhoneW"/>
							<input type="checkbox" ng-model="detailsCtrl.page.demo.scrPreferredPhone" 
								ng-change="detailsCtrl.setPreferredPhone()" 
								ng-true-value="'W'" 
								ng-disabled="detailsCtrl.isPhoneVoid(page.demo.scrWorkPhone)"
							/>
						</label>
					<div class="col-md-4">
						<div class="input-group">
							<input type="text" class="form-control form-control-details phone-num" 
								placeholder="<bean:message key="demographic.demographiceditdemographic.formPhoneW"/>" 
								title="<bean:message key="demographic.demographiceditdemographic.formPhoneW"/>" 
								ng-model="detailsCtrl.page.demo.scrWorkPhone" 
								ng-change="detailsCtrl.checkPhone('W')"
							/>
							<input type="text" class="form-control form-control-details phone-ext" 
								placeholder="<bean:message key="demographic.demographiceditdemographic.msgExt"/>" 
								title="Work Phone Extension" 
								ng-model="detailsCtrl.page.demo.scrWPhoneExt" 
								ng-change="detailsCtrl.checkPhone('WX')"
							/>
						</div>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographicaddrecordhtm.formPhoneComment"/></label>
					<div class="col-md-10">
						<input type="text" class="form-control form-control-details" 
							placeholder="<bean:message key="demographic.demographicaddrecordhtm.formPhoneComment"/>" 
							title="<bean:message key="demographic.demographicaddrecordhtm.formPhoneComment"/>" 
							ng-model="detailsCtrl.page.demo.scrPhoneComment"
						/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formNewsLetter"/></label>
					<div class="col-md-4">	
						<select class="form-control form-control-details" title="<bean:message key="demographic.demographiceditdemographic.formNewsLetter"/>" 
								ng-model="detailsCtrl.page.demo.newsletter">
							<option value="">--</option>
							<option value="<bean:message key="demographic.demographicaddrecordhtm.formNewsLetter.optNo"/>"><bean:message key="demographic.demographicaddrecordhtm.formNewsLetter.optNo"/></option>
							<option value="<bean:message key="demographic.demographicaddrecordhtm.formNewsLetter.optPaper"/>"><bean:message key="demographic.demographicaddrecordhtm.formNewsLetter.optPaper"/></option>
							<option value="<bean:message key="demographic.demographicaddrecordhtm.formNewsLetter.optElectronic"/>"><bean:message key="demographic.demographicaddrecordhtm.formNewsLetter.optElectronic"/></option>
						</select>
					</div>
				</div>
			</div>

			<hr class="form-divider">

			<div class="form-horizontal">
				<div class="row">
					<div class="col-md-12">
						<h3 class="form-heading"><bean:message key="demographic.demographiceditdemographic.msgHealthIns"/></h3>
					</div>
				</div>
				<div class="alert-warning" ng-show="detailsCtrl.page.HCValidation=='n/a'">
					Online Health Card Validation unavailable
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label">
						HIN
						<span ng-show="detailsCtrl.page.HCValidation=='valid'" title="HIN Valid" style="font-size:large; color:#009900">&#10004;</span>
						<span ng-show="detailsCtrl.page.HCValidation=='invalid'" title="HIN Invalid" style="font-size:large; color:red">&#10008;</span>
						<span ng-show="detailsCtrl.page.HCValidation=='n/a'" title="Online Health Card Validation unavailable" style="font-size:large; color:#ff5500">?</span>
						<button class="btn" title="Validate HIN #" 
							ng-click="detailsCtrl.validateHC()" 
							ng-hide="detailsCtrl.page.demo.hin==null || detailsCtrl.page.demo.hin=='' || detailsCtrl.page.demo.hcType!='ON'" 
							style="padding: 0px 5px; font-size: small">
							Validate
						</button>
					</label>
					<div class="col-md-4">
						<div class="input-group">
							<div ng-class="{'has-warning': detailsCtrl.displayMessages.field_warnings()['hin']}"
							>
								<input type="text" class="form-control form-control-details"
								       placeholder="<bean:message key="demographic.demographiceditdemographic.msgHealthIns"/>"
								       title="<bean:message key="demographic.demographiceditdemographic.msgHealthIns"/>"
								       ng-model="detailsCtrl.page.demo.hin"
								       ng-change="detailsCtrl.checkHin()"
								       style="width:70%"
								/>
							</div>
							<div ng-class="{'has-warning': detailsCtrl.displayMessages.field_warnings()['ver']}">
								<input type="text" class="form-control form-control-details"
							            placeholder="<bean:message key="demographic.demographiceditdemographic.formVer"/>"
							            title="HIN Version"
							            ng-model="detailsCtrl.page.demo.ver"
							            ng-change="detailsCtrl.checkHinVer()"
							            style="width:30%;"
								/>
							</div>
						</div>
					</div>

					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formHCType"/></label>
					<div class="col-md-4"
					     ng-class="{'has-warning': detailsCtrl.displayMessages.field_warnings()['hcType']}"
					>
						<select class="form-control form-control-details" title="Health Card Type" 
								ng-model="detailsCtrl.page.demo.hcType" 
								ng-options="hct.value as hct.label for hct in detailsCtrl.page.provinces"
							>
							<option value="" >--</option>
						</select>
					</div>
				</div>
				<div class="row">
					<div class="col-md-6">
						<ca-field-date
								ca-title="<bean:message key="demographic.demographiceditdemographic.formEFFDate"/>"
								ca-label-size="col-md-4 control-label"
								ca-input-size="col-md-8"
								ca-date-picker-id="effDatePicker"
								ca-name="effDatePicker"
								ca-model="detailsCtrl.page.demo.effDate"
								ca-warning="{{detailsCtrl.displayMessages.field_warnings()['effDate']}}"
								ca-orientation="auto"
						></ca-field-date>
					</div>
					<div class="col-md-6">
						<ca-field-date
								ca-title="<bean:message key="demographic.demographiceditdemographic.formHCRenewDate"/>"
								ca-label-size="col-md-4 control-label"
								ca-input-size="col-md-8"
								ca-date-picker-id="hcRenewDatePicker"
								ca-name="hcRenewDatePicker"
								ca-model="detailsCtrl.page.demo.hcRenewDate"
								ca-warning="{{detailsCtrl.displayMessages.field_warnings()['endDate']}}"
								ca-orientation="auto"
						></ca-field-date>
					</div>
				</div>
			</div>

			<hr class="form-divider">
			
			<div class="form-horizontal">
				<div class="row">
					<div class="col-md-12">
						<h3 class="form-heading"><bean:message key="web.record.details.careTeam"/></h3>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="web.record.details.mrp"/></label>
					<div class="col-md-4">
						<select class="form-control form-control-details" title="MRP" 
								ng-model="detailsCtrl.page.demo.providerNo" 
								ng-options="mrp.providerNo as mrp.name for mrp in detailsCtrl.page.doctors">
							<option value="">--</option>
						</select>
					</div>
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formNurse"/></label>
					<div class="col-md-4">
						<select class="form-control form-control-details" title="Nurse" 
								ng-model="detailsCtrl.page.demo.nurse"
								ng-options="ns.providerNo as ns.name for ns in detailsCtrl.page.nurses">
							<option value="">--</option>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="web.record.details.midwife"/></label>
					<div class="col-md-4">
						<select class="form-control form-control-details" title="Midwife" 
								ng-model="detailsCtrl.page.demo.midwife" 
								ng-options="mw.providerNo as mw.name for mw in detailsCtrl.page.midwives">
							<option value="">--</option>
						</select>
					</div>
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formResident"/></label>
					<div class="col-md-4">
						<select class="form-control form-control-details" title="Resident" 
								ng-model="detailsCtrl.page.demo.resident" 
								ng-options="res.providerNo as res.name for res in detailsCtrl.page.doctors">
							<option value="">--</option>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formRefDoc"/></label>
					<div class="col-md-4">
						<input type="text" class="form-control form-control-details"
						       placeholder="<bean:message key="demographic.demographiceditdemographic.formRefDoc"/>"
						       title="<bean:message key="demographic.demographiceditdemographic.formRefDoc"/>"

						       uib-typeahead="i.label for i in detailsCtrl.searchReferralDocsName($viewValue)"
						       typeahead-on-select="detailsCtrl.chooseReferralDoc($item, $model, $label);"
						       ng-model="detailsCtrl.page.demo.scrReferralDoc"
						       typeahead-min-length="3"
							   ng-change="detailsCtrl.checkReferralDocNo()"
						/>
					</div>

					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formRefDocNo"/></label>
					<div class="col-md-4">
						<input type="text" class="form-control form-control-details"
						       placeholder="<bean:message key="demographic.demographiceditdemographic.formRefDocNo"/>"
						       title="<bean:message key="demographic.demographiceditdemographic.formRefDocNo"/>"

						       uib-typeahead="i.label as i.label for i in detailsCtrl.searchReferralDocsRefNo($viewValue);"
						       typeahead-on-select="detailsCtrl.chooseReferralDoc($item, $model, $label);"
						       ng-model="detailsCtrl.page.demo.scrReferralDocNo"
						       typeahead-min-length="3"
							   ng-change="detailsCtrl.checkReferralDocNo()"
						/>
					</div>
				</div>

				<div class="form-group" ng-show="detailsCtrl.properties.demographic_family_doctor">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.familyDoctor"/></label>
					<div class="col-md-4">
						<input type="text" class="form-control form-control-details"
							   placeholder="<bean:message key="demographic.demographiceditdemographic.familyDoctor"/>"
							   title="<bean:message key="demographic.demographiceditdemographic.familyDoctor"/>"

							   uib-typeahead="i.label for i in detailsCtrl.searchReferralDocsName($viewValue)"
							   typeahead-on-select="detailsCtrl.chooseFamilyDoc($item, $model, $label);"
							   ng-model="detailsCtrl.page.demo.scrFamilyDoc"
							   typeahead-min-length="3"
							   ng-change="detailsCtrl.checkFamilyDocNo()"
						/>
					</div>

					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.familyDoctorNo"/></label>
					<div class="col-md-4">
						<input type="text" class="form-control form-control-details"
							   placeholder="<bean:message key="demographic.demographiceditdemographic.familyDoctorNo"/>"
							   title="<bean:message key="demographic.demographiceditdemographic.familyDoctorNo"/>"

							   uib-typeahead="i.label as i.label for i in detailsCtrl.searchReferralDocsRefNo($viewValue);"
							   typeahead-on-select="detailsCtrl.chooseFamilyDoc($item, $model, $label);"
							   ng-model="detailsCtrl.page.demo.scrFamilyDocNo"
							   typeahead-min-length="3"
							   ng-change="detailsCtrl.checkFamilyDocNo()"
						/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formRosterStatus"/></label>
					<div class="col-md-4">
						<div class="input-group">
							<select class="form-control form-control-details" title="<bean:message key="demographic.demographiceditdemographic.formRosterStatus"/>" 
									ng-model="detailsCtrl.page.demo.rosterStatus" 
									ng-options="rs.value as rs.label for rs in detailsCtrl.page.rosterStatusList" >
								<option value="">--</option>
							</select>
							<span class="input-group-btn">
								<button type="button" class="btn btn-success" 
									title="Add new roster status" 
									ng-click="detailsCtrl.showAddNewRosterStatus()">
									<bean:message key="global.btnAdd"/>
								</button>
							</span>
						</div>
						<div class="add-status-menu" ng-show="detailsCtrl.page.showAddNewRosterStatus">
							<input type="text" class="form-control" placeholder="New Roster Status" ng-model="detailsCtrl.page.newRosterStatus"/>
							<button type="button" class="btn btn-sm btn-success" 
								ng-click="detailsCtrl.addNewRosterStatus()">
								<bean:message key="web.record.details.addStatus"/>
							</button>
							<button type="button" class="btn btn-sm btn-danger" 
								ng-click="detailsCtrl.showAddNewRosterStatus()">
								<bean:message key="global.btnCancel"/>
							</button>
						</div>
					</div>
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.DateJoined"/></label>
					<div class="col-md-4">
						<%--<input id="rosterDate" ng-model="detailsCtrl.page.demo.rosterDate" --%>
							<%--type="date"--%>
							<%--class="form-control form-control-details" --%>
							<%--title="Roster Date" --%>
							<%--datepicker-popup="yyyy-MM-dd" --%>
							<%--datepicker-append-to-body="true" --%>
							<%--is-open="detailsCtrl.page.rosterDatePicker" --%>
							<%--ng-click="detailsCtrl.page.rosterDatePicker = true"--%>
							<%--placeholder="YYYY-MM-DD" --%>
							<%--ng-change="detailsCtrl.preventManualRosterDate()"--%>
						<%--/>--%>
						<juno-datepicker-popup  juno-model="detailsCtrl.page.demo.rosterDate" show-icon="true" type="Input"> </juno-datepicker-popup>
					</div>
				</div>
				<div class="form-group">
					<div class="col-md-10 col-md-offset-2">
						<button type="button" class="btn btn-primary" ng-click="detailsCtrl.showEnrollmentHistory()">
							<bean:message key="demographic.demographiceditdemographic.msgEnrollmentHistory"/>
						</button>
					</div>
				</div>
				<div class="form-group" ng-show="detailsCtrl.isRosterTerminated()">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.RosterTerminationDate"/></label>
					<div class="col-md-4">
						<juno-datepicker-popup juno-model="detailsCtrl.page.demo.rosterTerminationDate" show-icon="true" type="Input"></juno-datepicker-popup>
					</div>

					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.RosterTerminationReason"/></label>
					<div class="col-md-4">
						<select class="form-control form-control-details" title="<bean:message key="web.record.details.rosterTerminationReason"/>"
								ng-model="detailsCtrl.page.demo.rosterTerminationReason"
								ng-options="rtr.value as rtr.label for rtr in detailsCtrl.page.rosterTermReasons">
							<option value="">--</option>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formPatientStatus"/></label>
					<div class="col-md-4">
						<div class="input-group">
							<select class="form-control form-control-details" title="Patient Status" 
									ng-model="detailsCtrl.page.demo.patientStatus" 
									ng-options="ps.value as ps.label for ps in detailsCtrl.page.patientStatusList"
									ng-blur="detailsCtrl.checkPatientStatus()">
								<option value="">--</option>
							</select>
							<span class="input-group-btn">
								<button type="button" class="btn btn-success" title="Add new patient status" ng-click="detailsCtrl.showAddNewPatientStatus()">Add</button>
							</span>
						</div>
						<div class="add-status-menu" ng-show="detailsCtrl.page.showAddNewPatientStatus">
							<input type="text" class="form-control" placeholder="New Patient Status" ng-model="detailsCtrl.page.newPatientStatus"/>
							<button type="button" class="btn btn-sm btn-success" ng-click="detailsCtrl.addNewPatientStatus()">Save</button>
							<button type="button" class="btn btn-sm btn-danger" ng-click="detailsCtrl.showAddNewPatientStatus()">Cancel</button>
						</div>
					</div>

					<label class="col-md-2 control-label" ><bean:message key="demographic.demographiceditdemographic.PatientStatusDate"/></label>
					<div class="col-md-4">
						<juno-datepicker-popup  juno-model="detailsCtrl.page.demo.patientStatusDate" show-icon="true" type="Input"> </juno-datepicker-popup>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formDateJoined1"/></label>
					<div class="col-md-4">
						<juno-datepicker-popup  juno-model="detailsCtrl.page.demo.dateJoined" show-icon="true" type="Input"> </juno-datepicker-popup>
					</div>

					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formEndDate"/></label>
					<div class="col-md-4">
						<juno-datepicker-popup  juno-model="detailsCtrl.page.demo.endDate" show-icon="true" type="Input"> </juno-datepicker-popup>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.formChartNo"/></label>
					<div class="col-md-4">
						<input type="text" class="form-control form-control-details" 
							placeholder="Chart Number" 
							title="Chart Number"
							ng-model="detailsCtrl.page.demo.chartNo" 
							ng-change="detailsCtrl.checkChartNo()"
						/>
					</div>

					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.cytolNum"/></label>
					<div class="col-md-4">
						<input type="text" class="form-control form-control-details" 
							placeholder="<bean:message key="demographic.demographiceditdemographic.cytolNum"/>" 
							title="<bean:message key="demographic.demographiceditdemographic.cytolNum"/>" 
							ng-model="detailsCtrl.page.demo.scrCytolNum" 
							ng-change="detailsCtrl.checkCytoNum()"
						/>
					</div>
				</div>
			</div>

			<hr class="form-divider">
				
			<div class="form-horizontal">
				<div class="row">
					<div class="col-md-12">
						<h3 class="form-heading"><bean:message key="web.record.details.addInformation"/></h3>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="web.record.details.archivedPaperChart"/></label>
					<div class="col-md-4">
						<select class="form-control form-control-details" title="Archived Paper Chart" ng-model="detailsCtrl.page.demo.scrPaperChartArchived">
							<option value="">--</option>
							<option value="NO"><bean:message key="demographic.demographiceditdemographic.paperChartIndicator.no"/></option>
							<option value="YES"><bean:message key="demographic.demographiceditdemographic.paperChartIndicator.yes"/></option>
						</select>
					</div>
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.paperChartIndicator.dateArchived"/></label>
					<div class="col-md-4">
						<%--<input id="paperChartArchivedDate"--%>
							   <%--ng-model="detailsCtrl.page.demo.scrPaperChartArchivedDate"--%>
							<%--type="date"--%>
							<%--class="form-control form-control-details" --%>
							<%--datepicker-popup="yyyy-MM-dd" --%>
							<%--datepicker-append-to-body="true" --%>
							<%--is-open="detailsCtrl.page.paperChartArchivedDatePicker" --%>
							<%--ng-click="detailsCtrl.page.paperChartArchivedDatePicker=true" --%>
							<%--title="YYYY-MM-DD" --%>
							<%--placeholder="<bean:message key="demographic.demographiceditdemographic.paperChartIndicator.dateArchived"/>" --%>
							<%--ng-change="detailsCtrl.preventManualPaperChartArchivedDate()"/>--%>
						<juno-datepicker-popup  juno-model="detailsCtrl.page.demo.extras['']" show-icon="true" type="Input"> </juno-datepicker-popup>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.msgWaitList"/></label>
					<div class="col-md-4">
						<select class="form-control form-control-details" title="Waiting List" 
								ng-model="detailsCtrl.page.demo.waitingListID" 
								ng-options="wln.id as wln.name for wln in detailsCtrl.page.demo.waitingListNames">
							<option value="">--</option>
						</select>
					</div>

					<label class="col-md-2 control-label"><bean:message key="demographic.demographicaddarecordhtm.msgDateOfReq"/></label>
					<div class="col-md-4">
						<%--<input id="onWaitingListSinceDate" --%>
							<%--ng-model="detailsCtrl.page.demo.onWaitingListSinceDate" --%>
							<%--type="date"--%>
							<%--class="form-control form-control-details" --%>
							<%--title="<bean:message key="demographic.demographicaddarecordhtm.msgDateOfReq"/>" --%>
							<%--datepicker-popup="yyyy-MM-dd" --%>
							<%--datepicker-append-to-body="true" --%>
							<%--is-open="detailsCtrl.page.onWaitingListSinceDatePicker" --%>
							<%--ng-click="detailsCtrl.page.onWaitingListSinceDatePicker = true" --%>
							<%--placeholder="YYYY-MM-DD" --%>
							<%--ng-change="detailsCtrl.preventManualOnWaitingListSinceDate()"--%>
						<%--/>--%>
						<juno-datepicker-popup  juno-model="detailsCtrl.page.demo.onWaitingListSinceDate" show-icon="true" type="Input"> </juno-datepicker-popup>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographicaddarecordhtm.msgWaitListNote"/></label>
					<div class="col-md-10">
						<input type="text" class="form-control form-control-details" 
							placeholder="<bean:message key="demographic.demographicaddarecordhtm.msgWaitListNote"/>" 
							title="<bean:message key="demographic.demographicaddarecordhtm.msgWaitListNote"/>" 
							ng-model="detailsCtrl.page.demo.waitingListNote"
						/>
					</div>
				</div>
				<div class="form-group"> 
					<label class="col-md-2 control-label"><bean:message key="web.record.details.privacyConsent"/></label>
					<div class="col-md-4">
						<input type="text" class="form-control form-control-details" 
							placeholder="<bean:message key="demographic.demographiceditdemographic.privacyConsent"/>" 
							title="<bean:message key="demographic.demographiceditdemographic.privacyConsent"/>" 
							ng-model="detailsCtrl.page.demo.scrPrivacyConsent"
						/>
					</div>
					<label class="col-md-2 control-label"><bean:message key="web.record.details.informedConsent"/></label>
					<div class="col-md-4">
						<input type="text" class="form-control form-control-details" 
							placeholder="<bean:message key="demographic.demographiceditdemographic.informedConsent"/>" 
							title="<bean:message key="demographic.demographiceditdemographic.informedConsent"/>" 
							ng-model="detailsCtrl.page.demo.scrInformedConsent"
						/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="demographic.demographiceditdemographic.usConsent"/></label>
					<div class="col-md-10">
						<input type="text" class="form-control form-control-details" 
							placeholder="<bean:message key="demographic.demographiceditdemographic.usConsent"/>" 
							title="<bean:message key="demographic.demographiceditdemographic.usConsent"/>" 
							ng-model="detailsCtrl.page.demo.scrUsSigned"
						/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="web.record.details.securityQuestion"/></label>
					<div class="col-md-10">
						<select class="form-control form-control-details" title="<bean:message key="web.record.details.selectSecurityQuestion"/>" 
								ng-model="detailsCtrl.page.demo.scrSecurityQuestion1" 
								ng-options="sq.value as sq.label for sq in detailsCtrl.page.securityQuestions">
							<option value="">--</option>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="web.record.details.answer"/></label>
					<div class="col-md-10">
						<input type="text" class="form-control form-control-details" 
							title="<bean:message key="web.record.details.answerToSecurityQuestion"/>" 
							placeholder="<bean:message key="web.record.details.answerToSecurityQuestion"/>" 
							ng-model="detailsCtrl.page.demo.scrSecurityAnswer1"
						/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label"><bean:message key="web.record.details.rxInteractionLevel"/></label>
					<div class="col-md-10">
						<select class="form-control form-control-details" 
							title="<bean:message key="web.record.details.rxInteractionLevel"/>" 
							ng-model="detailsCtrl.page.demo.scrRxInteractionLevel" 
							ng-options="r.value as r.name for r in detailsCtrl.page.rxInteractionLevels" 
						/>
					</div>
				</div>
			</div>
			</div>
		</div>
		<br/>

		<div class=" col-md-10 col-lg-4">
			<div class="clearfix col-md-12">
				<img class="pull-left" id="photo" 
					title="Click to upload photo" 
					ng-click="detailsCtrl.launchPhoto()" 
					ng-src="../imageRenderingServlet?source=local_client&clientId={{detailsCtrl.page.demo.demographicNo}}"
				/>
				<%--Remove this?--%>
				<%--<div class="pull-left" style="margin-left:5px;">
					<address>
						<strong>{{page.demo.lastName}}, {{page.demo.firstName}}</strong><br/>
						{{page.demo.address.address}}<br/>
						{{page.demo.address.city}}, {{page.demo.address.province}} {{page.demo.address.postal}}<br/>
						<abbr title="Phone" ng-show="page.preferredPhoneNumber"><bean:message key="web.record.details.phoneShortHand"/></abbr> {{page.preferredPhoneNumber}}
					</address>
					<a ng-click="macPHRDo('Verification')" ng-show="page.macPHRIdsSet"><bean:message key="global.phr"/>{{page.macPHRVerificationLevel}}</a>
				</div>--%>
			</div>
			<br/>
			<div>
				<div id="pd2" ng-click="detailsCtrl.checkAction($event)" ng-keypress="detailsCtrl.checkAction($event)">
					<div class="col-md-12">
						<h3 class="form-heading"><bean:message key="demographic.demographiceditdemographic.formAlert"/></h3>
						<textarea id="alert-text-box" ng-model="detailsCtrl.page.demo.alert" class="form-control form-control-details"></textarea>
					</div>
					<br/>
					<div class="col-md-12">
						<h3 class="form-heading"><bean:message key="demographic.demographiceditdemographic.formNotes"/></h3>
						<textarea id="notes-text-box" ng-model="detailsCtrl.page.demo.scrNotes" class="form-control form-control-details"></textarea>
					</div>
				</div>
				<hr>
				<div class="col-md-12">
					<h3 class="form-heading">
						<bean:message key="global.contacts"/>
						<button type="button" class="btn btn-primary btn-sm pull-right" ng-click="detailsCtrl.manageContacts()">
							<bean:message key="web.record.details.manage"/>
						</button>
					</h3>
					<div class="form-group" ng-repeat="dc in detailsCtrl.page.demoContacts">
						<div class="col-md-12" style="font-weight:bold">{{dc.role}}</div>
						<div class="col-md-7" style="white-space:nowrap">{{dc.lastName}}, {{dc.firstName}}</div>
						<div class="col-md-5">{{dc.phone}}</div>
					</div>
				</div>
				<hr>
				<div class="col-md-12">
					<h3 class="form-heading">
						<bean:message key="web.record.details.proContacts"/>
						<button type="button" class="btn btn-primary btn-sm pull-right" ng-click="detailsCtrl.manageContacts()">
							<bean:message key="web.record.details.manage"/>
						</button>
					</h3>
					<div class="form-group" ng-repeat="dc in detailsCtrl.page.demoContactPros">
						<div class="col-md-12" style="font-weight:bold">{{dc.role}}</div>
						<div class="col-md-7" style="white-space:nowrap">{{dc.lastName}}, {{dc.firstName}}</div>
						<div class="col-md-5">{{dc.phone}}</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-lg-8 col-md-10">
			<div class="alert alert-success" ng-show="detailsCtrl.page.saving">
				<bean:message key="web.record.details.saving"/>
			</div>
			
			<button id="save-button" type="button" 
				class="btn btn-success" 
				ng-click="detailsCtrl.validateHCSave(true)" 
				ng-disabled="detailsCtrl.page.dataChanged === false">
				Save
			</button>
		</div>
	</div>
</div>
