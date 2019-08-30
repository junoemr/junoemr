
<div class="summary-module-base form-horizontal">
	<div class="summary-module-header">
		<div class="flex-row align-items-center">
			<h4 class="flex-grow">{{$ctrl.module.displayName}}</h4>
			<button ng-if="$ctrl.enableAddButton"
					class="btn btn-xs btn-success btn-add pull-right"
			        ng-click="$ctrl.addBtnCallback()"
			>
				Add
			</button>
		</div>
		<ca-field-text
				ng-if="$ctrl.enableFilter"
				ca-name="summary-mod-item-filter"
				ca-title="Filter"
				ca-no-label="true"
				ca-input-size="col-md-12"
				ca-model="$ctrl.itemFilter"
				ca-rows="1"
		>
		</ca-field-text>
	</div>
	<div class="summary-module-body">
		<ul>
			<li ng-repeat="item in $ctrl.module.summaryItem | filter: $ctrl.itemFilter"
			    ng-show="($ctrl.showAllItems || $index < $ctrl.itemDisplayCount)">

				<a ng-click="$ctrl.itemCallback(item)"
				   class="hand-hover"
				   ng-class="{true: 'abnormal', false: ''}[item.abnormalFlag]"
				   title="{{item.displayName}} {{item.warning}}"
				>

					<span>{{item.displayName | limitTo: $ctrl.maxItemNameLength }} {{(item.displayName.length > $ctrl.maxItemNameLength)? '...' : '' }}</span>
					<small ng-show="item.classification">({{item.classification}})</small>
					<span class="pull-right">{{item.date | date : 'yyyy-MM-dd'}}</span>
				</a>
			</li>
		</ul>
	</div>
	<div class="summary-module-footer">
		<button class="btn btn-icon show-all-button pull-right"
		        ng-click="$ctrl.toggleShowAllItems()"
		        ng-show="($ctrl.module.summaryItem.length > $ctrl.itemDisplayCount)"
		>
			<i class="icon icon-chevron-up" ng-show="$ctrl.showAllItems"></i>
			<i class="icon icon-chevron-down" ng-hide="$ctrl.showAllItems"></i>
		</button>
	</div>
</div>