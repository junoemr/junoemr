<div class="juno-collapse-area" ng-class="$ctrl.componentClasses()">
	<div class="header flex-row align-items-center w-100 m-b-16" ng-click="$ctrl.toggleCollapse()">
		<i class="icon m-r-8" ng-class="$ctrl.iconClass()"></i>
		<div>{{$ctrl.label}}</div>
	</div>

	<ng-transclude ng-if="!$ctrl.collapsed" class="w-100">
	</ng-transclude>
</div>