<div class="filter-panel">
	<a class="flex-row expand-filters-bar"
	   ng-click="$ctrl.toggleShowFilters()"
	   title="{{$ctrl.tooltip}}"
	>
		<div class="flex-row vertical-align flex-grow justify-content-between">
			<i class="icon icon-filter"></i>
			<span>{{$ctrl.label}}</span>
			<i class="icon icon-view-off" ng-if="!$ctrl.showFilters"></i>
			<i class="icon icon-view" ng-if="$ctrl.showFilters"></i>
		</div>
	</a>
	<div class="filters-main-panel form-horizontal" ng-show="$ctrl.showFilters">
		<ng-transclude>
		</ng-transclude>
	</div>
</div>