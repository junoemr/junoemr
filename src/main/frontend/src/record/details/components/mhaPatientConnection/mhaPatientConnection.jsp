<div class="mha-patient-connection">
	<juno-button ng-click="$ctrl.onClick()"
	             title="{{$ctrl.getToolTip()}}"
	             label="Connection Status"
	             disabled="$ctrl.buttonDisabled()"
	             button-color="$ctrl.getButtonColor()"
	             button-color-pattern="$ctrl.getButtonColorPattern()"
	             component-style="$ctrl.componentStyle">
		<div class="button-content">
			<i class="icon icon-mha text-left"></i>
			<div>{{ $ctrl.getButtonText() }}</div>
			<i class="icon icon-check text-right"></i>
		</div>
	</juno-button>
</div>