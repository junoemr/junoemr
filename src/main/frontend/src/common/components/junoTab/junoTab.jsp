<div class="juno-tab" ng-class="$ctrl.componentClasses()">
	<div ng-repeat="tab in $ctrl.tabs" ng-click="$ctrl.onTabSelect(tab)" ng-class="$ctrl.tabClasses(tab)" class="tab">
		{{tab.label}}
	</div>
</div>