<div class="juno-tab" ng-class="$ctrl.componentClasses()">
	
	<!-- normal tab style -->
	<div ng-if="$ctrl.type === JUNO_TAB_TYPE.NORMAL"
			 ng-repeat="tab in $ctrl.tabs" ng-click="$ctrl.onTabSelect(tab)"
			 ng-class="$ctrl.tabClasses(tab)"
			 class="tab">
		{{tab.label}}
	</div>
	
	<!-- swim lane tab style -->
	<div ng-if="$ctrl.type === JUNO_TAB_TYPE.SWIM_LANE" class="height-100 width-100">
		<swim-lane-tab ng-model="$ctrl.ngModel"
		               tabs="$ctrl.tabs"
		               component-style="$ctrl.componentStyle">
		</swim-lane-tab>
	</div>

	<!-- folder tab style -->
	<div ng-if="$ctrl.type === JUNO_TAB_TYPE.FOLDER_TAB" class="height-100 width-100">
		<folder-tab ng-model="$ctrl.ngModel"
		            tabs="$ctrl.tabs"
		            component-style="$ctrl.componentStyle">
		</folder-tab>
	</div>
</div>