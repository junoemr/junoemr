<div class="juno-list-item-selector" ng-class="$ctrl.componentClasses()">
	<div class="list-container unselected">
		<label ng-if="$ctrl.labelSelected" class="juno-font-body-small">{{$ctrl.labelSelected}}</label>
		<div class="list-group">
			<button ng-repeat="item in $ctrl.selected"
			        class="list-group-item list-group-item-action"
			        ng-class="{'active': (item === $ctrl.activeSelection)}"
			        ng-disabled="$ctrl.disabled"
			        title="{{item.tooltip}}"
			        ng-click="$ctrl.setActiveSelection(item)">
				{{item.label}}
			</button>
		</div>
	</div>
	<div class="flex-column selection-controls">
		<juno-button disabled="$ctrl.disabled"
		             click="$ctrl.addToSelected($ctrl.activeOption)">
			<i class="icon icon-arrow-left"></i>
		</juno-button>
		<juno-button disabled="$ctrl.disabled"
		             click="$ctrl.removeFromSelected($ctrl.activeSelection)">
			<i class="icon icon-arrow-right"></i>
		</juno-button>
	</div>
	<div class="list-container options">
		<label ng-if="$ctrl.labelOptions" class="juno-font-body-small">{{$ctrl.labelOptions}}</label>
		<div class="list-group">
			<button ng-repeat="item in $ctrl.options"
			        class="list-group-item list-group-item-action"
			        ng-class="{'active': (item === $ctrl.activeOption)}"
			        ng-disabled="$ctrl.disabled"
			        title="{{item.tooltip}}"
					ng-click="$ctrl.setActiveOption(item)">
				{{item.label}}
			</button>
		</div>
	</div>
</div>