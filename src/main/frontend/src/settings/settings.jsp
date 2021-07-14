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
<div id="settings-page">
	<div class="settings-header">
		<div class="flex-row flex-grow align-items-center">
			<h3>User Settings</h3>
		</div>
		<div class="pull-right control-right">
			<juno-security-check permissions="settingsCtrl.SecurityPermissions.PREFERENCE_READ">
			<button type="button" class="btn btn-default btn-lg"
			        onClick="window.open('../provider/providerpreference.jsp?provider_no=999998','prefs','width=715,height=680,scrollbars=yes')">
				<span class="glyphicon glyphicon-cog"></span> Open Classic Preferences
			</button>
			</juno-security-check>
		</div>
	</div>

	<div class="row">
		<div class="col-xs-12">
			<nav class="nav nav-tabs settings-nav" role="navigation">
				<!-- Brand and toggle get grouped for better mobile display -->
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
					<a class="navbar-brand navbar-toggle pull-left" href="#">Select Module</a>
				</div>

				<!-- Collect the nav links, forms, and other content for toggling   removed data-toggle="tab"  from a ngclick changeTab3 -->
				<div class="collapse navbar-collapse navbar-ex1-collapse" style="padding-left: 0px;">
					<ul class="nav navbar-nav" id="myTabs">
						<li ng-repeat="tab in settingsCtrl.tabs" ng-model="settingsCtrl.currentTab" ng-class="{'active': settingsCtrl.isActive(tab)}">
							<a ng-click="settingsCtrl.changeTab(tab)" class="hand-hover">{{tab.displayName}}</a>
						</li>
					</ul>
				</div>
				<!-- /.navbar-collapse -->
			</nav>
		</div>
	</div>
	<div class="primary-content-window">

		<juno-security-check show-placeholder="true" permissions="settingsCtrl.SecurityPermissions.PREFERENCE_READ">

			<div class="ui-view-wrapper flex-grow">
				<ui-view></ui-view>
			</div>

			<form>

				<div class="row" ng-show="settingsCtrl.currentTab.path == 'persona'">
					<div class="col-sm-6">
						<div class="col-sm-12">
							<h3>Dashboard</h3>
							<hr>
						</div>
						<div class="form-group col-sm-12">
							Enable Juno UI on login:<br>
							<label class="radio-inline" for="radios-per-0">
								<input ng-model="settingsCtrl.pref.useCobaltOnLogin" name="radios-per-0" id="radios-per-0" ng-value="true" type="radio">
								Enable
							</label>
							<label class="radio-inline" for="radios-1">
								<input ng-model="settingsCtrl.pref.useCobaltOnLogin" name="radios-per-0" id="radios-per-1" ng-value="false" type="radio">
								Disable
							</label>
						</div>
						<div class="col-sm-12">
							<h3>Recent Patient List</h3>
							<hr>
						</div>
						<div class="form-group col-sm-4">
							Number of recent patients to display:<br>
							<select ng-model="settingsCtrl.pref.recentPatients" class="form-control">
								<option value="1">1</option>
								<option value="2">2</option>
								<option value="3">3</option>
								<option value="4">4</option>
								<option value="5">5</option>
								<option value="6">6</option>
								<option value="7">7</option>
								<option value="8">8</option>
								<option value="9">9</option>
								<option value="10">10</option>
								<option value="11">11</option>
								<option value="12">12</option>
								<option value="13">13</option>
								<option value="14">14</option>
								<option value="15">15</option>
								<option value="16">16</option>
							</select>
						</div>
						<!-- Dashboard -->
					</div>
				</div>
				<!-- persona -->

				<div class="row" ng-show="settingsCtrl.currentTab.path == 'general'">
					<div class="col-sm-6">
						<div class="col-sm-12">
							<h3>Override Clinic</h3>
							<hr>
						</div>
						<div class="form-group col-sm-6">
							<label class="control-label">Address:</label>
							<input ng-model="settingsCtrl.pref.rxAddress" placeholder="Address" class="form-control" type="text">
						</div>
						<div class="form-group col-sm-6">
							<label class="control-label">City:</label>
							<input ng-model="settingsCtrl.pref.rxCity" placeholder="City" class="form-control" type="text">
						</div>
						<div class="form-group col-sm-6">
							<label>Province:</label>
							<input ng-model="settingsCtrl.pref.rxProvince" placeholder="Province" class="form-control" type="text">
						</div>
						<div class="form-group col-sm-6">
							<label>Postal Code:</label>
							<input ng-model="settingsCtrl.pref.rxPostal" placeholder="Postal Code" class="form-control" type="text">
						</div>
						<div class="form-group col-sm-6">
							<label>Phone:</label>
							<input ng-model="settingsCtrl.pref.rxPhone" placeholder="Phone" class="form-control" type="text">
						</div>
						<div class="form-group col-sm-6">
							<label>Fax:</label>
							<input ng-model="settingsCtrl.pref.faxNumber" placeholder="Fax" class="form-control" type="text">
						</div>

						<div class="form-group col-sm-6">
							<label>Tickler Window Provider:</label>
							<select ng-model="settingsCtrl.pref.ticklerWarningProvider" class="form-control" ng-options="p.providerNo as p.name for p in settingsCtrl.providerList">
							</select>
						</div>

						<div class="form-group col-sm-6">
							<label>Workload Management:</label>
							<select ng-model="settingsCtrl.pref.workloadManagement" class="form-control" ng-options="item.name as item.type for item in settingsCtrl.billingServiceTypesMod">
							</select>
						</div>

						<div class="form-group col-sm-6">
							<label></label>
							<div class="controls">
								<button class="btn btn-default" ng-click="settingsCtrl.openChangePasswordModal()">Change Password</button>
							</div>
						</div>

						<div class="form-group col-sm-6">
							<label>Enable Tickler Window:</label>
							<div class="controls">
								<label class="checkbox-inline" for="radios-0">
									<input ng-model="settingsCtrl.pref.newTicklerWarningWindow" name="radios" id="radios-0" value="enabled" type="radio">
									Enable
								</label>
								<label class="checkbox-inline" for="radios-1">
									<input ng-model="settingsCtrl.pref.newTicklerWarningWindow" name="radios" id="radios-1" value="disabled" type="radio">
									Disable
								</label>
							</div>
						</div>

						<div class="form-group col-sm-6">
							<label>Default Tickler View</label>
							<div>
								<label class="checkbox-inline" for="all-ticklers-radio">
									<input ng-model="settingsCtrl.pref.ticklerViewOnlyMine" name="tickler-view-radios" id="all-ticklers-radio" ng-value="false" type="radio">
									All ticklers
								</label>
								<label class="checkbox-inline" for="onlymine-ticklers-radio">
									<input ng-model="settingsCtrl.pref.ticklerViewOnlyMine" name="tickler-view-radios" id="onlymine-ticklers-radio" ng-value="true" type="radio">
									View mine only
								</label>
							</div>
						</div>
					</div>
				</div>
				<!--  end row -->

				<div class="row" ng-show="settingsCtrl.currentTab.path == 'schedule'">
					<div class="col-md-8 col-sm-12 md-margin-top" >
						<div class="form-group col-sm-6">
							<label>Start Hour (0-23):</label>
							<input ng-model="settingsCtrl.pref.startHour" placeholder="Start Hour" class="form-control" type="text">
						</div>
						<div class="form-group col-sm-6">
							<label>End Hour (0-23):</label>
							<input ng-model="settingsCtrl.pref.endHour" placeholder="End Hour" class="form-control" type="text">
						</div>
						<div class="form-group col-sm-6">
							<label>Period:</label>
							<input ng-model="settingsCtrl.pref.period" placeholder="Period" class="form-control" type="text">
						</div>
						<ca-field-select
								class="col-sm-6"
								ca-name="settings-schedule-groupNo"
								ca-title="Group No"
								ca-template="label"
								ca-model="settingsCtrl.pref.groupNo"
								ca-options="settingsCtrl.scheduleOptions"
						>
						</ca-field-select>
						<ca-field-select
								class="col-sm-6"
								<%--ca-hide="!eventController.sitesEnabled"--%>
								ca-name="settings-schedule-site"
								ca-title="Site"
								ca-template="label"
								ca-model="settingsCtrl.pref.siteSelected"
								ca-options="settingsCtrl.siteOptions"
								ca-empty-option="true"
						>
						</ca-field-select>

						<div class="form-group col-sm-6">
							<label>Length of patient name to display on appointment screen:</label>
							<input ng-model="settingsCtrl.pref.patientNameLength" placeholder="Length" class="form-control" type="text">
						</div>
						<div class="form-group col-sm-6">
							<label>Length of link and form names to display on appointment screen (> 0):</label>
							<input ng-model="settingsCtrl.pref.appointmentScreenLinkNameDisplayLength" placeholder="Length" class="form-control" type="text">
						</div>
						<div class="form-group col-sm-6">
							<label>Use classic eChart</label>
							<div class="controls">
								<label class="checkbox-inline" for="radioh-0">
									<input ng-model="settingsCtrl.pref.hideOldEchartLinkInAppointment" ng-value="false" id="radioh-0" type="radio">
									Enable
								</label>
								<label class="checkbox-inline" for="radioh-1">
									<input ng-model="settingsCtrl.pref.hideOldEchartLinkInAppointment" ng-value="true" id="radioh-1" type="radio">
									Disable
								</label>
							</div>
						</div>
						<div class="form-group col-sm-6">
							<label>Enable Intake Form</label>
							<div class="controls">
								<label class="checkbox-inline" for="radio-intake-0">
									<input ng-model="settingsCtrl.pref.intakeFormEnabled" ng-value="true" id="radio-intake-0" type="radio">
									Enable
								</label>
								<label class="checkbox-inline" for="radio-intake-1">
									<input ng-model="settingsCtrl.pref.intakeFormEnabled" ng-value="false" id="radio-intake-1" type="radio">
									Disable
								</label>
							</div>
						</div>

						<div class="form-group col-sm-6">
							<label>Encounter Forms to display on appointment screen</label>
							<div style="height:10em;border:solid grey 1px;overflow:auto;white-space:nowrap">
								<span ng-repeat="f in settingsCtrl.encounterForms">
									<input type="checkbox" ng-model="f.checked" ng-change="settingsCtrl.selectEncounterForms()" >{{f.formName}}<br/>
								</span>

							</div>
						</div>
						<div class="form-group col-sm-6">
							<label>Eforms to display on appointment screen</label>
							<div style="height:10em;border:solid grey 1px;overflow:auto;white-space:nowrap">
								<span ng-repeat="f in settingsCtrl.eforms"><input type="checkbox" ng-model="f.checked" ng-change="settingsCtrl.selectEForms()">{{f.formName}}<br/></span>
							</div>
						</div>
						<div class="form-group col-sm-6">
							<label>Quick links to display on appointment screen</label>
							<div style="height:10em;border:solid grey 1px;overflow:auto;white-space:nowrap">
								<span ng-repeat="q in settingsCtrl.pref.appointmentScreenQuickLinks"><input type="checkbox" ng-model="settingsCtrl.q.checked">{{q.name}}<br/></span>
								<button class="btn-sm" ng-click="removeQuickLinks()">Remove</button>
								<button class="btn-sm" ng-click="openQuickLinkModal()">Add</button>
							</div>
						</div>
						<div class="form-group col-sm-6">
							<label>Specify which appointment types are included by the appointment counter:</label>
							<div class="controls">
								<div class="form-group">
									<ca-field-boolean
										ca-name="checkApptCountEnabled"
										ca-title="Enable Counter"
										ca-template="juno"
										ca-model="settingsCtrl.pref.appointmentCountEnabled"
										ca-value="false">
								</div>
								<div  ng-if="settingsCtrl.pref.appointmentCountEnabled">
									<div class="form-group">
										<ca-field-boolean
											ca-name="checkApptCountCanceled"
											ca-title="Include cancelled appointments"
											ca-template="juno"
											ca-model="settingsCtrl.pref.appointmentCountIncludeCancelled"
											ca-value="false">
									</div>
									<div class="form-group">
										<ca-field-boolean
											ca-name="checkApptCountNoShow"
											ca-title="Include no-show appointments"
											ca-template="juno"
											ca-model="settingsCtrl.pref.appointmentCountIncludeNoShow"
											ca-value="false">
									</div>
									<div class="form-group">
										<ca-field-boolean
											ca-name="checkApptCountNoDemographic"
											ca-title="Include appointments not associated with a patient"
											ca-template="juno"
											ca-model="settingsCtrl.pref.appointmentCountIncludeNoDemographic"
											ca-value="false">
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>


				<div class="row" ng-show="settingsCtrl.currentTab.path == 'billing'">
					<div class="col-sm-6 md-margin-top">
						<div class="form-group col-sm-6">
							<label>Default Billing Form:</label>
							<select class="form-control" ng-model="settingsCtrl.pref.defaultServiceType"
								ng-options="item.type as item.name for item in settingsCtrl.billingServiceTypesMod">
							</select>
						</div>
						<div class="form-group col-sm-6">
							<label>Default Diagnostic Code:</label>
							<div class="input-group">
								<input ng-model="settingsCtrl.pref.defaultDxCode" placeholder="" class="form-control" type="text">
								<span class="input-group-btn">
									<button class="btn btn-default btn-search" ng-disabled="true">Search</button>
								</span>
							</div>
						</div>

						<div class="form-group col-sm-6">
							<label>Do Not Delete Previous Billing:</label>
							<div class="controls">
								<label class="checkbox-inline" for="radiosx-0">
									<input ng-model="settingsCtrl.pref.defaultDoNotDeleteBilling" name="radiosx" id="radiosx-0" ng-value="true"  type="radio">
									Enable
								</label>
								<label class="checkbox-inline" for="radiosx-1">
									<input ng-model="settingsCtrl.pref.defaultDoNotDeleteBilling" name="radiosx" id="radiosx-1" ng-value="false" type="radio">
									Disable
								</label>
							</div>
						</div>
					</div>
				</div>

				<div class="row" ng-show="settingsCtrl.currentTab.path == 'rx'">
					<div class="col-sm-6">
						<div class="col-sm-6 no-padding md-margin-top">
							<div class="form-group col-sm-12">
								<label>RX3:</label>
								<div class="controls">
									<label class="radio-inline" for="radios-rx-0">
										<input name="radios-rx-0" id="radios-rx-0" ng-model="settingsCtrl.pref.useRx3" ng-value="true" type="radio">
										Enable
									</label>
									<label class="radio-inline" for="radios-rx-1">
										<input name="radios-rx-1" id="radios-rx-1" ng-model="settingsCtrl.pref.useRx3" ng-value="false" type="radio">
										Disable
									</label>
								</div>
							</div>

							<div class="form-group col-sm-12">
								<label>Show Patient's DOB:</label>
								<div class="controls">
									<label class="radio-inline" for="radios-rx-2">
										<input name="radios-rx-2" id="radios-rx-2" ng-model="settingsCtrl.pref.showPatientDob" ng-value="true" type="radio">
										Enable
									</label>
									<label class="radio-inline" for="radios-rx-3">
										<input name="radios-rx-3" id="radios-rx-3" ng-model="settingsCtrl.pref.showPatientDob" ng-value="false" type="radio">
										Disable
									</label>
								</div>
							</div>
							<div class="form-group col-sm-12">
								<label>Signature:</label>
								<input ng-model="settingsCtrl.pref.signature" placeholder="Signature" class="form-control" type="text">
							</div>

							<div class="form-group col-sm-12">
								<label>Default Quantity:</label>
								<input ng-model="settingsCtrl.pref.rxDefaultQuantity" placeholder="Default Qty" class="form-control" type="text">
							</div>
							<div class="form-group col-sm-12">
								<label>Page Size:</label>
								<select ng-model="settingsCtrl.pref.rxPageSize" class="form-control" ng-options="p.value as p.label for p in settingsCtrl.pageSizes">
								</select>
							</div>
							<div class="form-group col-sm-12">
								<label>Rx Interaction Warning Level:</label>
									<select ng-model="pref.rxInteractionWarningLevel" class="form-control" ng-options="p.value as p.label for p in settingsCtrl.rxInteractionWarningLevels">
								</select>
							</div>

							<div class="form-group col-sm-12">
								<label>Print QR Codes:</label>
								<div class="controls">
									<label class="radio-inline" for="radios-rx-4">
										<input name="radios-rx-4" id="radios-rx-4" ng-model="settingsCtrl.pref.printQrCodeOnPrescription" ng-value="true" type="radio">
										Enable
									</label>
									<label class="radio-inline" for="radios-rx-5">
										<input name="radios-rx-5" id="radios-rx-5" ng-model="settingsCtrl.pref.printQrCodeOnPrescription" ng-value="false" type="radio">
										Disable
									</label>
								</div>
							</div>
						</div>
						<div class="col-sm-6 no-padding">
							<div class="col-sm-12">
								<h3>External Prescriber</h3>
								<hr>
							</div>
							<div class="form-group col-sm-12">
								<label></label>
								<div class="controls">
									<label class="radio-inline" for="radios-rx-6">
										<input name="radios-rx-6" id="radios-rx-6" ng-model="settingsCtrl.pref.eRxEnabled" ng-value="true" type="radio">
										Enable
									</label>
									<label class="radio-inline" for="radios-rx-7">
										<input name="radios-rx-7" id="radios-rx-7" ng-model="settingsCtrl.pref.eRxEnabled" ng-value="false" type="radio">
										Disable
									</label>
								</div>
							</div>
							<div class="form-group col-sm-12">
								<label>Training Mode:</label>
								<div class="controls">
									<label class="radio-inline" for="radios-rx-8">
										<input name="radios-rx-8" id="radios-rx-8" ng-model="settingsCtrl.pref.eRxTrainingMode" ng-value="true" type="radio">
										Enable
									</label>
									<label class="radio-inline" for="radios-rx-9">
										<input name="radios-rx-9" id="radios-rx-9" ng-model="settingsCtrl.pref.eRxTrainingMode" ng-value="false" type="radio">
										Disable
									</label>
								</div>
							</div>
							<div class="form-group col-sm-12">
								<label>Username:</label>
								<input ng-model="settingsCtrl.pref.eRxUsername" placeholder="Username" class="form-control" type="text">
							</div>
							<div class="form-group col-sm-12">
								<label>Password:</label>
								<input ng-model="settingsCtrl.pref.eRxPassword" placeholder="Password" class="form-control" type="text">
							</div>
							<div class="form-group col-sm-12">
								<label>Clinic #:</label>
								<input ng-model="settingsCtrl.pref.eRxFacility" placeholder="Clinic #" class="form-control" type="text">
							</div>
							<div class="form-group col-sm-12">
								<label>URL:</label>
								<input ng-model="settingsCtrl.pref.eRxURL" placeholder="URL" class="form-control" type="text">
							</div>
						</div>
					</div>
				</div>

				<div class="row" ng-show="settingsCtrl.currentTab.path == 'masterdemo'">
					<div class="col-lg-4 col-sm-6 md-margin-top">
						<div class="form-group col-sm-12">
							<label>Default HC Type:</label>
							<select class="form-control" ng-model="settingsCtrl.pref.defaultHcType">
								<option value="" >--</option>
								<option value="AB" >AB-Alberta</option>
								<option value="BC" >BC-British Columbia</option>
								<option value="MB" >MB-Manitoba</option>
								<option value="NB" >NB-New Brunswick</option>
								<option value="NL" >NL-Newfoundland Labrador</option>
								<option value="NT" >NT-Northwest Territory</option>
								<option value="NS" >NS-Nova Scotia</option>
								<option value="NU" >NU-Nunavut</option>
								<option value="ON" >ON-Ontario</option>
								<option value="PE" >PE-Prince Edward Island</option>
								<option value="QC" >QC-Quebec</option>
								<option value="SK" >SK-Saskatchewan</option>
								<option value="YT" >YT-Yukon</option>
								<option value="US" >US resident</option>
								<option value="US-AK" >US-AK-Alaska</option>
								<option value="US-AL" >US-AL-Alabama</option>
								<option value="US-AR" >US-AR-Arkansas</option>
								<option value="US-AZ" >US-AZ-Arizona</option>
								<option value="US-CA" >US-CA-California</option>
								<option value="US-CO" >US-CO-Colorado</option>
								<option value="US-CT" >US-CT-Connecticut</option>
								<option value="US-CZ" >US-CZ-Canal Zone</option>
								<option value="US-DC" >US-DC-District Of Columbia</option>
								<option value="US-DE" >US-DE-Delaware</option>
								<option value="US-FL" >US-FL-Florida</option>
								<option value="US-GA" >US-GA-Georgia</option>
								<option value="US-GU" >US-GU-Guam</option>
								<option value="US-HI" >US-HI-Hawaii</option>
								<option value="US-IA" >US-IA-Iowa</option>
								<option value="US-ID" >US-ID-Idaho</option>
								<option value="US-IL" >US-IL-Illinois</option>
								<option value="US-IN" >US-IN-Indiana</option>
								<option value="US-KS" >US-KS-Kansas</option>
								<option value="US-KY" >US-KY-Kentucky</option>
								<option value="US-LA" >US-LA-Louisiana</option>
								<option value="US-MA" >US-MA-Massachusetts</option>
								<option value="US-MD" >US-MD-Maryland</option>
								<option value="US-ME" >US-ME-Maine</option>
								<option value="US-MI" >US-MI-Michigan</option>
								<option value="US-MN" >US-MN-Minnesota</option>
								<option value="US-MO" >US-MO-Missouri</option>
								<option value="US-MS" >US-MS-Mississippi</option>
								<option value="US-MT" >US-MT-Montana</option>
								<option value="US-NC" >US-NC-North Carolina</option>
								<option value="US-ND" >US-ND-North Dakota</option>
								<option value="US-NE" >US-NE-Nebraska</option>
								<option value="US-NH" >US-NH-New Hampshire</option>
								<option value="US-NJ" >US-NJ-New Jersey</option>
								<option value="US-NM" >US-NM-New Mexico</option>
								<option value="US-NU" >US-NU-Nunavut</option>
								<option value="US-NV" >US-NV-Nevada</option>
								<option value="US-NY" >US-NY-New York</option>
								<option value="US-OH" >US-OH-Ohio</option>
								<option value="US-OK" >US-OK-Oklahoma</option>
								<option value="US-OR" >US-OR-Oregon</option>
								<option value="US-PA" >US-PA-Pennsylvania</option>
								<option value="US-PR" >US-PR-Puerto Rico</option>
								<option value="US-RI" >US-RI-Rhode Island</option>
								<option value="US-SC" >US-SC-South Carolina</option>
								<option value="US-SD" >US-SD-South Dakota</option>
								<option value="US-TN" >US-TN-Tennessee</option>
								<option value="US-TX" >US-TX-Texas</option>
								<option value="US-UT" >US-UT-Utah</option>
								<option value="US-VA" >US-VA-Virginia</option>
								<option value="US-VI" >US-VI-Virgin Islands</option>
								<option value="US-VT" >US-VT-Vermont</option>
								<option value="US-WA" >US-WA-Washington</option>
								<option value="US-WI" >US-WI-Wisconsin</option>
								<option value="US-WV" >US-WV-West Virginia</option>
								<option value="US-WY" >US-WY-Wyoming</option>
								<option value="OT">Other</option>
							</select>
						</div>
						<div class="form-group col-sm-12">
							<label>Default Sex:</label>
							<select ng-model="settingsCtrl.pref.defaultSex" class="form-control">
								<option value="M">Male</option>
								<option value="F">Female</option>
								<option value="T">Transgender</option>
								<option value="O">Other</option>
								<option value="U">Undefined</option>
							</select>
						</div>
					</div>
				</div>

				<div class="row" ng-show="settingsCtrl.currentTab.path == 'consults'">
					<div class="col-lg-4 col-sm-6 md-margin-top">
						<div class="form-group col-sm-12">
							<label>Consultation Cutoff Time Warning (in months):</label>
							<input ng-model="settingsCtrl.pref.consultationTimePeriodWarning" placeholder="Cutoff Time Warning" class="form-control" type="text">
						</div>
						<div class="form-group col-sm-12">
							<label>Consultation Team Warning:</label>
							<select class="form-control" ng-model="settingsCtrl.pref.consultationTeamWarning" ng-options="item.value as item.label for item in settingsCtrl.teams">
							</select>
						</div>
						<div class="form-group col-sm-12">
							<label>Paste Format:</label>
							<select ng-model="settingsCtrl.pref.consultationPasteFormat" class="form-control" ng-options="p.value as p.label for p in settingsCtrl.pasteFormats">
							</select>
						</div>

						<div class="form-group col-sm-12">
							<label>Consult Letterhead Name Default:</label>
							<select ng-model="settingsCtrl.pref.consultationLetterHeadNameDefault" class="form-control" ng-options="l.value as l.label for l in settingsCtrl.letterHeadNameDefaults">
							</select>
						</div>
					</div>
				</div>

				<div class="row" ng-show="settingsCtrl.currentTab.path == 'documents'">
					<div class="col-lg-4 col-sm-6 md-margin-top">
						<div class="form-group col-sm-12">
							<label>Document Browser In Document Report:</label>
							<div class="controls">
								<label class="radio-inline" for="radios-doc-0">
									<input name="radios-doc-0" id="radios-doc-0" ng-model="settingsCtrl.pref.documentBrowserInDocumentReport" ng-value="true" type="radio">
									Enable
								</label>
								<label class="radio-inline" for="radios-doc-1">
									<input name="radios-doc-1" id="radios-doc-1" ng-model="settingsCtrl.pref.documentBrowserInDocumentReport" ng-value="false" type="radio">
									Disable
								</label>
							</div>
						</div>
						<div class="form-group col-sm-12">
							<label>Document Browser In Master File:</label>
							<div class="controls">
								<label class="radio-inline" for="radios-doc-2">
									<input name="radios-doc-2" id="radios-doc-2" ng-model="settingsCtrl.pref.documentBrowserInMasterFile" ng-value="true" type="radio">
									Enable
								</label>
								<label class="radio-inline" for="radios-doc-3">
									<input name="radios-doc-3" id="radios-doc-3" ng-model="settingsCtrl.pref.documentBrowserInMasterFile" ng-value="false" type="radio">
									Disable
								</label>
							</div>
						</div>
						<div class="form-group col-sm-12">
							<label>Clinic Document Template</label>
							<div class="controls">
								<button class="btn btn-default" ng-click="settingsCtrl.editDocumentTemplates()">Manage Document Templates</button>
							</div>
						</div>
					</div>
				</div>

				<div class="row" ng-show="settingsCtrl.currentTab.path == 'summary'" id="summary">
					<div class="col-md-7">
						<div class="col-sm-12">
							<h3>Notes</h3>
							<hr>
						</div>
						<div class="form-group col-sm-12">
							<label>CPP Single Line</label>
							<div class="controls">
								<label class="radio-inline" for="radios-enc-0">
									<input name="radios-enc-0" id="radios-enc-0" ng-model="settingsCtrl.pref.cppSingleLine" ng-value="true" type="radio">
									Enable
								</label>
								<label class="radio-inline" for="radios-enc-1">
									<input name="radios-enc-1" id="radios-enc-1" ng-model="settingsCtrl.pref.cppSingleLine" ng-value="false" type="radio">
									Disable
								</label>
							</div>
						</div>
						<div class="form-group col-sm-12">
							<label>Use Single View</label>
							<div class="controls">
								<label class="radio-inline" for="radios-enc-2">
									<input name="radios-enc-2" id="radios-enc-2" ng-model="settingsCtrl.pref.cmeNoteFormat" ng-value="true" type="radio">
									Enable
								</label>
								<label class="radio-inline" for="radios-enc-3">
									<input name="radios-enc-3" id="radios-enc-3" ng-model="settingsCtrl.pref.cmeNoteFormat" ng-value="false" type="radio">
									Disable
								</label>
							</div>
						</div>

						<div class="form-group col-sm-12">
							<label>Stale Date</label>
							<small><em>Please set how many months in the past before a Case Management Note is fully visible e.g. Set to 6 will display fully all notes within the last 6 months</em></small>
							<select ng-model="settingsCtrl.pref.cmeNoteDate" class="form-control" ng-options="p.value as p.label for p in settingsCtrl.staleDates">
							</select>
						</div>

						<div class="form-group col-sm-12">
							<label>Default Quick Chart Size</label>
							<small><em>Enter the number of notes for quick chart size.</em></small>
							<input ng-model="settingsCtrl.pref.quickChartSize" class="form-control" type="text">
						</div>
					<div class="col-sm-12">
						<h3>Patient Summary Viewable Items</h3>
						<hr>
					</div>

					<div class="form-group col-sm-12" id="summary-items">
						<label>Enable Custom Summary</label>
						<small><em>Enabling this feature will allow you to to hide or display CPP and Summary Items.</em></small>
						<div class="controls">
							<label class="radio-inline" for="radios-enc-4">
								<input name="radios-enc-4" id="radios-enc-4" ng-model="settingsCtrl.pref.summaryItemCustomDisplay" ng-value="true" type="radio">
								Enable
							</label>
							<label class="radio-inline" for="radios-enc-5">
								<input name="radios-enc-5" id="radios-enc-5" ng-model="settingsCtrl.pref.summaryItemCustomDisplay" ng-value="false" type="radio">
								Disable
							</label>
						</div>
					</div>
					</div>
					<patient-summary-items
							ng-if="settingsCtrl.pref.summaryItemCustomDisplay"
							pref="settingsCtrl.pref"
					></patient-summary-items>
					<div class="col-md-3 pull-right col-xs-12">
						<h4>Classic Encounter Preferences:</h4>
						<div class="well">
							<a href="javascript:void(0)" ng-click="settingsCtrl.showDefaultEncounterWindowSizePopup()">Set Default Encounter Window Size</a><br>
							<a href="javascript:void(0)" ng-click="settingsCtrl.showProviderColourPopup()">Set Provider Colour</a><br>
							<a href="javascript:void(0)" ng-click="settingsCtrl.openConfigureEChartCppPopup()">Configure EChart CPP</a><br>
						</div>
					</div>
					<!-- container -->
				</div>
				<!--  row summary  -->

				<div class="row" ng-show="settingsCtrl.currentTab.path == 'eforms'">
					<div class="col-lg-4 col-sm-6 md-margin-top">
						<div class="form-group col-sm-12 juno-modal">
							<label>Favorite Group:</label>
							<select ng-model="settingsCtrl.pref.favoriteFormGroup" class="form-control" ng-options="p.value as p.label for p in settingsCtrl.formGroupNames">
							</select>
							<div class="flex-row md-margin-top">
								<div class="flex-grow lg-margin-right" ng-class="{'required-field': !settingsCtrl.pref.eformPopupWidth}">
									<ca-field-number
													ca-title="Eform Popup Width"
													ca-name="popup_width"
													ca-model="settingsCtrl.pref.eformPopupWidth"
													ca-rows="1">
									</ca-field-number>
								</div>
								<div class="flex-grow lg-margin-right" ng-class="{'required-field': !settingsCtrl.pref.eformPopupHeight}">
									<ca-field-number
													class="flex-grow"
													ca-title="Eform Popup Height"
													ca-name="popup_height"
													ca-model="settingsCtrl.pref.eformPopupHeight"
													ca-rows="1">
									</ca-field-number>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="row" ng-show="settingsCtrl.currentTab.path == 'inbox'">
					<div class="col-lg-4 col-sm-6 md-margin-top">
						<div class="form-group col-sm-12">
							<label>Disable comment on acknowledgment:</label>
							<div class="controls">
								<label class="radio-inline" for="radios-inb-0">
									<input name="radios-inb-0" id="radios-inb-0" ng-model="settingsCtrl.pref.disableCommentOnAck" ng-value="true" type="radio">
									Enable
								</label>
								<label class="radio-inline" for="radios-inb-1">
									<input name="radios-inb-1" id="radios-inb-1" ng-model="settingsCtrl.pref.disableCommentOnAck" ng-value="false" type="radio">
									Disable
								</label>
							</div>
						</div>

					</div>
				</div>

				<div class="row" ng-show="settingsCtrl.currentTab.path == 'programs'">
					<div class="col-lg-4 col-sm-6 md-margin-top">
						<div class="form-group col-sm-6">
							<label>Default PMM:</label>
							<div class="controls">
								<label class="radio-inline" for="radios-pro-0">
									<input name="radios-pro-0" id="radios-pro-0" ng-model="settingsCtrl.pref.defaultPmm" ng-value="true" type="radio">
									Enable
								</label>
								<label class="radio-inline" for="radios-pro-1">
									<input name="radios-pro-1" id="radios-pro-1" ng-model="settingsCtrl.pref.defaultPmm" ng-value="false" type="radio">
									Disable
								</label>
							</div>
						</div>
					</div>
				</div>

				<div class="row" ng-show="settingsCtrl.currentTab.path == 'integration'">
					<div class="col-lg-6 col-sm-10 md-margin-top">
							<div class="form-group col-sm-6">
								<label>OLIS Default Reporting Laboratory:</label>
								<select class="form-control" ng-model="settingsCtrl.pref.olisDefaultReportingLab"
									ng-options="item.value as item.label for item in settingsCtrl.olisLabs">
								</select>
							</div>
							<div class="form-group col-sm-6">
								<label>OLIS Default Exclude Reporting Laboratory:</label>
								<select class="form-control" ng-model="settingsCtrl.pref.olisDefaultExcludeReportingLab"
									ng-options="item.value as item.label for item in settingsCtrl.olisLabs">
								</select>
							</div>

							<div class="form-group col-sm-6">
								<label>MyDrugRef ID:</label>
								<input ng-model="settingsCtrl.pref.myDrugRefId" placeholder="MyDrugRef ID" class="form-control" type="text">
							</div>
							<div class="form-group col-sm-6">
								<label>Use MyMeds:</label>
								<div class="controls">
									<label class="radio-inline" for="enable-use-my-meds">
										<input name="enableUseMyMeds" id="enable-use-my-meds" ng-model="settingsCtrl.pref.useMyMeds" ng-value="true" type="radio">
										Enable
									</label>
									<label class="radio-inline" for="disable-use-my-meds">
										<input name="disableUseMyMeds" id="disable-use-my-meds" ng-model="settingsCtrl.pref.useMyMeds" ng-value="false" type="radio">
										Disable
									</label>
								</div>
							</div>

							<div class="form-group col-sm-6">
								<label>BORN prompts in RBR/NDDS:</label>
								<div class="controls">
									<label class="radio-inline" for="enable-born-prompt">
										<input name="enableBornPrompt" id="enable-born-prompt" ng-model="settingsCtrl.pref.disableBornPrompts" ng-value="false" type="radio">
										Enable
									</label>
									<label class="radio-inline" for="disable-born-prompt">
										<input name="disableBornPrompt" id="disable-born-prompt" ng-model="settingsCtrl.pref.disableBornPrompts" ng-value="true" type="radio">
										Disable
									</label>
								</div>
							</div>

							<div class="form-group col-sm-6">
								<label>Apps: <a ng-click="settingsCtrl.refreshAppList()">Refresh</a></label>
								<table class="table table-striped table-bordered">
									<tr>
										<th>App Name</th>
										<th>Status</th>
									</tr>
									<tr ng-repeat="app in settingsCtrl.loadedApps">
										<td>{{app.name}}</td>
										<td ng-show="app.authenticated">{{app.authenticated}}</td>
										<td ng-hide="app.authenticated"><a ng-click="settingsCtrl.authenticate(app)">Authenticate</a></td>
									</tr>
								</table>
							</div>

							<div class="form-group col-sm-12">
								<div class="controls">
									<button class="btn btn-default" ng-click="settingsCtrl.openManageAPIClientPopup()">Manage API Clients</button>
								</div>
							</div>

							<div class="form-group col-sm-12">
								<div class="controls">
									<button class="btn btn-default" ng-click="settingsCtrl.openMyOscarUsernamePopup()">Set PHR Username</button>
								</div>
							</div>

						</div>
					</div>
			</form>

			<div class="col-sm-4 settings-footer">
				<button class="btn btn-primary" ng-click="settingsCtrl.cancel()">Cancel</button>
				<button class="btn btn-success"
				        ng-disabled="!settingsCtrl.saveEnabled()"
				        ng-click="settingsCtrl.save()">
					Save All Settings
				</button>
			</div>
		</juno-security-check>
	</div>
</div>