<div class="loading-indicator"
     ng-class="{
	'horizontal': $ctrl.isAlignmentHorizontal(),
	'vertical': $ctrl.isAlignmentVertical(),
	}">
	<h1 ng-if="$ctrl.message"
	    class="loading-message">
		{{$ctrl.message}}
	</h1>
	<div ng-if="$ctrl.indicatorType === 'dot-pulse'"
	     class="loading-dot-container">
		<div class="dot-pulse"></div>
	</div>
	<div ng-if="$ctrl.indicatorType === 'spinner'"
	     class="loading-spinner-container">
		<div class="bg-image image-spinner"></div>
	</div>
</div>