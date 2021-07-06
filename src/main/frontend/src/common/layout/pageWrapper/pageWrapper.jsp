<div class="page-wrapper" ng-class="$ctrl.componentClasses()">
	<div ng-if="!$ctrl.noHeader" ng-transclude="header" class="header"></div>
	<div ng-transclude="body" class="body"></div>
</div>