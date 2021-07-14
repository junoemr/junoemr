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