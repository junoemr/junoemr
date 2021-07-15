<div class="flowsheet-item-group">
	<panel no-header="!$ctrl.model.name">
		<panel-header>
			<h6>{{$ctrl.model.name}}</h6>
			<span>{{$ctrl.model.description}}</span>
			<juno-simple-close-button ng-if="$ctrl.showDelete"
			                          click="$ctrl.onClose()"
			                          class="group-close-button">
			</juno-simple-close-button>
		</panel-header>
		<panel-body>
			<ng-transclude></ng-transclude>
		</panel-body>
	</panel>
</div>