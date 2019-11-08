
<div class="zero-state-display flex-row justify-content-center">
	<div class="square">
		<div class="content-container bg-image image-zero-state-background">
			<div class="flex-row justify-content-center flex-grow">
				<div ng-transclude="content"
				     class="zero-state-content">
				</div>
				<h4 class="zero-state-message"
				    ng-if="$ctrl.message">
					{{$ctrl.message}}
				</h4>
			</div>
		</div>
	</div>
</div>