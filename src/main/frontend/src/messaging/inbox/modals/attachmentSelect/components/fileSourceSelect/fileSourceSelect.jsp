<div class="file-source-select flex-col h-100 w-100">
	<div ng-if="!$ctrl.hideChartSources" class="flex-col flex-item-grow">
		<div class="source flex-row align-items-center"
		     ng-class="$ctrl.sourceClasses(FileSource.DOCUMENTS)"
		     ng-click="$ctrl.selectSource(FileSource.DOCUMENTS)">
			<span> Documents </span>
		</div>
	</div>

	<juno-button ng-click="$ctrl.selectSource(FileSource.COMPUTER)"
	             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
	             class="flex-item-no-grow m-l-8 m-r-8">
		<div class="flex-row align-items-center justify-content-center">
			<i class="icon icon-plus m-r-8"></i>
			<span> From Computer </span>
		</div>
	</juno-button>
</div>