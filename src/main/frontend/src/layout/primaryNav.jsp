<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<div ng-init="$ctrl.init()"
     ng-show="$ctrl.me != null"
     id="primary-navigation">
	<nav class="nav no-print">
		<div class="container-fluid">
			<div class="navbar-header">
				<button class="btn btn-icon"
						ng-click="$ctrl.onClickNavIcon()"
						title="{{ $ctrl.navTitle() }}"
				>
					<a ng-if="$ctrl.customNavIcon !== null && !$ctrl.customNavIcon"
					   class="icon icon-logo">
					</a>
					<img ng-if="$Sctrl.customNavIcon !== null && $ctrl.customNavIcon"
						 src="../imageRenderingServlet?source=custom_nav_icon"
						 alt="dashboard icon"
						 class="icon icon-custom"/>
				</button>
			</div>
			<div class="navbar-collapse collapse" id="main-nav-collapse">
				<div class="navbar-left">
					<form class="vertical-align patient-search-form" role="search">
						<div class="form-group breakpoint-sm-visible" ng-cloak>
							<juno-patient-search-typeahead
									juno-disabled="!$ctrl.patientSearchEnabled()"
									juno-model="$ctrl.demographicSearch"
									juno-icon-left="true"
									juno-placeholder="<bean:message key="navbar.searchPatients" bundle="ui"/>"
							>
							</juno-patient-search-typeahead>
						</div>
						<div class="form-group">
							<button class="btn btn-icon btn-visible"
							        ng-disabled="!$ctrl.patientSearchEnabled()"
							        title="<bean:message key="navbar.searchPatients" bundle="ui"/>"
							ng-click="$ctrl.onPatientSearch($ctrl.demographicSearch)">
							<i class="icon icon-user-search"></i>
							</button>
						</div>
						<div class="form-group">
							<button class="btn btn-icon btn-visible"
							        ng-disabled="!$ctrl.patientCreationEnabled()"
							        title="<bean:message key="navbar.newPatient" bundle="ui"/>"
							ng-click="$ctrl.newDemographic()">
							<i class="icon icon-user-add"></i>
							</button>
						</div>
					</form>
				</div>

				<!-- Large view -->
				<ul class="nav navbar-nav breakpoint-lg-visible-exclusive" ng-cloak>
					<li ng-repeat="item in $ctrl.menuItems"
					    ng-class="{'active': $ctrl.isActive(item) }">

						<a href="javascript:void(0)"
						   ng-if="!item.dropdown"
						   ng-click="$ctrl.transition(item)">{{item.label}}
							<span ng-if="$ctrl.getCountForLabel(item) > 0"
							      class="badge badge-danger"
							>{{item.labelCount}}</span>
							<span class="unclaimed-inbox-button badge badge-warning"
							      ng-if="(item.label === 'Inbox') && ($ctrl.unclaimedCount > 0)"
							      ng-click="$ctrl.transition(item,'&providerNo=0&searchProviderNo=0&status=N'); $event.stopPropagation()"
							>{{$ctrl.unclaimedCount}}</span>
						</a>

						<a href="javascript:void(0)"
						   ng-if="item.dropdown"
						   class="dropdown-toggle"
						   data-toggle="dropdown">{{item.label}}
							<span class="caret"></span>
						</a>

						<ul ng-if="item.dropdown"
						    class="dropdown-menu"
						    role="menu">
							<li ng-repeat="dropdownItem in item.dropdownItems">
								<a href="javascript:void(0)"
								   ng-click="$ctrl.transition(dropdownItem)">{{dropdownItem.label}}</a>
							</li>
						</ul>
					</li>
				</ul>

				<!-- Medium view -->
				<ul class="nav navbar-nav breakpoint-md-visible-exclusive" ng-cloak>
					<li ng-repeat="item in $ctrl.menuItems | filter: $ctrl.mediumNavItemFilter(false)"
					    ng-class="{'active': $ctrl.isActive(item) }">

						<a href="javascript:void(0)"
						   ng-if="!item.dropdown"
						   ng-click="$ctrl.transition(item)">{{item.label}}
							<span ng-if="$ctrl.getCountForLabel(item) > 0"
							      class="badge badge-danger"
							>{{item.labelCount}}</span>
							<span class="unclaimed-inbox-button badge badge-warning"
							      ng-if="(item.label === 'Inbox') && ($ctrl.unclaimedCount > 0)"
							      ng-click="$ctrl.transition(item,'&providerNo=0&searchProviderNo=0&status=N'); $event.stopPropagation()"
							>{{$ctrl.unclaimedCount}}</span>
						</a>

						<a href="javascript:void(0)"
						   ng-if="item.dropdown"
						   class="dropdown-toggle"
						   data-toggle="dropdown">{{item.label}}
							<span class="caret"></span>
						</a>

						<ul ng-if="item.dropdown"
						    class="dropdown-menu"
						    role="menu">
							<li ng-repeat="dropdownItem in item.dropdownItems">
								<a href="javascript:void(0)"
								   ng-click="$ctrl.transition(dropdownItem)">{{dropdownItem.label}}</a>
							</li>
						</ul>
					</li>
					<li class="dropdown hand-hover">
						<a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown">
							More
							<b class="caret"></b>
						</a>

						<ul class="dropdown-menu" role="menu">
							<li ng-repeat="item in $ctrl.menuItems | filter: $ctrl.mediumNavItemFilter(true)"
							    ng-class="{'active': $ctrl.isActive(item) }">
								<a href="javascript:void(0)"

								   ng-click="$ctrl.transition(item)" data-toggle="tab">{{item.label}}
									<span ng-if="item.extra.length>0">({{item.extra}})</span>
								</a>
							</li>
						</ul>
					</li>
				</ul>

				<!--Small View-->
				<ul class="nav navbar-nav breakpoint-sm-visible-exclusive" ng-cloak>
					<li ng-repeat="item in $ctrl.menuItems | filter: $ctrl.smallNavItemFilter(false)"
					    ng-class="{'active': $ctrl.isActive(item) }">
						<a ng-click="$ctrl.transition(item)" data-toggle="tab">{{item.label}}
							<span ng-if="item.extra.length>0">({{item.extra}})</span>
						</a>
					</li>
					<li class="dropdown hand-hover">
						<a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown">
							More
							<b class="caret"></b>
						</a>

						<ul class="dropdown-menu" role="menu">
							<li ng-repeat="item in $ctrl.menuItems | filter: $ctrl.smallNavItemFilter(true)"
							    ng-class="{'active': $ctrl.isActive(item) }">
								<a href="javascript:void(0)"
								   ng-if="!item.dropdown"
								   ng-click="$ctrl.transition(item)">{{item.label}}
									<span ng-if="$ctrl.getCountForLabel(item) > 0"
									      class="badge badge-danger"
									>{{item.labelCount}}</span>
									<span class="unclaimed-inbox-button badge badge-warning"
									      ng-if="(item.label === 'Inbox') && ($ctrl.unclaimedCount > 0)"
									      ng-click="$ctrl.transition(item,'&providerNo=0&searchProviderNo=0&status=N'); $event.stopPropagation()"
									>{{$ctrl.unclaimedCount}}</span>
								</a>

								<a href="javascript:void(0)"
								   ng-if="item.dropdown"
								   ng-repeat="dropdownItem in item.dropdownItems"
								   ng-class="{'active': $ctrl.isActive(dropdownItem) }"
								   ng-click="$ctrl.transition(dropdownItem)">
									{{dropdownItem.label}}
								</a>
							</li>
						</ul>
					</li>
				</ul>

				<!-- Mobile View -->
				<ul class="nav navbar-nav breakpoint-mb-visible-exclusive" ng-cloak>
					<li ng-repeat="item in $ctrl.menuItems | filter: $ctrl.mobileNavItemFilter(false)"
					    ng-class="{'active': $ctrl.isActive(item) }">
						<a ng-click="$ctrl.transition(item)" data-toggle="tab">{{item.label}}
							<span ng-if="item.extra.length>0">({{item.extra}})</span>
						</a>
					</li>
					<li class="dropdown hand-hover">
						<a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown">
							More
							<b class="caret"></b>
						</a>

						<ul class="dropdown-menu" role="menu">
							<li ng-repeat="item in $ctrl.menuItems | filter: $ctrl.mobileNavItemFilter(true)"
							    ng-class="{'active': $ctrl.isActive(item) }">
								<a href="javascript:void(0)"
								   ng-if="!item.dropdown"
								   ng-click="$ctrl.transition(item)">{{item.label}}
									<span ng-if="$ctrl.getCountForLabel(item) > 0"
									      class="badge badge-danger"
									>{{item.labelCount}}</span>
									<span class="unclaimed-inbox-button badge badge-warning"
									      ng-if="(item.label === 'Inbox') && ($ctrl.unclaimedCount > 0)"
									      ng-click="$ctrl.transition(item,'&providerNo=0&searchProviderNo=0&status=N'); $event.stopPropagation()"
									>{{$ctrl.unclaimedCount}}</span>
								</a>

								<a href="javascript:void(0)"
								   ng-if="item.dropdown"
								   ng-repeat="dropdownItem in item.dropdownItems"
								   ng-class="{'active': $ctrl.isActive(dropdownItem) }"
								   ng-click="$ctrl.transition(dropdownItem)">
									{{dropdownItem.label}}
								</a>
							</li>
						</ul>
					</li>
				</ul>

				<!-- Nav Bar top right -->
				<div class="vertical-align pull-right navbar-right-menu">
					<ul class="nav navbar-nav" ng-cloak>
						<li>
							<a class="flex-row justify-content-center align-items-center"
							   title="<bean:message key="navbar.scratchpad" bundle="ui"/>"
							ng-click="$ctrl.openScratchpad()">
							<i class="icon icon-write"></i>
							</a>
						</li>
						<li>
							<juno-security-check permissions="$ctrl.SecurityPermissions.MessageRead">
								<a href="javascript:void(0)"
								   class="dropdown-toggle"
								   data-toggle="dropdown">
									<i class="icon icon-chat"></i>
									<span class="caret"></span>
									<span ng-show="$ctrl.getMessengerMessageCount() > 0" class="badge badge-danger flex-row align-items-center">
												{{$ctrl.getMessengerMessageCount()}}
									</span>
								</a>
								<ul class="dropdown-menu"
								    role="menu">
									<li ng-if="$ctrl.mhaEnabled">
										<a href="javascript:void(0)" ng-click="$ctrl.openMhaInbox()">
											<span> Patient Messenger </span>
											<span ng-show="$ctrl.mhaUnreadMessageTotal > 0" class="badge badge-danger flex-row align-items-center m-r-8 m-l-8">
												{{$ctrl.mhaUnreadMessageTotal}}
											</span>
										</a>
									</li>
									<li>
										<a href="javascript:void(0)" ng-click="$ctrl.openMessenger()">
											<span> Internal Messenger </span>
											<span ng-show="$ctrl.unreadMessageTotal > 0" class="badge badge-danger flex-row align-items-center m-r-8 m-l-8">
												{{$ctrl.unreadMessageTotal}}
											</span>
										</a>
									</li>
								</ul>
							</juno-security-check>
						</li>
						<li>
							<a class="dropdown-toggle flex-row justify-content-center align-items-center"
							   title="<bean:message key="navbar.user" bundle="ui"/>"
							data-toggle="dropdown">
							<i class="icon icon-user-md"></i>
							<span class="user-first-name">
								{{$ctrl.me.firstName}}
							</span>
							</a>
							<ul class="dropdown-menu" role="menu">
								<li ng-repeat="item in $ctrl.userMenuItems">
									<a ng-click="$ctrl.transition(item)"
									   ng-class="{'more-tab-highlight': $ctrl.isActive(item) }"
									   class="hand-hover">{{item.label}}</a>
								</li>
							</ul>
						</li>
					</ul>
				</div>
			</div>
			<!--/.nav-collapse -->
		</div>
	</nav>
</div>