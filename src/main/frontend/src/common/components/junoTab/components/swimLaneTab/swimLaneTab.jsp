<div class="juno-tab-swim-lane swim-lane width-100 height-100 flex-row align-items-center">
	<juno-button ng-repeat="tab in $ctrl.tabs"
	             ng-mousedown="$ctrl.startScroll($event, tab)"
	             ng-mouseup="$ctrl.endScroll($event, tab)"
	             ng-class="$ctrl.tabClasses(tab)"
	             button-color-override="tab.color">
		<div class="tab-text">
			{{tab.label}}
			<div class="tab-count" ng-if="tab.tabCount">
				<div class="content">
					{{ tab.tabCount }}
				</div>
			</div>
		</div>
	</juno-button>
</div>