<div class="record-nav">
	<nav class="nav record-navbar no-print" role="navigation" id="record-nav">
		<!-- Brand and toggle get grouped for better mobile display -->
		<div class="navbar-header">
			<button type="button"
			        class="navbar-toggle"
			        data-toggle="collapse"
			        data-target="#record-nav-collapse">
				<span class="sr-only">Toggle navigation</span>
				<i class="icon icon-bars"></i>
			</button>
		</div>

		<!-- Collect the nav links, forms, and other content for toggling -->
		<div class="collapse navbar-collapse" id="record-nav-collapse">
			<ul class="nav navbar-nav" id="myTabs">
				<li ng-repeat="tab in $ctrl.recordTabs"
				    ng-class="{'active': $ctrl.isActive(tab) }">
					<a href="javascript:void(0)"
					   ng-if="!tab.dropdown"
					   ng-click="$ctrl.changeTab(tab)">
						{{tab.label}}
						<span ng-show="tab.extra"
						      title="{{tab.extra}}"
						      class="badge badge-danger ng-binding ng-scope">
							!
						</span>
					</a>

					<a href="javascript:void(0)"
					   ng-if="tab.dropdown"
					   class="dropdown-toggle"
					   data-toggle="dropdown">{{tab.label}}
						<span class="caret"></span>
					</a>

					<ul ng-if="tab.dropdown"
					    class="dropdown-menu"
					    role="menu">
						<li ng-repeat="dropdownItem in tab.dropdownItems"
						    ng-class="{'active': $ctrl.isActive(dropdownItem) }">
							<a href="javascript:void(0)"
							   ng-click="$ctrl.changeTab(dropdownItem)">{{dropdownItem.label}}</a>
						</li>
					</ul>
				</li>
			</ul>
		</div>
	</nav>
</div>