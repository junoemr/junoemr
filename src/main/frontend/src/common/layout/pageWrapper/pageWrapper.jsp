<div class="page-wrapper flex-col">
	<div ng-if="$ctrl.showHeader" ng-transclude="header" class="header"></div>
	<div class="page-fill-wrapper flex-item-grow">
		<div ng-transclude="body" class="body"></div>
	</div>
</div>