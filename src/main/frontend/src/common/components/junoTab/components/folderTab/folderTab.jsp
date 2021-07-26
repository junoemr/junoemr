<div class="juno-tab-folder-tab flex-row" ng-ref="$ctrl.tabContainer">
	<div ng-repeat="tab in $ctrl.tabs"
	     ng-click="$ctrl.onTabSelect(tab)"
	     class="folder-tab flex-row justify-content-center align-items-center overflow-hidden p-l-16 p-r-16 m-l-16"
	     ng-class="$ctrl.tabClasses(tab)">
		<div class="text-ellipsis">
			{{tab.label}}
		</div>
	</div>
</div>