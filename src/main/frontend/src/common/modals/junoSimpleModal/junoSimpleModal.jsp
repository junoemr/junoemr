<div class="juno-simple-modal height-100 width-100" ng-class="$ctrl.getComponentClasses()">
	<div class="inner-container height-100 width-100" ng-class="$ctrl.getInnerContainerClasses()">
		<juno-button class="close-btn"
		             ng-click="$ctrl.onCancel()"
		             button-color="JUNO_BUTTON_COLOR.PRIMARY"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             component-style="$ctrl.componentStyle">
			<div class="flex-column justify-content-center juno-text">
				<i class="close-icon icon icon-close"></i>
			</div>
		</juno-button>

		<ng-transclude class="width-100 height-100">
		</ng-transclude>
	</div>
</div>