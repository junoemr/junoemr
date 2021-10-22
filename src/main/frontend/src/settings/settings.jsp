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
			<juno-security-check permissions="settingsCtrl.SecurityPermissions.PreferenceRead">
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

		<juno-security-check show-placeholder="true" permissions="settingsCtrl.SecurityPermissions.PreferenceRead">

			<div class="ui-view-wrapper flex-grow">
				<ui-view></ui-view>
			</div>

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