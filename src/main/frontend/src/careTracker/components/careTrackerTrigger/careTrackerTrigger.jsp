<div class="flex-row align-items-center care-tracker-trigger">
	<span class="row-item">
		{{$ctrl.model.codingSystem}}
	</span>
	<span class="row-item badge badge-primary badge-pill"
	      title="{{$ctrl.model.description}}">
		{{$ctrl.model.code}}
	</span>
	<juno-simple-close-button
			class="row-item"
			click="$ctrl.onClose()"
			disabled="$ctrl.disabled"
			tooltip="Remove Trigger Code">
	</juno-simple-close-button>
</div>