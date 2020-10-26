<div class="juno-round-button">
	<juno-button
					label="{{$ctrl.label}}"
					label-position="$ctrl.labelPosition"
					component-style="$ctrl.componentStyle"
					button-color="$ctrl.buttonColor"
					button-color-pattern="$ctrl.buttonColorPattern"
					button-color-override="$ctrl.buttonColorOverride"
					disabled="$ctrl.disabled"
	>
		<ng-transclude></ng-transclude>
	</juno-button>
</div>