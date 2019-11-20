<div class="icon-card" ng-click="$ctrl.onCardClick()">
	<div class="icon-card-inner flex flex-column" ng-class="$ctrl.cardSize">
		<div class="card-icon flex flex-row">
			<i class="icon" ng-class="$ctrl.icon"></i>
		</div>
		<div class="card-text flex flex-row">
			<span>{{ $ctrl.text }}</span>
		</div>
	</div>
</div>