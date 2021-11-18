
<div class="summary-module-base form-horizontal">
	<div class="summary-module-header">
		<div class="flex-row align-items-center">
			<div ng-switch="$ctrl.clickableTitle" class="flex-grow title">
				<div class="body-small-bold flex-grow title" ng-switch-when="false">{{$ctrl.module.displayName}}</div>
				<div class="body-small-bold flex-grow title" ng-switch-when="true"><a href="javascript:" ng-click="$ctrl.clickTitleCallback()">{{$ctrl.module.displayName}}</a></div>
			</div>
			<button ng-if="$ctrl.addButton"
			        ng-disabled="!$ctrl.addButtonEnabled"
					class="btn btn-xs btn-primary-inverted btn-add"
			        ng-click="$ctrl.addBtnCallback()"
			>
				Add
				<i class="icon icon-plus-circle"></i>
			</button>
		</div>
        <div ng-if="$ctrl.enableFilter && $ctrl.filterTypeOptions.length > 0" class="grid-column-2 grid-gap-8 grid-align-items-start m-b-8">
            <juno-check-box ng-repeat="filterType in $ctrl.filterTypeOptions"
                            class="grid-item"
                            label="{{filterType.label}}"
                            ng-model="$ctrl.filterTypeOptionModels[filterType.value]"
                            change="$ctrl.setTypeFilter(value, filterType.value)">
            </juno-check-box>
        </div>
        <juno-input ng-if="$ctrl.enableFilter"
                    title="Filter"
                    ng-model="$ctrl.itemFilter"
                    placeholder="Filter by name">
        </juno-input>
	</div>
	<div class="summary-module-body">
		<ul>
			<li ng-repeat="item in $ctrl.module.summaryItem | filter: {displayName: $ctrl.itemFilter, type: $ctrl.itemTypeFilter}"
			    ng-show="($ctrl.showAllItems || $index < $ctrl.itemDisplayCount)">

				<a ng-click="$ctrl.itemCallback(item)"
				   class="hand-hover"
				   ng-class="{true: 'abnormal', false: ''}[item.abnormalFlag]"
				   title="{{item.displayName}} {{item.warning}}"
				>

					<span>{{item.displayName | limitTo: $ctrl.maxItemNameLength }} {{(item.displayName.length > $ctrl.maxItemNameLength)? '...' : '' }}</span>
					<small ng-show="item.classification">({{item.classification}})</small>
					<span class="pull-right" ng-if="!$ctrl.hideDate">{{item.date | date : $ctrl.displayDateFormat}}</span>
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
