<div class="flowsheet-item-group">
	<panel no-header="!$ctrl.model.name">
		<panel-header>
			<h6>{{$ctrl.model.name}}</h6>
			<span>{{$ctrl.model.description}}</span>
		</panel-header>
		<panel-body>
			<ng-transclude></ng-transclude>
		</panel-body>
	</panel>
</div>