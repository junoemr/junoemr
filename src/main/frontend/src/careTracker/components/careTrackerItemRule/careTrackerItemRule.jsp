<div class="care-tracker-item-rule">
	<div class="flex-row justify-content-between align-items-center">
		<span>{{$ctrl.model.name}}</span>
		<div>{{$ctrl.model.description}}</div>
		<juno-simple-close-button click="$ctrl.onClose()"
		                          class="rule-close-button"
		                          disabled="$ctrl.disabled">
		</juno-simple-close-button>
	</div>
</div>