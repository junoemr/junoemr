<div class="care-tracker-item-group">
	<panel no-header="!$ctrl.model.name">
		<panel-header>
			<h6 class="care-tracker-item-group-header">{{$ctrl.model.name}}</h6>
			<span class="care-tracker-item-group-description">{{$ctrl.model.description}}</span>
			<juno-simple-close-button ng-if="$ctrl.showDelete"
			                          click="$ctrl.onClose()"
			                          disabled="$ctrl.disabled"
			                          class="group-close-button">
			</juno-simple-close-button>
		</panel-header>
		<panel-body>
			<ng-transclude></ng-transclude>
		</panel-body>
	</panel>
</div>